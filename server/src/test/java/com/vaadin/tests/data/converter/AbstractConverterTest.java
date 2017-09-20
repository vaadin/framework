package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public abstract class AbstractConverterTest {

    @Test
    public void testNullConversion() {
        assertValue(null,
                getConverter().convertToModel(null, new ValueContext()));
    }

    protected abstract Converter<?, ?> getConverter();

    protected <T> void assertValue(T expectedValue, Result<?> result) {
        assertValue(null, expectedValue, result);
    }

    protected <T> void assertValue(String assertMessage, T expectedValue,
            Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertFalse("Result is not ok", result.isError());
        Assert.assertEquals(expectedValue,
                result.getOrThrow(message -> new AssertionError(
                        assertMessage != null ? assertMessage : message)));
    }

    protected void assertError(String expectedResultMessage, Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertTrue("Result should be an error", result.isError());
        Assert.assertEquals(expectedResultMessage, result.getMessage().get());
    }

    protected String getErrorMessage() {
        return "conversion failed";
    }

}
