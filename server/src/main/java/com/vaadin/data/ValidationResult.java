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

import com.vaadin.shared.ui.ErrorLevel;

/**
 * Represents the result of a validation. A result may be either successful or
 * contain an error message in case of a failure.
 * <p>
 * ValidationResult instances are created using the factory methods
 * {@link #ok()} and {@link #error(String)}, denoting success and failure
 * respectively.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public interface ValidationResult extends Serializable {

    class SimpleValidationResult implements ValidationResult {

        private final String error;
        private final ErrorLevel errorLevel;

        SimpleValidationResult(String error, ErrorLevel errorLevel) {
            if (error != null && errorLevel == null) {
                throw new IllegalStateException("ValidationResult has an "
                        + "error message, but no ErrorLevel is provided.");
            }
            this.error = error;
            this.errorLevel = errorLevel;
        }

        @Override
        public String getErrorMessage() {
            if (!getErrorLevel().isPresent()) {
                throw new IllegalStateException("The result is not an error. "
                        + "It cannot contain error message");
            } else {
                return error != null ? error : "";
            }
        }

        public Optional<ErrorLevel> getErrorLevel() {
            return Optional.ofNullable(errorLevel);
        }

        @Override
        public boolean isError() {
            // Info and Warning are not error by default.
            return errorLevel != null && errorLevel != ErrorLevel.INFO
                    && errorLevel != ErrorLevel.WARNING;
        }

    }

    /**
     * Returns the result message.
     * <p>
     * Throws an {@link IllegalStateException} if the result represents success.
     *
     * @return the error message
     * @throws IllegalStateException
     *             if the result represents success
     */
    String getErrorMessage();

    /**
     * Returns optional error level for this validation result.
     *
     * @return the error level if present
     * @throws IllegalStateException
     *             if the result represents success
     */
    Optional<ErrorLevel> getErrorLevel();

    /**
     * Checks if the result denotes an error.
     *
     * @return <code>true</code> if the result denotes an error,
     *         <code>false</code> otherwise
     */
    boolean isError();

    /**
     * Returns a successful result.
     *
     * @return the successful result
     */
    public static ValidationResult ok() {
        return new SimpleValidationResult(null, null);
    }

    /**
     * Creates the validation result which represent an error with the given
     * {@code errorMessage}.
     *
     * @param errorMessage
     *            error message, not {@code null}
     * @return validation result which represent an error with the given
     *         {@code errorMessage}
     * @throws NullPointerException
     *             if {@code errorMessage} is null
     */
    public static ValidationResult error(String errorMessage) {
        Objects.requireNonNull(errorMessage);
        return failure(errorMessage, ErrorLevel.ERROR);
    }

    /**
     * Creates the validation result which represent a failure with the given
     * {@code errorMessage} and {@code errorLevel}. Failures with
     * {@link ErrorLevel} of {@code INFO} or {@code WARNING} are not considered
     * blocking failures.
     *
     * @param errorMessage
     *            error message, not {@code null}
     * @param errorLevel
     *            error level, not {@code null}
     * @return validation result which represent a failure with the given
     *         {@code errorMessage} and {@code errorLevel}
     * @throws NullPointerException
     *             if {@code errorMessage} is null
     */
    public static ValidationResult failure(String errorMessage,
            ErrorLevel errorLevel) {
        Objects.requireNonNull(errorMessage);
        Objects.requireNonNull(errorLevel);
        return new SimpleValidationResult(errorMessage, errorLevel);
    }
}
