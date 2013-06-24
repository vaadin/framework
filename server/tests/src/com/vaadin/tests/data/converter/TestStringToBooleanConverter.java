package com.vaadin.tests.data.converter;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.StringToBooleanConverter;

public class TestStringToBooleanConverter extends TestCase {

    StringToBooleanConverter converter = new StringToBooleanConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Boolean.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", Boolean.class, null));
    }

    public void testValueConversion() {
        assertTrue(converter.convertToModel("true", Boolean.class, null));
        assertFalse(converter.convertToModel("false", Boolean.class, null));
    }
}
