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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A generic data source for any back end and Listing UI components.
 * 
 * @param <T>
 *            data source data type
 */
public class DataSource<T> implements
        Function<Query, Stream<T>>, java.io.Serializable {

    protected Function<Query, Stream<T>> request;
    protected Function<Query, Integer> sizeCallback;

    protected DataSource() {
    }

    /**
     * Constructs a new DataSource to request data from an arbitrary back end
     * request function.
     * 
     * @param request
     *            function that requests data from back end based on query
     * @param sizeCallback
     *            function that return the amount of data in back end for query
     */
    public DataSource(Function<Query, Stream<T>> request,
            Function<Query, Integer> sizeCallback) {
        Objects.requireNonNull(request, "Request function can't be null");
        Objects.requireNonNull(sizeCallback, "Size callback can't be null");
        this.request = request;
        this.sizeCallback = sizeCallback;
    }

    /**
     * This method creates a new {@link InMemoryDataSource} from a given
     * Collection. The InMemoryDataSource creates a protective List copy of all
     * the contents in the Collection.
     *
     * @param data
     *            collection of data
     * @return in-memory data source
     */
    public static <T> InMemoryDataSource<T> create(Collection<T> data) {
        return new InMemoryDataSource<>(data);
    }

    /**
     * This method creates a new {@link InMemoryDataSource} from given
     * objects.The InMemoryDataSource creates a protective List copy of all the
     * contents in the array.
     *
     * @param data
     *            data objects
     * @return in-memory data source
     */
    @SafeVarargs
    public static <T> InMemoryDataSource<T> create(T... data) {
        return new InMemoryDataSource<>(Arrays.asList(data));
    }

    @Override
    public Stream<T> apply(Query t) {
        return request.apply(t);
    }

    public int size(Query t) {
        return sizeCallback.apply(t);
    }

    public boolean isInMemory() {
        return false;
    }

}
