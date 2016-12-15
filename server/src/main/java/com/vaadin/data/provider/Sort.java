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
 * Helper class for constructing SortOrders.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public abstract class Sort implements Serializable {

    /**
     * SortBuilder is a helper class with fluent API for constructing sort order
     * lists. When the sort order is ready to be passed on, calling
     * {@link #build()} will create the list of sort orders
     *
     * @param <S>
     *            sort order data type
     *
     * @see Sort
     * @see Sort#asc(Object)
     * @see Sort#desc(Object)
     * @see #build()
     */
    public static class SortBuilder<S> implements Serializable {
        private List<SortOrder<S>> sortOrder = new ArrayList<>();

        /**
         * Constructs an empty SortBuilder.
         */
        protected SortBuilder() {
        }

        /**
         * Appends sorting with ascending sort direction.
         *
         * @param by
         *            the object to sort by
         * @return this sort builder
         */
        public SortBuilder<S> thenAsc(S by) {
            return append(by, SortDirection.ASCENDING);
        }

        /**
         * Appends sorting with descending sort direction.
         *
         * @param by
         *            the object to sort by
         * @return this sort builder
         */
        public SortBuilder<S> thenDesc(S by) {
            return append(by, SortDirection.DESCENDING);
        }

        /**
         * Appends sorting with given sort direction.
         *
         * @param by
         *            the object to sort by
         * @param direction
         *            the sort direction
         *
         * @return this sort builder
         */
        protected SortBuilder<S> append(S by, SortDirection direction) {
            sortOrder.add(new SortOrder<>(by, direction));
            return this;
        }

        /**
         * Returns an unmodifiable list of the current sort order in this sort
         * builder.
         *
         * @return the unmodifiable sort order list
         */
        public List<SortOrder<S>> build() {
            return Collections.unmodifiableList(sortOrder);
        }
    }

    /**
     * Creates a new sort builder with given sorting using ascending sort
     * direction.
     *
     * @param by
     *            the object to sort by
     * @param <S>
     *            sort order data type
     *
     * @return the sort builder
     */
    public static <S> SortBuilder<S> asc(S by) {
        return new SortBuilder<S>().thenAsc(by);
    }

    /**
     * Creates a new sort builder with given sorting using descending sort
     * direction.
     *
     * @param by
     *            the object to sort by
     * @param <S>
     *            sort order data type
     *
     * @return the sort builder
     */
    public static <S> SortBuilder<S> desc(S by) {
        return new SortBuilder<S>().thenDesc(by);
    }
}
