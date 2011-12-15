/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * A converter that converts from {@link Integer} to {@link String} and back.
 * Uses the given locale and a {@link NumberFormat} instance for formatting and
 * parsing.
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class IntegerToStringConverter implements Converter<Integer, String> {

    /**
     * Returns the format used by
     * {@link #convertFromSourceToTarget(Integer, Locale)} and
     * {@link #convertFromTargetToSource(String, Locale)}.
     * 
     * @param locale
     *            The locale to use
     * @return A NumberFormat instance
     */
    protected NumberFormat getFormat(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return NumberFormat.getIntegerInstance(locale);
    }

    public Integer convertFromTargetToSource(String value, Locale locale)
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
                    + "' to " + getSourceType().getName());
        }

        if (parsedValue == null) {
            // Convert "" to null
            return null;
        }
        return parsedValue.intValue();
    }

    public String convertFromSourceToTarget(Integer value, Locale locale)
            throws ConversionException {
        if (value == null) {
            return null;
        }

        return getFormat(locale).format(value);
    }

    public Class<Integer> getSourceType() {
        return Integer.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

}
