package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.data.util.converter.StringToShortConverter;

public class StringToShortConverterTest extends TestCase {

    StringToShortConverter converter = new StringToShortConverter();
    Converter<Short, String> reverseConverter = new ReverseConverter<Short, String>(
            converter);

    public void testNullConversion() {
        assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, Short.class, null));
    }

    public void testReverseNullConversion() {
        assertEquals("Null value reversely was converted incorrectly", null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", Short.class, null));
    }

    public void testValueConversion() {
        assertEquals("Short value was converted incorrectly",
                Short.valueOf((short) 10),
                converter.convertToModel("10", Short.class, null));
    }

    public void testReverseValueConversion() {
        assertEquals(
                "Short value reversely was converted incorrectly",
                reverseConverter.convertToModel((short) 10, String.class, null),
                "10");
    }

    public void testExtremeShortValueConversion() {
        short b = converter.convertToModel("32767", Short.class, null);
        Assert.assertEquals(Short.MAX_VALUE, b);
        b = converter.convertToModel("-32768", Short.class, null);
        assertEquals("Min short value was converted incorrectly",
                Short.MIN_VALUE, b);
    }

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
        assertFalse("Accepted value outside range of int", accepted);
    }
}
