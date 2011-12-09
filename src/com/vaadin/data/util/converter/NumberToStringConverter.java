/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class NumberToStringConverter implements Converter<Number, String> {

    protected NumberFormat getFormatter(Locale locale) {
        if (locale == null) {
            return NumberFormat.getNumberInstance();
        } else {
            return NumberFormat.getNumberInstance(locale);
        }
    }

    public Number convertFromTargetToSource(String value, Locale locale) {
        if (value == null) {
            return null;
        }

        // Remove extra spaces
        value = value.trim();

        // Parse and detect errors. If the full string was not used, it is
        // an error.
        ParsePosition parsePosition = new ParsePosition(0);
        Number parsedValue = getFormatter(locale).parse(value, parsePosition);
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

    public String convertFromSourceToTarget(Number value, Locale locale) {
        if (value == null) {
            return null;
        }

        return getFormatter(locale).format(value);
    }

    public Class<Number> getSourceType() {
        return Number.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

}
