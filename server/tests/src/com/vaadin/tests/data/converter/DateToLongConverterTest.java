package com.vaadin.tests.data.converter;

import java.util.Date;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.DateToLongConverter;

public class DateToLongConverterTest extends TestCase {

    DateToLongConverter converter = new DateToLongConverter();

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Long.class, null));
    }

    public void testValueConversion() {
        Date d = new Date(100, 0, 1);
        assertEquals(
            Long.valueOf(946677600000l + (d.getTimezoneOffset() + 120) * 60 * 1000L),
            converter.convertToModel(d, Long.class, null));
    }
}
