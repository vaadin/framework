/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.Comparator;
import java.util.Objects;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

/**
 * Verifies that a value is within the given range.
 *
 * @param <T>
 *            the type to validate
 * @author Vaadin Ltd.
 * @since 8.0
 */
public class RangeValidator<T> extends AbstractValidator<T> {

    private T minValue = null;
    private T maxValue = null;
    private boolean minValueIncluded = true;
    private boolean maxValueIncluded = true;
    private final Comparator<? super T> comparator;

    /**
     * Creates a new range validator of the given type. Passing null to either
     * {@code minValue} or {@code maxValue} means there is no limit in that
     * direction. Both limits may be null; this can be useful if the limits are
     * resolved programmatically. The result of passing null to {@code apply}
     * depends on the given comparator.
     *
     * @param errorMessage
     *            the error message to return if validation fails, not null
     * @param comparator
     *            the comparator to compare with, not null
     * @param minValue
     *            the least value of the accepted range or null for no limit
     * @param maxValue
     *            the greatest value of the accepted range or null for no limit
     */
    public RangeValidator(String errorMessage, Comparator<? super T> comparator,
            T minValue, T maxValue) {
        super(errorMessage);
        Objects.requireNonNull(comparator, "comparator cannot be null");

        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minValueIncluded = minValue != null;
        this.maxValueIncluded = maxValue != null;
        this.comparator = comparator;
    }

    /**
     * Returns a {@code RangeValidator} comparing values of a {@code Comparable}
     * type using their <i>natural order</i>. Passing null to either
     * {@code minValue} or {@code maxValue} means there is no limit in that
     * direction. Both limits may be null; this can be useful if the limits are
     * resolved programmatically.
     * <p>
     * Null is considered to be less than any non-null value. This means null
     * never passes validation if a minimum value is specified.
     *
     * @param <C>
     *            the {@code Comparable} value type
     * @param errorMessage
     *            the error message to return if validation fails, not null
     * @param minValue
     *            the least value of the accepted range or null for no limit
     * @param maxValue
     *            the greatest value of the accepted range or null for no limit
     * @return the new validator
     */
    public static <C extends Comparable<? super C>> RangeValidator<C> of(
            String errorMessage, C minValue, C maxValue) {
        return new RangeValidator<>(errorMessage,
                Comparator.nullsFirst(Comparator.naturalOrder()), minValue,
                maxValue);
    }

    /**
     * Returns {@code Result.ok} if the value is within the specified bounds,
     * {@code Result.error} otherwise. If null is passed to {@code apply}, the
     * behavior depends on the used comparator.
     */
    @Override
    public ValidationResult apply(T value, ValueContext context) {
        return toResult(value, isValid(value));
    }

    /**
     * Returns whether the minimum value is part of the accepted range.
     *
     * @return true if the minimum value is part of the range, false otherwise
     */
    public boolean isMinValueIncluded() {
        return minValueIncluded;
    }

    /**
     * Sets whether the minimum value is part of the accepted range.
     *
     * @param minValueIncluded
     *            true if the minimum value should be part of the range, false
     *            otherwise
     */
    public void setMinValueIncluded(boolean minValueIncluded) {
        this.minValueIncluded = minValueIncluded;
    }

    /**
     * Returns whether the maximum value is part of the accepted range.
     *
     * @return true if the maximum value is part of the range, false otherwise
     */
    public boolean isMaxValueIncluded() {
        return maxValueIncluded;
    }

    /**
     * Sets whether the maximum value is part of the accepted range.
     *
     * @param maxValueIncluded
     *            true if the maximum value should be part of the range, false
     *            otherwise
     */
    public void setMaxValueIncluded(boolean maxValueIncluded) {
        this.maxValueIncluded = maxValueIncluded;
    }

    /**
     * Returns the minimum value of the range.
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
     * Gets the maximum value of the range.
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

    @Override
    public String toString() {
        T min = getMinValue();
        T max = getMaxValue();
        return String.format("%s %c%s, %s%c", getClass().getSimpleName(),
                isMinValueIncluded() ? '[' : '(', min != null ? min : "-∞",
                max != null ? max : "∞", isMaxValueIncluded() ? ']' : ')');
    }

    /**
     * Returns whether the given value lies in the valid range.
     *
     * @param value
     *            the value to validate
     * @return true if the value is valid, false otherwise
     */
    protected boolean isValid(T value) {
        if (value == null) {
            return true;
        }
        if (getMinValue() != null) {
            int result = comparator.compare(value, getMinValue());
            if (result < 0) {
                return false;
            } else if (result == 0 && !isMinValueIncluded()) {
                return false;
            }
        }
        if (getMaxValue() != null) {
            int result = comparator.compare(value, getMaxValue());
            if (result > 0) {
                return false;
            } else if (result == 0 && !isMaxValueIncluded()) {
                return false;
            }
        }
        return true;
    }
}
