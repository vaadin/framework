package com.itmill.toolkit.data.validator;

import com.itmill.toolkit.data.Validator;

/**
 * Default Validator base class. See
 * {@link com.itmill.toolkit.data.validator.Validator} for more information.
 * <p>
 * If the validation fails, the exception thrown contains the error message with
 * its argument 0 replaced with the toString() of the object being validated.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 */
public abstract class AbstractValidator implements Validator {

    /**
     * Error message.
     */
    private String errorMessage;

    /**
     * Constructs a validator with an error message.
     * 
     * @param errorMessage
     *            the message included in the exception (with its parameter {0}
     *            replaced by toString() of the object to be validated) in case
     *            the validation fails
     */
    public AbstractValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value)) {
            String message;
            if (value == null) {
                message = errorMessage.replace("{0}", "null");
            } else {
                message = errorMessage.replace("{0}", value.toString());
            }
            throw new InvalidValueException(message);
        }
    }

    /**
     * Gets the message to be displayed in case the value does not validate.
     * 
     * @return the Error Message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the message to be displayed in case the value does not validate.
     * 
     * @param errorMessage
     *            the Error Message to set.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
