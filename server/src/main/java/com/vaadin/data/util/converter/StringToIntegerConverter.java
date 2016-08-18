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

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.Result;
import com.vaadin.legacy.data.util.converter.LegacyConverter.ConversionException;

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

    private final String errorMessage;

    /**
     * Creates a new converter instance with the given error message.
     *
     * @param errorMessage
     *            the error message to use if conversion fails
     */
    public StringToIntegerConverter(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the format used by
     * {@link #convertToPresentation(Integer, Locale)} and
     * {@link #convertToModel(String, Locale)}.
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
    public Result<Integer> convertToModel(String value, Locale locale)
            throws ConversionException {
        Number n = convertToNumber(value, locale);

        if (n == null) {
            return null;
        }

        int intValue = n.intValue();
        if (intValue == n.longValue()) {
            // If the value of n is outside the range of long, the return value
            // of longValue() is either Long.MIN_VALUE or Long.MAX_VALUE. The
            // above comparison promotes int to long and thus does not need to
            // consider wrap-around.
            return Result.ok(intValue);
        } else {
            return Result.error(errorMessage);
        }
    }

}
