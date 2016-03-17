package com.vaadin.tests.data.converter;

import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToDateConverter;

public class StringToDateConverterTest {

    StringToDateConverter converter = new StringToDateConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, Date.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null,
                converter.convertToModel("", Date.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(new Date(100, 0, 1), converter.convertToModel(
                "Jan 1, 2000 12:00:00 AM", Date.class, Locale.ENGLISH));
    }
}
