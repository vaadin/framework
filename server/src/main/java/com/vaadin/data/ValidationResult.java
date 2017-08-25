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

        private final Severity severity; 
        private final String error;

        SimpleValidationResult(String error) {
            this.severity = Severity.ERROR;
            this.error = error;
        }
        
        SimpleValidationResult(String error, Severity severity) {
            this.severity = severity;
            this.error = error;
        }

        @Override
        public String getErrorMessage() {
            if (error == null) {
                throw new IllegalStateException("The result is not an error. "
                        + "It cannot contain error message");
            } else {
                return error;
            }
        }

        @Override
        public boolean isError() {
            return error != null && Severity.ERROR.equals(severity);
        }

        @Override
        public boolean isInfo() {
            return Severity.INFO.equals(severity);
        }
        
        @Override
        public boolean isWarn() {
            return Severity.WARN.equals(severity);
        }
        
        @Override
        public Severity getSeverity() {
            return severity;
        }

    }
    
    public default Severity getSeverity() {
      return Severity.ERROR;
    }
    
    /**
     * Checks if the result denotes an info.
     *
     * @return <code>true</code> if the result denotes an info,
     *         <code>false</code> otherwise
     * @since 8.2
     */
    public default boolean isInfo() {
      return false;
    }

    /**
     * Checks if the result denotes a warning.
     *
     * @return <code>true</code> if the result denotes an warning,
     *         <code>false</code> otherwise
     * @since 8.2
     */
    public default boolean isWarn() {
      return false;
    }

    /**
     * Returns the result message.
     * <p>
     * Throws an {@link IllegalStateException} if the result represents success.
     *
     * @return the error message
     * @throws IllegalStateException
     *             if the result represents success
     * @since 8.2
     */
    String getErrorMessage();

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
        return new SimpleValidationResult(null);
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
    public static ValidationResult result(String message, Severity severity) {
        Objects.requireNonNull(message);
        return new SimpleValidationResult(message, severity);
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
        return new SimpleValidationResult(errorMessage);
    }
}
