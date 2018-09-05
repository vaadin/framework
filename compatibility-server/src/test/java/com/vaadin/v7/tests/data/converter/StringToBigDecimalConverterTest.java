package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.StringToBigDecimalConverter;

public class StringToBigDecimalConverterTest {

    StringToBigDecimalConverter converter = new StringToBigDecimalConverter();

    @Test
    public void testNullConversion() {
        assertEquals(null,
                converter.convertToModel(null, BigDecimal.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertEquals(null,
                converter.convertToModel("", BigDecimal.class, null));
    }

    @Test
    public void testValueParsing() {
        BigDecimal converted = converter.convertToModel("10", BigDecimal.class,
                null);
        BigDecimal expected = new BigDecimal(10);
        assertEquals(expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigDecimal bd = new BigDecimal(12.5);
        String expected = "12,5";

        String converted = converter.convertToPresentation(bd, String.class,
                Locale.GERMAN);
        assertEquals(expected, converted);
    }
}
