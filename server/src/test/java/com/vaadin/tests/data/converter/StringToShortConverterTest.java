package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.data.util.converter.StringToShortConverter;

public class StringToShortConverterTest {

    StringToShortConverter converter = new StringToShortConverter();
    Converter<Short, String> reverseConverter = new ReverseConverter<Short, String>(
            converter);

    @Test
    public void testNullConversion() {
        Assert.assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, Short.class, null));
    }

    @Test
    public void testReverseNullConversion() {
        Assert.assertEquals("Null value reversely was converted incorrectly",
                null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", Short.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals("Short value was converted incorrectly",
                Short.valueOf((short) 10),
                converter.convertToModel("10", Short.class, null));
    }

    @Test
    public void testReverseValueConversion() {
        Assert.assertEquals("Short value reversely was converted incorrectly",
                reverseConverter.convertToModel((short) 10, String.class, null),
                "10");
    }

    @Test
    public void testExtremeShortValueConversion() {
        short b = converter.convertToModel("32767", Short.class, null);
        Assert.assertEquals(Short.MAX_VALUE, b);
        b = converter.convertToModel("-32768", Short.class, null);
        Assert.assertEquals("Min short value was converted incorrectly",
                Short.MIN_VALUE, b);
    }

    @Test
    public void testValueOutOfRange() {
        Double[] values = new Double[] { Integer.MAX_VALUE * 2.0,
                Integer.MIN_VALUE * 2.0, Long.MAX_VALUE * 2.0,
                Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                converter.convertToModel(String.format("%.0f", value),
                        Short.class, null);
                accepted = true;
            } catch (ConversionException expected) {
            }
        }
        Assert.assertFalse("Accepted value outside range of int", accepted);
    }
}
