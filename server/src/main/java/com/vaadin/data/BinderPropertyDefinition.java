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
package com.vaadin.data;

import java.io.Serializable;
import java.util.Optional;

import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.server.Setter;

/**
 * A property from a {@link BinderPropertySet}.
 *
 * @author Vaadin Ltd
 * @since
 *
 * @param <T>
 *            the type of the binder property set
 * @param <V>
 *            the property type
 */
public interface BinderPropertyDefinition<T, V> extends Serializable {
    /**
     * Gets the value provider that is used for finding the value of this
     * property for a bean.
     *
     * @return the getter, not <code>null</code>
     */
    public ValueProvider<T, V> getGetter();

    /**
     * Gets an optional setter for storing a property value in a bean.
     *
     * @return the setter, or an empty optional if this property is read-only
     */
    public Optional<Setter<T, V>> getSetter();

    /**
     * Gets the type of this property.
     *
     * @return the property type. not <code>null</code>
     */
    public Class<V> getType();

    /**
     * Hook for modifying a binding before it is being bound to this property.
     * This method can return the provided {@link BindingBuilder} as-is if no
     * modifications are necessary.
     *
     * @param originalBuilder
     *            the original binding builder that is being bound, not
     *            <code>null</code>
     * @return the binding builder to use for creating the binding, not
     *         <code>null</code>
     */
    public BindingBuilder<T, V> beforeBind(
            BindingBuilder<T, V> originalBuilder);

    /**
     * Gets the name of this property.
     *
     * @return the property name, not <code>null</code>
     */
    public String getName();

    /**
     * Gets the {@link BinderPropertySet} that this property belongs to.
     *
     * @return the binder property set, not <code>null</code>
     */
    public BinderPropertySet<T> getPropertySet();
}
