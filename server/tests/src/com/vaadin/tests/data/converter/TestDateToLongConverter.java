package com.vaadin.tests.data.converter;

import java.util.Date;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.DateToLongConverter;

public class TestDateToLongConverter extends TestCase {

    DateToLongConverter converter = new DateToLongConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Long.class, null));
    }

    public void testValueConversion() {
        assertEquals(Long.valueOf(946677600000l),
                converter.convertToModel(new Date(100, 0, 1), Long.class, null));
    }
}
