/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.validator;

/**
 * String validator for a double precision floating point number. See
 * {@link com.vaadin.data.validator.AbstractStringValidator} for more
 * information.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 5.4
 * @deprecated in Vaadin 7.0. Use an Double converter on the field instead.
 */
@Deprecated
@SuppressWarnings("serial")
public class DoubleValidator extends AbstractStringValidator {

    /**
     * Creates a validator for checking that a string can be parsed as an
     * double.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @deprecated in Vaadin 7.0. Use a Double converter on the field instead
     *             and/or use a {@link DoubleRangeValidator} for validating that
     *             the value is inside a given range.
     */
    @Deprecated
    public DoubleValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected boolean isValidValue(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
