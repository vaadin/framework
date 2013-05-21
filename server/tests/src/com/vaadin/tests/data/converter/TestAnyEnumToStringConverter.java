/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.ui.TextField;

public class TestAnyEnumToStringConverter {

    public class AnyEnumToStringConverter implements Converter<Enum, String> {

        private Class<? extends Enum>[] enumClass;

        public AnyEnumToStringConverter(Class<? extends Enum>... enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public String convertToModel(Enum value, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            if (value == null) {
                return null;
            }

            return value.toString();
        }

        @Override
        public Enum convertToPresentation(String value, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            if (value == null) {
                return null;
            }
            for (Class<? extends Enum> candidate : enumClass) {
                for (Enum e : candidate.getEnumConstants()) {
                    if (e.toString().equals(value)) {
                        return e;
                    }
                }
            }

            return null;
        }

        @Override
        public Class<String> getModelType() {
            return String.class;
        }

        @Override
        public Class<Enum> getPresentationType() {
            return Enum.class;
        }

    }

    private AnyEnumToStringConverter converter;

    @Before
    public void setup() {
        converter = new AnyEnumToStringConverter(TestEnum.class,
                AnotherTestEnum.class);
    }

    @Test
    public void nullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, null));
    }

    @Test
    public void enumToStringConversion() {
        Assert.assertEquals(TestEnum.TWO.toString(),
                converter.convertToModel(TestEnum.TWO, null));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(),
                converter.convertToModel(AnotherTestEnum.TWO, null));
    }

    @Test
    public void stringToEnumConversion() {
        Assert.assertEquals(TestEnum.TWO,
                converter.convertToPresentation(TestEnum.TWO.toString(), null));
        Assert.assertEquals(AnotherTestEnum.TWO, converter
                .convertToPresentation(AnotherTestEnum.TWO.toString(), null));
    }

    @Test
    public void stringToEnumWithField() {
        TextField tf = new TextField();
        tf.setConverter(new ReverseConverter(converter));
        tf.setPropertyDataSource(new ObjectProperty(AnotherTestEnum.TWO));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(), tf.getValue());
        tf.setValue(AnotherTestEnum.ONE.toString());
        Assert.assertEquals(AnotherTestEnum.ONE.toString(), tf.getValue());
        Assert.assertEquals(AnotherTestEnum.ONE, tf.getConvertedValue());
        Assert.assertEquals(AnotherTestEnum.ONE, tf.getPropertyDataSource()
                .getValue());

        tf.setPropertyDataSource(new ObjectProperty(TestEnum.TWO));
        Assert.assertEquals(TestEnum.TWO.toString(), tf.getValue());
        tf.setValue(TestEnum.ONE.toString());
        Assert.assertEquals(TestEnum.ONE.toString(), tf.getValue());
        Assert.assertEquals(TestEnum.ONE, tf.getConvertedValue());
        Assert.assertEquals(TestEnum.ONE, tf.getPropertyDataSource().getValue());

    }
}
