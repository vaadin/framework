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

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.server.SerializableComparator;
import com.vaadin.server.SerializablePredicate;

/**
 * {@link DataProvider} wrapper for {@link Collection}s.
 *
 * @param <T>
 *            data type
 * @since 8.0
 */
public class ListDataProvider<T>
        extends AbstractDataProvider<T, SerializablePredicate<T>>
        implements InMemoryDataProvider<T> {

    private SerializableComparator<T> sortOrder = null;

    private SerializablePredicate<T> filter;

    private final Collection<T> backend;

    /**
     * Constructs a new ListDataProvider.
     * <p>
     * No protective copy is made of the list, and changes in the provided
     * backing Collection will be visible via this data provider. The caller
     * should copy the list if necessary.
     *
     * @param items
     *            the initial data, not null
     */
    public ListDataProvider(Collection<T> items) {
        Objects.requireNonNull(items, "items cannot be null");
        backend = items;
        sortOrder = null;
    }

    /**
     * Returns the underlying data items.
     *
     * @return the underlying data items
     */
    public Collection<T> getItems() {
        return backend;
    }

    @Override
    public Stream<T> fetch(Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = getFilteredStream(query);

        Optional<Comparator<T>> comparing = Stream
                .of(query.getInMemorySorting(), sortOrder)
                .filter(c -> c != null)
                .reduce((c1, c2) -> c1.thenComparing(c2));

        if (comparing.isPresent()) {
            stream = stream.sorted(comparing.get());
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int size(Query<T, SerializablePredicate<T>> query) {
        return (int) getFilteredStream(query).count();
    }

    private Stream<T> getFilteredStream(
            Query<T, SerializablePredicate<T>> query) {
        Stream<T> stream = backend.stream();

        // Apply our own filters first so that query filters never see the items
        // that would already have been filtered out
        if (filter != null) {
            stream = stream.filter(filter);
        }

        stream = query.getFilter().map(stream::filter).orElse(stream);

        return stream;
    }

    @Override
    public SerializableComparator<T> getSortComparator() {
        return sortOrder;
    }

    @Override
    public void setSortComparator(SerializableComparator<T> comparator) {
        this.sortOrder = comparator;
        refreshAll();
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
}
