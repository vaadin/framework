package com.vaadin.tests.components.abstractfield;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class Vaadin6ImplicitDoubleConverter implements
        Converter<Double, String> {

    public Double convertFromTargetToSource(String value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (null == value) {
            return null;
        }
        return new Double(value.toString());
    }

    public String convertFromSourceToTarget(Double value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }
        return value.toString();

    }

    public Class<Double> getSourceType() {
        return Double.class;
    }

    public Class<String> getTargetType() {
        return String.class;
    }

}
