/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util.converter;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class DoubleToStringConverter implements Converter<Double, String> {

    protected NumberFormat getFormatter(Locale locale) {
        return NumberFormat.getNumberInstance(locale);
    }

    public Double convertFromTargetToSource(String value, Locale locale) {
        ParsePosition parsePosition = new ParsePosition(0);
        Number parsedValue = getFormatter(locale).parse(value, parsePosition);
        if (parsePosition.getIndex() != value.length()) {
            throw new ConversionException("Could not convert '" + value
                    + "' to " + getTargetType().getName());
        }
        return parsedValue.doubleValue();
    }

    public String convertFromSourceToTarget(Double value, Locale locale) {
        if (value == null) {
            return null;
        }

        return getFormatter(locale).format(value);
    }

    public Class<Double> getSourceType() {
        return Double.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }
}
