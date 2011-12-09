/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

public class DateToStringConverter implements Converter<Date, String> {

    public Date convertFromTargetToSource(String value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }

        ParsePosition parsePosition = new ParsePosition(0);
        Date parsedValue = getFormat(locale).parse(value, parsePosition);
        if (parsePosition.getIndex() != value.length()) {
            throw new ConversionException("Could not convert '" + value
                    + "' to " + getTargetType().getName());
        }

        return parsedValue;
    }

    public String convertFromSourceToTarget(Date value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }

        return getFormat(locale).format(value);
    }

    protected DateFormat getFormat(Locale locale) {
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, locale);
        f.setLenient(false);
        return f;
    }

    public Class<Date> getSourceType() {
        return Date.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

}
