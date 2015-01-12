package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.data.util.converter.StringToByteConverter;

public class StringToByteConverterTest extends TestCase {

    StringToByteConverter converter = new StringToByteConverter();
    Converter<Byte, String> reverseConverter = new ReverseConverter<Byte, String>(
            converter);

    public void testNullConversion() {
        assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, Byte.class, null));
    }

    public void testReverseNullConversion() {
        assertEquals("Null value reversely was converted incorrectly", null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", Byte.class, null));
    }

    public void testValueConversion() {
        assertEquals("Byte value was converted incorrectly",
                Byte.valueOf((byte) 10),
                converter.convertToModel("10", Byte.class, null));
    }

    public void testReverseValueConversion() {
        assertEquals("Byte value reversely was converted incorrectly",
                reverseConverter.convertToModel((byte) 10, String.class, null),
                "10");
    }

    public void testExtremeByteValueConversion() {
        byte b = converter.convertToModel("127", Byte.class, null);
        Assert.assertEquals(Byte.MAX_VALUE, b);
        b = converter.convertToModel("-128", Byte.class, null);
        assertEquals("Min byte value was converted incorrectly",
                Byte.MIN_VALUE, b);
    }

    public void testValueOutOfRange() {
        Double[] values = new Double[] { Byte.MAX_VALUE * 2.0,
                Byte.MIN_VALUE * 2.0, Long.MAX_VALUE * 2.0,
                Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                converter.convertToModel(String.format("%.0f", value),
                        Byte.class, null);
                accepted = true;
            } catch (ConversionException expected) {
            }
        }
        assertFalse("Accepted value outside range of int", accepted);
    }
}
