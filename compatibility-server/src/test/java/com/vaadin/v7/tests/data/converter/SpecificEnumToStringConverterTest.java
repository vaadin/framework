package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.tests.data.bean.AnotherTestEnum;
import com.vaadin.tests.data.bean.TestEnum;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.ReverseConverter;
import com.vaadin.v7.ui.TextField;

public class SpecificEnumToStringConverterTest {

    public class SpecificEnumToStringConverter
            implements Converter<Enum, String> {

        private Class<? extends Enum> enumClass;

        public SpecificEnumToStringConverter(Class<? extends Enum> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public String convertToModel(Enum value,
                Class<? extends String> targetType, Locale locale)
                throws ConversionException {
            if (value == null) {
                return null;
            }

            return value.toString();
        }

        @Override
        public Enum convertToPresentation(String value,
                Class<? extends Enum> targetType, Locale locale)
                throws ConversionException {
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
        assertEquals(null, testEnumConverter.convertToModel(null, null, null));
    }

    @Test
    public void enumToStringConversion() {
        assertEquals(TestEnum.TWO.toString(), testEnumConverter
                .convertToModel(TestEnum.TWO, String.class, null));
    }

    @Test
    public void stringToEnumConversion() {
        assertEquals(TestEnum.TWO, testEnumConverter.convertToPresentation(
                TestEnum.TWO.toString(), TestEnum.class, null));
    }

    @Test
    public void stringToEnumWithField() {
        TextField tf = new TextField();
        tf.setConverter(new ReverseConverter(anotherTestEnumConverter));
        tf.setPropertyDataSource(new ObjectProperty(AnotherTestEnum.TWO));
        assertEquals(AnotherTestEnum.TWO.toString(), tf.getValue());
        tf.setValue(AnotherTestEnum.ONE.toString());
        assertEquals(AnotherTestEnum.ONE.toString(), tf.getValue());
        assertEquals(AnotherTestEnum.ONE, tf.getConvertedValue());
        assertEquals(AnotherTestEnum.ONE,
                tf.getPropertyDataSource().getValue());

    }
}
