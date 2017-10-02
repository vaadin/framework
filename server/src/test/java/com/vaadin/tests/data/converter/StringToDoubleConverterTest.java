package com.vaadin.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToDoubleConverter;

public class StringToDoubleConverterTest extends AbstractConverterTest {

    @Override
    protected StringToDoubleConverter getConverter() {
        return new StringToDoubleConverter("Failed");
    }

    @Test
    public void testEmptyStringConversion() {
        assertValue(null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        Result<Double> value = getConverter().convertToModel("10",
                new ValueContext());
        assertValue(10.0d, value);
    }

    @Test
    public void testErrorMessage() {
        Result<Double> result = getConverter().convertToModel("abc",
                new ValueContext());
        assertTrue(result.isError());
        assertEquals("Failed", result.getMessage().get());
    }

    @Test
    public void customEmptyValue() {
        StringToDoubleConverter converter = new StringToDoubleConverter(0.0,
                getErrorMessage());

        assertValue(0.0, converter.convertToModel("", new ValueContext()));
        assertEquals("0",
                converter.convertToPresentation(0.0, new ValueContext()));
    }

}
