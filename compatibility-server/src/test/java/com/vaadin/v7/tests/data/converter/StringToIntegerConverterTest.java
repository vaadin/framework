package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;

public class StringToIntegerConverterTest {

    StringToIntegerConverter converter = new StringToIntegerConverter();

    @Test
    public void testNullConversion() {
        assertNull(converter.convertToModel(null, Integer.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertNull(converter.convertToModel("", Integer.class, null));
    }

    @Test
    public void testValueOutOfRange() {
        Double[] values = { Integer.MAX_VALUE * 2.0, Integer.MIN_VALUE * 2.0,
                Long.MAX_VALUE * 2.0, Long.MIN_VALUE * 2.0 };

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

    @Test
    public void testValueConversion() {
        assertEquals(Integer.valueOf(10),
                converter.convertToModel("10", Integer.class, null));
    }
}
