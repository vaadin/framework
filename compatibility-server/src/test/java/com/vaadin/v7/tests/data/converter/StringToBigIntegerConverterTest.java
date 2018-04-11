package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.StringToBigIntegerConverter;

public class StringToBigIntegerConverterTest {

    StringToBigIntegerConverter converter = new StringToBigIntegerConverter();

    @Test
    public void testNullConversion() {
        assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, BigInteger.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", BigInteger.class, null));
    }

    @Test
    public void testValueParsing() {
        String bigInt = "1180591620717411303424"; // 2^70 > 2^63 - 1
        BigInteger converted = converter.convertToModel(bigInt,
                BigInteger.class, null);
        BigInteger expected = new BigInteger(bigInt);
        assertEquals("Value bigger than max long was converted incorrectly",
                expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigInteger bd = new BigInteger("1000");
        String expected = "1.000";

        String converted = converter.convertToPresentation(bd, String.class,
                Locale.GERMAN);
        assertEquals("Value with specific locale was converted incorrectly",
                expected, converted);
    }
}
