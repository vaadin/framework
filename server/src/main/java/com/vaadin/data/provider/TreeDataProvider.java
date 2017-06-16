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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.data.TreeData;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;

/**
 * An in-memory data provider for listing components that display hierarchical
 * data. Uses an instance of {@link TreeData} as its source of data.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 */
public class TreeDataProvider<T>
        extends AbstractHierarchicalDataProvider<T, SerializablePredicate<T>>
        implements InMemoryDataProvider<T> {

    private final TreeData<T> treeData;

    private SerializablePredicate<T> filter = null;

    private SerializableComparator<T> sortOrder = null;

    /**
     * Constructs a new TreeDataProvider.
     * <p>
     * This data provider should be refreshed after making changes to the
     * underlying {@link TreeData} instance.
     *
     * @param treeData
     *            the backing {@link TreeData} for this provider, not
     *            {@code null}
     */
    public TreeDataProvider(TreeData<T> treeData) {
        Objects.requireNonNull(treeData, "treeData cannot be null");
        this.treeData = treeData;
    }

    /**
     * Return the underlying hierarchical data of this provider.
     *
     * @return the underlying data of this provider
     */
    public TreeData<T> getTreeData() {
        return treeData;
    }

    @Override
    public boolean hasChildren(T item) {
        if (!treeData.contains(item)) {
            throw new IllegalArgumentException("Item " + item
                    + " could not be found in the backing TreeData. "
                    + "Did you forget to refresh this data provider after item removal?");
        }

        return !treeData.getChildren(item).isEmpty();
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        return (int) fetchChildren(query).count();
    }

    @Override
    public Stream<T> fetchChildren(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        if (!treeData.contains(query.getParent())) {
            throw new IllegalArgumentException("The queried item "
                    + query.getParent()
                    + " could not be found in the backing TreeData. "
                    + "Did you forget to refresh this data provider after item removal?");
        }

        Stream<T> childStream = getFilteredStream(
                treeData.getChildren(query.getParent()).stream(),
                query.getFilter());

        Optional<Comparator<T>> comparing = Stream
                .of(query.getInMemorySorting(), sortOrder)
                .filter(c -> c != null)
                .reduce((c1, c2) -> c1.thenComparing(c2));

        if (comparing.isPresent()) {
            childStream = childStream.sorted(comparing.get());
        }

        return childStream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public SerializablePredicate<T> getFilter() {
        return filter;
    }

    @Override
    public void setFilter(SerializablePredicate<T> filter) {
        this.filter = filter;
        refreshAll();
    }

    @Override
    public SerializableComparator<T> getSortComparator() {
        return sortOrder;
    }

    @Override
    public void setSortComparator(SerializableComparator<T> comparator) {
        sortOrder = comparator;
        refreshAll();
    }

    @Override
    public <C> DataProvider<T, C> withConvertedFilter(
            SerializableFunction<C, SerializablePredicate<T>> filterConverter) {
        Objects.requireNonNull(filterConverter,
                "Filter converter can't be null");
        return new DataProviderWrapper<T, C, SerializablePredicate<T>>(this) {

            @Override
            protected SerializablePredicate<T> getFilter(Query<T, C> query) {
                return query.getFilter().map(filterConverter).orElse(null);
            }

            @Override
            public int size(Query<T, C> t) {
                if (t instanceof HierarchicalQuery<?, ?>) {
                    return dataProvider.size(new HierarchicalQuery<>(
                            t.getOffset(), t.getLimit(), t.getSortOrders(),
                            t.getInMemorySorting(), getFilter(t),
                            ((HierarchicalQuery<T, C>) t).getParent()));
                }
                throw new IllegalArgumentException(
                        "Hierarchical data provider doesn't support non-hierarchical queries");
            }

            @Override
            public Stream<T> fetch(Query<T, C> t) {
                if (t instanceof HierarchicalQuery<?, ?>) {
                    return dataProvider.fetch(new HierarchicalQuery<>(
                            t.getOffset(), t.getLimit(), t.getSortOrders(),
                            t.getInMemorySorting(), getFilter(t),
                            ((HierarchicalQuery<T, C>) t).getParent()));
                }
                throw new IllegalArgumentException(
                        "Hierarchical data provider doesn't support non-hierarchical queries");
            }
        };
    }

    private Stream<T> getFilteredStream(Stream<T> stream,
            Optional<SerializablePredicate<T>> queryFilter) {
        if (filter != null) {
            stream = stream.filter(filter);
        }
        return queryFilter.map(stream::filter).orElse(stream);
    }
}
