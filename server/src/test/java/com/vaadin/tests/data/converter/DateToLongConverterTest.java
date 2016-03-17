package com.vaadin.tests.data.converter;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.DateToLongConverter;

public class DateToLongConverterTest {

    DateToLongConverter converter = new DateToLongConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, Long.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(Long.valueOf(946677600000l),
                converter.convertToModel(new Date(100, 0, 1), Long.class, null));
    }
}
