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

package com.vaadin.server.communication.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.Indexed.ItemAddEvent;
import com.vaadin.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

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
     * Class for keeping track of current items and ValueChangeListeners.
     * 
     * @since 7.6
     */
    private class ActiveItemHandler implements Serializable, DataGenerator {

        private final Map<Object, GridValueChangeListener> activeItemMap = new HashMap<Object, GridValueChangeListener>();
        private final KeyMapper<Object> keyMapper = new KeyMapper<Object>();
        private final Set<Object> droppedItems = new HashSet<Object>();

        /**
         * Registers ValueChangeListeners for given item ids.
         * <p>
         * Note: This method will clean up any unneeded listeners and key
         * mappings
         * 
         * @param itemIds
         *            collection of new active item ids
         */
        public void addActiveItems(Collection<?> itemIds) {
            for (Object itemId : itemIds) {
                if (!activeItemMap.containsKey(itemId)) {
                    activeItemMap.put(itemId, new GridValueChangeListener(
                            itemId, container.getItem(itemId)));
                }
            }

            // Remove still active rows that were "dropped"
            droppedItems.removeAll(itemIds);
            internalDropItems(droppedItems);
            droppedItems.clear();
        }

        /**
         * Marks given item id as dropped. Dropped items are cleared when adding
         * new active items.
         * 
         * @param itemId
         *            dropped item id
         */
        public void dropActiveItem(Object itemId) {
            if (activeItemMap.containsKey(itemId)) {
                droppedItems.add(itemId);
            }
        }

        /**
         * Gets a collection copy of currently active item ids.
         * 
         * @return collection of item ids
         */
        public Collection<Object> getActiveItemIds() {
            return new HashSet<Object>(activeItemMap.keySet());
        }

        /**
         * Gets a collection copy of currently active ValueChangeListeners.
         * 
         * @return collection of value change listeners
         */
        public Collection<GridValueChangeListener> getValueChangeListeners() {
            return new HashSet<GridValueChangeListener>(activeItemMap.values());
        }

        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {
            rowData.put(GridState.JSONKEY_ROWKEY, keyMapper.key(itemId));
        }

        @Override
        public void destroyData(Object itemId) {
            keyMapper.remove(itemId);
            removeListener(itemId);
        }

        private void removeListener(Object itemId) {
            GridValueChangeListener removed = activeItemMap.remove(itemId);

            if (removed != null) {
                removed.removeListener();
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
        private final Item item;

        public GridValueChangeListener(Object itemId, Item item) {
            /*
             * Using an assert instead of an exception throw, just to optimize
             * prematurely
             */
            assert itemId != null : "null itemId not accepted";
            this.itemId = itemId;
            this.item = item;

            internalAddColumns(getGrid().getColumns());
        }

        @Override
        public void valueChange(ValueChangeEvent event) {
            updateRowData(itemId);
        }

        public void removeListener() {
            removeColumns(getGrid().getColumns());
        }

        public void addColumns(Collection<Column> addedColumns) {
            internalAddColumns(addedColumns);
            updateRowData(itemId);
        }

        private void internalAddColumns(Collection<Column> addedColumns) {
            for (final Column column : addedColumns) {
                final Property<?> property = item.getItemProperty(column
                        .getPropertyId());
                if (property instanceof ValueChangeNotifier) {
                    ((ValueChangeNotifier) property)
                            .addValueChangeListener(this);
                }
            }
        }

        public void removeColumns(Collection<Column> removedColumns) {
            for (final Column column : removedColumns) {
                final Property<?> property = item.getItemProperty(column
                        .getPropertyId());
                if (property instanceof ValueChangeNotifier) {
                    ((ValueChangeNotifier) property)
                            .removeValueChangeListener(this);
                }
            }
        }
    }

    private final Indexed container;

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
                // Remove obsolete value change listeners.
                Set<Object> keySet = new HashSet<Object>(
                        activeItemHandler.activeItemMap.keySet());
                for (Object itemId : keySet) {
                    activeItemHandler.removeListener(itemId);
                }

                /* Mark as dirty to push changes in beforeClientResponse */
                bareItemSetTriggeredSizeChange = true;
                markAsDirty();
            }
        }
    };

    /** RpcDataProvider should send the current cache again. */
    private boolean refreshCache = false;

    /** Set of updated item ids */
    private transient Set<Object> updatedItemIds;

    /**
     * Queued RPC calls for adding and removing rows. Queue will be handled in
     * {@link beforeClientResponse}
     */
    private transient List<Runnable> rowChanges;

    /** Size possibly changed with a bare ItemSetChangeEvent */
    private boolean bareItemSetTriggeredSizeChange = false;

    private final Set<DataGenerator> dataGenerators = new LinkedHashSet<DataGenerator>();

    private final ActiveItemHandler activeItemHandler = new ActiveItemHandler();

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
                pushRowData(firstRow, numberOfRows, firstCachedRowIndex,
                        cacheSize);
            }

            @Override
            public void dropRows(JsonArray rowKeys) {
                for (int i = 0; i < rowKeys.length(); ++i) {
                    activeItemHandler.dropActiveItem(getKeyMapper().get(
                            rowKeys.getString(i)));
                }
            }
        });

        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container)
                    .addItemSetChangeListener(itemListener);
        }

        addDataGenerator(activeItemHandler);
    }

    /**
     * {@inheritDoc}
     * <p>
     * RpcDataProviderExtension makes all actual RPC calls from this function
     * based on changes in the container.
     */
    @Override
    public void beforeClientResponse(boolean initial) {
        if (initial || bareItemSetTriggeredSizeChange) {
            /*
             * Push initial set of rows, assuming Grid will initially be
             * rendered scrolled to the top and with a decent amount of rows
             * visible. If this guess is right, initial data can be shown
             * without a round-trip and if it's wrong, the data will simply be
             * discarded.
             */
            int size = container.size();
            rpc.resetDataAndSize(size);

            int numberOfRows = Math.min(40, size);
            pushRowData(0, numberOfRows, 0, 0);
        } else {
            // Only do row changes if not initial response.
            if (rowChanges != null) {
                for (Runnable r : rowChanges) {
                    r.run();
                }
            }

            // Send current rows again if needed.
            if (refreshCache) {
                for (Object itemId : activeItemHandler.getActiveItemIds()) {
                    updateRowData(itemId);
                }
            }
        }

        internalUpdateRows(updatedItemIds);

        // Clear all changes.
        if (rowChanges != null) {
            rowChanges.clear();
        }
        if (updatedItemIds != null) {
            updatedItemIds.clear();
        }
        refreshCache = false;
        bareItemSetTriggeredSizeChange = false;

        super.beforeClientResponse(initial);
    }

    private void pushRowData(int firstRowToPush, int numberOfRows,
            int firstCachedRowIndex, int cacheSize) {
        Range newRange = Range.withLength(firstRowToPush, numberOfRows);
        Range cached = Range.withLength(firstCachedRowIndex, cacheSize);
        Range fullRange = newRange;
        if (!cached.isEmpty()) {
            fullRange = newRange.combineWith(cached);
        }

        List<?> itemIds = container.getItemIds(fullRange.getStart(),
                fullRange.length());

        JsonArray rows = Json.createArray();

        // Offset the index to match the wanted range.
        int diff = 0;
        if (!cached.isEmpty() && newRange.getStart() > cached.getStart()) {
            diff = cached.length();
        }

        for (int i = 0; i < newRange.length() && i + diff < itemIds.size(); ++i) {
            Object itemId = itemIds.get(i + diff);

            Item item = container.getItem(itemId);

            rows.set(i, getRowData(getGrid().getColumns(), itemId, item));
        }
        rpc.setRowData(firstRowToPush, rows);

        activeItemHandler.addActiveItems(itemIds);
    }

    private JsonObject getRowData(Collection<Column> columns, Object itemId,
            Item item) {

        final JsonObject rowObject = Json.createObject();
        for (DataGenerator dg : dataGenerators) {
            dg.generateData(itemId, item, rowObject);
        }

        return rowObject;
    }

    /**
     * Makes the data source available to the given {@link Grid} component.
     * 
     * @param component
     *            the remote data grid component to extend
     * @param columnKeys
     *            the key mapper for columns
     */
    public void extend(Grid component) {
        super.extend(component);
    }

    /**
     * Adds a {@link DataGenerator} for this {@code RpcDataProviderExtension}.
     * DataGenerators are called when sending row data to client. If given
     * DataGenerator is already added, this method does nothing.
     * 
     * @since 7.6
     * @param generator
     *            generator to add
     */
    public void addDataGenerator(DataGenerator generator) {
        dataGenerators.add(generator);
    }

    /**
     * Removes a {@link DataGenerator} from this
     * {@code RpcDataProviderExtension}. If given DataGenerator is not added to
     * this data provider, this method does nothing.
     * 
     * @since 7.6
     * @param generator
     *            generator to remove
     */
    public void removeDataGenerator(DataGenerator generator) {
        dataGenerators.remove(generator);
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
    private void insertRowData(final int index, final int count) {
        if (rowChanges == null) {
            rowChanges = new ArrayList<Runnable>();
        }

        if (rowChanges.isEmpty()) {
            markAsDirty();
        }

        /*
         * Since all changes should be processed in a consistent order, we don't
         * send the RPC call immediately. beforeClientResponse will decide
         * whether to send these or not. Valid situation to not send these is
         * initial response or bare ItemSetChange event.
         */
        rowChanges.add(new Runnable() {
            @Override
            public void run() {
                rpc.insertRowData(index, count);
            }
        });
    }

    /**
     * Informs the client side that rows have been removed from the data source.
     * 
     * @param index
     *            the index of the first row removed
     * @param count
     *            the number of rows removed
     * @param firstItemId
     *            the item id of the first removed item
     */
    private void removeRowData(final int index, final int count) {
        if (rowChanges == null) {
            rowChanges = new ArrayList<Runnable>();
        }

        if (rowChanges.isEmpty()) {
            markAsDirty();
        }

        /* See comment in insertRowData */
        rowChanges.add(new Runnable() {
            @Override
            public void run() {
                rpc.removeRowData(index, count);
            }
        });
    }

    /**
     * Informs the client side that data of a row has been modified in the data
     * source.
     * 
     * @param itemId
     *            the item Id the row that was updated
     */
    public void updateRowData(Object itemId) {
        if (updatedItemIds == null) {
            updatedItemIds = new LinkedHashSet<Object>();
        }

        if (updatedItemIds.isEmpty()) {
            // At least one new item will be updated. Mark as dirty to actually
            // update before response to client.
            markAsDirty();
        }

        updatedItemIds.add(itemId);
    }

    private void internalUpdateRows(Set<Object> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }

        Collection<Object> activeItemIds = activeItemHandler.getActiveItemIds();
        List<Column> columns = getGrid().getColumns();
        JsonArray rowData = Json.createArray();
        int i = 0;
        for (Object itemId : itemIds) {
            if (activeItemIds.contains(itemId)) {
                Item item = container.getItem(itemId);
                if (item != null) {
                    JsonObject row = getRowData(columns, itemId, item);
                    rowData.set(i++, row);
                }
            }
        }
        rpc.updateRowData(rowData);
    }

    /**
     * Pushes a new version of all the rows in the active cache range.
     */
    public void refreshCache() {
        if (!refreshCache) {
            refreshCache = true;
            markAsDirty();
        }
    }

    @Override
    public void setParent(ClientConnector parent) {
        if (parent == null) {
            // We're being detached, release various listeners
            internalDropItems(activeItemHandler.getActiveItemIds());

            if (container instanceof ItemSetChangeNotifier) {
                ((ItemSetChangeNotifier) container)
                        .removeItemSetChangeListener(itemListener);
            }

        } else if (!(parent instanceof Grid)) {
            throw new IllegalStateException(
                    "Grid is the only accepted parent type");
        }
        super.setParent(parent);
    }

    /**
     * Informs all DataGenerators than an item id has been dropped.
     * 
     * @param droppedItemIds
     *            collection of dropped item ids
     */
    private void internalDropItems(Collection<Object> droppedItemIds) {
        for (Object itemId : droppedItemIds) {
            for (DataGenerator generator : dataGenerators) {
                generator.destroyData(itemId);
            }
        }
    }

    /**
     * Informs this data provider that given columns have been removed from
     * grid.
     * 
     * @param removedColumns
     *            a list of removed columns
     */
    public void columnsRemoved(List<Column> removedColumns) {
        for (GridValueChangeListener l : activeItemHandler
                .getValueChangeListeners()) {
            l.removeColumns(removedColumns);
        }

        // No need to resend unchanged data. Client will remember the old
        // columns until next set of rows is sent.
    }

    /**
     * Informs this data provider that given columns have been added to grid.
     * 
     * @param addedColumns
     *            a list of added columns
     */
    public void columnsAdded(List<Column> addedColumns) {
        for (GridValueChangeListener l : activeItemHandler
                .getValueChangeListeners()) {
            l.addColumns(addedColumns);
        }

        // Resend all rows to contain new data.
        refreshCache();
    }

    public KeyMapper<Object> getKeyMapper() {
        return activeItemHandler.keyMapper;
    }

    protected Grid getGrid() {
        return (Grid) getParent();
    }
}
