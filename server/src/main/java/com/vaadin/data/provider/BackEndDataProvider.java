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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A data provider that lazy loads items from a back end.
 *
 * @param <T>
 *            data provider data type
 * @param <F>
 *            data provider filter type
 * @since 8.0
 */
public interface BackEndDataProvider<T, F> extends DataProvider<T, F> {

    /**
     * Sets a list of sort orders to use as the default sorting for this data
     * provider. This overrides the sorting set by any other method that
     * manipulates the default sorting of this data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @see #setSortOrder(QuerySortOrder)
     *
     * @param sortOrders
     *            a list of sort orders to set, not <code>null</code>
     */
    void setSortOrders(List<QuerySortOrder> sortOrders);

    /**
     * Sets the sort order to use, given a {@link QuerySortOrderBuilder}.
     * Shorthand for {@code setSortOrders(builder.build())}.
     *
     * @see QuerySortOrderBuilder
     *
     * @param builder
     *            the sort builder to retrieve the sort order from
     * @throws NullPointerException
     *             if builder is null
     */
    default void setSortOrders(QuerySortOrderBuilder builder) {
        Objects.requireNonNull("Sort builder cannot be null.");
        setSortOrders(builder.build());
    }

    /**
     * Sets a single sort order to use as the default sorting for this data
     * provider. This overrides the sorting set by any other method that
     * manipulates the default sorting of this data provider.
     * <p>
     * The default sorting is used if the query defines no sorting. The default
     * sorting is also used to determine the ordering of items that are
     * considered equal by the sorting defined in the query.
     *
     * @see #setSortOrders(List)
     *
     * @param sortOrder
     *            a sort order to set, or <code>null</code> to clear any
     *            previously set sort orders
     */
    default void setSortOrder(QuerySortOrder sortOrder) {
        if (sortOrder == null) {
            setSortOrders(Collections.emptyList());
        } else {
            setSortOrders(Collections.singletonList(sortOrder));
        }
    }

    @Override
    default boolean isInMemory() {
        return false;
    }
}
