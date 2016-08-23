package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToIntegerConverter;

public class StringToIntegerConverterTest extends AbstractConverterTest {

    @Override
    protected StringToIntegerConverter getConverter() {
        return new StringToIntegerConverter("Failed");
    }

    @Test
    public void testEmptyStringConversion() {
        assertResult(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueOutOfRange() {
        Double[] values = new Double[] { Integer.MAX_VALUE * 2.0,
                Integer.MIN_VALUE * 2.0, Long.MAX_VALUE * 2.0,
                Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                getConverter().convertToModel(String.format("%.0f", value),
                        null);
            } catch (Exception e) {
                accepted = true;
            }
        }
        Assert.assertFalse("Accepted value outside range of int", accepted);
    }

    @Test
    public void testValueConversion() {
        assertResult(Integer.valueOf(10),
                getConverter().convertToModel("10", null));
    }
}
