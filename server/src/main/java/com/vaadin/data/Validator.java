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

package com.vaadin.data;

import java.io.Serializable;

import com.vaadin.server.VaadinServlet;

/**
 * Interface that implements a method for validating if an {@link Object} is
 * valid or not.
 * <p>
 * Implementors of this class can be added to any
 * {@link com.vaadin.data.Validatable Validatable} implementor to verify its
 * value.
 * </p>
 * <p>
 * {@link #validate(Object)} can be used to check if a value is valid. An
 * {@link InvalidValueException} with an appropriate validation error message is
 * thrown if the value is not valid.
 * </p>
 * <p>
 * Validators must not have any side effects.
 * </p>
 * <p>
 * Since Vaadin 7, the method isValid(Object) does not exist in the interface -
 * {@link #validate(Object)} should be used instead, and the exception caught
 * where applicable. Concrete classes implementing {@link Validator} can still
 * internally implement and use isValid(Object) for convenience or to ease
 * migration from earlier Vaadin versions.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface Validator extends Serializable {

    /**
     * Checks the given value against this validator. If the value is valid the
     * method does nothing. If the value is invalid, an
     * {@link InvalidValueException} is thrown.
     * 
     * @param value
     *            the value to check
     * @throws Validator.InvalidValueException
     *             if the value is invalid
     */
    public void validate(Object value) throws Validator.InvalidValueException;

    /**
     * Exception that is thrown by a {@link Validator} when a value is invalid.
     * 
     * <p>
     * The default implementation of InvalidValueException does not support HTML
     * in error messages. To enable HTML support, override
     * {@link #getHtmlMessage()} and use the subclass in validators.
     * </p>
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class InvalidValueException extends RuntimeException {

        /**
         * Array of one or more validation errors that are causing this
         * validation error.
         */
        private InvalidValueException[] causes = null;

        /**
         * Constructs a new {@code InvalidValueException} with the specified
         * message.
         * 
         * @param message
         *            The detail message of the problem.
         */
        public InvalidValueException(String message) {
            this(message, new InvalidValueException[] {});
        }

        /**
         * Constructs a new {@code InvalidValueException} with a set of causing
         * validation exceptions. The causing validation exceptions are included
         * when the exception is painted to the client.
         * 
         * @param message
         *            The detail message of the problem.
         * @param causes
         *            One or more {@code InvalidValueException}s that caused
         *            this exception.
         */
        public InvalidValueException(String message,
                InvalidValueException... causes) {
            super(message);
            if (causes == null) {
                throw new NullPointerException(
                        "Possible causes array must not be null");
            }

            this.causes = causes;
        }

        /**
         * Check if the error message should be hidden.
         * 
         * An empty (null or "") message is invisible unless it contains nested
         * exceptions that are visible.
         * 
         * @return true if the error message should be hidden, false otherwise
         */
        public boolean isInvisible() {
            String msg = getMessage();
            if (msg != null && msg.length() > 0) {
                return false;
            }
            if (causes != null) {
                for (int i = 0; i < causes.length; i++) {
                    if (!causes[i].isInvisible()) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Returns the message of the error in HTML.
         * 
         * Note that this API may change in future versions.
         */
        public String getHtmlMessage() {
            return VaadinServlet.safeEscapeForHtml(getLocalizedMessage());
        }

        /**
         * Returns the {@code InvalidValueExceptions} that caused this
         * exception.
         * 
         * @return An array containing the {@code InvalidValueExceptions} that
         *         caused this exception. Returns an empty array if this
         *         exception was not caused by other exceptions.
         */
        public InvalidValueException[] getCauses() {
            return causes;
        }

    }

    /**
     * A specific type of {@link InvalidValueException} that indicates that
     * validation failed because the value was empty. What empty means is up to
     * the thrower.
     * 
     * @author Vaadin Ltd.
     * @since 5.3.0
     */
    @SuppressWarnings("serial")
    public class EmptyValueException extends Validator.InvalidValueException {

        public EmptyValueException(String message) {
            super(message);
        }

    }
}
