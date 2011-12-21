package com.vaadin.tests.components.abstractfield;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class Vaadin6ImplicitDoubleConverter implements
        Converter<Double, String> {

    public Double convertToModel(String value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (null == value) {
            return null;
        }
        return new Double(value.toString());
    }

    public String convertToPresentation(Double value, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if (value == null) {
            return null;
        }
        return value.toString();

    }

    public Class<Double> getModelType() {
        return Double.class;
    }

    public Class<String> getPresentationType() {
        return String.class;
    }

}
