package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.StringToIntegerConverter;

public class TestStringToIntegerConverter extends TestCase {

    StringToIntegerConverter converter = new StringToIntegerConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Integer.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", Integer.class, null));
    }

    public void testValueOutOfRange() {
        Double[] values = new Double[] { Integer.MAX_VALUE * 2.0,
                Integer.MIN_VALUE * 2.0, Long.MAX_VALUE * 2.0,
                Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                converter.convertToModel(String.format("%.0f", value),
                        Integer.class, null);
                accepted = true;
            } catch (ConversionException expected) {
            }
        }
        assertFalse("Accepted value outside range of int", accepted);
    }

    public void testValueConversion() {
        assertEquals(Integer.valueOf(10),
                converter.convertToModel("10", Integer.class, null));
    }
}
