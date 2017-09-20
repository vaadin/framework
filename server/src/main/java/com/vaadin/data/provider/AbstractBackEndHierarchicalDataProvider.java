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
 * Abstract base class for implementing
 * {@link BackEndHierarchicalDataProvider}s.
 *
 * @author Vaadin Ltd
 * @since 8.1
 *
 * @param <T>
 *            data type
 * @param <F>
 *            filter type
 */
public abstract class AbstractBackEndHierarchicalDataProvider<T, F>
        extends AbstractHierarchicalDataProvider<T, F>
        implements BackEndHierarchicalDataProvider<T, F> {

    private List<QuerySortOrder> sortOrders = new ArrayList<>();

    private HierarchicalQuery<T, F> mixInSortOrders(
            HierarchicalQuery<T, F> query) {
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

        return new HierarchicalQuery<>(query.getOffset(), query.getLimit(),
                combinedSortOrders, query.getInMemorySorting(),
                query.getFilter().orElse(null), query.getParent());
    }

    @Override
    public Stream<T> fetchChildren(HierarchicalQuery<T, F> query) {
        return fetchChildrenFromBackEnd(mixInSortOrders(query));
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public void setSortOrders(List<QuerySortOrder> sortOrders) {
        this.sortOrders = Objects.requireNonNull(sortOrders,
                "Sort orders cannot be null");
        refreshAll();
    }

    /**
     * Fetches data from the back end using the given query.
     *
     * @see HierarchicalQuery
     *
     * @param query
     *            the query that defines sorting, filtering, paging and the
     *            parent item to fetch children from
     * @return a stream of items matching the query
     */
    protected abstract Stream<T> fetchChildrenFromBackEnd(
            HierarchicalQuery<T, F> query);
}
