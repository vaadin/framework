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

import com.vaadin.data.Property;

/**
 * A simple data object containing one typed value. This class is a
 * straightforward implementation of the the {@link com.vaadin.data.Property}
 * interface.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ObjectProperty<T> extends AbstractProperty<T> {

    /**
     * The value contained by the Property.
     */
    private T value;

    /**
     * Data type of the Property's value.
     */
    private final Class<T> type;

    /**
     * Creates a new instance of ObjectProperty with the given value. The type
     * of the property is automatically initialized to be the type of the given
     * value.
     * 
     * @param value
     *            the Initial value of the Property.
     */
    @SuppressWarnings("unchecked")
    // the cast is safe, because an object of type T has class Class<T>
    public ObjectProperty(T value) {
        this(value, (Class<T>) value.getClass());
    }

    /**
     * Creates a new instance of ObjectProperty with the given value and type.
     * 
     * Since Vaadin 7, only values of the correct type are accepted, and no
     * automatic conversions are performed.
     * 
     * @param value
     *            the Initial value of the Property.
     * @param type
     *            the type of the value. The value must be assignable to given
     *            type.
     */
    public ObjectProperty(T value, Class<T> type) {

        // Set the values
        this.type = type;
        setValue(value);
    }

    /**
     * Creates a new instance of ObjectProperty with the given value, type and
     * read-only mode status.
     * 
     * Since Vaadin 7, only the correct type of values is accepted, see
     * {@link #ObjectProperty(Object, Class)}.
     * 
     * @param value
     *            the Initial value of the property.
     * @param type
     *            the type of the value. <code>value</code> must be assignable
     *            to this type.
     * @param readOnly
     *            Sets the read-only mode.
     */
    public ObjectProperty(T value, Class<T> type, boolean readOnly) {
        this(value, type);
        setReadOnly(readOnly);
    }

    /**
     * Returns the type of the ObjectProperty. The methods <code>getValue</code>
     * and <code>setValue</code> must be compatible with this type: one must be
     * able to safely cast the value returned from <code>getValue</code> to the
     * given type and pass any variable assignable to this type as an argument
     * to <code>setValue</code>.
     * 
     * @return type of the Property
     */
    @Override
    public final Class<T> getType() {
        return type;
    }

    /**
     * Gets the value stored in the Property.
     * 
     * @return the value stored in the Property
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * Sets the value of the property.
     * 
     * Note that since Vaadin 7, no conversions are performed and the value must
     * be of the correct type.
     * 
     * @param newValue
     *            the New value of the property.
     * @throws <code>Property.ReadOnlyException</code> if the object is in
     *         read-only mode
     */
    @Override
    public void setValue(T newValue) throws Property.ReadOnlyException {

        // Checks the mode
        if (isReadOnly()) {
            throw new Property.ReadOnlyException();
        }

        this.value = newValue;

        fireValueChange();
    }
}
