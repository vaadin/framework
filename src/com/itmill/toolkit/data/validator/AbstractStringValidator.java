package com.itmill.toolkit.data.validator;

/**
 * Validator base class for validating strings. See
 * {@link com.itmill.toolkit.data.validator.AbstractValidator} for more
 * information.
 * 
 * <p>
 * If the validation fails, the exception thrown contains the error message with
 * its argument 0 replaced with the string being validated.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 */
public abstract class AbstractStringValidator extends AbstractValidator {

    /**
     * Constructs a validator for strings.
     * <p>
     * Null and empty string values are always accepted. To disallow empty
     * values, set the field being validated as required.
     * </p>
     * 
     * @param errorMessage
     *            the message included in the exception (with its parameter {0}
     *            replaced by the string to be validated) in case the validation
     *            fails
     */
    public AbstractStringValidator(String errorMessage) {
        super(errorMessage);
    }

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
