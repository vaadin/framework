package com.vaadin.tests.data.converter;

import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.legacy.data.util.converter.LegacyDateToSqlDateConverter;

public class DateToSqlDateConverterTest {

    LegacyDateToSqlDateConverter converter = new LegacyDateToSqlDateConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, java.sql.Date.class, null));
    }

    @Test
    public void testValueConversion() {
        Date testDate = new Date(100, 0, 1);
        long time = testDate.getTime();
        Assert.assertEquals(testDate, converter.convertToModel(
                new java.sql.Date(time), java.sql.Date.class, Locale.ENGLISH));
    }
}
