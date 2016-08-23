package com.vaadin.tests.data.converter;

import org.junit.Test;

import com.vaadin.data.util.converter.StringToFloatConverter;

public class StringToFloatConverterTest extends AbstractConverterTest {

    @Override
    protected StringToFloatConverter getConverter() {
        return new StringToFloatConverter();
    }

    @Override
    @Test
    public void testNullConversion() {
        assertResult(null, getConverter().convertToModel(null, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertResult(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueConversion() {
        assertResult(Float.valueOf(10),
                getConverter().convertToModel("10", null));
    }
}
