package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.DateToSqlDateConverter;

public class DateToSqlDateConverterTest {

    DateToSqlDateConverter converter = new DateToSqlDateConverter();

    @Test
    public void testNullConversion() {
        assertEquals(null,
                converter.convertToModel(null, java.sql.Date.class, null));
    }

    @Test
    public void testValueConversion() {
        Date testDate = new Date(100, 0, 1);
        long time = testDate.getTime();
        assertEquals(testDate, converter.convertToModel(new java.sql.Date(time),
                java.sql.Date.class, Locale.ENGLISH));
    }
}
