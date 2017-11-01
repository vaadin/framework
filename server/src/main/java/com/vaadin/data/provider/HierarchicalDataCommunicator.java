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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.TreeData;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.shared.Range;
import com.vaadin.shared.extension.datacommunicator.HierarchicalDataCommunicatorState;
import com.vaadin.ui.ItemCollapseAllowedProvider;

/**
 * Data communicator that handles requesting hierarchical data from
 * {@link HierarchicalDataProvider} and sending it to client side.
 *
 * @param <T>
 *            the bean type
 * @author Vaadin Ltd
 * @since 8.1
 */
public class HierarchicalDataCommunicator<T> extends DataCommunicator<T> {

    private HierarchyMapper<T, ?> mapper;

    /**
     * Collapse allowed provider used to allow/disallow collapsing nodes.
     */
    private ItemCollapseAllowedProvider<T> itemCollapseAllowedProvider = t -> true;

    /**
     * Construct a new hierarchical data communicator backed by a
     * {@link TreeDataProvider}.
     */
    public HierarchicalDataCommunicator() {
        super();
        setDataProvider(new TreeDataProvider<>(new TreeData<>()), null);
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
    public List<T> fetchItemsWithRange(int offset, int limit) {
        // Instead of adding logic to this class, delegate request to the
        // separate object handling hierarchies.
        return mapper.fetchItems(Range.withLength(offset, limit))
                .collect(Collectors.toList());
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
        SerializableConsumer<F> consumer = super.setDataProvider(dataProvider,
                initialFilter);

        // Remove old mapper
        if (mapper != null) {
            removeDataGenerator(mapper);
        }
        mapper = new HierarchyMapper<>(dataProvider);

        // Set up mapper for requests
        mapper.setBackEndSorting(getBackEndSorting());
        mapper.setInMemorySorting(getInMemorySorting());
        mapper.setFilter(getFilter());
        mapper.setItemCollapseAllowedProvider(getItemCollapseAllowedProvider());

        // Provide hierarchy data to json
        addDataGenerator(mapper);

        return consumer;
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
            return setDataProvider(
                    (HierarchicalDataProvider<T, F>) dataProvider,
                    initialFilter);
        }
        throw new IllegalArgumentException(
                "Only " + HierarchicalDataProvider.class.getName()
                        + " and subtypes supported.");
    }

    /**
     * Collapses given item, removing all its subtrees. Calling this method will
     * have no effect if the row is already collapsed.
     *
     * @param item
     *            the item to collapse
     */
    public void collapse(T item) {
        if (mapper.isExpanded(item)) {
            doCollapse(item, mapper.getIndexOf(item));
        }
    }

    /**
     * Collapses given item, removing all its subtrees. Calling this method will
     * have no effect if the row is already collapsed. The index is provided by
     * the client-side or calculated from a full data request.
     *
     * @see #collapse(Object)
     *
     * @param item
     *            the item to collapse
     * @param index
     *            the index of the item
     */
    public void doCollapse(T item, Optional<Integer> index) {
        if (mapper.isExpanded(item)) {
            Range removedRows = mapper.doCollapse(item, index);
            if (!reset && !removedRows.isEmpty()) {
                getClientRpc().removeRows(removedRows.getStart(),
                        removedRows.length());
            }
            refresh(item);
        }
    }

    /**
     * Expands the given item. Calling this method will have no effect if the
     * row is already expanded.
     *
     * @param item
     *            the item to expand
     */
    public void expand(T item) {
        if (!mapper.isExpanded(item) && mapper.hasChildren(item)) {
            doExpand(item, mapper.getIndexOf(item));
        }
    }

    /**
     * Expands the given item at given index. Calling this method will have no
     * effect if the row is already expanded. The index is provided by the
     * client-side or calculated from a full data request.
     *
     * @see #expand(Object)
     *
     * @param item
     *            the item to expand
     * @param index
     *            the index of the item
     */
    public void doExpand(T item, Optional<Integer> index) {
        if (!mapper.isExpanded(item)) {
            Range addedRows = mapper.doExpand(item, index);
            if (!reset && !addedRows.isEmpty()) {
                int start = addedRows.getStart();
                getClientRpc().insertRows(start, addedRows.length());
                Stream<T> children = mapper.fetchItems(item,
                        Range.withLength(0, addedRows.length()));
                pushData(start, children.collect(Collectors.toList()));
            }
            refresh(item);
        }
    }

    /**
     * Returns whether given item has children.
     *
     * @param item
     *            the item to test
     * @return {@code true} if item has children; {@code false} if not
     */
    public boolean hasChildren(T item) {
        return mapper.hasChildren(item);
    }

    /**
     * Returns whether given item is expanded.
     *
     * @param item
     *            the item to test
     * @return {@code true} if item is expanded; {@code false} if not
     */
    public boolean isExpanded(T item) {
        return mapper.isExpanded(item);
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
            ItemCollapseAllowedProvider<T> provider) {
        Objects.requireNonNull(provider, "Provider can't be null");
        itemCollapseAllowedProvider = provider;
        // Update hierarchy mapper
        mapper.setItemCollapseAllowedProvider(provider);

        getActiveDataHandler().getActiveData().values().forEach(this::refresh);
    }

    /**
     * Returns parent index for the row or {@code null}.
     *
     * @param item
     *            the item to find the parent of
     * @return the parent index or {@code null} for top-level items
     */
    public Integer getParentIndex(T item) {
        return mapper.getParentIndex(item);
    }

    /**
     * Gets the item collapse allowed provider.
     *
     * @return the item collapse allowed provider
     */
    public ItemCollapseAllowedProvider<T> getItemCollapseAllowedProvider() {
        return itemCollapseAllowedProvider;
    }

    @Override
    public int getDataProviderSize() {
        return mapper.getTreeSize();
    }

    @Override
    public void setBackEndSorting(List<QuerySortOrder> sortOrder) {
        if (mapper != null) {
            mapper.setBackEndSorting(sortOrder);
        }
        super.setBackEndSorting(sortOrder);
    }

    @Override
    public void setInMemorySorting(Comparator<T> comparator) {
        if (mapper != null) {
            mapper.setInMemorySorting(comparator);
        }
        super.setInMemorySorting(comparator);
    }

    @Override
    protected <F> void setFilter(F filter) {
        if (mapper != null) {
            mapper.setFilter(filter);
        }
        super.setFilter(filter);
    }

}
