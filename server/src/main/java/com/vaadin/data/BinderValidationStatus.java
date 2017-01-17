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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vaadin.data.Binder.BindingBuilder;

/**
 * Binder validation status change. Represents the outcome of binder level
 * validation. Has information about the validation results for the
 * {@link BindingBuilder#withValidator(Validator) field level} and
 * {@link Binder#withValidator(Validator) binder level} validation.
 * <p>
 * Note: if there are any field level validation errors, the bean level
 * validation is not run.
 * <p>
 * Use {@link Binder#setValidationStatusHandler(BinderValidationStatusHandler)} to handle
 * form level validation status changes.
 *
 * @author Vaadin Ltd
 *
 * @param <BEAN>
 *            the bean type of the binder
 *
 * @see BinderValidationStatusHandler
 * @see Binder#setValidationStatusHandler(BinderValidationStatusHandler)
 * @see Binder#validate()
 * @see BindingValidationStatus
 *
 * @since 8.0
 */
public class BinderValidationStatus<BEAN> implements Serializable {

    private final Binder<BEAN> binder;
    private final List<BindingValidationStatus<?>> bindingStatuses;
    private final List<ValidationResult> binderStatuses;

    /**
     * Convenience method for creating a unresolved validation status for the
     * given binder.
     * <p>
     * In practice this status means that the values might not be valid, but
     * validation errors should be hidden.
     *
     * @param source
     *            the source binder
     * @return a unresolved validation status
     * @param <BEAN>
     *            the bean type of the binder
     */
    public static <BEAN> BinderValidationStatus<BEAN> createUnresolvedStatus(
            Binder<BEAN> source) {
        return new BinderValidationStatus<>(source,
                source.getBindings().stream().map(
                        b -> BindingValidationStatus.createUnresolvedStatus(b))
                        .collect(Collectors.toList()),
                Collections.emptyList());
    }

    /**
     * Creates a new binder validation status for the given binder and
     * validation results.
     *
     * @param source
     *            the source binder
     * @param bindingStatuses
     *            the validation results for the fields
     * @param binderStatuses
     *            the validation results for binder level validation
     */
    public BinderValidationStatus(Binder<BEAN> source,
            List<BindingValidationStatus<?>> bindingStatuses,
            List<ValidationResult> binderStatuses) {
        Objects.requireNonNull(binderStatuses,
                "binding statuses cannot be null");
        Objects.requireNonNull(binderStatuses,
                "binder statuses cannot be null");
        this.binder = source;
        this.bindingStatuses = Collections.unmodifiableList(bindingStatuses);
        this.binderStatuses = Collections.unmodifiableList(binderStatuses);
    }

    /**
     * Gets whether validation for the binder passed or not.
     *
     * @return {@code true} if validation has passed, {@code false} if not
     */
    public boolean isOk() {
        return !hasErrors();
    }

    /**
     * Gets whether the validation for the binder failed or not.
     *
     * @return {@code true} if validation failed, {@code false} if validation
     *         passed
     */
    public boolean hasErrors() {
        return binderStatuses.stream().filter(ValidationResult::isError)
                .findAny().isPresent()
                || bindingStatuses.stream()
                        .filter(BindingValidationStatus::isError).findAny()
                        .isPresent();
    }

    /**
     * Gets the source binder of the status.
     *
     * @return the source binder
     */
    public Binder<BEAN> getBinder() {
        return binder;
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
     * Gets the field level validation statuses.
     * <p>
     * The field level validtors have been added with
     * {@link BindingBuilder#withValidator(Validator)}.
     *
     * @return the field validation statuses
     */
    public List<BindingValidationStatus<?>> getFieldValidationStatuses() {
        return bindingStatuses;
    }

    /**
     * Gets the bean level validation results.
     *
     * @return the bean level validation results
     */
    public List<ValidationResult> getBeanValidationResults() {
        return binderStatuses;
    }

    /**
     * Gets the failed field level validation statuses.
     * <p>
     * The field level validtors have been added with
     * {@link BindingBuilder#withValidator(Validator)}.
     *
     * @return a list of failed field level validation statuses
     */
    public List<BindingValidationStatus<?>> getFieldValidationErrors() {
        return bindingStatuses.stream().filter(BindingValidationStatus::isError)
                .collect(Collectors.toList());
    }

    /**
     * Gets the failed bean level validation results.
     *
     * @return a list of failed bean level validation results
     */
    public List<ValidationResult> getBeanValidationErrors() {
        return binderStatuses.stream().filter(ValidationResult::isError)
                .collect(Collectors.toList());
    }
}
