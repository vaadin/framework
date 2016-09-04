package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.StringToDoubleConverter;

public class StringToDoubleConverterTest extends AbstractConverterTest {

    @Override
    protected StringToDoubleConverter getConverter() {
        return new StringToDoubleConverter("Failed");
    }

    @Test
    public void testEmptyStringConversion() {
        assertValue(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueConversion() {
        Result<Double> value = getConverter().convertToModel("10", null);
        assertValue(10.0d, value);
    }

    @Test
    public void testErrorMessage() {
        Result<Double> result = getConverter().convertToModel("abc", null);
        Assert.assertTrue(result.isError());
        Assert.assertEquals("Failed", result.getMessage().get());
    }

}
