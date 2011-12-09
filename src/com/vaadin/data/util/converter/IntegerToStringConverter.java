/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class IntegerToStringConverter implements Converter<Integer, String> {

    protected NumberFormat getFormatter(Locale locale) {
        if (locale == null) {
            return NumberFormat.getIntegerInstance();
        } else {
            return NumberFormat.getIntegerInstance(locale);
        }
    }

    public Integer convertFromTargetToSource(String value, Locale locale) {
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
        return parsedValue.intValue();
    }

    public String convertFromSourceToTarget(Integer value, Locale locale) {
        if (value == null) {
            return null;
        }

        return getFormatter(locale).format(value);
    }

    public Class<Integer> getSourceType() {
        return Integer.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

}
