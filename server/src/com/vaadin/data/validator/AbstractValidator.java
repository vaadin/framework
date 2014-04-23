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
package com.vaadin.data.validator;

import com.vaadin.data.Validator;

/**
 * Abstract {@link com.vaadin.data.Validator Validator} implementation that
 * provides a basic Validator implementation except the
 * {@link #isValidValue(Object)} method.
 * <p>
 * To include the value that failed validation in the exception message you can
 * use "{0}" in the error message. This will be replaced with the failed value
 * (converted to string using {@link #toString()}) or "null" if the value is
 * null.
 * </p>
 * <p>
 * The default implementation of AbstractValidator does not support HTML in
 * error messages. To enable HTML support, override
 * {@link InvalidValueException#getHtmlMessage()} and throw such exceptions from
 * {@link #validate(Object)}.
 * </p>
 * <p>
 * Since Vaadin 7, subclasses can either implement {@link #validate(Object)}
 * directly or implement {@link #isValidValue(Object)} when migrating legacy
 * applications. To check validity, {@link #validate(Object)} should be used.
 * </p>
 * 
 * @param <T>
 *            The type
 * @author Vaadin Ltd.
 * @since 5.4
 */
public abstract class AbstractValidator<T> implements Validator {

    /**
     * Error message that is included in an {@link InvalidValueException} if
     * such is thrown.
     */
    private String errorMessage;

    /**
     * Constructs a validator with the given error message.
     * 
     * @param errorMessage
     *            the message to be included in an {@link InvalidValueException}
     *            (with "{0}" replaced by the value that failed validation).
     */
    public AbstractValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Since Vaadin 7, subclasses of AbstractValidator should override
     * {@link #isValidValue(Object)} or {@link #validate(Object)} instead of
     * {@link #isValid(Object)}. {@link #validate(Object)} should normally be
     * used to check values.
     * 
     * @param value
     * @return true if the value is valid
     */
    public boolean isValid(Object value) {
        try {
            validate(value);
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }

    /**
     * Internally check the validity of a value. This method can be used to
     * perform validation in subclasses if customization of the error message is
     * not needed. Otherwise, subclasses should override
     * {@link #validate(Object)} and the return value of this method is ignored.
     * 
     * This method should not be called from outside the validator class itself.
     * 
     * @param value
     * @return
     */
    protected abstract boolean isValidValue(T value);

    @Override
    public void validate(Object value) throws InvalidValueException {
        // isValidType ensures that value can safely be cast to TYPE
        if (!isValidType(value) || !isValidValue((T) value)) {
            String message = getErrorMessage().replace("{0}",
                    String.valueOf(value));
            throw new InvalidValueException(message);
        }
    }

    /**
     * Checks the type of the value to validate to ensure it conforms with
     * getType. Enables sub classes to handle the specific type instead of
     * Object.
     * 
     * @param value
     *            The value to check
     * @return true if the value can safely be cast to the type specified by
     *         {@link #getType()}
     */
    protected boolean isValidType(Object value) {
        if (value == null) {
            return true;
        }

        return getType().isAssignableFrom(value.getClass());
    }

    /**
     * Returns the message to be included in the exception in case the value
     * does not validate.
     * 
     * @return the error message provided in the constructor or using
     *         {@link #setErrorMessage(String)}.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the message to be included in the exception in case the value does
     * not validate. The exception message is typically shown to the end user.
     * 
     * @param errorMessage
     *            the error message. "{0}" is automatically replaced by the
     *            value that did not validate.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public abstract Class<T> getType();
}
