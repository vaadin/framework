package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.legacy.data.util.converter.LegacyConverter;
import com.vaadin.legacy.data.util.converter.LegacyDefaultConverterFactory;

public class MyConverterFactory extends LegacyDefaultConverterFactory {
    @Override
    protected <PRESENTATION, MODEL> LegacyConverter<PRESENTATION, MODEL> findConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
        // Handle String <-> Double
        if (presentationType == String.class && modelType == Double.class) {
            return (LegacyConverter<PRESENTATION, MODEL>) new MyStringToDoubleConverter();
        }
        // Let default factory handle the rest
        return super.findConverter(presentationType, modelType);
    }
}
