package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.Converter;

public abstract class AbstractStringConverterTest
        extends AbstractConverterTest {

    @Override
    protected abstract Converter<String, ?> getConverter();

    @Test
    public void testEmptyStringConversion() {
        assertValue("Null value was converted incorrectly", null,
                getConverter().convertToModel("", null));
    }

    @Test
    public void testErrorMessage() {
        Result<?> result = getConverter().convertToModel("abc", null);
        Assert.assertTrue(result.isError());
        Assert.assertEquals(getErrorMessage(), result.getMessage().get());
    }

    protected String getErrorMessage() {
        return "conversion failed";
    }

}
