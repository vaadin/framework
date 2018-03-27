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

public class AnyEnumToStringConverterTest {

    public class AnyEnumToStringConverter implements Converter<Enum, String> {

        public AnyEnumToStringConverter() {
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
            for (Enum e : targetType.getEnumConstants()) {
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
            return Enum.class;
        }

    }

    private AnyEnumToStringConverter converter;

    @Before
    public void setup() {
        converter = new AnyEnumToStringConverter();
    }

    @Test
    public void nullConversion() {
        Assert.assertEquals(null, converter.convertToModel(null, null, null));
    }

    @Test
    public void enumToStringConversion() {
        Assert.assertEquals(TestEnum.TWO.toString(),
                converter.convertToModel(TestEnum.TWO, String.class, null));
        Assert.assertEquals(AnotherTestEnum.TWO.toString(), converter
                .convertToModel(AnotherTestEnum.TWO, String.class, null));
    }

    @Test
    public void stringToEnumConversion() {
        Assert.assertEquals(TestEnum.TWO, converter.convertToPresentation(
                TestEnum.TWO.toString(), TestEnum.class, null));
        Assert.assertEquals(AnotherTestEnum.TWO,
                converter.convertToPresentation(AnotherTestEnum.TWO.toString(),
                        AnotherTestEnum.class, null));
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
        Assert.assertEquals(AnotherTestEnum.ONE,
                tf.getPropertyDataSource().getValue());

        tf.setPropertyDataSource(new ObjectProperty(TestEnum.TWO));
        Assert.assertEquals(TestEnum.TWO.toString(), tf.getValue());
        tf.setValue(TestEnum.ONE.toString());
        Assert.assertEquals(TestEnum.ONE.toString(), tf.getValue());
        Assert.assertEquals(TestEnum.ONE, tf.getConvertedValue());
        Assert.assertEquals(TestEnum.ONE,
                tf.getPropertyDataSource().getValue());

    }
}
