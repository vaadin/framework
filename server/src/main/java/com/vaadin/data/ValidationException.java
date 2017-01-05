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
 * Indicates validation errors in a {@link Binder} when a field value is
 * validated.
 *
 * @see Binder#writeBean(Object)
 * @see Binder#writeBeanIfValid(Object)
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class ValidationException extends Exception {

    private final List<BindingValidationStatus<?>> fieldValidationErrors;
    private final List<ValidationResult> beanValidationErrors;

    /**
     * Constructs a new exception with validation {@code errors} list.
     *
     * @param fieldValidationErrors
     *            binding validation errors list
     * @param beanValidationErrors
     *            binder validation errors list
     */
    public ValidationException(
            List<BindingValidationStatus<?>> fieldValidationErrors,
            List<ValidationResult> beanValidationErrors) {
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
    public List<ValidationResult> getValidationErrors() {
        ArrayList<ValidationResult> errors = new ArrayList<>(
                getFieldValidationErrors().stream()
                        .map(s -> s.getResult().get())
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
    public List<BindingValidationStatus<?>> getFieldValidationErrors() {
        return fieldValidationErrors;
    }

    /**
     * Returns a list of the bean level validation errors which caused the
     * exception, or an empty list if the exception was caused by
     * {@link #getFieldValidationErrors() field level validation errors}.
     *
     * @return binder validation errors list
     */
    public List<ValidationResult> getBeanValidationErrors() {
        return beanValidationErrors;
    }
}
