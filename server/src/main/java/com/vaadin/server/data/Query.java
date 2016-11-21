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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Immutable query object used to request data from a backend. Contains index
 * limits, sorting and filtering information.
 *
 * @param <F>
 *            filter type
 *
 * @since 8.0
 */
public class Query<F> implements Serializable {

    private final int offset;
    private final int limit;
    private final List<SortOrder<String>> sortOrders;
    private final F filter;

    /**
     * Constructs a Query for all rows from 0 to {@link Integer#MAX_VALUE}
     * without sorting and filtering.
     */
    public Query() {
        offset = 0;
        limit = Integer.MAX_VALUE;
        sortOrders = Collections.emptyList();
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
     *            sorting order for fetching
     * @param filter
     *            filtering for fetching; can be null
     */
    public Query(int offset, int limit, List<SortOrder<String>> sortOrders,
            F filter) {
        this.offset = offset;
        this.limit = limit;
        this.sortOrders = sortOrders;
        this.filter = filter;
    }

    /**
     * Gets the first index of items to fetch.
     *
     * @return offset for data request
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the limit of items to fetch.
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
     * Gets the sorting for items to fetch.
     *
     * @return list of sort orders
     */
    public List<SortOrder<String>> getSortOrders() {
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
}
