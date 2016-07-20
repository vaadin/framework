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
package com.vaadin.tokka.server.communication.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Query object used to request data from a backend. Contains index limits,
 * sorting and filtering information.
 * 
 * @since
 */
public class Query implements Serializable {

    private final int offset;
    private final int limit;
    private final List<SortOrder<String>> sortOrders;
    private final Set<Object> filters;

    /**
     * Constructs a Query for all rows from 0 to {@link Integer#MAX_VALUE}
     * without sorting and filtering.
     */
    public Query() {
        offset = 0;
        limit = Integer.MAX_VALUE;
        sortOrders = Collections.emptyList();
        filters = Collections.emptySet();
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
     * @param filters
     *            filtering for fetching
     */
    public Query(int offset, int limit, List<SortOrder<String>> sortOrders,
            Set<Object> filters) {
        this.offset = offset;
        this.limit = limit;
        this.sortOrders = sortOrders;
        this.filters = filters;
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
     * Gets the filters for items to fetch.
     * 
     * @return set of filters
     */
    public Set<Object> getFilters() {
        return filters;
    }
}
