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

import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;

/**
 * Verifies that the length of a string is within the given range.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
@SuppressWarnings("serial")
public class StringLengthValidator extends AbstractValidator<String> {

    private final RangeValidator<Integer> validator;

    /**
     * Creates a new StringLengthValidator with a given error message and
     * minimum and maximum length limits.
     *
     * @param errorMessage
     *            the error message to return if validation fails
     * @param minLength
     *            the minimum permissible length of the string or null for no
     *            limit.
     * @param maxLength
     *            the maximum permissible length of the string or null for no
     *            limit.
     */
    public StringLengthValidator(String errorMessage, Integer minLength,
            Integer maxLength) {
        super(errorMessage);
        validator = RangeValidator.of(errorMessage, minLength, maxLength);
    }

    @Override
    public ValidationResult apply(String value, ValueContext context) {
        if (value == null) {
            return toResult(value, true);
        }
        ValidationResult lengthCheck = validator.apply(value.length(), context);
        return toResult(value, !lengthCheck.isError());
    }

    /**
     * Gets the maximum permissible length of the string.
     *
     * @return the maximum length of the string or null if there is no limit
     */
    public Integer getMaxLength() {
        return validator.getMaxValue();
    }

    /**
     * Gets the minimum permissible length of the string.
     *
     * @return the minimum length of the string or null if there is no limit
     */
    public Integer getMinLength() {
        return validator.getMinValue();
    }

    /**
     * Sets the maximum permissible length of the string.
     *
     * @param maxLength
     *            the maximum length to accept or null for no limit
     */
    public void setMaxLength(Integer maxLength) {
        validator.setMaxValue(maxLength);
    }

    /**
     * Sets the minimum permissible length.
     *
     * @param minLength
     *            the minimum length to accept or null for no limit
     */
    public void setMinLength(Integer minLength) {
        validator.setMaxValue(minLength);
    }

    @Override
    public String toString() {
        return String.format("%s[%d, %d]", getClass().getSimpleName(),
                getMinLength(), getMaxLength());
    }

}
