package com.vaadin.tests.data.converter;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.DateToSqlDateConverter;

public class DateToSqlDateConverterTest extends TestCase {

    DateToSqlDateConverter converter = new DateToSqlDateConverter();

    public void testNullConversion() {
        assertEquals(null,
                converter.convertToModel(null, java.sql.Date.class, null));
    }

    public void testValueConversion() {
        Date testDate = new Date(100, 0, 1);
        long time = testDate.getTime();
        assertEquals(testDate, converter.convertToModel(
                new java.sql.Date(time), java.sql.Date.class, Locale.ENGLISH));
    }
}
