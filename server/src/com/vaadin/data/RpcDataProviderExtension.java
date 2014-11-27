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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.collect.BiMap;
import com.google.gwt.thirdparty.guava.common.collect.HashBiMap;
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
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataProviderState;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.Renderer;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Provides Vaadin server-side container data source to a
 * {@link com.vaadin.client.ui.grid.GridConnector}. This is currently
 * implemented as an Extension hardcoded to support a specific connector type.
 * This will be changed once framework support for something more flexible has
 * been implemented.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class RpcDataProviderExtension extends AbstractExtension {

    /**
     * ItemId to Key to ItemId mapper.
     * <p>
     * This class is used when transmitting information about items in container
     * related to Grid. It introduces a consistent way of mapping ItemIds and
     * its container to a String that can be mapped back to ItemId.
     * <p>
     * <em>Technical note:</em> This class also keeps tabs on which indices are
     * being shown/selected, and is able to clean up after itself once the
     * itemId &lrarr; key mapping is not needed anymore. In other words, this
     * doesn't leak memory.
     */
    public class DataProviderKeyMapper implements Serializable {
        private final BiMap<Integer, Object> indexToItemId = HashBiMap.create();
        private final BiMap<Object, String> itemIdToKey = HashBiMap.create();
        private Set<Object> pinnedItemIds = new HashSet<Object>();
        private Range activeRange = Range.withLength(0, 0);
        private long rollingIndex = 0;

        private DataProviderKeyMapper() {
            // private implementation
        }

        void preActiveRowsChange(Range newActiveRange, int firstNewIndex,
                List<?> itemIds) {
            final Range[] removed = activeRange.partitionWith(newActiveRange);
            final Range[] added = newActiveRange.partitionWith(activeRange);

            removeActiveRows(removed[0]);
            removeActiveRows(removed[2]);
            addActiveRows(added[0], firstNewIndex, itemIds);
            addActiveRows(added[2], firstNewIndex, itemIds);

            activeRange = newActiveRange;
        }

        private void removeActiveRows(final Range deprecated) {
            for (int i = deprecated.getStart(); i < deprecated.getEnd(); i++) {
                final Integer ii = Integer.valueOf(i);
                final Object itemId = indexToItemId.get(ii);

                if (!isPinned(itemId)) {
                    itemIdToKey.remove(itemId);
                    indexToItemId.remove(ii);
                }
            }
        }

        private void addActiveRows(final Range added, int firstNewIndex,
                List<?> newItemIds) {

            for (int i = added.getStart(); i < added.getEnd(); i++) {

                /*
                 * We might be in a situation we have an index <-> itemId entry
                 * already. This happens when something was selected, scrolled
                 * out of view and now we're scrolling it back into view. It's
                 * unnecessary to overwrite it in that case.
                 * 
                 * Fun thought: considering branch prediction, it _might_ even
                 * be a bit faster to simply always run the code beyond this
                 * if-state. But it sounds too stupid (and most often too
                 * insignificant) to try out.
                 */
                final Integer ii = Integer.valueOf(i);
                if (indexToItemId.containsKey(ii)) {
                    continue;
                }

                /*
                 * We might be in a situation where we have an itemId <-> key
                 * entry already, but no index for it. This happens when
                 * something that is out of view is selected programmatically.
                 * In that case, we only want to add an index for that entry,
                 * and not overwrite the key.
                 */
                final Object itemId = newItemIds.get(i - firstNewIndex);
                if (!itemIdToKey.containsKey(itemId)) {
                    itemIdToKey.put(itemId, nextKey());
                }
                indexToItemId.forcePut(ii, itemId);
            }
        }

        private String nextKey() {
            return String.valueOf(rollingIndex++);
        }

        String getKey(Object itemId) {
            String key = itemIdToKey.get(itemId);
            if (key == null) {
                key = nextKey();
                itemIdToKey.put(itemId, key);
            }
            return key;
        }

        /**
         * Gets keys for a collection of item ids.
         * <p>
         * If the itemIds are currently cached, the existing keys will be used.
         * Otherwise new ones will be created.
         * 
         * @param itemIds
         *            the item ids for which to get keys
         * @return keys for the {@code itemIds}
         */
        public List<String> getKeys(Collection<Object> itemIds) {
            if (itemIds == null) {
                throw new IllegalArgumentException("itemIds can't be null");
            }

            ArrayList<String> keys = new ArrayList<String>(itemIds.size());
            for (Object itemId : itemIds) {
                keys.add(getKey(itemId));
            }
            return keys;
        }

        /**
         * Gets the registered item id based on its key.
         * <p>
         * A key is used to identify a particular row on both a server and a
         * client. This method can be used to get the item id for the row key
         * that the client has sent.
         * 
         * @param key
         *            the row key for which to retrieve an item id
         * @return the item id corresponding to {@code key}
         * @throws IllegalStateException
         *             if the key mapper does not have a record of {@code key} .
         */
        public Object getItemId(String key) throws IllegalStateException {
            Object itemId = itemIdToKey.inverse().get(key);
            if (itemId != null) {
                return itemId;
            } else {
                throw new IllegalStateException("No item id for key " + key
                        + " found.");
            }
        }

        /**
         * Gets corresponding item ids for each of the keys in a collection.
         * 
         * @param keys
         *            the keys for which to retrieve item ids
         * @return a collection of item ids for the {@code keys}
         * @throws IllegalStateException
         *             if one or more of keys don't have a corresponding item id
         *             in the cache
         */
        public Collection<Object> getItemIds(Collection<String> keys)
                throws IllegalStateException {
            if (keys == null) {
                throw new IllegalArgumentException("keys may not be null");
            }

            ArrayList<Object> itemIds = new ArrayList<Object>(keys.size());
            for (String key : keys) {
                itemIds.add(getItemId(key));
            }
            return itemIds;
        }

        /**
         * Pin an item id to be cached indefinitely.
         * <p>
         * Normally when an itemId is not an active row, it is discarded from
         * the cache. Pinning an item id will make sure that it is kept in the
         * cache.
         * <p>
         * In effect, while an item id is pinned, it always has the same key.
         * 
         * @param itemId
         *            the item id to pin
         * @throws IllegalStateException
         *             if {@code itemId} was already pinned
         * @see #unpin(Object)
         * @see #isPinned(Object)
         * @see #getItemIds(Collection)
         */
        public void pin(Object itemId) throws IllegalStateException {
            if (isPinned(itemId)) {
                throw new IllegalStateException("Item id " + itemId
                        + " was pinned already");
            }
            pinnedItemIds.add(itemId);
        }

        /**
         * Unpin an item id.
         * <p>
         * This cancels the effect of pinning an item id. If the item id is
         * currently inactive, it will be immediately removed from the cache.
         * 
         * @param itemId
         *            the item id to unpin
         * @throws IllegalStateException
         *             if {@code itemId} was not pinned
         * @see #pin(Object)
         * @see #isPinned(Object)
         * @see #getItemIds(Collection)
         */
        public void unpin(Object itemId) throws IllegalStateException {
            if (!isPinned(itemId)) {
                throw new IllegalStateException("Item id " + itemId
                        + " was not pinned");
            }

            pinnedItemIds.remove(itemId);
            final Integer index = indexToItemId.inverse().get(itemId);
            if (index == null || !activeRange.contains(index.intValue())) {
                itemIdToKey.remove(itemId);
                indexToItemId.remove(index);
            }
        }

        /**
         * Checks whether an item id is pinned or not.
         * 
         * @param itemId
         *            the item id to check for pin status
         * @return {@code true} iff the item id is currently pinned
         */
        public boolean isPinned(Object itemId) {
            return pinnedItemIds.contains(itemId);
        }

        Object itemIdAtIndex(int index) {
            return indexToItemId.inverse().get(Integer.valueOf(index));
        }
    }

    /**
     * A helper class that handles the client-side Escalator logic relating to
     * making sure that whatever is currently visible to the user, is properly
     * initialized and otherwise handled on the server side (as far as
     * required).
     * <p>
     * This bookeeping includes, but is not limited to:
     * <ul>
     * <li>listening to the currently visible {@link com.vaadin.data.Property
     * Properties'} value changes on the server side and sending those back to
     * the client; and
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
            if (addedPropertyIds.isEmpty()) {
                return;
            }

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

                updateRowData(i);
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

    private DataProviderRpc rpc;

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
                removeRowData(firstIndex, count);
            }

            else {

                /*
                 * Clear everything we have in view, and let the client
                 * re-request for whatever it needs.
                 * 
                 * Why this shortcut? Well, since anything could've happened, we
                 * don't know what has happened. There are a lot of use-cases we
                 * can cover at once with this carte blanche operation:
                 * 
                 * 1) Grid is scrolled somewhere in the middle and all the
                 * rows-inview are removed. We need a new pageful.
                 * 
                 * 2) Grid is scrolled somewhere in the middle and none of the
                 * visible rows are removed. We need no new rows.
                 * 
                 * 3) Grid is scrolled all the way to the bottom, and the last
                 * rows are being removed. Grid needs to scroll up and request
                 * for more rows at the top.
                 * 
                 * 4) Grid is scrolled pretty much to the bottom, and the last
                 * rows are being removed. Grid needs to be aware that some
                 * scrolling is needed, but not to compensate for all the
                 * removed rows. And it also needs to request for some more rows
                 * to the top.
                 * 
                 * 5) Some ranges of rows are removed from view. We need to
                 * collapse the gaps with existing rows and load the missing
                 * rows.
                 * 
                 * 6) The ultimate use case! Grid has 1.5 pages of rows and
                 * scrolled a bit down. One page of rows is removed. We need to
                 * make sure that new rows are loaded, but not all old slots are
                 * occupied, since the page can't be filled with new row data.
                 * It also needs to be scrolled to the top.
                 * 
                 * So, it's easier (and safer) to do the simple thing instead of
                 * taking all the corner cases into account.
                 */

                activeRowHandler.activeRange = Range.withLength(0, 0);
                activeRowHandler.valueChangeListeners.clear();
                rpc.resetDataAndSize(event.getContainer().size());
                getState().containerSize = event.getContainer().size();
            }
        }
    };

    private final DataProviderKeyMapper keyMapper = new DataProviderKeyMapper();

    private KeyMapper<Object> columnKeys;

    /* Has client been initialized */
    private boolean clientInitialized = false;

    /**
     * Creates a new data provider using the given container.
     * 
     * @param container
     *            the container to make available
     */
    public RpcDataProviderExtension(Indexed container) {
        this.container = container;
        rpc = getRpcProxy(DataProviderRpc.class);

        registerRpc(new DataRequestRpc() {
            @Override
            public void requestRows(int firstRow, int numberOfRows,
                    int firstCachedRowIndex, int cacheSize) {

                Range active = Range.withLength(firstRow, numberOfRows);
                if (cacheSize != 0) {
                    Range cached = Range.withLength(firstCachedRowIndex,
                            cacheSize);
                    active = active.combineWith(cached);
                }

                List<?> itemIds = RpcDataProviderExtension.this.container
                        .getItemIds(firstRow, numberOfRows);
                keyMapper.preActiveRowsChange(active, firstRow, itemIds);
                pushRows(firstRow, itemIds);

                activeRowHandler.setActiveRows(active.getStart(),
                        active.length());
            }

            @Override
            public void setPinned(String key, boolean isPinned) {
                if (isPinned) {
                    keyMapper.pin(keyMapper.getItemId(key));
                } else {
                    keyMapper.unpin(keyMapper.getItemId(key));
                }
            }
        });

        getState().containerSize = container.size();

        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container)
                    .addItemSetChangeListener(itemListener);
        }

    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        clientInitialized = true;
    }

    private void pushRows(int firstRow, List<?> itemIds) {
        Collection<?> propertyIds = container.getContainerPropertyIds();
        JsonArray rows = Json.createArray();
        for (int i = 0; i < itemIds.size(); ++i) {
            rows.set(i, getRowData(propertyIds, itemIds.get(i)));
        }
        rpc.setRowData(firstRow, rows.toJson());
    }

    private JsonValue getRowData(Collection<?> propertyIds, Object itemId) {
        Item item = container.getItem(itemId);

        JsonObject rowData = Json.createObject();

        Grid grid = getGrid();

        int i = 0;
        for (Object propertyId : propertyIds) {
            Column column = grid.getColumn(propertyId);

            Object propertyValue = item.getItemProperty(propertyId).getValue();
            JsonValue encodedValue = encodeValue(propertyValue,
                    column.getRenderer(), column.getConverter(),
                    grid.getLocale());

            rowData.put(columnKeys.key(propertyId), encodedValue);
        }

        final JsonObject rowObject = Json.createObject();
        rowObject.put(GridState.JSONKEY_DATA, rowData);
        rowObject.put(GridState.JSONKEY_ROWKEY, keyMapper.getKey(itemId));
        return rowObject;
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
    public void extend(Grid component, KeyMapper<Object> columnKeys) {
        this.columnKeys = columnKeys;
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
        if (clientInitialized) {
            rpc.insertRowData(index, count);
        }

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
    private void removeRowData(int firstIndex, int count) {
        getState().containerSize -= count;
        if (clientInitialized) {
            rpc.removeRowData(firstIndex, count);
        }

        for (int i = 0; i < count; i++) {
            Object itemId = keyMapper.itemIdAtIndex(firstIndex + i);
            if (itemId != null) {
                activeRowHandler.removeItemId(itemId);
            }
        }
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
        JsonValue row = getRowData(container.getContainerPropertyIds(), itemId);
        JsonArray rowArray = Json.createArray();
        rowArray.set(0, row);
        rpc.setRowData(index, rowArray.toJson());
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

    public DataProviderKeyMapper getKeyMapper() {
        return keyMapper;
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
    public static <T> JsonValue encodeValue(Object modelValue,
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

        JsonValue encodedValue = renderer.encode(presentationValue);

        return encodedValue;
    }
}
