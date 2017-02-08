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
 * A converter that converts from {@link String} to {@link Long} and back. Uses
 * the given locale and a {@link NumberFormat} instance for formatting and
 * parsing.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class StringToLongConverter
        extends AbstractStringToNumberConverter<Long> {

    /**
     * Creates a new converter instance with the given error message. Empty
     * strings are converted to <code>null</code>.
     *
     * @param errorMessage
     *            the error message to use if conversion fails
     */
    public StringToLongConverter(String errorMessage) {
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
    public StringToLongConverter(Long emptyValue, String errorMessage) {
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
    public Result<Long> convertToModel(String value, ValueContext context) {
        Result<Number> n = convertToNumber(value,
                context.getLocale().orElse(null));
        return n.map(number -> {
            if (number == null) {
                return null;
            } else {
                return number.longValue();
            }
        });
    }

}
