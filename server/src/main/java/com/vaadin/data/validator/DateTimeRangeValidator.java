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

import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Validator for validating that a {@link LocalDateTime} is inside a given
 * range.
 *
 * @author Vaadin Ltd.
 * @since 8.0
 */
public class DateTimeRangeValidator extends RangeValidator<LocalDateTime> {

    /**
     * Creates a validator for checking that a {@link LocalDateTime} is within a
     * given range.
     * <p>
     * By default the range is inclusive i.e. both minValue and maxValue are
     * valid values. Use {@link #setMinValueIncluded(boolean)} or
     * {@link #setMaxValueIncluded(boolean)} to change it.
     * </p>
     *
     * @param errorMessage
     *            the message to display in case the value does not validate.
     * @param minValue
     *            The minimum value to accept or null for no limit
     * @param maxValue
     *            The maximum value to accept or null for no limit
     */
    public DateTimeRangeValidator(String errorMessage, LocalDateTime minValue,
            LocalDateTime maxValue) {
        super(errorMessage, Comparator.naturalOrder(), minValue, maxValue);
    }

}
