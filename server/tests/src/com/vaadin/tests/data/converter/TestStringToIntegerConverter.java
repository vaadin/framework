package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.StringToIntegerConverter;

public class TestStringToIntegerConverter extends TestCase {

    StringToIntegerConverter converter = new StringToIntegerConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", null));
    }

    public void testValueConversion() {
        assertEquals(Integer.valueOf(10), converter.convertToModel("10", null));
    }
}
