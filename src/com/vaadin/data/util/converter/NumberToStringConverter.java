/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * A converter that converts from {@link Number} to {@link String} and back.
 * Uses the given locale and {@link NumberFormat} for formatting and parsing.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class NumberToStringConverter implements Converter<Number, String> {

    /**
     * Returns the format used by
     * {@link #convertFromSourceToTarget(Number, Locale)} and
     * {@link #convertFromTargetToSource(String, Locale)}.
     * 
     * @param locale
     *            The locale to use
     * @return A NumberFormat instance
     */
    protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            return NumberFormat.getNumberInstance();
        } else {
            return NumberFormat.getNumberInstance(locale);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromTargetToSource(java
     * .lang.Object, java.util.Locale)
     */
    public Number convertFromTargetToSource(String value, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }

        // Remove leading and trailing white space
        value = value.trim();

        // Parse and detect errors. If the full string was not used, it is
        // an error.
        ParsePosition parsePosition = new ParsePosition(0);
        Number parsedValue = getFormat(locale).parse(value, parsePosition);
        if (parsePosition.getIndex() != value.length()) {
            throw new ConversionException("Could not convert '" + value
                    + "' to " + getTargetType().getName());
        }

        if (parsedValue == null) {
            // Convert "" to null
            return null;
        }
        return parsedValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromSourceToTarget(java
     * .lang.Object, java.util.Locale)
     */
    public String convertFromSourceToTarget(Number value, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }

        return getFormat(locale).format(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getSourceType()
     */
    public Class<Number> getSourceType() {
        return Number.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.converter.Converter#getTargetType()
     */
    public Class<String> getTargetType() {
        return String.class;
    }

}
