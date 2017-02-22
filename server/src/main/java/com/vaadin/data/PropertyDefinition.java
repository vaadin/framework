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

import com.vaadin.server.Setter;

/**
 * A property from a {@link PropertySet}.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <T>
 *            the type of the property set
 * @param <V>
 *            the property type
 */
public interface PropertyDefinition<T, V> extends Serializable {
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
     * Gets the name of this property.
     *
     * @return the property name, not <code>null</code>
     */
    public String getName();

    /**
     * Gets the human readable caption to show for this property.
     *
     * @return the caption to show, not <code>null</code>
     */
    public String getCaption();

    /**
     * Gets the {@link PropertySet} that this property belongs to.
     *
     * @return the property set, not <code>null</code>
     */
    public PropertySet<T> getPropertySet();
}
