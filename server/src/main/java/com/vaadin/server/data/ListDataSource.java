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
package com.vaadin.server.data;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link DataSource} wrapper for {@link Collection}s. This class does not
 * actually handle the {@link Query} parameters.
 *
 * @param <T>
 *            data type
 */
public class ListDataSource<T> extends AbstractDataSource<T> {

    private Comparator<T> sortOrder;
    private final Collection<T> backend;

    /**
     * Constructs a new ListDataSource. This method makes a protective copy of
     * the contents of the Collection.
     *
     * @param items
     *            the initial data, not null
     */
    public ListDataSource(Collection<T> items) {
        Objects.requireNonNull(items, "items cannot be null");
        backend = items;
        sortOrder = null;
    }

    /**
     * Chaining constructor for making modified {@link ListDataSource}s. This
     * Constructor is used internally for making sorted and filtered variants of
     * a base data source with actual data.
     * 
     * @param items
     *            the backend data from the original list data source
     * @param sortOrder
     *            a {@link Comparator} providing the needed sorting order
     *
     */
    protected ListDataSource(Collection<T> items, Comparator<T> sortOrder) {
        this(items);
        this.sortOrder = sortOrder;
    }

    @Override
    public Stream<T> apply(Query query) {
        Stream<T> stream = backend.stream();
        if (sortOrder != null) {
            stream = stream.sorted(sortOrder);
        }
        return stream;
    }

    /**
     * Creates a new list data source based on this list data source with the
     * given sort order.
     * <p>
     * <b>NOTE</b>: this data source is not modified in any way.
     *
     * @param sortOrder
     *            a {@link Comparator} providing the needed sorting order
     * @return new data source with modified sorting
     */
    public ListDataSource<T> sortingBy(Comparator<T> sortOrder) {
        return new ListDataSource<>(backend, sortOrder);
    }

    /**
     * Creates a new list data source based on this list data source with the
     * given sort order.
     * <p>
     * <b>NOTE</b>: this data source is not modified in any way.
     * <p>
     * This method is a short-hand for
     * {@code sortingBy(Comparator.comparing(sortOrder))}.
     *
     * @param sortOrder
     *            function to sort by
     * @param <U>
     *            the type of the Comparable sort key
     * @return new data source with modified sorting
     */
    public <U extends Comparable<? super U>> ListDataSource<T> sortingBy(
            Function<T, U> sortOrder) {
        return sortingBy(Comparator.comparing(sortOrder));
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * For in-memory data source the query is not handled, and it will always
     * return the full size.
     */
    @Override
    public int size(Query t) {
        return backend.size();
    }

}
