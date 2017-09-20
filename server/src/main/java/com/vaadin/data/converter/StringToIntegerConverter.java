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

package com.vaadin.data.converter;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * A converter that converts from {@link String} to {@link Integer} and back.
 * Uses the given locale and a {@link NumberFormat} instance for formatting and
 * parsing.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class StringToIntegerConverter
        extends AbstractStringToNumberConverter<Integer> {

    /**
     * Creates a new converter instance with the given error message. Empty
     * strings are converted to <code>null</code>.
     *
     * @param errorMessage
     *            the error message to use if conversion fails
     */
    public StringToIntegerConverter(String errorMessage) {
        this(null, errorMessage);
    }

    /**
     * Creates a new converter instance with the given empty string value and
     * error message.
     *
     * @param emptyValue
     *            the presentation value to return when converting an empty
     *            string, may be <code>null</code>
     * @param errorMessage
     *            the error message to use if conversion fails
     */
    public StringToIntegerConverter(Integer emptyValue, String errorMessage) {
        super(emptyValue, errorMessage);
    }

    /**
     * Returns the format used by
     * {@link #convertToPresentation(Object, ValueContext)} and
     * {@link #convertToModel(String, ValueContext)}.
     *
     * @param locale
     *            The locale to use
     * @return A NumberFormat instance
     */
    @Override
    protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return NumberFormat.getIntegerInstance(locale);
    }

    @Override
    public Result<Integer> convertToModel(String value, ValueContext context) {
        Result<Number> n = convertToNumber(value,
                context.getLocale().orElse(null));
        return n.flatMap(number -> {
            if (number == null) {
                return Result.ok(null);
            } else {
                int intValue = number.intValue();
                if (intValue == number.longValue()) {
                    // If the value of n is outside the range of long, the
                    // return value of longValue() is either Long.MIN_VALUE or
                    // Long.MAX_VALUE. The/ above comparison promotes int to
                    // long and thus does not need to consider wrap-around.
                    return Result.ok(intValue);
                } else {
                    return Result.error(getErrorMessage());
                }
            }
        });
    }

}
