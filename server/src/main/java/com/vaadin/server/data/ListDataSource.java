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
package com.vaadin.server.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * {@link DataSource} wrapper for {@link Collection}s. This class does not
 * actually handle the {@link Query} parameters.
 *
 * @param <T>
 *            data type
 */
public class ListDataSource<T> implements DataSource<T> {

    private Function<Query, Stream<T>> request;
    private int size;

    /**
     * Constructs a new ListDataSource. This method makes a protective copy of
     * the contents of the Collection.
     *
     * @param collection
     *            initial data
     */
    public ListDataSource(Collection<T> collection) {
        final List<T> backend = new ArrayList<>(collection);
        request = query -> backend.stream();
        size = backend.size();
    }

    /**
     * Chaining constructor for making modified {@link ListDataSource}s. This
     * Constructor is used internally for making sorted and filtered variants of
     * a base data source with actual data.
     *
     * @param request
     *            request for the new data source
     */
    protected ListDataSource(Function<Query, Stream<T>> request) {
        this.request = request;
    }

    @Override
    public Stream<T> apply(Query query) {
        return request.apply(query);
    }

    /**
     * Sets a default sorting order to the data source.
     *
     * @param sortOrder
     *            a {@link Comparator} providing the needed sorting order
     * @return new data source with modified sorting
     */
    public ListDataSource<T> sortingBy(Comparator<T> sortOrder) {
        return new ListDataSource<>(q -> request.apply(q).sorted(sortOrder));
    }

    /**
     * Sets a default sorting order to the data source. This method is a
     * short-hand for {@code sortingBy(Comparator.comparing(sortOrder))}.
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
        return size;
    }
}
