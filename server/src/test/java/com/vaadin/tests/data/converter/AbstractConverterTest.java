package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.Converter;

public abstract class AbstractConverterTest {

    @Test
    public void testNullConversion() {
        assertResult(null, getConverter().convertToModel(null, null));
    }

    protected abstract Converter<?, ?> getConverter();

    protected void assertResult(Object object, Result<?> result) {
        assertResult(null, object, result);
    }

    protected void assertResult(String error, Object object, Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertFalse("Result is not ok", result.isError());
        Assert.assertEquals(object,
                result.getOrThrow(message -> new AssertionError(
                        error != null ? error : message)));
    }

}
