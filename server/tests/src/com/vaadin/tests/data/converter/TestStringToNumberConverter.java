package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.StringToNumberConverter;

public class TestStringToNumberConverter extends TestCase {

    StringToNumberConverter converter = new StringToNumberConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", null));
    }

    public void testValueConversion() {
        assertEquals(Long.valueOf(10), converter.convertToModel("10", null));
        assertEquals(10.5, converter.convertToModel("10.5", null));
    }
}
