package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.DateToLongConverter;

public class DateToLongConverterTest {

    DateToLongConverter converter = new DateToLongConverter();

    @Test
    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Long.class, null));
    }

    @Test
    public void testValueConversion() {
        Date d = new Date(100, 0, 1);
        assertEquals(
                Long.valueOf(946677600000l
                        + (d.getTimezoneOffset() + 120) * 60 * 1000L),
                converter.convertToModel(d, Long.class, null));
    }
}
