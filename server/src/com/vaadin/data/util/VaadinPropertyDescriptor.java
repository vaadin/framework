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
package com.vaadin.data.util;

import java.io.Serializable;

import com.vaadin.data.Property;

/**
 * Property descriptor that can create a property instance for a bean.
 * 
 * Used by {@link BeanItem} and {@link AbstractBeanContainer} to keep track of
 * the set of properties of items.
 * 
 * @param <BT>
 *            bean type
 * 
 * @since 6.6
 */
public interface VaadinPropertyDescriptor<BT> extends Serializable {
    /**
     * Returns the name of the property.
     * 
     * @return
     */
    public String getName();

    /**
     * Returns the type of the property.
     * 
     * @return Class<?>
     */
    public Class<?> getPropertyType();

    /**
     * Creates a new {@link Property} instance for this property for a bean.
     * 
     * @param bean
     * @return
     */
    public Property<?> createProperty(BT bean);
}
