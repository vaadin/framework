package com.vaadin.data.util.converter;

import java.util.Locale;

public class BooleanToStringConverter implements Converter<Boolean, String> {

    public Boolean convertFromTargetToSource(String value, Locale locale) {
        try {
            return Boolean.valueOf(value);
        } catch (Exception e) {
            throw new ConversionException("Cannot convert " + value
                    + " to Boolean");
        }
    }

    public String convertFromSourceToTarget(Boolean value, Locale locale) {
        if (value == null) {
            return "";
        }

        return value.toString();
    }

    public Class<Boolean> getSourceType() {
        return Boolean.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

}
