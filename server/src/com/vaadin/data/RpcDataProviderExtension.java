/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.Indexed.ItemAddEvent;
import com.vaadin.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataProviderState;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.ui.components.grid.Grid;
import com.vaadin.ui.components.grid.GridColumn;
import com.vaadin.ui.components.grid.Renderer;

/**
 * Provides Vaadin server-side container data source to a
 * {@link com.vaadin.client.ui.grid.GridConnector}. This is currently
 * implemented as an Extension hardcoded to support a specific connector type.
 * This will be changed once framework support for something more flexible has
 * been implemented.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class RpcDataProviderExtension extends AbstractExtension {

    /**
     * A helper class that handles the client-side Escalator logic relating to
     * making sure that whatever is currently visible to the user, is properly
     * initialized and otherwise handled on the server side (as far as
     * required).
     * <p>
     * This bookeeping includes, but is not limited to:
     * <ul>
     * <li>listening to the currently visible {@link Property Properties'} value
     * changes on the server side and sending those back to the client; and
     * <li>attaching and detaching {@link com.vaadin.ui.Component Components}
     * from the Vaadin Component hierarchy.
     * </ul>
     */
    private class ActiveRowHandler implements Serializable {
        /**
         * A map from itemId to the value change listener used for all of its
         * properties
         */
        private final Map<Object, GridValueChangeListener> valueChangeListeners = new HashMap<Object, GridValueChangeListener>();

        /**
         * The currently active range. Practically, it's the range of row
         * indices being cached currently.
         */
        private Range activeRange = Range.withLength(0, 0);

        /**
         * A hook for making sure that appropriate data is "active". All other
         * rows should be "inactive".
         * <p>
         * "Active" can mean different things in different contexts. For
         * example, only the Properties in the active range need
         * ValueChangeListeners. Also, whenever a row with a Component becomes
         * active, it needs to be attached (and conversely, when inactive, it
         * needs to be detached).
         * 
         * @param firstActiveRow
         *            the first active row
         * @param activeRowCount
         *            the number of active rows
         */
        public void setActiveRows(int firstActiveRow, int activeRowCount) {

            final Range newActiveRange = Range.withLength(firstActiveRow,
                    activeRowCount);

            // TODO [[Components]] attach and detach components

            /*-
             *  Example
             * 
             *  New Range:       [3, 4, 5, 6, 7]
             *  Old Range: [1, 2, 3, 4, 5]
             *  Result:    [1, 2][3, 4, 5]      []
             */
            final Range[] depractionPartition = activeRange
                    .partitionWith(newActiveRange);
            removeValueChangeListeners(depractionPartition[0]);
            removeValueChangeListeners(depractionPartition[2]);

            /*-
             *  Example
             *  
             *  Old Range: [1, 2, 3, 4, 5]
             *  New Range:       [3, 4, 5, 6, 7]
             *  Result:    []    [3, 4, 5][6, 7]
             */
            final Range[] activationPartition = newActiveRange
                    .partitionWith(activeRange);
            addValueChangeListeners(activationPartition[0]);
            addValueChangeListeners(activationPartition[2]);

            activeRange = newActiveRange;
        }

        private void addValueChangeListeners(Range range) {
            for (int i = range.getStart(); i < range.getEnd(); i++) {

                final Object itemId = container.getIdByIndex(i);
                final Item item = container.getItem(itemId);

                if (valueChangeListeners.containsKey(itemId)) {
                    /*
                     * This might occur when items are removed from above the
                     * viewport, the escalator scrolls up to compensate, but the
                     * same items remain in the view: It looks as if one row was
                     * scrolled, when in fact the whole viewport was shifted up.
                     */
                    continue;
                }

                GridValueChangeListener listener = new GridValueChangeListener(
                        itemId);
                valueChangeListeners.put(itemId, listener);

                for (final Object propertyId : item.getItemPropertyIds()) {
                    final Property<?> property = item
                            .getItemProperty(propertyId);
                    if (property instanceof ValueChangeNotifier) {
                        ((ValueChangeNotifier) property)
                                .addValueChangeListener(listener);
                    }
                }
            }
        }

        private void removeValueChangeListeners(Range range) {
            for (int i = range.getStart(); i < range.getEnd(); i++) {
                final Object itemId = container.getIdByIndex(i);
                final Item item = container.getItem(itemId);
                final GridValueChangeListener listener = valueChangeListeners
                        .remove(itemId);

                if (listener != null) {
                    for (final Object propertyId : item.getItemPropertyIds()) {
                        final Property<?> property = item
                                .getItemProperty(propertyId);
                        if (property instanceof ValueChangeNotifier) {
                            ((ValueChangeNotifier) property)
                                    .removeValueChangeListener(listener);
                        }
                    }
                }
            }
        }

        /**
         * Manages removed properties in active rows.
         * 
         * @param removedPropertyIds
         *            the property ids that have been removed from the container
         */
        public void propertiesRemoved(@SuppressWarnings("unused")
        Collection<Object> removedPropertyIds) {
            /*
             * no-op, for now.
             * 
             * The Container should be responsible for cleaning out any
             * ValueChangeListeners from removed Properties. Components will
             * benefit from this, however.
             */
        }

        /**
         * Manages added properties in active rows.
         * 
         * @param addedPropertyIds
         *            the property ids that have been added to the container
         */
        public void propertiesAdded(Collection<Object> addedPropertyIds) {
            for (int i = activeRange.getStart(); i < activeRange.getEnd(); i++) {
                final Object itemId = container.getIdByIndex(i);
                final Item item = container.getItem(itemId);
                final GridValueChangeListener listener = valueChangeListeners
                        .get(itemId);
                assert (listener != null) : "a listener should've been pre-made by addValueChangeListeners";

                for (final Object propertyId : addedPropertyIds) {
                    final Property<?> property = item
                            .getItemProperty(propertyId);
                    if (property instanceof ValueChangeNotifier) {
                        ((ValueChangeNotifier) property)
                                .addValueChangeListener(listener);
                    }
                }
            }
        }

        /**
         * Handles the insertion of rows.
         * <p>
         * This method's responsibilities are to:
         * <ul>
         * <li>shift the internal bookkeeping by <code>count</code> if the
         * insertion happens above currently active range
         * <li>ignore rows inserted below the currently active range
         * <li>shift (and deactivate) rows pushed out of view
         * <li>activate rows that are inserted in the current viewport
         * </ul>
         * 
         * @param firstIndex
         *            the index of the first inserted rows
         * @param count
         *            the number of rows inserted at <code>firstIndex</code>
         */
        public void insertRows(int firstIndex, int count) {
            if (firstIndex < activeRange.getStart()) {
                activeRange = activeRange.offsetBy(count);
            } else if (firstIndex < activeRange.getEnd()) {
                final Range deprecatedRange = Range.withLength(
                        activeRange.getEnd(), count);
                removeValueChangeListeners(deprecatedRange);

                final Range freshRange = Range.between(firstIndex, count);
                addValueChangeListeners(freshRange);
            } else {
                // out of view, noop
            }
        }

        /**
         * Removes a single item by its id.
         * 
         * @param itemId
         *            the id of the removed id. <em>Note:</em> this item does
         *            not exist anymore in the datasource
         */
        public void removeItemId(Object itemId) {
            final GridValueChangeListener removedListener = valueChangeListeners
                    .remove(itemId);
            if (removedListener != null) {
                /*
                 * We removed an item from somewhere in the visible range, so we
                 * make the active range shorter. The empty hole will be filled
                 * by the client-side code when it asks for more information.
                 */
                activeRange = Range.withLength(activeRange.getStart(),
                        activeRange.length() - 1);
            }
        }
    }

    /**
     * A class to listen to changes in property values in the Container added
     * with {@link Grid#setContainerDatasource(Container.Indexed)}, and notifies
     * the data source to update the client-side representation of the modified
     * item.
     * <p>
     * One instance of this class can (and should) be reused for all the
     * properties in an item, since this class will inform that the entire row
     * needs to be re-evaluated (in contrast to a property-based change
     * management)
     * <p>
     * Since there's no Container-wide possibility to listen to any kind of
     * value changes, an instance of this class needs to be attached to each and
     * every Item's Property in the container.
     * 
     * @see Grid#addValueChangeListener(Container, Object, Object)
     * @see Grid#valueChangeListeners
     */
    private class GridValueChangeListener implements ValueChangeListener {
        private final Object itemId;

        public GridValueChangeListener(Object itemId) {
            /*
             * Using an assert instead of an exception throw, just to optimize
             * prematurely
             */
            assert itemId != null : "null itemId not accepted";
            this.itemId = itemId;
        }

        @Override
        public void valueChange(ValueChangeEvent event) {
            updateRowData(container.indexOfId(itemId));
        }
    }

    private final Indexed container;

    private final ActiveRowHandler activeRowHandler = new ActiveRowHandler();

    private final ItemSetChangeListener itemListener = new ItemSetChangeListener() {
        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {

            if (event instanceof ItemAddEvent) {
                ItemAddEvent addEvent = (ItemAddEvent) event;
                int firstIndex = addEvent.getFirstIndex();
                int count = addEvent.getAddedItemsCount();
                insertRowData(firstIndex, count);
            }

            else if (event instanceof ItemRemoveEvent) {
                ItemRemoveEvent removeEvent = (ItemRemoveEvent) event;
                int firstIndex = removeEvent.getFirstIndex();
                int count = removeEvent.getRemovedItemsCount();
                removeRowData(firstIndex, count, removeEvent.getFirstItemId());
            }

            else {
                // TODO no diff info available, redraw everything
                throw new UnsupportedOperationException("bare "
                        + "ItemSetChangeEvents are currently "
                        + "not supported, use a container that "
                        + "uses AddItemEvents and RemoveItemEvents.");
            }
        }
    };

    /**
     * Creates a new data provider using the given container.
     * 
     * @param container
     *            the container to make available
     */
    public RpcDataProviderExtension(Indexed container) {
        this.container = container;

        registerRpc(new DataRequestRpc() {
            @Override
            public void requestRows(int firstRow, int numberOfRows,
                    int firstCachedRowIndex, int cacheSize) {
                pushRows(firstRow, numberOfRows);

                Range active = Range.withLength(firstRow, numberOfRows);
                if (cacheSize != 0) {
                    Range cached = Range.withLength(firstCachedRowIndex,
                            cacheSize);
                    active = active.combineWith(cached);
                }

                activeRowHandler.setActiveRows(active.getStart(),
                        active.length());
            }
        });

        getState().containerSize = container.size();

        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container)
                    .addItemSetChangeListener(itemListener);
        }

    }

    private void pushRows(int firstRow, int numberOfRows) {
        List<?> itemIds = container.getItemIds(firstRow, numberOfRows);
        Collection<?> propertyIds = container.getContainerPropertyIds();
        JSONArray rows = new JSONArray();
        for (Object itemId : itemIds) {
            rows.put(getRowData(propertyIds, itemId));
        }
        String jsonString = rows.toString();
        getRpcProxy(DataProviderRpc.class).setRowData(firstRow, jsonString);
    }

    private JSONObject getRowData(Collection<?> propertyIds, Object itemId) {
        Item item = container.getItem(itemId);

        JSONArray rowData = new JSONArray();

        Grid grid = getGrid();
        try {
            for (Object propertyId : propertyIds) {
                GridColumn column = grid.getColumn(propertyId);

                Object propertyValue = item.getItemProperty(propertyId)
                        .getValue();
                Object encodedValue = encodeValue(propertyValue,
                        column.getRenderer(), column.getConverter(),
                        grid.getLocale());

                rowData.put(encodedValue);
            }

            final JSONObject rowObject = new JSONObject();
            rowObject.put(GridState.JSONKEY_DATA, rowData);
            /*
             * TODO: selection wants to put here something in the lines of:
             * 
             * rowObject.put(GridState.JSONKEY_ROWKEY, getKey(itemId))
             * 
             * Henrik Paul: 18.6.2014
             */
            return rowObject;
        } catch (final JSONException e) {
            throw new RuntimeException("Grid was unable to serialize "
                    + "data for row (this should've been caught "
                    + "eariler by other Grid logic)", e);
        }
    }

    @Override
    protected DataProviderState getState() {
        return (DataProviderState) super.getState();
    }

    /**
     * Makes the data source available to the given {@link Grid} component.
     * 
     * @param component
     *            the remote data grid component to extend
     */
    public void extend(Grid component) {
        super.extend(component);
    }

    /**
     * Informs the client side that new rows have been inserted into the data
     * source.
     * 
     * @param index
     *            the index at which new rows have been inserted
     * @param count
     *            the number of rows inserted at <code>index</code>
     */
    private void insertRowData(int index, int count) {
        getState().containerSize += count;
        getRpcProxy(DataProviderRpc.class).insertRowData(index, count);

        activeRowHandler.insertRows(index, count);
    }

    /**
     * Informs the client side that rows have been removed from the data source.
     * 
     * @param firstIndex
     *            the index of the first row removed
     * @param count
     *            the number of rows removed
     * @param firstItemId
     *            the item id of the first removed item
     */
    private void removeRowData(int firstIndex, int count, Object firstItemId) {
        getState().containerSize -= count;
        getRpcProxy(DataProviderRpc.class).removeRowData(firstIndex, count);

        /*
         * Unfortunately, there's no sane way of getting the rest of the removed
         * itemIds unless we cache a mapping between index and itemId.
         * 
         * Fortunately, the only time _currently_ an event with more than one
         * removed item seems to be when calling
         * AbstractInMemoryContainer.removeAllElements(). Otherwise, it's only
         * removing one item at a time.
         * 
         * We _could_ have a backup of all the itemIds, and compare to that one,
         * but we really really don't want to go there.
         */
        activeRowHandler.removeItemId(firstItemId);
    }

    /**
     * Informs the client side that data of a row has been modified in the data
     * source.
     * 
     * @param index
     *            the index of the row that was updated
     */
    public void updateRowData(int index) {
        /*
         * TODO: ignore duplicate requests for the same index during the same
         * roundtrip.
         */
        Object itemId = container.getIdByIndex(index);
        JSONObject row = getRowData(container.getContainerPropertyIds(), itemId);
        JSONArray rowArray = new JSONArray(Collections.singleton(row));
        String jsonString = rowArray.toString();
        getRpcProxy(DataProviderRpc.class).setRowData(index, jsonString);
    }

    @Override
    public void setParent(ClientConnector parent) {
        super.setParent(parent);
        if (parent == null) {
            // We're detached, release various listeners

            activeRowHandler
                    .removeValueChangeListeners(activeRowHandler.activeRange);

            if (container instanceof ItemSetChangeNotifier) {
                ((ItemSetChangeNotifier) container)
                        .removeItemSetChangeListener(itemListener);
            }

        }
    }

    /**
     * Informs this data provider that some of the properties have been removed
     * from the container.
     * <p>
     * Please note that we could add our own
     * {@link com.vaadin.data.Container.PropertySetChangeListener
     * PropertySetChangeListener} to the container, but then we'd need to
     * implement the same bookeeping for finding what's added and removed that
     * Grid already does in its own listener.
     * 
     * @param removedColumns
     *            a list of property ids for the removed columns
     */
    public void propertiesRemoved(List<Object> removedColumns) {
        activeRowHandler.propertiesRemoved(removedColumns);
    }

    /**
     * Informs this data provider that some of the properties have been added to
     * the container.
     * <p>
     * Please note that we could add our own
     * {@link com.vaadin.data.Container.PropertySetChangeListener
     * PropertySetChangeListener} to the container, but then we'd need to
     * implement the same bookeeping for finding what's added and removed that
     * Grid already does in its own listener.
     * 
     * @param addedPropertyIds
     *            a list of property ids for the added columns
     */
    public void propertiesAdded(HashSet<Object> addedPropertyIds) {
        activeRowHandler.propertiesAdded(addedPropertyIds);
    }

    protected Grid getGrid() {
        return (Grid) getParent();
    }

    /**
     * Converts and encodes the given data model property value using the given
     * converter and renderer. This method is public only for testing purposes.
     * 
     * @param renderer
     *            the renderer to use
     * @param converter
     *            the converter to use
     * @param modelValue
     *            the value to convert and encode
     * @param locale
     *            the locale to use in conversion
     * @return an encoded value ready to be sent to the client
     */
    public static <T> Object encodeValue(Object modelValue,
            Renderer<T> renderer, Converter<?, ?> converter, Locale locale) {
        Class<T> presentationType = renderer.getPresentationType();
        T presentationValue;

        if (converter == null) {
            try {
                presentationValue = presentationType.cast(modelValue);
            } catch (ClassCastException e) {
                throw new Converter.ConversionException(
                        "Unable to convert value of type "
                                + modelValue.getClass().getName()
                                + " to presentation type "
                                + presentationType.getName()
                                + ". No converter is set and the types are not compatible.");
            }
        } else {
            assert presentationType.isAssignableFrom(converter
                    .getPresentationType());
            @SuppressWarnings("unchecked")
            Converter<T, Object> safeConverter = (Converter<T, Object>) converter;
            presentationValue = safeConverter.convertToPresentation(modelValue,
                    safeConverter.getPresentationType(), locale);
        }

        Object encodedValue = renderer.encode(presentationValue);

        /*
         * because this is a relatively heavy operation, we'll hide this behind
         * an assert so that the check will be removed in production mode
         */
        assert jsonSupports(encodedValue) : "org.json.JSONObject does not know how to serialize objects of type "
                + encodedValue.getClass().getName();
        return encodedValue;
    }

    private static boolean jsonSupports(Object encodedValue) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("test", encodedValue);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}
