/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

/**
 * A converter that converts from {@link Date} to {@link String} and back. Uses
 * the given locale and {@link DateFormat} for formatting and parsing.
 * <p>
 * Leading and trailing white spaces are ignored when converting from a String.
 * </p>
 * <p>
 * Override and overwrite {@link #getFormat(Locale)} to use a different format.
 * </p>
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
 * @since 7.0
 */
public class DateToStringConverter implements Converter<Date, String> {

    /**
     * Returns the format used by
     * {@link #convertFromSourceToTarget(Date, Locale)} and
     * {@link #convertFromTargetToSource(String, Locale)}.
     * 
     * @param locale
     *            The locale to use
     * @return A DateFormat instance
     */
    protected DateFormat getFormat(Locale locale) {
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, locale);
        f.setLenient(false);
        return f;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.converter.Converter#convertFromTargetToSource(java
     * .lang.Object, java.util.Locale)
     */
    public Date convertFromTargetToSource(String value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }

        // Remove leading and trailing white space
        value = value.trim();

        ParsePosition parsePosition = new ParsePosition(0);
        Date parsedValue = getFormat(locale).parse(value, parsePosition);
        if (parsePosition.getIndex() != value.length()) {
            throw new ConversionException("Could not convert '" + value
                    + "' to " + getTargetType().getName());
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
    public String convertFromSourceToTarget(Date value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
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
    public Class<Date> getSourceType() {
        return Date.class;
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
