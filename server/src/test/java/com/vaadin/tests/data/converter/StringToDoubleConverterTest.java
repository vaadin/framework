package com.vaadin.tests.data.converter;

import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.StringToDoubleConverter;

public class StringToDoubleConverterTest extends AbstractConverterTest {

    @Override
    protected StringToDoubleConverter getConverter() {
        return new StringToDoubleConverter();
    }

    @Test
    public void testEmptyStringConversion() {
        assertResult(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueConversion() {
        Result<Double> value = getConverter().convertToModel("10", null);
        assertResult(10.0d, value);
    }
}
