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
 * This validator is used for validating properties that do or do not allow null
 * values. By default, nulls are not allowed.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class NullValidator implements Validator {

    private boolean onlyNullAllowed;

    private String errorMessage;

    /**
     * Creates a new NullValidator.
     * 
     * @param errorMessage
     *            the error message to display on invalidation.
     * @param onlyNullAllowed
     *            Are only nulls allowed?
     */
    public NullValidator(String errorMessage, boolean onlyNullAllowed) {
        setErrorMessage(errorMessage);
        setNullAllowed(onlyNullAllowed);
    }

    /**
     * Validates the data given in value.
     * 
     * @param value
     *            the value to validate.
     * @throws Validator.InvalidValueException
     *             if the value was invalid.
     */
    @Override
    public void validate(Object value) throws Validator.InvalidValueException {
        if ((onlyNullAllowed && value != null)
                || (!onlyNullAllowed && value == null)) {
            throw new Validator.InvalidValueException(errorMessage);
        }
    }

    /**
     * Returns <code>true</code> if nulls are allowed otherwise
     * <code>false</code>.
     */
    public final boolean isNullAllowed() {
        return onlyNullAllowed;
    }

    /**
     * Sets if nulls (and only nulls) are to be allowed.
     * 
     * @param onlyNullAllowed
     *            If true, only nulls are allowed. If false only non-nulls are
     *            allowed. Do we allow nulls?
     */
    public void setNullAllowed(boolean onlyNullAllowed) {
        this.onlyNullAllowed = onlyNullAllowed;
    }

    /**
     * Gets the error message that is displayed in case the value is invalid.
     * 
     * @return the Error Message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message to be displayed on invalid value.
     * 
     * @param errorMessage
     *            the Error Message to set.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
