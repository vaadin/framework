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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract base class for implementing back end data providers.
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            data provider filter type
 * @since 8.0
 */
public abstract class AbstractBackEndDataProvider<T, F> extends
        AbstractDataProvider<T, F> implements BackEndDataProvider<T, F> {

    private List<QuerySortOrder> sortOrders = new ArrayList<>();

    private Query<T, F> mixInSortOrders(Query<T, F> query) {
        if (sortOrders.isEmpty()) {
            return query;
        }

        Set<String> sortedPropertyNames = query.getSortOrders().stream()
                .map(SortOrder::getSorted).collect(Collectors.toSet());

        List<QuerySortOrder> combinedSortOrders = Stream
                .concat(query.getSortOrders().stream(),
                        sortOrders.stream()
                                .filter(order -> !sortedPropertyNames
                                        .contains(order.getSorted())))
                .collect(Collectors.toList());

        return new Query<>(query.getOffset(), query.getLimit(),
                combinedSortOrders, query.getInMemorySorting(),
                query.getFilter().orElse(null));
    }

    @Override
    public Stream<T> fetch(Query<T, F> query) {
        return fetchFromBackEnd(mixInSortOrders(query));
    }

    @Override
    public int size(Query<T, F> query) {
        return sizeInBackEnd(mixInSortOrders(query));
    }

    /**
     * Fetches data from the back end using the given query.
     *
     * @param query
     *            the query that defines sorting, filtering and paging for
     *            fetching the data
     * @return a stream of items matching the query
     */
    protected abstract Stream<T> fetchFromBackEnd(Query<T, F> query);

    /**
     * Counts the number of items available in the back end.
     *
     * @param query
     *            the query that defines filtering to be used for counting the
     *            number of items
     * @return the number of available items
     */
    protected abstract int sizeInBackEnd(Query<T, F> query);

    @Override
    public void setSortOrders(List<QuerySortOrder> sortOrders) {
        this.sortOrders = Objects.requireNonNull(sortOrders,
                "Sort orders cannot be null");
        refreshAll();
    }

}
