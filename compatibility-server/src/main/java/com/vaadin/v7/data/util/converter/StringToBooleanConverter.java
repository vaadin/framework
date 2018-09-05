/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.v7.data.util.converter;

import java.util.Locale;

/**
 * A converter that converts from {@link String} to {@link Boolean} and back.
 * The String representation is given by {@link Boolean#toString()} or provided
 * in constructor {@link #StringToBooleanConverter(String, String)}.
 * <p>
 * Leading and trailing white spaces are ignored when converting from a String.
 * </p>
 * <p>
 * For language-dependent representation, subclasses should overwrite
 * {@link #getFalseString(Locale)} and {@link #getTrueString(Locale)}
 * </p>
 *
 * @author Vaadin Ltd
 * @since 7.0
 */
@Deprecated
public class StringToBooleanConverter implements Converter<String, Boolean> {

    private final String trueString;

    private final String falseString;

    /**
     * Creates converter with default string representations - "true" and
     * "false".
     *
     */
    public StringToBooleanConverter() {
        this(Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }

    /**
     * Creates converter with custom string representation.
     *
     * @since 7.5.4
     * @param falseString
     *            string representation for <code>false</code>
     * @param trueString
     *            string representation for <code>true</code>
     */
    public StringToBooleanConverter(String trueString, String falseString) {
        this.trueString = trueString;
        this.falseString = falseString;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object,
     * java.lang.Class, java.util.Locale)
     */
    @Override
    public Boolean convertToModel(String value,
            Class<? extends Boolean> targetType, Locale locale)
            throws ConversionException {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // Remove leading and trailing white space
        value = value.trim();

        if (getTrueString().equals(value)) {
            return true;
        } else if (getFalseString().equals(value)) {
            return false;
        } else {
            throw new ConversionException("Cannot convert " + value + " to "
                    + getModelType().getName());
        }
    }

    /**
     * Gets the string representation for true. Default is "true", if not set in
     * constructor.
     *
     * @return the string representation for true
     */
    protected String getTrueString() {
        return trueString;
    }

    /**
     * Gets the string representation for false. Default is "false", if not set
     * in constructor.
     *
     * @return the string representation for false
     */
    protected String getFalseString() {
        return falseString;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang
     * .Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(Boolean value,
            Class<? extends String> targetType, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }
        if (value) {
            return getTrueString(locale);
        } else {
            return getFalseString(locale);
        }
    }

    /**
     * Gets the locale-depended string representation for false. Default is
     * locale-independent value provided by {@link #getFalseString()}
     *
     * @since 7.5.4
     * @param locale
     *            to be used
     * @return the string representation for false
     */
    protected String getFalseString(Locale locale) {
        return getFalseString();
    }

    /**
     * Gets the locale-depended string representation for true. Default is
     * locale-independent value provided by {@link #getTrueString()}
     *
     * @since 7.5.4
     * @param locale
     *            to be used
     * @return the string representation for true
     */
    protected String getTrueString(Locale locale) {
        return getTrueString();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<Boolean> getModelType() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.data.util.converter.Converter#getPresentationType()
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
