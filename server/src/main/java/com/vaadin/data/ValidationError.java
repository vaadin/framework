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

/**
 * Represents a validation error. An error contains a reference to a field whose
 * value is invalid and a message describing a validation failure.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 * @param <V>
 *            the field value type
 */
public class ValidationError<V> implements Serializable {

    private HasValue<V> field;
    private String message;

    /**
     * Creates a new instance of ValidationError with provided validated field
     * and error message.
     *
     * @param field
     *            the validated field
     * @param message
     *            the validation error message, not {@code null}
     */
    public ValidationError(HasValue<V> field, String message) {
        Objects.requireNonNull(message, "message cannot be null");
        this.field = field;
        this.message = message;
    }

    /**
     * Returns a reference to the validated field.
     *
     * @return the validated field
     */
    public HasValue<V> getField() {
        return field;
    }

    /**
     * Returns a validation error message.
     *
     * @return the validation error message
     */
    public String getMessage() {
        return message;
    }
}
