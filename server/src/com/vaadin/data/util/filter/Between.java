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

public class Between implements Filter {

    private final Object propertyId;
    private final Comparable startValue;
    private final Comparable endValue;

    public Between(Object propertyId, Comparable startValue, Comparable endValue) {
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
            Comparable cval = (Comparable) value;
            return cval.compareTo(getStartValue()) >= 0
                    && cval.compareTo(getEndValue()) <= 0;
        }
        return false;
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return getPropertyId() != null && getPropertyId().equals(propertyId);
    }

    @Override
    public int hashCode() {
        return getPropertyId().hashCode() + getStartValue().hashCode()
                + getEndValue().hashCode();
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
        boolean propertyIdEqual = (null != getPropertyId()) ? getPropertyId()
                .equals(o.getPropertyId()) : null == o.getPropertyId();
        boolean startValueEqual = (null != getStartValue()) ? getStartValue()
                .equals(o.getStartValue()) : null == o.getStartValue();
        boolean endValueEqual = (null != getEndValue()) ? getEndValue().equals(
                o.getEndValue()) : null == o.getEndValue();
        return propertyIdEqual && startValueEqual && endValueEqual;

    }
}
