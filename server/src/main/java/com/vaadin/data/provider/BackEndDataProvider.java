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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.SerializableFunction;
import com.vaadin.server.SerializableToIntFunction;

/**
 * A {@link DataProvider} for any back end.
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            data provider filter type
 */
public class BackEndDataProvider<T, F> extends AbstractDataProvider<T, F> {

    private List<SortOrder<String>> sortOrders = new ArrayList<>();

    private final SerializableFunction<Query<T, F>, Stream<T>> request;
    private final SerializableToIntFunction<Query<T, F>> sizeCallback;

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
            SerializableToIntFunction<Query<T, F>> sizeCallback) {
        Objects.requireNonNull(request, "Request function can't be null");
        Objects.requireNonNull(sizeCallback, "Size callback can't be null");
        this.request = request;
        this.sizeCallback = sizeCallback;
    }

    @Override
    public Stream<T> fetch(Query<T, F> query) {
        return request.apply(mixInSortOrders(query));
    }

    @Override
    public int size(Query<T, F> query) {
        return sizeCallback.applyAsInt(mixInSortOrders(query));
    }

    private Query<T, F> mixInSortOrders(Query<T, F> query) {
        Set<String> sortedPropertyNames = query.getSortOrders().stream()
                .map(SortOrder::getSorted).collect(Collectors.toSet());

        List<SortOrder<String>> combinedSortOrders = Stream
                .concat(query.getSortOrders().stream(),
                        sortOrders.stream()
                                .filter(order -> !sortedPropertyNames
                                        .contains(order.getSorted())))
                .collect(Collectors.toList());

        return new Query<>(query.getOffset(), query.getLimit(),
                combinedSortOrders, query.getInMemorySorting(),
                query.getFilter().orElse(null));
    }

    /**
     * Sets a list of sort orders to use as the default sorting for this data
     * provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @param sortOrders
     *            a list of sort orders to set, not <code>null</code>
     */
    public void setSortOrders(List<SortOrder<String>> sortOrders) {
        this.sortOrders = Objects.requireNonNull(sortOrders,
                "Sort orders cannot be null");
        refreshAll();
    }

    /**
     * Sets a single sort order to use as the default sorting for this data
     * provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     * <p>
     * This method is a shorthand for calling {@link #setSortOrders(List)} with
     * a list made up of zero or one sort order instances.
     *
     * @param sortOrder
     *            a sort order to set, or <code>null</code> to clear any
     *            previously set sort orders
     */
    public void setSortOrder(SortOrder<String> sortOrder) {
        if (sortOrder == null) {
            setSortOrders(Collections.emptyList());
        } else {
            setSortOrders(Collections.singletonList(sortOrder));
        }
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

}
