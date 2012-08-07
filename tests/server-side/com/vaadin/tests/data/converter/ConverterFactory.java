/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.data.converter;

import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.Application;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DefaultConverterFactory;
import com.vaadin.ui.TextField;

public class ConverterFactory extends TestCase {

    public static class ConvertTo42 implements Converter<String, Integer> {

        @Override
        public Integer convertToModel(String value, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            return 42;
        }

        @Override
        public String convertToPresentation(Integer value, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            return "42";
        }

        @Override
        public Class<Integer> getModelType() {
            return Integer.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }

    }

    public static class ConverterFactory42 extends DefaultConverterFactory {
        @Override
        public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
                Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
            if (modelType == Integer.class) {
                return (Converter<PRESENTATION, MODEL>) new ConvertTo42();
            }

            return super.createConverter(presentationType, modelType);
        }
    }

    public void testApplicationConverterFactoryInBackgroundThread() {
        Application.setCurrent(null);
        final Application appWithCustomIntegerConverter = new Application();
        appWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());

        TextField tf = new TextField("", "123") {
            @Override
            public Application getApplication() {
                return appWithCustomIntegerConverter;
            };
        };
        tf.setConverter(Integer.class);
        // The application converter always returns 42. Current application is
        // null
        assertEquals(42, tf.getConvertedValue());
    }

    public void testApplicationConverterFactoryForDetachedComponent() {
        final Application appWithCustomIntegerConverter = new Application();
        appWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());
        Application.setCurrent(appWithCustomIntegerConverter);

        TextField tf = new TextField("", "123");
        tf.setConverter(Integer.class);
        // The application converter always returns 42. Current application is
        // null
        assertEquals(42, tf.getConvertedValue());
    }

    public void testApplicationConverterFactoryForDifferentThanCurrentApplication() {
        final Application fieldAppWithCustomIntegerConverter = new Application();
        fieldAppWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());
        Application.setCurrent(new Application());

        TextField tf = new TextField("", "123") {
            @Override
            public Application getApplication() {
                return fieldAppWithCustomIntegerConverter;
            }
        };
        tf.setConverter(Integer.class);

        // The application converter always returns 42. Application.getCurrent()
        // should not be used
        assertEquals(42, tf.getConvertedValue());
    }
}
