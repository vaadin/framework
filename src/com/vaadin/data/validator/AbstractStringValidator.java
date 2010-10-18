/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.validator;

/**
 * Validator base class for validating strings. See
 * {@link com.vaadin.data.validator.AbstractValidator} for more information.
 * 
 * <p>
 * To include the value that failed validation in the exception message you can
 * use "{0}" in the error message. This will be replaced with the failed value
 * (converted to string using {@link #toString()}) or "null" if the value is
 * null.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 */
@SuppressWarnings("serial")
public abstract class AbstractStringValidator extends AbstractValidator {

    /**
     * Constructs a validator for strings.
     * 
     * <p>
     * Null and empty string values are always accepted. To reject empty values,
     * set the field being validated as required.
     * </p>
     * 
     * @param errorMessage
     *            the message to be included in an {@link InvalidValueException}
     *            (with "{0}" replaced by the value that failed validation).
     * */
    public AbstractStringValidator(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Tests if the given value is a valid string.
     * <p>
     * Null values are always accepted. Values that are not {@link String}s are
     * always rejected. Uses {@link #isValidString(String)} to validate the
     * value.
     * </p>
     * 
     * @param value
     *            the value to check
     * @return true if the value is a valid string, false otherwise
     */
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof String)) {
            return false;
        }
        return isValidString((String) value);
    }

    /**
     * Checks if the given string is valid.
     * 
     * @param value
     *            String to check. Can never be null.
     * @return true if the string is valid, false otherwise
     */
    protected abstract boolean isValidString(String value);
}
