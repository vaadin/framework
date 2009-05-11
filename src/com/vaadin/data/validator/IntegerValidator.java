package com.vaadin.data.validator;

/**
 * String validator for integers. See
 * {@link com.vaadin.data.validator.AbstractStringValidator} for more
 * information.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 */
@SuppressWarnings("serial")
public class IntegerValidator extends AbstractStringValidator {

    /**
     * Creates a validator for checking that a string can be parsed as an
     * integer.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public IntegerValidator(String errorMessage) {
        super(errorMessage);

    }

    @Override
    protected boolean isValidString(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
