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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.collect.ImmutableSet;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.Indexed.ItemAddEvent;
import com.vaadin.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.KeyMapper;
import com.vaadin.shared.data.DataProviderRpc;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.shared.ui.grid.GridClientRpc;
import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.shared.ui.grid.Range;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.DetailsGenerator;
import com.vaadin.ui.Grid.RowReference;

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
            internalDropActiveItems(droppedItems);
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

        private void internalDropActiveItems(Collection<Object> itemIds) {
            for (Object itemId : droppedItems) {
                assert activeItemMap.containsKey(itemId) : "Item ID should exist in the activeItemMap";

                activeItemMap.remove(itemId).removeListener();
                keyMapper.remove(itemId);
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

    /**
     * A class that makes detail component related internal communication
     * possible between {@link RpcDataProviderExtension} and grid.
     * 
     * @since 7.5.0
     * @author Vaadin Ltd
     */
    // TODO this should probably be a static nested class
    public final class DetailComponentManager implements DataGenerator {
        /**
         * This map represents all the components that have been requested for
         * each item id.
         * <p>
         * Normally this map is consistent with what is displayed in the
         * component hierarchy (and thus the DOM). The only time this map is out
         * of sync with the DOM is between the any calls to
         * {@link #createDetails(Object)} or {@link #destroyDetails(Object)},
         * and {@link GridClientRpc#setDetailsConnectorChanges(Set)}.
         * <p>
         * This is easily checked: if {@link #unattachedComponents} is
         * {@link Collection#isEmpty() empty}, then this field is consistent
         * with the connector hierarchy.
         */
        private final Map<Object, Component> visibleDetailsComponents = Maps
                .newHashMap();

        /**
         * Keeps tabs on all the details that did not get a component during
         * {@link #createDetails(Object)}.
         */
        private final Set<Object> emptyDetails = Sets.newHashSet();

        private Grid grid;

        /**
         * Creates a details component by the request of the client side, with
         * the help of the user-defined {@link DetailsGenerator}.
         * <p>
         * Also keeps internal bookkeeping up to date.
         * 
         * @param itemId
         *            the item id for which to create the details component.
         *            Assumed not <code>null</code> and that a component is not
         *            currently present for this item previously
         * @throws IllegalStateException
         *             if the current details generator provides a component
         *             that was manually attached, or if the same instance has
         *             already been provided
         */
        public void createDetails(Object itemId) throws IllegalStateException {
            assert itemId != null : "itemId was null";

            if (visibleDetailsComponents.containsKey(itemId)
                    || emptyDetails.contains(itemId)) {
                // Don't overwrite existing components
                return;
            }

            RowReference rowReference = new RowReference(grid);
            rowReference.set(itemId);

            DetailsGenerator detailsGenerator = grid.getDetailsGenerator();
            Component details = detailsGenerator.getDetails(rowReference);
            if (details != null) {
                if (details.getParent() != null) {
                    String name = detailsGenerator.getClass().getName();
                    throw new IllegalStateException(name
                            + " generated a details component that already "
                            + "was attached. (itemId: " + itemId
                            + ", component: " + details + ")");
                }

                visibleDetailsComponents.put(itemId, details);

                details.setParent(grid);
                grid.markAsDirty();

                assert !emptyDetails.contains(itemId) : "Bookeeping thinks "
                        + "itemId is empty even though we just created a "
                        + "component for it (" + itemId + ")";
            } else {
                emptyDetails.add(itemId);
            }

        }

        /**
         * Destroys correctly a details component, by the request of the client
         * side.
         * <p>
         * Also keeps internal bookkeeping up to date.
         * 
         * @param itemId
         *            the item id for which to destroy the details component
         */
        public void destroyDetails(Object itemId) {
            emptyDetails.remove(itemId);

            Component removedComponent = visibleDetailsComponents
                    .remove(itemId);
            if (removedComponent == null) {
                return;
            }

            removedComponent.setParent(null);
            grid.markAsDirty();
        }

        /**
         * Gets all details components that are currently attached to the grid.
         * <p>
         * Used internally by the Grid object.
         * 
         * @return all details components that are currently attached to the
         *         grid
         */
        public Collection<Component> getComponents() {
            Set<Component> components = new HashSet<Component>(
                    visibleDetailsComponents.values());
            return components;
        }

        public void refresh(Object itemId) {
            destroyDetails(itemId);
            createDetails(itemId);
        }

        void setGrid(Grid grid) {
            if (this.grid != null) {
                throw new IllegalStateException("Grid may injected only once.");
            }
            this.grid = grid;
        }

        /**
         * {@inheritDoc}
         * 
         * @since 7.6
         */
        @Override
        public void generateData(Object itemId, Item item, JsonObject rowData) {
            if (visibleDetails.contains(itemId)) {
                // Double check to be sure details component exists.
                detailComponentManager.createDetails(itemId);
                Component detailsComponent = visibleDetailsComponents
                        .get(itemId);
                rowData.put(
                        GridState.JSONKEY_DETAILS_VISIBLE,
                        (detailsComponent != null ? detailsComponent
                                .getConnectorId() : ""));
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

                for (Object itemId : visibleDetails) {
                    detailComponentManager.destroyDetails(itemId);
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
    private Set<Object> updatedItemIds = new LinkedHashSet<Object>();

    /**
     * Queued RPC calls for adding and removing rows. Queue will be handled in
     * {@link beforeClientResponse}
     */
    private List<Runnable> rowChanges = new ArrayList<Runnable>();

    /** Size possibly changed with a bare ItemSetChangeEvent */
    private boolean bareItemSetTriggeredSizeChange = false;

    /**
     * This map represents all the details that are user-defined as visible.
     * This does not reflect the status in the DOM.
     */
    // TODO this should probably be inside DetailComponentManager
    private final Set<Object> visibleDetails = new HashSet<Object>();

    private final DetailComponentManager detailComponentManager = new DetailComponentManager();

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
        addDataGenerator(detailComponentManager);
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
            for (Runnable r : rowChanges) {
                r.run();
            }

            // Send current rows again if needed.
            if (refreshCache) {
                updatedItemIds.addAll(activeItemHandler.getActiveItemIds());
            }
        }

        internalUpdateRows(updatedItemIds);

        // Clear all changes.
        rowChanges.clear();
        refreshCache = false;
        updatedItemIds.clear();
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

            rows.set(i, getRowData(getGrid().getColumns(), itemId));
        }
        rpc.setRowData(firstRowToPush, rows);

        activeItemHandler.addActiveItems(itemIds);
    }

    private JsonObject getRowData(Collection<Column> columns, Object itemId) {
        Item item = container.getItem(itemId);

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
        detailComponentManager.setGrid(component);
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
        if (updatedItemIds.isEmpty()) {
            // At least one new item will be updated. Mark as dirty to actually
            // update before response to client.
            markAsDirty();
        }

        updatedItemIds.add(itemId);
    }

    private void internalUpdateRows(Set<Object> itemIds) {
        if (itemIds.isEmpty()) {
            return;
        }

        JsonArray rowData = Json.createArray();
        int i = 0;
        for (Object itemId : itemIds) {
            if (activeItemHandler.getActiveItemIds().contains(itemId)) {
                JsonObject row = getRowData(getGrid().getColumns(), itemId);
                rowData.set(i++, row);
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
            activeItemHandler.internalDropActiveItems(activeItemHandler
                    .getActiveItemIds());

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

    /**
     * Marks a row's details to be visible or hidden.
     * <p>
     * If that row is currently in the client side's cache, this information
     * will be sent over to the client.
     * 
     * @since 7.5.0
     * @param itemId
     *            the id of the item of which to change the details visibility
     * @param visible
     *            <code>true</code> to show the details, <code>false</code> to
     *            hide
     */
    public void setDetailsVisible(Object itemId, boolean visible) {
        if (visible) {
            visibleDetails.add(itemId);

            /*
             * This might be an issue with a huge number of open rows, but as of
             * now this works in most of the cases.
             */
            detailComponentManager.createDetails(itemId);
        } else {
            visibleDetails.remove(itemId);

            detailComponentManager.destroyDetails(itemId);
        }

        updateRowData(itemId);
    }

    /**
     * Checks whether the details for a row is marked as visible.
     * 
     * @since 7.5.0
     * @param itemId
     *            the id of the item of which to check the visibility
     * @return <code>true</code> iff the detials are visible for the item. This
     *         might return <code>true</code> even if the row is not currently
     *         visible in the DOM
     */
    public boolean isDetailsVisible(Object itemId) {
        return visibleDetails.contains(itemId);
    }

    /**
     * Refreshes all visible detail sections.
     * 
     * @since 7.5.0
     */
    public void refreshDetails() {
        for (Object itemId : ImmutableSet.copyOf(visibleDetails)) {
            detailComponentManager.refresh(itemId);
            updateRowData(itemId);
        }
    }

    /**
     * Gets the detail component manager for this data provider
     * 
     * @since 7.5.0
     * @return the detail component manager
     * */
    public DetailComponentManager getDetailComponentManager() {
        return detailComponentManager;
    }
}
