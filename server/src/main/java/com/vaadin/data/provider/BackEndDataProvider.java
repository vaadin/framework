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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.Registration;

/**
 * A {@link DataProvider} for any back end.
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            data provider filter type
 */
public class BackEndDataProvider<T, F> extends AbstractDataProvider<T, F> {

    private final SerializableFunction<Query<T, F>, Stream<T>> request;
    private final SerializableFunction<Query<T, F>, Integer> sizeCallback;

    /**
     * Constructs a new DataProvider to request data from an arbitrary back end
     * request function.
     *
     * @param request
     *            function that requests data from back end based on query
     * @param sizeCallback
     *            function that return the amount of data in back end for query
     */
    public BackEndDataProvider(
            SerializableFunction<Query<T, F>, Stream<T>> request,
            SerializableFunction<Query<T, F>, Integer> sizeCallback) {
        Objects.requireNonNull(request, "Request function can't be null");
        Objects.requireNonNull(sizeCallback, "Size callback can't be null");
        this.request = request;
        this.sizeCallback = sizeCallback;
    }

    @Override
    public Stream<T> fetch(Query<T, F> query) {
        return request.apply(query);
    }

    @Override
    public int size(Query<T, F> query) {
        return sizeCallback.apply(query);
    }

    /**
     * Sets a default sorting order to the data provider.
     *
     * @param sortOrders
     *            a list of sorting information containing field ids and
     *            directions
     * @return new data provider with modified sorting
     */
    @SuppressWarnings("serial")
    public BackEndDataProvider<T, F> sortingBy(
            List<SortOrder<String>> sortOrders) {
        BackEndDataProvider<T, F> parent = this;
        return new BackEndDataProvider<T, F>(query -> {
            List<SortOrder<String>> queryOrder = new ArrayList<>(
                    query.getSortOrders());
            queryOrder.addAll(sortOrders);
            return parent.fetch(new Query<>(query.getOffset(), query.getLimit(),
                    queryOrder, query.getInMemorySorting(),
                    query.getFilter().orElse(null)));
        }, sizeCallback) {

            @Override
            public Registration addDataProviderListener(
                    DataProviderListener listener) {
                return parent.addDataProviderListener(listener);
            }

            @Override
            public void refreshAll() {
                parent.refreshAll();
            }
        };
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

}
