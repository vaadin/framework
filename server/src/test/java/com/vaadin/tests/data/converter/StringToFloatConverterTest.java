package com.vaadin.tests.data.converter;

import org.junit.Test;

import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.data.converter.ValueContext;

public class StringToFloatConverterTest extends AbstractStringConverterTest {

    @Override
    protected StringToFloatConverter getConverter() {
        return new StringToFloatConverter(getErrorMessage());
    }

    @Override
    @Test
    public void testNullConversion() {
        assertValue(null,
                getConverter().convertToModel(null, new ValueContext()));
    }

    @Override
    @Test
    public void testEmptyStringConversion() {
        assertValue(null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        assertValue(Float.valueOf(10),
                getConverter().convertToModel("10", new ValueContext()));
    }
}
