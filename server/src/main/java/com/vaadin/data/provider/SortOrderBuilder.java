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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.shared.data.sort.SortDirection;

/**
 * Base class for helper classes with fluent API for constructing sort order
 * lists. When the sort order is ready to be passed on, calling {@link #build()}
 * will create the list of sort orders.
 *
 * @param <T>
 *            the sort order type
 * @param <V>
 *            the sorting type
 *
 * @see SortOrderBuilder#thenAsc(Object)
 * @see SortOrderBuilder#thenDesc(Object)
 * @see #build()
 * @since 8.0
 */
public abstract class SortOrderBuilder<T extends SortOrder<V>, V>
        implements Serializable {

    private final List<T> sortOrders = new ArrayList<>();

    /**
     * Appends sorting with ascending sort direction.
     *
     * @param by
     *            the object to sort by
     * @return this sort builder
     */
    public SortOrderBuilder<T, V> thenAsc(V by) {
        return append(createSortOrder(by, SortDirection.ASCENDING));
    }

    /**
     * Appends sorting with descending sort direction.
     *
     * @param by
     *            the object to sort by
     * @return this sort builder
     */
    public SortOrderBuilder<T, V> thenDesc(V by) {
        return append(createSortOrder(by, SortDirection.DESCENDING));
    }

    /**
     * Returns an unmodifiable copy of the list of current sort orders in this
     * sort builder.
     *
     * @return an unmodifiable sort order list
     */
    public final List<T> build() {
        return Collections.unmodifiableList(new ArrayList<>(sortOrders));
    }

    /**
     * Creates a sort order object with the given parameters.
     *
     * @param by
     *            the object to sort by
     * @param direction
     *            the sort direction
     *
     * @return the sort order object
     */
    protected abstract T createSortOrder(V by, SortDirection direction);

    /**
     * Append a sort order to {@code sortOrders}.
     *
     * @param sortOrder
     *            the sort order to append
     * @return this
     */
    private final SortOrderBuilder<T, V> append(T sortOrder) {
        sortOrders.add(sortOrder);
        return this;
    }
}
