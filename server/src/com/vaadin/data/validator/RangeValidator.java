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

/**
 * An base implementation for validating any objects that implement
 * {@link Comparable}.
 * 
 * Verifies that the value is of the given type and within the (optionally)
 * given limits. Typically you want to use a sub class of this like
 * {@link IntegerRangeValidator}, {@link DoubleRangeValidator} or
 * {@link DateRangeValidator} in applications.
 * <p>
 * Note that {@link RangeValidator} always accept null values. Make a field
 * required to ensure that no empty values are accepted or override
 * {@link #isValidValue(Comparable)}.
 * </p>
 * 
 * @param <T>
 *            The type of Number to validate. Must implement Comparable so that
 *            minimum and maximum checks work.
 * @author Vaadin Ltd.
 * @since 7.0
 */
public class RangeValidator<T extends Comparable> extends AbstractValidator<T> {

    private T minValue = null;
    private boolean minValueIncluded = true;
    private T maxValue = null;
    private boolean maxValueIncluded = true;
    private Class<T> type;

    /**
     * Creates a new range validator of the given type.
     * 
     * @param errorMessage
     *            The error message to use if validation fails
     * @param type
     *            The type of object the validator can validate.
     * @param minValue
     *            The minimum value that should be accepted or null for no limit
     * @param maxValue
     *            The maximum value that should be accepted or null for no limit
     */
    public RangeValidator(String errorMessage, Class<T> type, T minValue,
            T maxValue) {
        super(errorMessage);
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Checks if the minimum value is part of the accepted range
     * 
     * @return true if the minimum value is part of the range, false otherwise
     */
    public boolean isMinValueIncluded() {
        return minValueIncluded;
    }

    /**
     * Sets if the minimum value is part of the accepted range
     * 
     * @param minValueIncluded
     *            true if the minimum value should be part of the range, false
     *            otherwise
     */
    public void setMinValueIncluded(boolean minValueIncluded) {
        this.minValueIncluded = minValueIncluded;
    }

    /**
     * Checks if the maximum value is part of the accepted range
     * 
     * @return true if the maximum value is part of the range, false otherwise
     */
    public boolean isMaxValueIncluded() {
        return maxValueIncluded;
    }

    /**
     * Sets if the maximum value is part of the accepted range
     * 
     * @param maxValueIncluded
     *            true if the maximum value should be part of the range, false
     *            otherwise
     */
    public void setMaxValueIncluded(boolean maxValueIncluded) {
        this.maxValueIncluded = maxValueIncluded;
    }

    /**
     * Gets the minimum value of the range
     * 
     * @return the minimum value
     */
    public T getMinValue() {
        return minValue;
    }

    /**
     * Sets the minimum value of the range. Use
     * {@link #setMinValueIncluded(boolean)} to control whether this value is
     * part of the range or not.
     * 
     * @param minValue
     *            the minimum value
     */
    public void setMinValue(T minValue) {
        this.minValue = minValue;
    }

    /**
     * Gets the maximum value of the range
     * 
     * @return the maximum value
     */
    public T getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the maximum value of the range. Use
     * {@link #setMaxValueIncluded(boolean)} to control whether this value is
     * part of the range or not.
     * 
     * @param maxValue
     *            the maximum value
     */
    public void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.validator.AbstractValidator#isValidValue(java.lang.Object
     * )
     */
    @Override
    protected boolean isValidValue(T value) {
        if (value == null
                || (String.class.equals(getType()) && "".equals(value))) {
            return true;
        }

        if (getMinValue() != null) {
            // Ensure that the min limit is ok
            int result = value.compareTo(getMinValue());
            if (result < 0) {
                // value less than min value
                return false;
            } else if (result == 0 && !isMinValueIncluded()) {
                // values equal and min value not included
                return false;
            }
        }
        if (getMaxValue() != null) {
            // Ensure that the Max limit is ok
            int result = value.compareTo(getMaxValue());
            if (result > 0) {
                // value greater than max value
                return false;
            } else if (result == 0 && !isMaxValueIncluded()) {
                // values equal and max value not included
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.validator.AbstractValidator#getType()
     */
    @Override
    public Class<T> getType() {
        return type;
    }

}
