/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.validator;

import java.util.Date;

import com.vaadin.ui.DateField.Resolution;

/**
 * Validator for validating that a Date is inside a given range.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * @since 7.0
 */
public class DateRangeValidator extends RangeValidator<Date> {

    /**
     * Creates a validator for checking that an Date is within a given range.
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
    public DateRangeValidator(String errorMessage, Date minValue,
            Date maxValue, Resolution resolution) {
        super(errorMessage, Date.class, minValue, maxValue);
    }

}
