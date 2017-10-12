package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.StringToDoubleConverter;

public class StringToDoubleConverterTest {

    StringToDoubleConverter converter = new StringToDoubleConverter();

    @Test
    public void testNullConversion() {
        assertNull(converter.convertToModel(null, Double.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertNull(converter.convertToModel("", Double.class, null));
    }

    @Test
    public void testValueConversion() {
        Double value = converter.convertToModel("10", Double.class, null);
        assertEquals(10.0d, value, 0.01d);
    }
}
