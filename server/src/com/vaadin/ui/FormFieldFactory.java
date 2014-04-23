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
package com.vaadin.ui;

import java.io.Serializable;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;

/**
 * Factory interface for creating new Field-instances based on {@link Item},
 * property id and uiContext (the component responsible for displaying fields).
 * Currently this interface is used by {@link Form}, but might later be used by
 * some other components for {@link Field} generation.
 * 
 * <p>
 * 
 * @author Vaadin Ltd.
 * @since 6.0
 * @see TableFieldFactory
 * @deprecated As of 7.0, use {@link FieldGroup} instead of {@link Form} for
 *             more flexibility.
 */
@Deprecated
public interface FormFieldFactory extends Serializable {
    /**
     * Creates a field based on the item, property id and the component (most
     * commonly {@link Form}) where the Field will be presented.
     * 
     * @param item
     *            the item where the property belongs to.
     * @param propertyId
     *            the Id of the property.
     * @param uiContext
     *            the component where the field is presented, most commonly this
     *            is {@link Form}. uiContext will not necessary be the parent
     *            component of the field, but the one that is responsible for
     *            creating it.
     * @return Field the field suitable for editing the specified data.
     */
    Field<?> createField(Item item, Object propertyId, Component uiContext);
}
