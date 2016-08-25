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
import java.util.Objects;
import java.util.Optional;

import com.vaadin.data.Binder.Binding;

/**
 * Represents a validation error.
 * <p>
 * A validation error is either connected to a field validator (
 * {@link #getField()} returns a non-empty optional) or to an item level
 * validator ({@link #getField()} returns an empty optional).
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <V>
 *            the value type
 */
public class ValidationError<V> implements Serializable {

    /**
     * This is either a {@link Binding} or a {@link Binder}.
     */
    private final Object source;
    private final String message;
    /**
     * This is either HasValue<V> value (in case of Binding) or bean (in case of
     * Binder).
     */
    private final V value;

    /**
     * Creates a new instance of ValidationError using the provided source
     * ({@link Binding} or {@link Binder}), value and error message.
     *
     * @param source
     *            the validated binding or the binder
     * @param value
     *            the invalid value
     * @param message
     *            the validation error message, not {@code null}
     */
    public ValidationError(Object source, V value, String message) {
        Objects.requireNonNull(message, "message cannot be null");
        this.source = source;
        this.message = message;
        this.value = value;
    }

    /**
     * Returns a reference to the validated field or an empty optional if the
     * validation was not related to a single field.
     *
     * @return the validated field or an empty optional
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Optional<HasValue<V>> getField() {
        if (source instanceof Binding) {
            return Optional.of(((Binding) source).getField());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a validation error message.
     *
     * @return the validation error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the invalid value.
     * <p>
     * This is either the field value (if the validator error comes from a field
     * binding) or the bean (for item validators).
     *
     * @return the source value
     */
    public V getValue() {
        return value;
    }

}
