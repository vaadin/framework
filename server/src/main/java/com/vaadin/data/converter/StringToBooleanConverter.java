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

import java.util.Locale;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

/**
 * A converter that converts from {@link String} to {@link Boolean} and back.
 * The String representation is given by {@link Boolean#toString()} or provided
 * in constructor {@link StringToBooleanConverter#StringToBooleanConverter(String, String, String)}.
 * <p>
 * Leading and trailing white spaces are ignored when converting from a String.
 * </p>
 * <p>
 * For language-dependent representation, subclasses should overwrite
 * {@link #getFalseString(Locale)} and {@link #getTrueString(Locale)}
 * </p>
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class StringToBooleanConverter implements Converter<String, Boolean> {

    private final String trueString;

    private final String falseString;

    private String errorMessage;

    /**
     * Creates converter with default string representations - "true" and
     * "false".
     *
     * @param errorMessage
     *            the error message to use if conversion fails
     */
    public StringToBooleanConverter(String errorMessage) {
        this(errorMessage, Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    /**
     * Creates converter with custom string representation.
     *
     * @param errorMessage
     *            the error message to use if conversion fails
     * @param falseString
     *            string representation for <code>false</code>
     * @param trueString
     *            string representation for <code>true</code>
     */
    public StringToBooleanConverter(String errorMessage, String trueString,
            String falseString) {
        this.errorMessage = errorMessage;
        this.trueString = trueString;
        this.falseString = falseString;
    }

    @Override
    public Result<Boolean> convertToModel(String value, ValueContext context) {
        if (value == null) {
            return Result.ok(null);
        }

        // Remove leading and trailing white space
        value = value.trim();

        Locale locale = context.getLocale().orElse(null);
        if (getTrueString(locale).equals(value)) {
            return Result.ok(true);
        } else if (getFalseString(locale).equals(value)) {
            return Result.ok(false);
        } else if (value.isEmpty()) {
            return Result.ok(null);
        } else {
            return Result.error(errorMessage);
        }
    }

    @Override
    public String convertToPresentation(Boolean value, ValueContext context) {
        if (value == null) {
            return null;
        }
        Locale locale = context.getLocale().orElse(null);
        if (value) {
            return getTrueString(locale);
        } else {
            return getFalseString(locale);
        }
    }

    /**
     * Gets the locale-depended string representation for false. Default is
     * locale-independent value {@code false}
     *
     * @param locale
     *            to be used
     * @return the string representation for false
     */
    protected String getFalseString(Locale locale) {
        return falseString;
    }

    /**
     * Gets the locale-depended string representation for true. Default is
     * locale-independent value {@code true}
     *
     * @param locale
     *            to be used
     * @return the string representation for true
     */
    protected String getTrueString(Locale locale) {
        return trueString;
    }

}
