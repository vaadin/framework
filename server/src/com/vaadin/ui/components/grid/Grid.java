/*
 * Copyright 2000-2013 Vaadin Ltd.
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

package com.vaadin.ui.components.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed.ItemAddEvent;
import com.vaadin.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.ui.grid.ColumnGroupRowState;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridColumnState;
import com.vaadin.shared.ui.grid.GridServerRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * Data grid component
 * 
 * <h3>Lazy loading</h3> TODO To be revised when the data data source
 * implementation has been don.
 * 
 * <h3>Columns</h3> The grid columns are based on the property ids of the
 * underlying data source. Each property id represents one column in the grid.
 * To retrive a column in the grid you can use {@link Grid#getColumn(Object)}
 * with the property id of the column. A grid column contains properties like
 * the width, the footer and header captions of the column.
 * 
 * <h3>Auxiliary headers and footers</h3> TODO To be revised when column
 * grouping is implemented.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class Grid extends AbstractComponent {

    /**
     * A helper class that handles the client-side Escalator logic relating to
     * making sure that whatever is currently visible to the user, is properly
     * initialized and otherwise handled on the server side (as far as
     * requried).
     * <p>
     * This bookeeping includes, but is not limited to:
     * <ul>
     * <li>listening to the currently visible {@link Property Properties'} value
     * changes on the server side and sending those back to the client; and
     * <li>attaching and detaching {@link Component Components} from the Vaadin
     * Component hierarchy.
     * </ul>
     */
    private final class ActiveRowHandler implements Serializable {
        /**
         * A map from itemId to the value change listener used for all of its
         * properties
         */
        private final Map<Object, GridValueChangeListener> valueChangeListeners = new HashMap<Object, GridValueChangeListener>();

        /**
         * The currently active range. Practically, it's the range of row
         * indices being displayed currently.
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

                final Object itemId = datasource.getIdByIndex(i);
                final Item item = datasource.getItem(itemId);

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
                final Object itemId = datasource.getIdByIndex(i);
                final Item item = datasource.getItem(itemId);
                final GridValueChangeListener listener = valueChangeListeners
                        .remove(itemId);

                if (listener != null) {
                    for (final Object propertyId : item.getItemPropertyIds()) {
                        final Property<?> property = item
                                .getItemProperty(propertyId);

                        /*
                         * Because listener != null, we can be certain that this
                         * property is a ValueChangeNotifier: It wouldn't be
                         * inserted in addValueChangeListeners if the property
                         * wasn't a suitable type. I.e. No need for "instanceof"
                         * check.
                         */
                        ((ValueChangeNotifier) property)
                                .removeValueChangeListener(listener);
                    }
                }
            }
        }

        public void clear() {
            removeValueChangeListeners(activeRange);
            /*
             * we're doing an assert for emptiness there (instead of a
             * carte-blanche ".clear()"), to be absolutely sure that everything
             * is cleaned up properly, and that we have no dangling listeners.
             */
            assert valueChangeListeners.isEmpty() : "GridValueChangeListeners are leaking";

            activeRange = Range.withLength(0, 0);
        }

        /**
         * Manages removed properties in active rows.
         * 
         * @param removedPropertyIds
         *            the property ids that have been removed from the container
         */
        public void propertiesRemoved(Collection<Object> removedPropertyIds) {
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
                final Object itemId = datasource.getIdByIndex(i);
                final Item item = datasource.getItem(itemId);
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
            datasourceExtension.updateRowData(datasource.indexOfId(itemId));
        }
    }

    /**
     * The data source attached to the grid
     */
    private Container.Indexed datasource;

    /**
     * Property id to column instance mapping
     */
    private final Map<Object, GridColumn> columns = new HashMap<Object, GridColumn>();

    /**
     * Key generator for column server-to-client communication
     */
    private final KeyMapper<Object> columnKeys = new KeyMapper<Object>();

    /**
     * The column groups added to the grid
     */
    private final List<ColumnGroupRow> columnGroupRows = new ArrayList<ColumnGroupRow>();

    /**
     * Property listener for listening to changes in data source properties.
     */
    private final PropertySetChangeListener propertyListener = new PropertySetChangeListener() {

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            Collection<?> properties = new HashSet<Object>(event.getContainer()
                    .getContainerPropertyIds());

            // Cleanup columns that are no longer in grid
            List<Object> removedColumns = new LinkedList<Object>();
            for (Object columnId : columns.keySet()) {
                if (!properties.contains(columnId)) {
                    removedColumns.add(columnId);
                }
            }
            for (Object columnId : removedColumns) {
                GridColumn column = columns.remove(columnId);
                columnKeys.remove(columnId);
                getState().columns.remove(column.getState());
            }
            activeRowHandler.propertiesRemoved(removedColumns);

            // Add new columns
            HashSet<Object> addedPropertyIds = new HashSet<Object>();
            for (Object propertyId : properties) {
                if (!columns.containsKey(propertyId)) {
                    appendColumn(propertyId);
                    addedPropertyIds.add(propertyId);
                }
            }
            activeRowHandler.propertiesAdded(addedPropertyIds);

            Object frozenPropertyId = columnKeys
                    .get(getState(false).lastFrozenColumnId);
            if (!columns.containsKey(frozenPropertyId)) {
                setLastFrozenPropertyId(null);
            }
        }
    };

    private ItemSetChangeListener itemListener = new ItemSetChangeListener() {
        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {

            if (event instanceof ItemAddEvent) {
                ItemAddEvent addEvent = (ItemAddEvent) event;
                int firstIndex = addEvent.getFirstIndex();
                int count = addEvent.getAddedItemsCount();
                datasourceExtension.insertRowData(firstIndex, count);
                activeRowHandler.insertRows(firstIndex, count);
            }

            else if (event instanceof ItemRemoveEvent) {
                ItemRemoveEvent removeEvent = (ItemRemoveEvent) event;
                int firstIndex = removeEvent.getFirstIndex();
                int count = removeEvent.getRemovedItemsCount();
                datasourceExtension.removeRowData(firstIndex, count);

                /*
                 * Unfortunately, there's no sane way of getting the rest of the
                 * removed itemIds.
                 * 
                 * Fortunately, the only time _currently_ an event with more
                 * than one removed item seems to be when calling
                 * AbstractInMemoryContainer.removeAllElements(). Otherwise,
                 * it's only removing one item at a time.
                 * 
                 * We _could_ have a backup of all the itemIds, and compare to
                 * that one, but we really really don't want to go there.
                 */
                activeRowHandler.removeItemId(removeEvent.getFirstItemId());
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

    private RpcDataProviderExtension datasourceExtension;

    private final ActiveRowHandler activeRowHandler = new ActiveRowHandler();

    /**
     * Creates a new Grid using the given datasource.
     * 
     * @param datasource
     *            the data source for the grid
     */
    public Grid(Container.Indexed datasource) {
        setContainerDatasource(datasource);

        registerRpc(new GridServerRpc() {
            @Override
            public void setVisibleRows(int firstVisibleRow, int visibleRowCount) {
                activeRowHandler
                        .setActiveRows(firstVisibleRow, visibleRowCount);
            }
        });
    }

    /**
     * Sets the grid data source.
     * 
     * @param container
     *            The container data source. Cannot be null.
     * @throws IllegalArgumentException
     *             if the data source is null
     */
    public void setContainerDatasource(Container.Indexed container) {
        if (container == null) {
            throw new IllegalArgumentException(
                    "Cannot set the datasource to null");
        }
        if (datasource == container) {
            return;
        }

        // Remove old listeners
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .removePropertySetChangeListener(propertyListener);
        }
        if (datasource instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) datasource)
                    .removeItemSetChangeListener(itemListener);
        }
        activeRowHandler.clear();

        if (datasourceExtension != null) {
            removeExtension(datasourceExtension);
        }

        datasource = container;
        datasourceExtension = new RpcDataProviderExtension(container);
        datasourceExtension.extend(this);

        // Listen to changes in properties and remove columns if needed
        if (datasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) datasource)
                    .addPropertySetChangeListener(propertyListener);
        }
        if (datasource instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) datasource)
                    .addItemSetChangeListener(itemListener);
        }
        /*
         * activeRowHandler will be updated by the client-side request that
         * occurs on container change - no need to actively re-insert any
         * ValueChangeListeners at this point.
         */

        getState().columns.clear();
        setLastFrozenPropertyId(null);

        // Add columns
        for (Object propertyId : datasource.getContainerPropertyIds()) {
            if (!columns.containsKey(propertyId)) {
                GridColumn column = appendColumn(propertyId);

                // Add by default property id as column header
                column.setHeaderCaption(String.valueOf(propertyId));
            }
        }

    }

    /**
     * Returns the grid data source.
     * 
     * @return the container data source of the grid
     */
    public Container.Indexed getContainerDatasource() {
        return datasource;
    }

    /**
     * Returns a column based on the property id
     * 
     * @param propertyId
     *            the property id of the column
     * @return the column or <code>null</code> if not found
     */
    public GridColumn getColumn(Object propertyId) {
        return columns.get(propertyId);
    }

    /**
     * Sets the header rows visible.
     * 
     * @param visible
     *            <code>true</code> if the header rows should be visible
     */
    public void setColumnHeadersVisible(boolean visible) {
        getState().columnHeadersVisible = visible;
    }

    /**
     * Are the header rows visible?
     * 
     * @return <code>true</code> if the headers of the columns are visible
     */
    public boolean isColumnHeadersVisible() {
        return getState(false).columnHeadersVisible;
    }

    /**
     * Sets the footer rows visible.
     * 
     * @param visible
     *            <code>true</code> if the footer rows should be visible
     */
    public void setColumnFootersVisible(boolean visible) {
        getState().columnFootersVisible = visible;
    }

    /**
     * Are the footer rows visible.
     * 
     * @return <code>true</code> if the footer rows should be visible
     */
    public boolean isColumnFootersVisible() {
        return getState(false).columnFootersVisible;
    }

    /**
     * <p>
     * Adds a new column group to the grid.
     * 
     * <p>
     * Column group rows are rendered in the header and footer of the grid.
     * Column group rows are made up of column groups which groups together
     * columns for adding a common auxiliary header or footer for the columns.
     * </p>
     * </p>
     * 
     * <p>
     * Example usage:
     * 
     * <pre>
     * // Add a new column group row to the grid
     * ColumnGroupRow row = grid.addColumnGroupRow();
     * 
     * // Group &quot;Column1&quot; and &quot;Column2&quot; together to form a header in the row
     * ColumnGroup column12 = row.addGroup(&quot;Column1&quot;, &quot;Column2&quot;);
     * 
     * // Set a common header for &quot;Column1&quot; and &quot;Column2&quot;
     * column12.setHeader(&quot;Column 1&amp;2&quot;);
     * </pre>
     * 
     * </p>
     * 
     * @return a column group instance you can use to add column groups
     */
    public ColumnGroupRow addColumnGroupRow() {
        ColumnGroupRowState state = new ColumnGroupRowState();
        ColumnGroupRow row = new ColumnGroupRow(this, state, columnKeys);
        columnGroupRows.add(row);
        getState().columnGroupRows.add(state);
        return row;
    }

    /**
     * Adds a new column group to the grid at a specific index
     * 
     * @param rowIndex
     *            the index of the row
     * @return a column group instance you can use to add column groups
     */
    public ColumnGroupRow addColumnGroupRow(int rowIndex) {
        ColumnGroupRowState state = new ColumnGroupRowState();
        ColumnGroupRow row = new ColumnGroupRow(this, state, columnKeys);
        columnGroupRows.add(rowIndex, row);
        getState().columnGroupRows.add(rowIndex, state);
        return row;
    }

    /**
     * Removes a column group.
     * 
     * @param row
     *            the row to remove
     */
    public void removeColumnGroupRow(ColumnGroupRow row) {
        columnGroupRows.remove(row);
        getState().columnGroupRows.remove(row.getState());
    }

    /**
     * Gets the column group rows.
     * 
     * @return an unmodifiable list of column group rows
     */
    public List<ColumnGroupRow> getColumnGroupRows() {
        return Collections.unmodifiableList(new ArrayList<ColumnGroupRow>(
                columnGroupRows));
    }

    /**
     * Used internally by the {@link Grid} to get a {@link GridColumn} by
     * referencing its generated state id. Also used by {@link GridColumn} to
     * verify if it has been detached from the {@link Grid}.
     * 
     * @param columnId
     *            the client id generated for the column when the column is
     *            added to the grid
     * @return the column with the id or <code>null</code> if not found
     */
    GridColumn getColumnByColumnId(String columnId) {
        Object propertyId = getPropertyIdByColumnId(columnId);
        return getColumn(propertyId);
    }

    /**
     * Used internally by the {@link Grid} to get a property id by referencing
     * the columns generated state id.
     * 
     * @param columnId
     *            The state id of the column
     * @return The column instance or null if not found
     */
    Object getPropertyIdByColumnId(String columnId) {
        return columnKeys.get(columnId);
    }

    @Override
    protected GridState getState() {
        return (GridState) super.getState();
    }

    @Override
    protected GridState getState(boolean markAsDirty) {
        return (GridState) super.getState(markAsDirty);
    }

    /**
     * Creates a new column based on a property id and appends it as the last
     * column.
     * 
     * @param datasourcePropertyId
     *            The property id of a property in the datasource
     */
    private GridColumn appendColumn(Object datasourcePropertyId) {
        if (datasourcePropertyId == null) {
            throw new IllegalArgumentException("Property id cannot be null");
        }
        assert datasource.getContainerPropertyIds().contains(
                datasourcePropertyId) : "Datasource should contain the property id";

        GridColumnState columnState = new GridColumnState();
        columnState.id = columnKeys.key(datasourcePropertyId);
        getState().columns.add(columnState);

        GridColumn column = new GridColumn(this, columnState);
        columns.put(datasourcePropertyId, column);

        return column;
    }

    /**
     * Sets (or unsets) the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the given column will be frozen in place
     * when the grid is scrolled sideways.
     * 
     * @param lastFrozenColumn
     *            the rightmost column to freeze, or <code>null</code> to not
     *            have any columns frozen
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    void setLastFrozenColumn(GridColumn lastFrozenColumn) {
        /*
         * TODO: If and when Grid supports column reordering or insertion of
         * columns before other columns, make sure to mention that adding
         * columns before lastFrozenColumn will change the frozen column count
         */

        if (lastFrozenColumn == null) {
            getState().lastFrozenColumnId = null;
        } else if (columns.containsValue(lastFrozenColumn)) {
            getState().lastFrozenColumnId = lastFrozenColumn.getState().id;
        } else {
            throw new IllegalArgumentException(
                    "The given column isn't attached to this grid");
        }
    }

    /**
     * Sets (or unsets) the rightmost frozen column in the grid.
     * <p>
     * All columns up to and including the indicated property will be frozen in
     * place when the grid is scrolled sideways.
     * <p>
     * <em>Note:</em> If the container used by this grid supports a propertyId
     * <code>null</code>, it can never be defined as the last frozen column, as
     * a <code>null</code> parameter will always reset the frozen columns in
     * Grid.
     * 
     * @param propertyId
     *            the property id corresponding to the column that should be the
     *            last frozen column, or <code>null</code> to not have any
     *            columns frozen.
     * @throws IllegalArgumentException
     *             if {@code lastFrozenColumn} is not a column from this grid
     */
    public void setLastFrozenPropertyId(Object propertyId) {
        final GridColumn column;
        if (propertyId == null) {
            column = null;
        } else {
            column = getColumn(propertyId);
            if (column == null) {
                throw new IllegalArgumentException(
                        "property id does not exist.");
            }
        }
        setLastFrozenColumn(column);
    }

    /**
     * Gets the rightmost frozen column in the grid.
     * <p>
     * <em>Note:</em> Most often, this method returns the very value set with
     * {@link #setLastFrozenPropertyId(Object)}. This value, however, can be
     * reset to <code>null</code> if the column is detached from this grid.
     * 
     * @return the rightmost frozen column in the grid, or <code>null</code> if
     *         no columns are frozen.
     */
    public Object getLastFrozenPropertyId() {
        return columnKeys.get(getState().lastFrozenColumnId);
    }

    /**
     * Scrolls to a certain item, using {@link ScrollDestination#ANY}.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollToItem(Object itemId) throws IllegalArgumentException {
        scrollToItem(itemId, ScrollDestination.ANY);
    }

    /**
     * Scrolls to a certain item, using user-specified scroll destination.
     * 
     * @param itemId
     *            id of item to scroll to.
     * @param destination
     *            value specifying desired position of scrolled-to row.
     * @throws IllegalArgumentException
     *             if the provided id is not recognized by the data source.
     */
    public void scrollToItem(Object itemId, ScrollDestination destination)
            throws IllegalArgumentException {

        int row = datasource.indexOfId(itemId);

        if (row == -1) {
            throw new IllegalArgumentException(
                    "Item with specified ID does not exist in data source");
        }

        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToRow(row, destination);
    }

    /**
     * Scrolls to the beginning of the first data row.
     */
    public void scrollToStart() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToStart();
    }

    /**
     * Scrolls to the end of the last data row.
     */
    public void scrollToEnd() {
        GridClientRpc clientRPC = getRpcProxy(GridClientRpc.class);
        clientRPC.scrollToEnd();
    }

    /**
     * Sets the number of rows that should be visible in Grid's body, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * <p>
     * If Grid is currently not in {@link HeightMode#ROW}, the given value is
     * remembered, and applied once the mode is applied.
     * 
     * @param rows
     *            The height in terms of number of rows displayed in Grid's
     *            body. If Grid doesn't contain enough rows, white space is
     *            displayed instead. If <code>null</code> is given, then Grid's
     *            height is undefined
     * @throws IllegalArgumentException
     *             if {@code rows} is zero or less
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isInifinite(double)
     *             infinite}
     * @throws IllegalArgumentException
     *             if {@code rows} is {@link Double#isNaN(double) NaN}
     */
    public void setHeightByRows(double rows) {
        if (rows <= 0.0d) {
            throw new IllegalArgumentException(
                    "More than zero rows must be shown.");
        } else if (Double.isInfinite(rows)) {
            throw new IllegalArgumentException(
                    "Grid doesn't support infinite heights");
        } else if (Double.isNaN(rows)) {
            throw new IllegalArgumentException("NaN is not a valid row count");
        }

        getState().heightByRows = rows;
    }

    /**
     * Gets the amount of rows in Grid's body that are shown, while
     * {@link #getHeightMode()} is {@link HeightMode#ROW}.
     * 
     * @return the amount of rows that are being shown in Grid's body
     * @see #setHeightByRows(double)
     */
    public double getHeightByRows() {
        return getState(false).heightByRows;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <em>Note:</em> This method will change the widget's size in the browser
     * only if {@link #getHeightMode()} returns {@link HeightMode#CSS}.
     * 
     * @see #setHeightMode(HeightMode)
     */
    @Override
    public void setHeight(float height, Unit unit) {
        super.setHeight(height, unit);
    }

    /**
     * Defines the mode in which the Grid widget's height is calculated.
     * <p>
     * If {@link HeightMode#CSS} is given, Grid will respect the values given
     * via a {@code setHeight}-method, and behave as a traditional Component.
     * <p>
     * If {@link HeightMode#ROW} is given, Grid will make sure that the body
     * will display as many rows as {@link #getHeightByRows()} defines.
     * <em>Note:</em> If headers/footers are inserted or removed, the widget
     * will resize itself to still display the required amount of rows in its
     * body. It also takes the horizontal scrollbar into account.
     * 
     * @param heightMode
     *            the mode in to which Grid should be set
     */
    public void setHeightMode(HeightMode heightMode) {
        /*
         * This method is a workaround for the fact that Vaadin re-applies
         * widget dimensions (height/width) on each state change event. The
         * original design was to have setHeight an setHeightByRow be equals,
         * and whichever was called the latest was considered in effect.
         * 
         * But, because of Vaadin always calling setHeight on the widget, this
         * approach doesn't work.
         */

        getState().heightMode = heightMode;
    }

    /**
     * Returns the current {@link HeightMode} the Grid is in.
     * <p>
     * Defaults to {@link HeightMode#CSS}.
     * 
     * @return the current HeightMode
     */
    public HeightMode getHeightMode() {
        return getState(false).heightMode;
    }
}
