package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.Converter;

public abstract class AbstractConverterTest {

    @Test
    public void testNullConversion() {
        assertValue(null, getConverter().convertToModel(null, null));
    }

    protected abstract Converter<?, ?> getConverter();

    protected void assertValue(Object object, Result<?> result) {
        assertValue(null, object, result);
    }

    protected void assertValue(String error, Object object, Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertFalse("Result is not ok", result.isError());
        Assert.assertEquals(object,
                result.getOrThrow(message -> new AssertionError(
                        error != null ? error : message)));
    }

    protected void assertError(String expected, Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertTrue("Result should be an error", result.isError());
        Assert.assertEquals(expected, result.getMessage().get());
    }

    protected String getErrorMessage() {
        return "conversion failed";
    }

}
