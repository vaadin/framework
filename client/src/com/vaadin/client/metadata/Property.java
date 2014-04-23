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
package com.vaadin.client.metadata;

import com.vaadin.shared.annotations.DelegateToWidget;

public class Property {
    private final Type bean;
    private final String name;

    public Property(Type bean, String name) {
        this.bean = bean;
        this.name = name;
    }

    public Object getValue(Object bean) throws NoDataException {
        return TypeDataStore.getValue(this, bean);
    }

    public void setValue(Object bean, Object value) throws NoDataException {
        TypeDataStore.setValue(this, bean, value);
    }

    public String getDelegateToWidgetMethodName() {
        String value = TypeDataStore.getDelegateToWidget(this);
        if (value == null) {
            return null;
        } else {
            return DelegateToWidget.Helper.getDelegateTarget(getName(), value);
        }
    }

    public Type getType() throws NoDataException {
        return TypeDataStore.getType(this);
    }

    public Type getBeanType() {
        return bean;
    }

    /**
     * The unique signature used to identify this property. The structure of the
     * returned string may change without notice and should not be used for any
     * other purpose than identification. The signature is currently based on
     * the declaring type's signature and the property's name.
     * 
     * @return the unique signature of this property
     */
    public String getSignature() {
        return bean.getSignature() + "." + name;
    }

    /**
     * Gets the string that is internally used when looking up generated support
     * code for this method. This is the same as {@link #getSignature()}, but
     * without any type parameters.
     * 
     * @return the string to use for looking up generated support code
     * 
     * @since 7.2
     */
    public String getLookupKey() {
        return bean.getBaseTypeName() + "." + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Property) {
            Property other = (Property) obj;
            return getSignature().equals(other.getSignature());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getSignature();
    }

    /**
     * Gets the property name formatted for displaying in a user interface. This
     * returns a string where e.g. "camelCase" has been converted to
     * "Camel case".
     * 
     * @return the name of this property, formatted for humans to read
     */
    public String getDisplayName() {
        String camelCase = getName();
        StringBuilder b = new StringBuilder(camelCase.length());
        for (int i = 0; i < camelCase.length(); i++) {
            char charAt = camelCase.charAt(i);
            if (i == 0) {
                // First char always upper case
                b.append(Character.toUpperCase(charAt));
            } else if (Character.isUpperCase(charAt)) {
                b.append(' ');
                b.append(Character.toLowerCase(charAt));
            } else {
                b.append(charAt);
            }
        }
        return b.toString();
    }

}
