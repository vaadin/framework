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

import java.util.Arrays;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.shared.util.SharedUtil;

public class Between implements Filter {

    private final Object propertyId;
    private final Comparable<?> startValue;
    private final Comparable<?> endValue;

    public Between(Object propertyId, Comparable<?> startValue,
            Comparable<?> endValue) {
        this.propertyId = propertyId;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    public Object getPropertyId() {
        return propertyId;
    }

    public Comparable<?> getStartValue() {
        return startValue;
    }

    public Comparable<?> getEndValue() {
        return endValue;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item)
            throws UnsupportedOperationException {
        Object value = item.getItemProperty(getPropertyId()).getValue();
        if (value instanceof Comparable) {
            Comparable comparable = (Comparable) value;
            return isAfterStartValue(comparable)
                    && isBeforeEndValue(comparable);
        } else if (value == null) {
            return getStartValue() == null && getEndValue() == null;
        }
        return false;
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return getPropertyId() != null && getPropertyId().equals(propertyId);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { getPropertyId(), getStartValue(),
                getEndValue() });
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
        final Between o = (Between) obj;

        // Checks the properties one by one
        boolean propertyIdEqual = SharedUtil.equals(getPropertyId(),
                o.getPropertyId());
        boolean startValueEqual = SharedUtil.equals(getStartValue(),
                o.getStartValue());
        boolean endValueEqual = SharedUtil.equals(getEndValue(),
                o.getEndValue());
        return propertyIdEqual && startValueEqual && endValueEqual;

    }

    private boolean isAfterStartValue(Comparable comparable) {
        return getStartValue() == null
                || comparable.compareTo(getStartValue()) >= 0;
    }

    private boolean isBeforeEndValue(Comparable comparable) {
        return getEndValue() == null
                || comparable.compareTo(getEndValue()) <= 0;
    }
}
