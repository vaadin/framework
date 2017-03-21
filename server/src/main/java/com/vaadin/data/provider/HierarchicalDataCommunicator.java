/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.data.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.HierarchyData;
import com.vaadin.data.provider.HierarchyMapper.TreeLevelQuery;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.Range;
import com.vaadin.shared.extension.datacommunicator.HierarchicalDataCommunicatorState;
import com.vaadin.shared.ui.treegrid.TreeGridCommunicationConstants;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * Data communicator that handles requesting hierarchical data from
 * {@link HierarchicalDataProvider} and sending it to client side.
 *
 * @param <T>
 *            the bean type
 * @author Vaadin Ltd
 * @since
 */
public class HierarchicalDataCommunicator<T> extends DataCommunicator<T> {

    private static final Logger LOGGER = Logger
            .getLogger(HierarchicalDataCommunicator.class.getName());

    /**
     * The amount of root level nodes to fetch and push to the client.
     */
    private static final int INITIAL_FETCH_SIZE = 100;

    private HierarchyMapper mapper = new HierarchyMapper();

    /**
     * Collapse allowed provider used to allow/disallow collapsing nodes.
     */
    private SerializablePredicate<T> itemCollapseAllowedProvider = t -> true;

    /**
     * The captured client side cache size.
     */
    private int latestCacheSize = INITIAL_FETCH_SIZE;

    /**
     * Construct a new hierarchical data communicator backed by a
     * {@link InMemoryHierarchicalDataProvider}.
     */
    public HierarchicalDataCommunicator() {
        super();
        dataProvider = new InMemoryHierarchicalDataProvider<>(
                new HierarchyData<>());
    }

    @Override
    protected HierarchicalDataCommunicatorState getState() {
        return (HierarchicalDataCommunicatorState) super.getState();
    }

    @Override
    protected HierarchicalDataCommunicatorState getState(boolean markAsDirty) {
        return (HierarchicalDataCommunicatorState) super.getState(markAsDirty);
    }

    @Override
    protected void sendDataToClient(boolean initial) {
        // on purpose do not call super
        if (getDataProvider() == null) {
            return;
        }

        if (initial || reset) {
            loadInitialData();
        } else {
            loadRequestedRows();
        }

        if (!getUpdatedData().isEmpty()) {
            JsonArray dataArray = Json.createArray();
            int i = 0;
            for (T data : getUpdatedData()) {
                dataArray.set(i++, createDataObject(data, -1));
            }
            getClientRpc().updateData(dataArray);
            getUpdatedData().clear();
        }
    }

    private void loadInitialData() {
        int rootSize = doSizeQuery(null);
        mapper.reset(rootSize);

        if (rootSize != 0) {
            Range initialRange = getInitialRowsToPush(rootSize);
            assert !initialRange
                    .isEmpty() : "Initial range should never be empty.";
            Stream<T> rootItems = doFetchQuery(initialRange.getStart(),
                    initialRange.length(), null);

            // for now just fetching data for the root level as everything is
            // collapsed by default
            List<T> items = rootItems.collect(Collectors.toList());
            List<JsonObject> dataObjects = items.stream()
                    .map(item -> createDataObject(item, 0))
                    .collect(Collectors.toList());

            getClientRpc().reset(rootSize);
            sendData(0, dataObjects);
            getActiveDataHandler().addActiveData(items.stream());
            getActiveDataHandler().cleanUp(items.stream());
        }

        setPushRows(Range.withLength(0, 0));
        // any updated data is ignored at this point
        getUpdatedData().clear();
        reset = false;
    }

    private void loadRequestedRows() {
        final Range requestedRows = getPushRows();
        if (!requestedRows.isEmpty()) {
            Stream<TreeLevelQuery> levelQueries = mapper
                    .splitRangeToLevelQueries(requestedRows.getStart(),
                            requestedRows.getEnd() - 1);

            JsonObject[] dataObjects = new JsonObject[requestedRows.length()];
            BiConsumer<JsonObject, Integer> rowDataMapper = (object,
                    index) -> dataObjects[index
                            - requestedRows.getStart()] = object;
            List<T> fetchedItems = new ArrayList<>(dataObjects.length);

            levelQueries.forEach(query -> {
                List<T> results = doFetchQuery(query.startIndex, query.size,
                        getKeyMapper().get(query.node.getParentKey()))
                                .collect(Collectors.toList());
                // TODO if the size differers from expected, all goes to hell
                fetchedItems.addAll(results);
                List<JsonObject> rowData = results.stream()
                        .map(item -> createDataObject(item, query.depth))
                        .collect(Collectors.toList());
                mapper.reorderLevelQueryResultsToFlatOrdering(rowDataMapper,
                        query, rowData);
            });
            verifyNoNullItems(dataObjects, requestedRows);

            sendData(requestedRows.getStart(), Arrays.asList(dataObjects));
            getActiveDataHandler().addActiveData(fetchedItems.stream());
            getActiveDataHandler().cleanUp(fetchedItems.stream());
        }

        setPushRows(Range.withLength(0, 0));
    }

    /*
     * Verify that there are no null objects in the array, to fail eagerly and
     * not just on the client side.
     */
    private void verifyNoNullItems(JsonObject[] dataObjects,
            Range requestedRange) {
        List<Integer> nullItems = new ArrayList<>(0);
        AtomicInteger indexCounter = new AtomicInteger();
        Stream.of(dataObjects).forEach(object -> {
            int index = indexCounter.getAndIncrement();
            if (object == null) {
                nullItems.add(index);
            }
        });
        if (!nullItems.isEmpty()) {
            throw new IllegalStateException("For requested rows "
                    + requestedRange + ", there was null items for indexes "
                    + nullItems.stream().map(Object::toString)
                            .collect(Collectors.joining(", ")));
        }
    }

    private JsonObject createDataObject(T item, int depth) {
        JsonObject dataObject = getDataObject(item);

        JsonObject hierarchyData = Json.createObject();
        if (depth != -1) {
            hierarchyData.put(TreeGridCommunicationConstants.ROW_DEPTH, depth);
        }

        boolean isLeaf = !getDataProvider().hasChildren(item);
        if (isLeaf) {
            hierarchyData.put(TreeGridCommunicationConstants.ROW_LEAF, true);
        } else {
            String key = getKeyMapper().key(item);
            hierarchyData.put(TreeGridCommunicationConstants.ROW_COLLAPSED,
                    mapper.isCollapsed(key));
            hierarchyData.put(TreeGridCommunicationConstants.ROW_LEAF, false);
            hierarchyData.put(
                    TreeGridCommunicationConstants.ROW_COLLAPSE_ALLOWED,
                    itemCollapseAllowedProvider.test(item));
        }

        // add hierarchy information to row as metadata
        dataObject.put(TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION,
                hierarchyData);

        return dataObject;
    }

    private void sendData(int startIndex, List<JsonObject> dataObjects) {
        JsonArray dataArray = Json.createArray();
        int i = 0;
        for (JsonObject dataObject : dataObjects) {
            dataArray.set(i++, dataObject);
        }

        getClientRpc().setData(startIndex, dataArray);
    }

    /**
     * Returns the range of rows to push on initial response.
     *
     * @param rootLevelSize
     *            the amount of rows on the root level
     * @return the range of rows to push initially
     */
    private Range getInitialRowsToPush(int rootLevelSize) {
        // TODO optimize initial level to avoid unnecessary requests
        return Range.between(0, Math.min(rootLevelSize, latestCacheSize));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Stream<T> doFetchQuery(int start, int length, T parentItem) {
        return getDataProvider()
                .fetch(new HierarchicalQuery(start, length, getBackEndSorting(),
                        getInMemorySorting(), getFilter(), parentItem));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private int doSizeQuery(T parentItem) {
        return getDataProvider()
                .getChildCount(new HierarchicalQuery(getFilter(), parentItem));
    }

    @Override
    protected void onRequestRows(int firstRowIndex, int numberOfRows,
            int firstCachedRowIndex, int cacheSize) {
        super.onRequestRows(firstRowIndex, numberOfRows, firstCachedRowIndex,
                cacheSize);
    }

    @Override
    protected void onDropRows(JsonArray keys) {
        for (int i = 0; i < keys.length(); i++) {
            // cannot drop expanded rows since the parent item is needed always
            // when fetching more rows
            String itemKey = keys.getString(i);
            if (mapper.isCollapsed(itemKey)) {
                getActiveDataHandler().dropActiveData(itemKey);
            }
        }
    }

    @Override
    public HierarchicalDataProvider<T, ?> getDataProvider() {
        return (HierarchicalDataProvider<T, ?>) super.getDataProvider();
    }

    /**
     * Set the current hierarchical data provider for this communicator.
     *
     * @param dataProvider
     *            the data provider to set, not <code>null</code>
     * @param initialFilter
     *            the initial filter value to use, or <code>null</code> to not
     *            use any initial filter value
     *
     * @param <F>
     *            the filter type
     *
     * @return a consumer that accepts a new filter value to use
     */
    public <F> SerializableConsumer<F> setDataProvider(
            HierarchicalDataProvider<T, F> dataProvider, F initialFilter) {
        return super.setDataProvider(dataProvider, initialFilter);
    }

    /**
     * Set the current hierarchical data provider for this communicator.
     *
     * @param dataProvider
     *            the data provider to set, must extend
     *            {@link HierarchicalDataProvider}, not <code>null</code>
     * @param initialFilter
     *            the initial filter value to use, or <code>null</code> to not
     *            use any initial filter value
     *
     * @param <F>
     *            the filter type
     *
     * @return a consumer that accepts a new filter value to use
     */
    @Override
    public <F> SerializableConsumer<F> setDataProvider(
            DataProvider<T, F> dataProvider, F initialFilter) {
        if (dataProvider instanceof HierarchicalDataProvider) {
            return super.setDataProvider(dataProvider, initialFilter);
        }
        throw new IllegalArgumentException(
                "Only " + HierarchicalDataProvider.class.getName()
                        + " and subtypes supported.");
    }

    /**
     * Collapses given row, removing all its subtrees.
     *
     * @param collapsedRowKey
     *            the key of the row, not {@code null}
     * @param collapsedRowIndex
     *            the index of row to collapse
     */
    public void doCollapse(String collapsedRowKey, int collapsedRowIndex) {
        if (collapsedRowIndex < 0 | collapsedRowIndex >= mapper.getTreeSize()) {
            throw new IllegalArgumentException("Invalid row index "
                    + collapsedRowIndex + " when tree grid size of "
                    + mapper.getTreeSize());
        }
        Objects.requireNonNull(collapsedRowKey, "Row key cannot be null");
        T collapsedItem = getKeyMapper().get(collapsedRowKey);
        Objects.requireNonNull(collapsedItem,
                "Cannot find item for given key " + collapsedItem);

        int collapsedSubTreeSize = mapper.collapse(collapsedRowKey,
                collapsedRowIndex);

        getClientRpc().removeRows(collapsedRowIndex + 1, collapsedSubTreeSize);
        // FIXME seems like a slight overkill to do this just for refreshing
        // expanded status
        refresh(collapsedItem);
    }

    /**
     * Expands the given row.
     *
     * @param expandedRowKey
     *            the key of the row, not {@code null}
     * @param expandedRowIndex
     *            the index of the row to expand
     */
    public void doExpand(String expandedRowKey, final int expandedRowIndex) {
        if (expandedRowIndex < 0 | expandedRowIndex >= mapper.getTreeSize()) {
            throw new IllegalArgumentException("Invalid row index "
                    + expandedRowIndex + " when tree grid size of "
                    + mapper.getTreeSize());
        }
        Objects.requireNonNull(expandedRowKey, "Row key cannot be null");
        final T expandedItem = getKeyMapper().get(expandedRowKey);
        Objects.requireNonNull(expandedItem,
                "Cannot find item for given key " + expandedRowKey);

        final int expandedNodeSize = doSizeQuery(expandedItem);
        if (expandedNodeSize == 0) {
            // TODO handle 0 size -> not expandable
            throw new IllegalStateException("Row with index " + expandedRowIndex
                    + " returned no child nodes.");
        }

        mapper.expand(expandedRowKey, expandedRowIndex, expandedNodeSize);

        // TODO optimize by sending "enough" of the expanded items directly
        getClientRpc().insertRows(expandedRowIndex + 1, expandedNodeSize);
        // expanded node needs to be updated to be marked as expanded
        // FIXME seems like a slight overkill to do this just for refreshing
        // expanded status
        refresh(expandedItem);
    }

    /**
     * Sets the item collapse allowed provider for this
     * HierarchicalDataCommunicator. The provider should return {@code true} for
     * any item that the user can collapse.
     * <p>
     * <strong>Note:</strong> This callback will be accessed often when sending
     * data to the client. The callback should not do any costly operations.
     *
     * @param provider
     *            the item collapse allowed provider, not {@code null}
     */
    public void setItemCollapseAllowedProvider(
            SerializablePredicate<T> provider) {
        Objects.requireNonNull(provider, "Provider can't be null");
        itemCollapseAllowedProvider = provider;

        getActiveDataHandler().getActiveData().forEach(this::refresh);
    }

}
