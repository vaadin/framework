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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A {@link DataSource} for any back end.
 *
 * @param <T>
 *            data source data type
 */
public class BackEndDataSource<T> implements
        DataSource<T> {

    private Function<Query, Stream<T>> request;
    private Function<Query, Integer> sizeCallback;

    /**
     * Constructs a new DataSource to request data from an arbitrary back end
     * request function.
     *
     * @param request
     *            function that requests data from back end based on query
     * @param sizeCallback
     *            function that return the amount of data in back end for query
     */
    public BackEndDataSource(Function<Query, Stream<T>> request,
            Function<Query, Integer> sizeCallback) {
        Objects.requireNonNull(request, "Request function can't be null");
        Objects.requireNonNull(sizeCallback, "Size callback can't be null");
        this.request = request;
        this.sizeCallback = sizeCallback;
    }

    @Override
    public Stream<T> apply(Query t) {
        return request.apply(t);
    }

    @Override
    public int size(Query t) {
        return sizeCallback.apply(t);
    }

    /**
     * Sets a default sorting order to the data source.
     *
     * @param sortOrders
     *            a list of sorting information containing field ids and directions
     * @return new data source with modified sorting
     */
    public BackEndDataSource<T> sortingBy(List<SortOrder<String>> sortOrders) {
        return new BackEndDataSource<>(query -> {
            List<SortOrder<String>> queryOrder = new ArrayList<>(
                    query.getSortOrders());
            queryOrder.addAll(sortOrders);
            return request.apply(new Query(query.getLimit(), query.getOffset(),
                    queryOrder, query.getFilters()));
        }, sizeCallback);
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

}
