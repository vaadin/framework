package com.vaadin.tests.data.converter;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToDateConverter;

public class StringToDateConverterTest extends AbstractConverterTest {

    @Override
    protected StringToDateConverter getConverter() {
        return new StringToDateConverter();
    }

    @Test
    public void testEmptyStringConversion() {
        assertValue(null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        assertValue(new Date(100, 0, 1), getConverter().convertToModel(
                "Jan 1, 2000 12:00:00 AM", new ValueContext(Locale.ENGLISH)));
    }
}
