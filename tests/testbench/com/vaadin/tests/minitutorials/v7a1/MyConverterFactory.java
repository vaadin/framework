package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;

public class MyConverterFactory extends DefaultConverterFactory {
    @Override
    protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        // Handle String <-> Double
        if (presentationType == String.class && modelType == Double.class) {
            return (Converter<PRESENTATION, MODEL>) new MyStringToDoubleConverter();
        }
        // Let default factory handle the rest
        return super.findConverter(presentationType, modelType);
    }
}
