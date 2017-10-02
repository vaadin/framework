package com.vaadin.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;

public class StringToIntegerConverterTest extends AbstractConverterTest {

    @Override
    protected StringToIntegerConverter getConverter() {
        return new StringToIntegerConverter("Failed");
    }

    @Test
    public void testEmptyStringConversion() {
        assertValue(null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueOutOfRange() {
        Double[] values = { Integer.MAX_VALUE * 2.0, Integer.MIN_VALUE * 2.0,
                Long.MAX_VALUE * 2.0, Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                getConverter().convertToModel(String.format("%.0f", value),
                        new ValueContext());
            } catch (Exception e) {
                accepted = true;
            }
        }
        assertFalse("Accepted value outside range of int", accepted);
    }

    @Test
    public void testValueConversion() {
        assertValue(Integer.valueOf(10),
                getConverter().convertToModel("10", new ValueContext()));
    }

    @Test
    public void testErrorMessage() {
        Result<Integer> result = getConverter().convertToModel("abc",
                new ValueContext());
        assertTrue(result.isError());
        assertEquals("Failed", result.getMessage().get());
    }

    @Test
    public void customEmptyValue() {
        StringToIntegerConverter converter = new StringToIntegerConverter(0,
                getErrorMessage());

        assertValue(0, converter.convertToModel("", new ValueContext()));
        assertEquals("0",
                converter.convertToPresentation(0, new ValueContext()));
    }
}
