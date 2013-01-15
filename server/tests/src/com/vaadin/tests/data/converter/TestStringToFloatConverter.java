package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.StringToFloatConverter;

public class TestStringToFloatConverter extends TestCase {

    StringToFloatConverter converter = new StringToFloatConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", null));
    }

    public void testValueConversion() {
        assertEquals(Float.valueOf(10), converter.convertToModel("10", null));
    }
}
