package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.DefaultConverterFactory;
import com.vaadin.v7.ui.TextField;

public class ConverterFactoryTest {

    public static class ConvertTo42 implements Converter<String, Integer> {

        @Override
        public Integer convertToModel(String value,
                Class<? extends Integer> targetType, Locale locale)
                throws ConversionException {
            return 42;
        }

        @Override
        public String convertToPresentation(Integer value,
                Class<? extends String> targetType, Locale locale)
                throws ConversionException {
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

    @Test
    public void testApplicationConverterFactoryInBackgroundThread() {
        VaadinSession.setCurrent(null);
        final VaadinSession appWithCustomIntegerConverter = new AlwaysLockedVaadinSession(
                null);
        appWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());

        TextField tf = new TextField("", "123") {
            @Override
            public VaadinSession getSession() {
                return appWithCustomIntegerConverter;
            }
        };
        tf.setConverter(Integer.class);
        // The application converter always returns 42. Current application is
        // null
        assertEquals(42, tf.getConvertedValue());
    }

    @Test
    public void testApplicationConverterFactoryForDetachedComponent() {
        final VaadinSession appWithCustomIntegerConverter = new AlwaysLockedVaadinSession(
                null);
        appWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());
        VaadinSession.setCurrent(appWithCustomIntegerConverter);

        TextField tf = new TextField("", "123");
        tf.setConverter(Integer.class);
        // The application converter always returns 42. Current application is
        // null
        assertEquals(42, tf.getConvertedValue());
    }

    @Test
    public void testApplicationConverterFactoryForDifferentThanCurrentApplication() {
        final VaadinSession fieldAppWithCustomIntegerConverter = new AlwaysLockedVaadinSession(
                null);
        fieldAppWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));

        TextField tf = new TextField("", "123") {
            @Override
            public VaadinSession getSession() {
                return fieldAppWithCustomIntegerConverter;
            }
        };
        tf.setConverter(Integer.class);

        // The application converter always returns 42. Application.getCurrent()
        // should not be used
        assertEquals(42, tf.getConvertedValue());
    }
}
