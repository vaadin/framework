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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * @param <T> data type
 * @author Vaadin Ltd
 * @see HierarchyData
 * @since 8.1
 */
@SuppressWarnings("WeakerAccess")
public class SimpleHierarchicalDataProvider<T> extends
        AbstractHierarchicalDataProvider<T, SerializablePredicate<T>> implements
        HierarchyData<T>,
        ConfigurableFilterDataProvider<T, SerializablePredicate<T>, SerializablePredicate<T>> {

    private final HierarchyData<T> hierarchyData;

    private SerializablePredicate<T> filter = null;

    private SerializableComparator<T> sortOrder = null;

    /**
     * Constructs a new SimpleHierarchicalDataProvider.
     * <p>
     * All changes made to the given HierarchyData object will also be visible
     * through this data provider.
     *
     * @param hierarchyData the backing HierarchyData for this provider
     */
    public SimpleHierarchicalDataProvider(HierarchyData<T> hierarchyData) {
        this.hierarchyData = hierarchyData;
    }

    /**
     * Constructs a new SimpleHierarchicalDataProvider.
     * <p>
     * All changes made to the given HierarchyData object will also be visible
     * through this data provider.
     */
    public SimpleHierarchicalDataProvider() {
        this(new InMemoryHierarchyData<>());
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
     * @param filter the filter to add, not <code>null</code>
     * @see #addFilter(SerializablePredicate)
     * @see #setFilter(SerializablePredicate)
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
     * @param comparator a comparator to use, or <code>null</code> to clear any
     *                   previously set sort order
     * @see #addSortComparator(SerializableComparator)
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
     * @param comparator a comparator to add, not <code>null</code>
     * @see #setSortComparator(SerializableComparator)
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

    /**
     * Class for representing hierarchical data.
     *
     * @param <T> data type
     * @author Vaadin Ltd
     * @since 8.1
     */
    public static class InMemoryHierarchyData<T> implements HierarchyData<T> {

        private static class HierarchyWrapper<T> implements Serializable {
            private T item;
            private T parent;
            private List<T> children;

            public HierarchyWrapper(T item, T parent) {
                this.item = item;
                this.parent = parent;
                children = new ArrayList<>();
            }

            public T getItem() {
                return item;
            }

            public void setItem(T item) {
                this.item = item;
            }

            public T getParent() {
                return parent;
            }

            public void setParent(T parent) {
                this.parent = parent;
            }

            public List<T> getChildren() {
                return children;
            }

            public void setChildren(List<T> children) {
                this.children = children;
            }

            public void addChild(T child) {
                children.add(child);
            }

            public void removeChild(T child) {
                children.remove(child);
            }
        }

        private final Map<T, HierarchyWrapper<T>> itemToWrapperMap;

        /**
         * Creates an initially empty hierarchical data representation to which
         * items can be added or removed.
         */
        public InMemoryHierarchyData() {
            itemToWrapperMap = new LinkedHashMap<>();
            itemToWrapperMap.put(null, new HierarchyWrapper<>(null, null));
        }

        public InMemoryHierarchyData<T> addItem(T parent, T item) {
            Objects.requireNonNull(item, "Item cannot be null");
            if (parent != null && !contains(parent)) {
                throw new IllegalArgumentException(
                        "Parent needs to be added before children. "
                                + "To add root items, call with parent as null");
            }
            if (contains(item)) {
                throw new IllegalArgumentException(
                        "Cannot add the same item multiple times: " + item);
            }
            putItem(item, parent);
            return this;
        }

        public InMemoryHierarchyData<T> addItems(T parent,
                                                 @SuppressWarnings("unchecked") T... items) {
            Arrays.asList(items).forEach(item -> addItem(parent, item));
            return this;
        }

        public InMemoryHierarchyData<T> addItems(T parent, Collection<T> items) {
            items.forEach(item -> addItem(parent, item));
            return this;
        }

        public InMemoryHierarchyData<T> addItems(T parent, Stream<T> items) {
            items.forEach(item -> addItem(parent, item));
            return this;
        }

        public InMemoryHierarchyData<T> removeItem(T item) {
            if (!contains(item)) {
                throw new IllegalArgumentException(
                        "Item '" + item + "' not in the hierarchy");
            }
            new ArrayList<>(getChildren(item)).forEach(this::removeItem);
            itemToWrapperMap.get(itemToWrapperMap.get(item).getParent())
                    .removeChild(item);
            if (item != null) {
                // remove non root item from backing map
                itemToWrapperMap.remove(item);
            }
            return this;
        }

        public InMemoryHierarchyData<T> clear() {
            removeItem(null);
            return this;
        }

        public List<T> getChildren(T item) {
            if (!contains(item)) {
                throw new IllegalArgumentException(
                        "Item '" + item + "' not in the hierarchy");
            }
            return itemToWrapperMap.get(item).getChildren();
        }

        public boolean contains(T item) {
            return itemToWrapperMap.containsKey(item);
        }

        private void putItem(T item, T parent) {
            HierarchyWrapper<T> wrappedItem = new HierarchyWrapper<>(item, parent);
            if (itemToWrapperMap.containsKey(parent)) {
                itemToWrapperMap.get(parent).addChild(item);
            }
            itemToWrapperMap.put(item, wrappedItem);
        }
    }

    @Override
    public SimpleHierarchicalDataProvider<T> addItem(T parent, T item) {
        hierarchyData.addItem(parent, item);
        return this;
    }

    @Override
    public SimpleHierarchicalDataProvider<T> addItems(T parent, T... items) {
        hierarchyData.addItems(parent, items);
        return this;
    }

    @Override
    public SimpleHierarchicalDataProvider<T> addItems(T parent, Collection<T> items) {
        hierarchyData.addItems(parent, items);
        return this;
    }

    @Override
    public SimpleHierarchicalDataProvider<T> addItems(T parent, Stream<T> items) {
        hierarchyData.addItems(parent, items);
        return this;
    }

    @Override
    public SimpleHierarchicalDataProvider<T> removeItem(T item) {
        hierarchyData.removeItem(item);
        return this;
    }

    @Override
    public SimpleHierarchicalDataProvider<T> clear() {
        hierarchyData.clear();
        return this;
    }

    @Override
    public List<T> getChildren(T item) {
        return hierarchyData.getChildren(item);
    }

    @Override
    public boolean contains(T item) {
        return hierarchyData.contains(item);
    }
}
