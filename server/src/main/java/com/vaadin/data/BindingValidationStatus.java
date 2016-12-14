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
import com.vaadin.data.Binder.BindingBuilder;

/**
 * Represents the status of field validation. Status can be {@code Status.OK},
 * {@code Status.ERROR} or {@code Status.RESET}. Status OK and ERROR are always
 * associated with a ValidationResult {@link #getResult}.
 * <p>
 * Use
 * {@link BindingBuilder#withValidationStatusHandler(BindingValidationStatusHandler)}
 * to register a handler for field level validation status changes.
 *
 * @author Vaadin Ltd
 *
 * @param <TARGET>
 *            the target data type of the binding for which the validation
 *            status changed, matches the field type unless a converter has been
 *            set
 *
 * @see BindingBuilder#withValidationStatusHandler(BindingValidationStatusHandler)
 * @see Binding#validate()
 * @see BindingValidationStatusHandler
 * @see BinderValidationStatus
 *
 * @since 8.0
 */
public class BindingValidationStatus<TARGET> implements Serializable {

    /**
     * Status of the validation.
     * <p>
     * The status is the part of {@link BindingValidationStatus} which indicates
     * whether the validation failed or not, or whether it is in unresolved
     * state (e.g. after clear or reset).
     */
    public enum Status {
        /** Validation passed. */
        OK,
        /** Validation failed. */
        ERROR,
        /**
         * Unresolved status, e.g field has not yet been validated because value
         * was cleared.
         * <p>
         * In practice this status means that the value might be invalid, but
         * validation errors should be hidden.
         */
        UNRESOLVED;
    }

    private final Status status;
    private final ValidationResult result;
    private final Binding<?, TARGET> binding;

    /**
     * Convenience method for creating a {@link Status#UNRESOLVED} validation
     * status for the given binding.
     *
     * @param source
     *            the source binding
     * @return unresolved validation status
     * @param <TARGET>
     *            the target data type of the binding for which the validation
     *            status was reset
     */
    public static <TARGET> BindingValidationStatus<TARGET> createUnresolvedStatus(
            Binding<?, TARGET> source) {
        return new BindingValidationStatus<>(source, Status.UNRESOLVED, null);
    }

    /**
     * Creates a new validation status for the given binding and validation
     * result.
     *
     * @param source
     *            the source binding
     * @param result
     *            the result of the validation
     */
    public BindingValidationStatus(Binding<?, TARGET> source,
            ValidationResult result) {
        this(source, result.isError() ? Status.ERROR : Status.OK, result);
    }

    /**
     * Creates a new status change event.
     * <p>
     * The {@code message} must be {@code null} if the {@code status} is
     * {@link Status#OK}.
     *
     * @param source
     *            field whose status has changed, not {@code null}
     * @param status
     *            updated status value, not {@code null}
     * @param result
     *            the related result, may be {@code null}
     */
    public BindingValidationStatus(Binding<?, TARGET> source, Status status,
            ValidationResult result) {
        Objects.requireNonNull(source, "Event source may not be null");
        Objects.requireNonNull(status, "Status may not be null");
        if (Objects.equals(status, Status.OK) && result.isError()
                || Objects.equals(status, Status.ERROR) && !result.isError()
                || Objects.equals(status, Status.UNRESOLVED)
                        && result != null) {
            throw new IllegalStateException(
                    "Invalid validation status " + status + " for given result "
                            + (result == null ? "null" : result.toString()));
        }
        binding = source;
        this.status = status;
        this.result = result;
    }

    /**
     * Gets status of the validation.
     *
     * @return status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Gets whether the validation failed or not.
     *
     * @return {@code true} if validation failed, {@code false} if validation
     *         passed
     */
    public boolean isError() {
        return status == Status.ERROR;
    }

    /**
     * Gets error validation message if status is {@link Status#ERROR}.
     *
     * @return an optional validation error status or an empty optional if
     *         status is not an error
     */
    public Optional<String> getMessage() {
        return Optional.ofNullable(result).filter(ValidationResult::isError)
                .map(ValidationResult::getErrorMessage);
    }

    /**
     * Gets the validation result if status is either {@link Status#OK} or
     * {@link Status#ERROR} or an empty optional if status is
     * {@link Status#UNRESOLVED}.
     *
     * @return the validation result
     */
    public Optional<ValidationResult> getResult() {
        return Optional.ofNullable(result);
    }

    /**
     * Gets the source binding of the validation status.
     *
     * @return the source binding
     */
    public Binding<?, TARGET> getBinding() {
        return binding;
    }

    /**
     * Gets the bound field for this status.
     *
     * @return the field
     */
    public HasValue<?> getField() {
        return getBinding().getField();
    }
}
