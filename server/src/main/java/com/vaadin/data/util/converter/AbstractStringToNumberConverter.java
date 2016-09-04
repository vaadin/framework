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
import java.text.ParsePosition;
import java.util.Locale;

import com.vaadin.data.Result;

/**
 * A converter that converts from the number type T to {@link String} and back.
 * Uses the given locale and {@link NumberFormat} for formatting and parsing.
 * Automatically trims the input string, removing any leading and trailing white
 * space.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public abstract class AbstractStringToNumberConverter<T>
        implements Converter<String, T> {

    private final String errorMessage;

    /**
     * Creates a new converter instance with the given error message.
     *
     * @param errorMessage
     *            the error message to use if conversion fails
     */
    protected AbstractStringToNumberConverter(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the format used by {@link #convertToPresentation(Object, Locale)}
     * and {@link #convertToModel(Object, Locale)}.
     *
     * @param locale
     *            The locale to use
     * @return A NumberFormat instance
     */
    protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        return NumberFormat.getNumberInstance(locale);
    }

    /**
     * Convert the value to a Number using the given locale and
     * {@link #getFormat(Locale)}.
     *
     * @param value
     *            The value to convert
     * @param locale
     *            The locale to use for conversion
     * @return The converted value
     */
    protected Result<Number> convertToNumber(String value, Locale locale) {
        if (value == null) {
            return Result.ok(null);
        }

        // Remove leading and trailing white space
        value = value.trim();

        // Parse and detect errors. If the full string was not used, it is
        // an error.
        ParsePosition parsePosition = new ParsePosition(0);
        Number parsedValue = getFormat(locale).parse(value, parsePosition);
        if (parsePosition.getIndex() != value.length()) {
            return Result.error(getErrorMessage());
        }

        if (parsedValue == null) {
            // Convert "" to null
            return Result.ok(null);
        }

        return Result.ok(parsedValue);
    }

    /**
     * Gets the error message to use when conversion fails.
     *
     * @return the error message
     */
    protected String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String convertToPresentation(T value, Locale locale) {
        if (value == null) {
            return null;
        }

        return getFormat(locale).format(value);
    }

}
