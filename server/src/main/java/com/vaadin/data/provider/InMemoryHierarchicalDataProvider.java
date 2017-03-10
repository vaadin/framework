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

import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.data.HierarchyData;
import com.vaadin.data.ValueProvider;
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

    private SerializablePredicate<T> filter;

    public InMemoryHierarchicalDataProvider() {
        hierarchyData = new HierarchyData<>();
    }

    public InMemoryHierarchicalDataProvider(HierarchyData<T> hierarchyData) {
        this.hierarchyData = hierarchyData;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public boolean hasChildren(T item) {
        return !hierarchyData.getChildren(item).isEmpty();
    }

    @Override
    public int getChildCount(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        return hierarchyData.getChildren(query.getParent()).size();
    }

    @Override
    public Stream<T> fetchChildren(
            HierarchicalQuery<T, SerializablePredicate<T>> query) {
        Stream<T> childStream = getFilteredStream(
                hierarchyData.getChildren(query.getParent()).stream());
        return query.getFilter().map(childStream::filter).orElse(childStream)
                .skip(query.getOffset()).limit(query.getLimit());
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
     * @see #addFilter(ValueProvider, SerializablePredicate)
     * @see #addFilterByValue(ValueProvider, Object)
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

    private Stream<T> getFilteredStream(Stream<T> stream) {
        if (filter == null) {
            return stream;
        }
        return stream.filter(filter);
    }
}