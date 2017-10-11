package com.vaadin.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public abstract class AbstractStringConverterTest
        extends AbstractConverterTest {

    @Override
    protected abstract Converter<String, ?> getConverter();

    @Test
    public void testEmptyStringConversion() {
        assertValue("Null value was converted incorrectly", null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testErrorMessage() {
        Result<?> result = getConverter().convertToModel("abc",
                new ValueContext());
        assertTrue(result.isError());
        assertEquals(getErrorMessage(), result.getMessage().get());
    }

    @Override
    protected String getErrorMessage() {
        return "conversion failed";
    }

}
