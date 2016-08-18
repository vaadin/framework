/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.data.converter;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.legacy.data.util.converter.LegacyConverter;
import com.vaadin.legacy.data.util.converter.LegacyDefaultConverterFactory;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;

public class ConverterFactoryTest {

    public static class ConvertTo42
            implements LegacyConverter<String, Integer> {

        @Override
        public Integer convertToModel(String value,
                Class<? extends Integer> targetType, Locale locale)
                throws com.vaadin.legacy.data.util.converter.LegacyConverter.ConversionException {
            return 42;
        }

        @Override
        public String convertToPresentation(Integer value,
                Class<? extends String> targetType, Locale locale)
                throws com.vaadin.legacy.data.util.converter.LegacyConverter.ConversionException {
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

    public static class ConverterFactory42
            extends LegacyDefaultConverterFactory {
        @Override
        public <PRESENTATION, MODEL> LegacyConverter<PRESENTATION, MODEL> createConverter(
                Class<PRESENTATION> presentationType, Class<MODEL> modelType) {
            if (modelType == Integer.class) {
                return (LegacyConverter<PRESENTATION, MODEL>) new ConvertTo42();
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

        LegacyTextField tf = new LegacyTextField("", "123") {
            @Override
            public VaadinSession getSession() {
                return appWithCustomIntegerConverter;
            }
        };
        tf.setConverter(Integer.class);
        // The application converter always returns 42. Current application is
        // null
        Assert.assertEquals(42, tf.getConvertedValue());
    }

    @Test
    public void testApplicationConverterFactoryForDetachedComponent() {
        final VaadinSession appWithCustomIntegerConverter = new AlwaysLockedVaadinSession(
                null);
        appWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());
        VaadinSession.setCurrent(appWithCustomIntegerConverter);

        LegacyTextField tf = new LegacyTextField("", "123");
        tf.setConverter(Integer.class);
        // The application converter always returns 42. Current application is
        // null
        Assert.assertEquals(42, tf.getConvertedValue());
    }

    @Test
    public void testApplicationConverterFactoryForDifferentThanCurrentApplication() {
        final VaadinSession fieldAppWithCustomIntegerConverter = new AlwaysLockedVaadinSession(
                null);
        fieldAppWithCustomIntegerConverter
                .setConverterFactory(new ConverterFactory42());
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));

        LegacyTextField tf = new LegacyTextField("", "123") {
            @Override
            public VaadinSession getSession() {
                return fieldAppWithCustomIntegerConverter;
            }
        };
        tf.setConverter(Integer.class);

        // The application converter always returns 42. Application.getCurrent()
        // should not be used
        Assert.assertEquals(42, tf.getConvertedValue());
    }
}
