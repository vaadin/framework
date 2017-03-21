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
package com.vaadin.v7.data.util.filter;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;

/**
 * A compound {@link Filter} that accepts an item if all of its filters accept
 * the item.
 *
 * If no filters are given, the filter should accept all items.
 *
 * This filter also directly supports in-memory filtering when all sub-filters
 * do so.
 *
 * @see Or
 *
 * @since 6.6
 *
 * @deprecated As of 8.0, the whole filtering feature is integrated into {@link DataProvider}.
 * For in-memory case ({@link ListDataProvider}), use predicates as filters. For back-end DataProviders,
 * filters are specific to the implementation.
 */
@Deprecated
public final class And extends AbstractJunctionFilter {

    /**
     *
     * @param filters
     *            filters of which the And filter will be composed
     */
    public And(Filter... filters) {
        super(filters);
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedFilterException {
        for (Filter filter : getFilters()) {
            if (!filter.passesFilter(itemId, item)) {
                return false;
            }
        }
        return true;
    }

}
