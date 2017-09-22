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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Immutable hierarchical query object used to request data from a backend.
 * Contains the parent node, index limits, sorting and filtering information.
 *
 * @param <T>
 *            bean type
 * @param <F>
 *            filter type
 *
 * @since 8.1
 */
public class HierarchicalQuery<T, F> extends Query<T, F> {

    private final T parent;

    /**
     * Constructs a new hierarchical query object with given filter and parent
     * node.
     *
     * @param filter
     *            filtering for fetching; can be <code>null</code>
     * @param parent
     *            the hierarchical parent object, <code>null</code>
     *            corresponding to the root node
     */
    public HierarchicalQuery(F filter, T parent) {
        super(filter);
        this.parent = parent;
    }

    /**
     * Constructs a new hierarchical query object with given offset, limit,
     * sorting and filtering.
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
     *            filtering for fetching; can be <code>null</code>
     * @param parent
     *            the hierarchical parent object, <code>null</code>
     *            corresponding to the root node
     */
    public HierarchicalQuery(int offset, int limit,
            List<QuerySortOrder> sortOrders, Comparator<T> inMemorySorting,
            F filter, T parent) {
        super(offset, limit, sortOrders, inMemorySorting, filter);
        this.parent = parent;
    }

    /**
     * Get the hierarchical parent object, where <code>null</code> corresponds
     * to the root node.
     *
     * @return the hierarchical parent object
     */
    public T getParent() {
        return parent;
    }

    /**
     * Get an Optional of the hierarchical parent object.
     *
     * @see #getParent()
     * @return the result of {@link #getParent()} wrapped by an Optional
     */
    public Optional<T> getParentOptional() {
        return Optional.ofNullable(parent);
    }
}
