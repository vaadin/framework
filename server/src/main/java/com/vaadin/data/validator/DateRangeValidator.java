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

import java.util.Date;

import com.vaadin.shared.ui.datefield.Resolution;

/**
 * Validator for validating that a Date is inside a given range.
 * 
 * <p>
 * Note that the comparison is done directly on the Date object so take care
 * that the hours/minutes/seconds/milliseconds of the min/max values are
 * properly set.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 7.0
 */
public class DateRangeValidator extends RangeValidator<Date> {

    /**
     * Creates a validator for checking that an Date is within a given range.
     * <p>
     * By default the range is inclusive i.e. both minValue and maxValue are
     * valid values. Use {@link #setMinValueIncluded(boolean)} or
     * {@link #setMaxValueIncluded(boolean)} to change it.
     * </p>
     * <p>
     * Note that the comparison is done directly on the Date object so take care
     * that the hours/minutes/seconds/milliseconds of the min/max values are
     * properly set.
     * </p>
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
