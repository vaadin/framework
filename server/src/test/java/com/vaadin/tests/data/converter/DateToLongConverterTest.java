package com.vaadin.tests.data.converter;

import java.util.Date;

import org.junit.Test;

import com.vaadin.data.util.converter.DateToLongConverter;

public class DateToLongConverterTest extends AbstractConverterTest {

    @Override
    protected DateToLongConverter getConverter() {
        return new DateToLongConverter();
    }

    @Override
    @Test
    public void testNullConversion() {
        assertResult(null, getConverter().convertToModel(null, null));
    }

    @Test
    public void testValueConversion() {
        Date d = new Date(100, 0, 1);
        assertResult(
                Long.valueOf(946677600000l
                        + (d.getTimezoneOffset() + 120) * 60 * 1000L),
                getConverter().convertToModel(d, null));
    }
}
