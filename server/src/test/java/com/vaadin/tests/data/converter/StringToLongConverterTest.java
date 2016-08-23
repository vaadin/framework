package com.vaadin.tests.data.converter;

import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.StringToLongConverter;

public class StringToLongConverterTest extends AbstractConverterTest {

    @Override
    protected StringToLongConverter getConverter() {
        return new StringToLongConverter();
    }

    @Test
    public void testEmptyStringConversion() {
        assertResult(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueConversion() {
        assertResult(Long.valueOf(10),
                getConverter().convertToModel("10", null));
    }

    @Test
    public void testExtremeLongValueConversion() {
        Result<Long> l = getConverter().convertToModel("9223372036854775807",
                null);
        assertResult(Long.MAX_VALUE, l);
        l = getConverter().convertToModel("-9223372036854775808", null);
        assertResult(Long.MIN_VALUE, l);
    }

    @Test
    public void testOutOfBoundsValueConversion() {
        // Long.MAX_VALUE+1 is converted to Long.MAX_VALUE
        Result<Long> l = getConverter().convertToModel("9223372036854775808",
                null);
        assertResult(Long.MAX_VALUE, l);
        // Long.MIN_VALUE-1 is converted to Long.MIN_VALUE
        l = getConverter().convertToModel("-9223372036854775809", null);
        assertResult(Long.MIN_VALUE, l);

    }
}
