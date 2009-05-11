/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.data.validator;

import com.vaadin.data.Validator;

/**
 * This validator is used for validating properties that do or do not allow null
 * values. By default, nulls are not allowed.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
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
    public void validate(Object value) throws Validator.InvalidValueException {
        if ((onlyNullAllowed && value != null)
                || (!onlyNullAllowed && value == null)) {
            throw new Validator.InvalidValueException(errorMessage);
        }
    }

    /**
     * Tests if the given value is valid.
     * 
     * @param value
     *            the value to validate.
     * @returns <code>true</code> for valid value, otherwise <code>false</code>.
     */
    public boolean isValid(Object value) {
        return onlyNullAllowed ? value == null : value != null;
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
