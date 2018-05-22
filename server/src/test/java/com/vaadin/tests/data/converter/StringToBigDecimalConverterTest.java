package com.vaadin.tests.data.converter;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToBigDecimalConverter;

public class StringToBigDecimalConverterTest {

    StringToBigDecimalConverter converter = new StringToBigDecimalConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, BigDecimal.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null,
                converter.convertToModel("", BigDecimal.class, null));
    }

    @Test
    public void testValueParsing() {
        BigDecimal converted = converter.convertToModel("10", BigDecimal.class,
                null);
        BigDecimal expected = new BigDecimal(10);
        Assert.assertEquals(expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigDecimal bd = new BigDecimal(12.5);
        String expected = "12,5";

        String converted = converter.convertToPresentation(bd, String.class,
                Locale.GERMAN);
        Assert.assertEquals(expected, converted);
    }
}
