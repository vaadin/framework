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

    private final List<ValidationStatus<?>> bindingValidationErrors;
    private final List<Result<?>> binderValidationErrors;

    /**
     * Constructs a new exception with validation {@code errors} list.
     *
     * @param bindingValidationErrors
     *            binding validation errors list
     * @param binderValidationErrors
     *            binder validation errors list
     */
    public ValidationException(
            List<ValidationStatus<?>> bindingValidationErrors,
            List<Result<?>> binderValidationErrors) {
        super("Validation has failed for some fields");
        this.bindingValidationErrors = Collections
                .unmodifiableList(bindingValidationErrors);
        this.binderValidationErrors = Collections
                .unmodifiableList(binderValidationErrors);
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
     * Returns a list of the binding level validation errors which caused the
     * exception, or an empty list if was caused by
     * {@link #getBeanValidationErrors() binder level validation errors}.
     *
     * @return binding validation errors list
     */
    public List<ValidationStatus<?>> getFieldValidationErrors() {
        return bindingValidationErrors;
    }

    /**
     * Returns a list of the binding level validation errors which caused the
     * exception, or an empty list if was caused by
     * {@link #getBeanValidationErrors() binder level validation errors}.
     *
     * @return binder validation errors list
     */
    public List<Result<?>> getBeanValidationErrors() {
        return binderValidationErrors;
    }
}
