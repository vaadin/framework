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
import com.vaadin.data.Property;

/**
 * Simple container filter checking whether an item property value is null.
 * 
 * This filter also directly supports in-memory filtering.
 * 
 * @since 6.6
 */
public final class IsNull implements Filter {

    private final Object propertyId;

    /**
     * Constructor for a filter that compares the value of an item property with
     * null.
     * 
     * For in-memory filtering, a simple == check is performed. For other
     * containers, the comparison implementation is container dependent but
     * should correspond to the in-memory null check.
     * 
     * @param propertyId
     *            the identifier (not null) of the property whose value to check
     */
    public IsNull(Object propertyId) {
        this.propertyId = propertyId;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException {
        final Property<?> p = item.getItemProperty(getPropertyId());
        if (null == p) {
            return false;
        }
        return null == p.getValue();
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return getPropertyId().equals(propertyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        // Only objects of the same class can be equal
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        final IsNull o = (IsNull) obj;

        // Checks the properties one by one
        return (null != getPropertyId()) ? getPropertyId().equals(
                o.getPropertyId()) : null == o.getPropertyId();
    }

    @Override
    public int hashCode() {
        return (null != getPropertyId() ? getPropertyId().hashCode() : 0);
    }

    /**
     * Returns the property id of the property tested by the filter, not null
     * for valid filters.
     * 
     * @return property id (not null)
     */
    public Object getPropertyId() {
        return propertyId;
    }

}
