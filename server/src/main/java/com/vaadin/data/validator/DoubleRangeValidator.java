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
 * Validator for validating that a {@link Double} is inside a given range.
 * 
 * @author Vaadin Ltd.
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
