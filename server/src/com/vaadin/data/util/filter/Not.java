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
 * Negating filter that accepts the items rejected by another filter.
 * 
 * This filter directly supports in-memory filtering when the negated filter
 * does so.
 * 
 * @since 6.6
 */
public final class Not implements Filter {
    private final Filter filter;

    /**
     * Constructs a filter that negates a filter.
     * 
     * @param filter
     *            {@link Filter} to negate, not-null
     */
    public Not(Filter filter) {
        this.filter = filter;
    }

    /**
     * Returns the negated filter.
     * 
     * @return Filter
     */
    public Filter getFilter() {
        return filter;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException {
        return !filter.passesFilter(itemId, item);
    }

    /**
     * Returns true if a change in the named property may affect the filtering
     * result. Return value is the same as {@link #appliesToProperty(Object)}
     * for the negated filter.
     * 
     * @return boolean
     */
    @Override
    public boolean appliesToProperty(Object propertyId) {
        return filter.appliesToProperty(propertyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        return filter.equals(((Not) obj).getFilter());
    }

    @Override
    public int hashCode() {
        return filter.hashCode();
    }

}
