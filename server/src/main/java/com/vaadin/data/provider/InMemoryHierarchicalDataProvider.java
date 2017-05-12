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

import com.vaadin.data.HierarchyData;
import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializablePredicate;

/**
 * A {@link DataProvider} for in-memory hierarchical data.
 *
 * @see HierarchyData
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 */
public class InMemoryHierarchicalDataProvider<T> extends
        AbstractHierarchicalDataProvider<T, SerializablePredicate<T>> implements
        ConfigurableFilterDataProvider<T, SerializablePredicate<T>, SerializablePredicate<T>> {

    private final HierarchyData<T> hierarchyData;

    private SerializablePredicate<T> filter = null;

    private SerializableComparator<T> sortOrder = null;

    /**
     * Constructs a new InMemoryHierarchicalDataProvider.
     * <p>
     * All changes made to the given HierarchyData object will also be visible
     * through this data provider.
     *
     * @param hierarchyData
     *            the backing HierarchyData for this provider
     */
    public InMemoryHierarchicalDataProvider(HierarchyData<T> hierarchyData) {
        this.hierarchyData = hierarchyData;
    }

    /**
     * Return the underlying hierarchical data of this provider.
     *
     * @return the underlying data of this provider
     */
    public HierarchyData<T> getData() {
        return hierarchyData;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public boolean hasChildren(T item) {
        if (!hierarchyData.contains(item)) {
            throw new IllegalArgumentException("Item " + item
                    + " could not be found in the backing HierarchyData. "
                    + "Did you forget to refresh this data provider after item removal?");
        }

        return !hierarchyData.getChildren(item).isEmpty();
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        return (int) fetchChildren(query).count();
    }

    @Override
    public Stream<T> fetchChildren(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        if (!hierarchyData.contains(query.getParent())) {
            throw new IllegalArgumentException("The queried item "
                    + query.getParent()
                    + " could not be found in the backing HierarchyData. "
                    + "Did you forget to refresh this data provider after item removal?");
        }

        Stream<T> childStream = getFilteredStream(
                hierarchyData.getChildren(query.getParent()).stream(),
                query.getFilter());

        Optional<Comparator<T>> comparing = Stream
                .of(query.getInMemorySorting(), sortOrder)
                .filter(Objects::nonNull)
                .reduce((c1, c2) -> c1.thenComparing(c2));

        if (comparing.isPresent()) {
            childStream = childStream.sorted(comparing.get());
        }

        return childStream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public void setFilter(SerializablePredicate<T> filter) {
        this.filter = filter;
        refreshAll();
    }

    /**
     * Adds a filter to be applied to all queries. The filter will be used in
     * addition to any filter that has been set or added previously.
     *
     * @see #addFilter(SerializablePredicate)
     * @see #setFilter(SerializablePredicate)
     *
     * @param filter
     *            the filter to add, not <code>null</code>
     */
    public void addFilter(SerializablePredicate<T> filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");

        if (this.filter == null) {
            setFilter(filter);
        } else {
            SerializablePredicate<T> oldFilter = this.filter;
            setFilter(item -> oldFilter.test(item) && filter.test(item));
        }
    }

    /**
     * Sets the comparator to use as the default sorting for this data provider.
     * This overrides the sorting set by any other method that manipulates the
     * default sorting of this data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @see #addSortComparator(SerializableComparator)
     *
     * @param comparator
     *            a comparator to use, or <code>null</code> to clear any
     *            previously set sort order
     */
    public void setSortComparator(SerializableComparator<T> comparator) {
        sortOrder = comparator;
        refreshAll();
    }

    /**
     * Adds a comparator to the default sorting for this data provider. If no
     * default sorting has been defined, then the provided comparator will be
     * used as the default sorting. If a default sorting has been defined, then
     * the provided comparator will be used to determine the ordering of items
     * that are considered equal by the previously defined default sorting.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @see #setSortComparator(SerializableComparator)
     *
     * @param comparator
     *            a comparator to add, not <code>null</code>
     */
    public void addSortComparator(SerializableComparator<T> comparator) {
        Objects.requireNonNull(comparator, "Sort order to add cannot be null");
        SerializableComparator<T> originalComparator = sortOrder;
        if (originalComparator == null) {
            setSortComparator(comparator);
        } else {
            setSortComparator((a, b) -> {
                int result = originalComparator.compare(a, b);
                if (result == 0) {
                    result = comparator.compare(a, b);
                }
                return result;
            });
        }
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
