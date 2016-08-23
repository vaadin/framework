package com.vaadin.tests.data.converter;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.data.util.converter.StringToDateConverter;

public class StringToDateConverterTest extends AbstractConverterTest {

    @Override
    protected StringToDateConverter getConverter() {
        return new StringToDateConverter();
    }

    @Test
    public void testEmptyStringConversion() {
        assertResult(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueConversion() {
        assertResult(new Date(100, 0, 1), getConverter()
                .convertToModel("Jan 1, 2000 12:00:00 AM", Locale.ENGLISH));
    }
}
