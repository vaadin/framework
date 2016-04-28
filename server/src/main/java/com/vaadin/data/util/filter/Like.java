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

public class Like implements Filter {
    private final Object propertyId;
    private final String value;
    private boolean caseSensitive;

    public Like(Object propertyId, String value) {
        this(propertyId, value, true);
    }

    public Like(Object propertyId, String value, boolean caseSensitive) {
        this.propertyId = propertyId;
        this.value = value;
        setCaseSensitive(caseSensitive);
    }

    public Object getPropertyId() {
        return propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException {
        if (!item.getItemProperty(getPropertyId()).getType()
                .isAssignableFrom(String.class)) {
            // We can only handle strings
            return false;
        }
        String colValue = (String) item.getItemProperty(getPropertyId())
                .getValue();

        // Fix issue #10167 - avoid NPE and drop null property values
        if (colValue == null) {
            return false;
        }

        String pattern = getValue().replace("%", ".*");
        if (isCaseSensitive()) {
            return colValue.matches(pattern);
        }
        return colValue.toUpperCase().matches(pattern.toUpperCase());
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return getPropertyId() != null && getPropertyId().equals(propertyId);
    }

    @Override
    public int hashCode() {
        return getPropertyId().hashCode() + getValue().hashCode();
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
        final Like o = (Like) obj;

        // Checks the properties one by one
        boolean propertyIdEqual = (null != getPropertyId()) ? getPropertyId()
                .equals(o.getPropertyId()) : null == o.getPropertyId();
        boolean valueEqual = (null != getValue()) ? getValue().equals(
                o.getValue()) : null == o.getValue();
        return propertyIdEqual && valueEqual;
    }
}
