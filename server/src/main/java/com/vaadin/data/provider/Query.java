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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Immutable query object used to request data from a backend. Contains index
 * limits, sorting and filtering information.
 *
 * @param <T>
 *            bean type
 * @param <F>
 *            filter type
 *
 * @since 8.0
 */
public class Query<T, F> implements Serializable {

    private final int offset;
    private final int limit;
    private final List<QuerySortOrder> sortOrders;
    private final Comparator<T> inMemorySorting;
    private final F filter;

    /**
     * Constructs a Query for all rows from 0 to {@link Integer#MAX_VALUE}
     * without sorting and filtering.
     */
    public Query() {
        offset = 0;
        limit = Integer.MAX_VALUE;
        sortOrders = Collections.emptyList();
        inMemorySorting = null;
        filter = null;
    }

    /**
     * Constructs a Query for all rows from 0 to {@link Integer#MAX_VALUE} with
     * filtering.
     *
     * @param filter
     *            back end filter of a suitable type for the data provider; can
     *            be null
     */
    public Query(F filter) {
        offset = 0;
        limit = Integer.MAX_VALUE;
        sortOrders = Collections.emptyList();
        inMemorySorting = null;
        this.filter = filter;
    }

    /**
     * Constructs a new Query object with given offset, limit, sorting and
     * filtering.
     *
     * @param offset
     *            first index to fetch
     * @param limit
     *            fetched item count
     * @param sortOrders
     *            sorting order for fetching; used for sorting backends
     * @param inMemorySorting
     *            comparator for sorting in-memory data
     * @param filter
     *            filtering for fetching; can be null
     */
    public Query(int offset, int limit, List<QuerySortOrder> sortOrders,
            Comparator<T> inMemorySorting, F filter) {
        this.offset = offset;
        this.limit = limit;
        this.sortOrders = sortOrders;
        this.inMemorySorting = inMemorySorting;
        this.filter = filter;
    }

    /**
     * Gets the first index of items to fetch. The offset is only used when
     * fetching items, but not when counting the number of available items.
     *
     * @return offset for data request
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the limit of items to fetch. The limit is only used when fetching
     * items, but not when counting the number of available items.
     * <p>
     * <strong>Note: </strong>It is possible that
     * {@code offset + limit > item count}
     *
     * @return number of items to fetch
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Gets the sorting for items to fetch. This list of sort orders is used for
     * sorting backends. The sort orders are only used when fetching items, but
     * not when counting the number of available items.
     * <p>
     * <strong>Note: </strong> Sort orders and in-memory sorting are mutually
     * exclusive. If the {@link DataProvider} handles one, it should ignore the
     * other.
     *
     * @return list of sort orders
     */
    public List<QuerySortOrder> getSortOrders() {
        return sortOrders;
    }

    /**
     * Gets the filter for items to fetch.
     *
     * @return optional filter
     */
    public Optional<F> getFilter() {
        return Optional.ofNullable(filter);
    }

    /**
     * Gets the comparator for sorting in-memory data. The comparator is only
     * used when fetching items, but not when counting the number of available
     * items.
     * <p>
     * <strong>Note: </strong> Sort orders and in-memory sorting are mutually
     * exclusive. If the {@link DataProvider} handles one, it should ignore the
     * other.
     *
     * @return sorting comparator
     */
    public Comparator<T> getInMemorySorting() {
        return inMemorySorting;
    }
}
