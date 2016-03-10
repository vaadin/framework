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
package com.vaadin.data.util.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * A compound {@link Filter} that accepts an item if any of its filters accept
 * the item.
 * 
 * If no filters are given, the filter should reject all items.
 * 
 * This filter also directly supports in-memory filtering when all sub-filters
 * do so.
 * 
 * @see And
 * 
 * @since 6.6
 */
public final class Or extends AbstractJunctionFilter {

    /**
     * 
     * @param filters
     *            filters of which the Or filter will be composed
     */
    public Or(Filter... filters) {
        super(filters);
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedFilterException {
        for (Filter filter : getFilters()) {
            if (filter.passesFilter(itemId, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if a change in the named property may affect the filtering
     * result. If some of the sub-filters are not in-memory filters, true is
     * returned.
     * 
     * By default, all sub-filters are iterated to check if any of them applies.
     * If there are no sub-filters, true is returned as an empty Or rejects all
     * items.
     */
    @Override
    public boolean appliesToProperty(Object propertyId) {
        if (getFilters().isEmpty()) {
            // empty Or filters out everything
            return true;
        } else {
            return super.appliesToProperty(propertyId);
        }
    }

}
