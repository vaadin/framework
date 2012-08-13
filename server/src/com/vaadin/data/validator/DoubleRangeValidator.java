/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.validator;

/**
 * Validator for validating that a {@link Double} is inside a given range.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
@SuppressWarnings("serial")
public class DoubleRangeValidator extends RangeValidator<Double> {

    /**
     * Creates a validator for checking that an Double is within a given range.
     * 
     * By default the range is inclusive i.e. both minValue and maxValue are
     * valid values. Use {@link #setMinValueIncluded(boolean)} or
     * {@link #setMaxValueIncluded(boolean)} to change it.
     * 
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @param minValue
     *            The minimum value to accept or null for no limit
     * @param maxValue
     *            The maximum value to accept or null for no limit
     */
    public DoubleRangeValidator(String errorMessage, Double minValue,
            Double maxValue) {
        super(errorMessage, Double.class, minValue, maxValue);
    }

}
