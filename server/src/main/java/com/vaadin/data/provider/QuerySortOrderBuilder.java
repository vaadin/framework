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

import com.vaadin.shared.data.sort.SortDirection;

/**
 * Helper classes with fluent API for constructing {@link QuerySortOrder} lists.
 * When the sort order is ready to be passed on, calling {@link #build()} will
 * create the list of sort orders.
 *
 * @see QuerySortOrder
 * @see #thenDesc(String)
 * @see #thenDesc(String)
 * @see #build()
 * @since 8.0
 */
public class QuerySortOrderBuilder
        extends SortOrderBuilder<QuerySortOrder, String> {

    @Override
    public QuerySortOrderBuilder thenAsc(String by) {
        return (QuerySortOrderBuilder) super.thenAsc(by);
    }

    @Override
    public QuerySortOrderBuilder thenDesc(String by) {
        return (QuerySortOrderBuilder) super.thenDesc(by);
    }

    @Override
    protected QuerySortOrder createSortOrder(String by,
            SortDirection direction) {
        return new QuerySortOrder(by, direction);
    }
}
