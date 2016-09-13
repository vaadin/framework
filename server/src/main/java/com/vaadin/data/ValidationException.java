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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Indicates validation errors in a {@link Binder} when save is requested.
 *
 * @see Binder#save(Object)
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class ValidationException extends Exception {

    private final List<ValidationStatus<?>> fieldValidationErrors;
    private final List<Result<?>> beanValidationErrors;

    /**
     * Constructs a new exception with validation {@code errors} list.
     *
     * @param fieldValidationErrors
     *            binding validation errors list
     * @param beanValidationErrors
     *            binder validation errors list
     */
    public ValidationException(List<ValidationStatus<?>> fieldValidationErrors,
            List<Result<?>> beanValidationErrors) {
        super("Validation has failed for some fields");
        this.fieldValidationErrors = Collections
                .unmodifiableList(fieldValidationErrors);
        this.beanValidationErrors = Collections
                .unmodifiableList(beanValidationErrors);
    }

    /**
     * Gets both field and bean level validation errors.
     *
     * @return a list of all validation errors
     */
    public List<Result<?>> getValidationErrors() {
        ArrayList<Result<?>> errors = new ArrayList<>(getFieldValidationErrors()
                .stream().map(s -> s.getResult().get())
                .collect(Collectors.toList()));
        errors.addAll(getBeanValidationErrors());
        return errors;
    }

    /**
     * Returns a list of the field level validation errors which caused the
     * exception, or an empty list if the exception was caused by
     * {@link #getBeanValidationErrors() bean level validation errors}.
     *
     * @return binding validation errors list
     */
    public List<ValidationStatus<?>> getFieldValidationErrors() {
        return fieldValidationErrors;
    }

    /**
     * Returns a list of the bean level validation errors which caused the
     * exception, or an empty list if the exception was caused by
     * {@link #getBindingValidationErrors() binder level validation errors}.
     *
     * @return binder validation errors list
     */
    public List<Result<?>> getBeanValidationErrors() {
        return beanValidationErrors;
    }
}
