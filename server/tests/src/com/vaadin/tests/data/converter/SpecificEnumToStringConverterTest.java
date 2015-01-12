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
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.tests.data.bean.AnotherTestEnum;
import com.vaadin.tests.data.bean.TestEnum;
import com.vaadin.ui.TextField;

public class SpecificEnumToStringConverterTest {

    public class SpecificEnumToStringConverter implements
            Converter<Enum, String> {

        private Class<? extends Enum> enumClass;

        public SpecificEnumToStringConverter(Class<? extends Enum> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public String convertToModel(Enum value,
                Class<? extends String> targetType, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            if (value == null) {
                return null;
            }

            return value.toString();
        }

        @Override
        public Enum convertToPresentation(String value,
                Class<? extends Enum> targetType, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            if (value == null) {
                return null;
            }

            for (Enum e : enumClass.getEnumConstants()) {
                if (e.toString().equals(value)) {
                    return e;
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
            return (Class<Enum>) enumClass;
        }

    }

    SpecificEnumToStringConverter testEnumConverter;
    SpecificEnumToStringConverter anotherTestEnumConverter;

    @Before
    public void setup() {
        testEnumConverter = new SpecificEnumToStringConverter(TestEnum.class);
        anotherTestEnumConverter = new SpecificEnumToStringConverter(
                AnotherTestEnum.class);
    }

    @Test
    public void nullConversion() {
        Assert.assertEquals(null,
                testEnumConverter.convertToModel(null, null, null));
    }

    @Test
    public void enumToStringConversion() {
        Assert.assertEquals(TestEnum.TWO.toString(), testEnumConverter
                .convertToModel(TestEnum.TWO, String.class, null));
    }

    @Test
    public void stringToEnumConversion() {
        Assert.assertEquals(TestEnum.TWO, testEnumConverter
                .convertToPresentation(TestEnum.TWO.toString(), TestEnum.class,
                        null));
    }

    @Test
    public void stringToEnumWithField() {
        TextField tf = new TextField();
        tf.setConverter(new ReverseConverter(anotherTestEnumConverter));
        tf.setPropertyDataSource(new ObjectProperty(AnotherTestEnum.TWO));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(), tf.getValue());
        tf.setValue(AnotherTestEnum.ONE.toString());
        Assert.assertEquals(AnotherTestEnum.ONE.toString(), tf.getValue());
        Assert.assertEquals(AnotherTestEnum.ONE, tf.getConvertedValue());
        Assert.assertEquals(AnotherTestEnum.ONE, tf.getPropertyDataSource()
                .getValue());

    }
}
