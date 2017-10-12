package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.StringToFloatConverter;

public class StringToFloatConverterTest {

    StringToFloatConverter converter = new StringToFloatConverter();

    @Test
    public void testNullConversion() {
        assertNull(converter.convertToModel(null, Float.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertNull(converter.convertToModel("", Float.class, null));
    }

    @Test
    public void testValueConversion() {
        assertEquals(Float.valueOf(10),
                converter.convertToModel("10", Float.class, null));
    }
}
