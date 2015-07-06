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
package com.vaadin.ui.renderers;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.ui.Grid.AbstractRenderer;

import elemental.json.JsonValue;

/**
 * A renderer for presenting number values.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class NumberRenderer extends AbstractRenderer<Number> {
    private final Locale locale;
    private final NumberFormat numberFormat;
    private final String formatString;

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render with the number's natural string
     * representation in the default locale.
     */
    public NumberRenderer() {
        this(Locale.getDefault());
    }

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render the number as defined with the given
     * number format.
     *
     * @param numberFormat
     *            the number format with which to display numbers
     * @throws IllegalArgumentException
     *             if {@code numberFormat} is {@code null}
     */
    public NumberRenderer(NumberFormat numberFormat) {
        this(numberFormat, "");
    }

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render the number as defined with the given
     * number format.
     *
     * @param numberFormat
     *            the number format with which to display numbers
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     * @throws IllegalArgumentException
     *             if {@code numberFormat} is {@code null}
     */
    public NumberRenderer(NumberFormat numberFormat, String nullRepresentation)
            throws IllegalArgumentException {
        super(Number.class, nullRepresentation);

        if (numberFormat == null) {
            throw new IllegalArgumentException("Number format may not be null");
        }

        locale = null;
        this.numberFormat = numberFormat;
        formatString = null;
    }

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render with the number's natural string
     * representation in the given locale.
     *
     * @param locale
     *            the locale in which to display numbers
     * @throws IllegalArgumentException
     *             if {@code locale} is {@code null}
     */
    public NumberRenderer(Locale locale) throws IllegalArgumentException {
        this("%s", locale);
    }

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render with the number's natural string
     * representation in the given locale.
     *
     * @param formatString
     *            the format string with which to format the number
     * @param locale
     *            the locale in which to display numbers
     * @throws IllegalArgumentException
     *             if {@code locale} is {@code null}
     */
    public NumberRenderer(String formatString, Locale locale)
            throws IllegalArgumentException {
        this(formatString, locale, ""); // This will call #toString() during
                                        // formatting
    }

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render with the given format string in the
     * default locale.
     *
     * @param formatString
     *            the format string with which to format the number
     * @throws IllegalArgumentException
     *             if {@code formatString} is {@code null}
     * @see <a
     *      href="http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax">Format
     *      String Syntax</a>
     */
    public NumberRenderer(String formatString) throws IllegalArgumentException {
        this(formatString, Locale.getDefault(), "");
    }

    /**
     * Creates a new number renderer.
     * <p/>
     * The renderer is configured to render with the given format string in the
     * given locale.
     *
     * @param formatString
     *            the format string with which to format the number
     * @param locale
     *            the locale in which to present numbers
     * @throws IllegalArgumentException
     *             if either argument is {@code null}
     * @see <a
     *      href="http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax">Format
     *      String Syntax</a>
     */
    public NumberRenderer(String formatString, Locale locale,
            String nullRepresentation) {
        super(Number.class, nullRepresentation);

        if (formatString == null) {
            throw new IllegalArgumentException("Format string may not be null");
        }

        if (locale == null) {
            throw new IllegalArgumentException("Locale may not be null");
        }

        this.locale = locale;
        numberFormat = null;
        this.formatString = formatString;
    }

    @Override
    public JsonValue encode(Number value) {
        String stringValue;
        if (value == null) {
            stringValue = getNullRepresentation();
        } else if (formatString != null && locale != null) {
            stringValue = String.format(locale, formatString, value);
        } else if (numberFormat != null) {
            stringValue = numberFormat.format(value);
        } else {
            throw new IllegalStateException(String.format("Internal bug: "
                    + "%s is in an illegal state: "
                    + "[locale: %s, numberFormat: %s, formatString: %s]",
                    getClass().getSimpleName(), locale, numberFormat,
                    formatString));
        }
        return encode(stringValue, String.class);
    }

    @Override
    public String toString() {
        final String fieldInfo;
        if (numberFormat != null) {
            fieldInfo = "numberFormat: " + numberFormat.toString();
        } else {
            fieldInfo = "locale: " + locale + ", formatString: " + formatString;
        }

        return String.format("%s [%s]", getClass().getSimpleName(), fieldInfo);
    }

    @Override
    public String getNullRepresentation() {
        return super.getNullRepresentation();
    }
}
