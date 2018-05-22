package com.vaadin.tests.data.converter;

import java.math.BigInteger;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToBigIntegerConverter;

public class StringToBigIntegerConverterTest {

    StringToBigIntegerConverter converter = new StringToBigIntegerConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, BigInteger.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", BigInteger.class, null));
    }

    @Test
    public void testValueParsing() {
        String bigInt = "1180591620717411303424"; // 2^70 > 2^63 - 1
        BigInteger converted = converter.convertToModel(bigInt,
                BigInteger.class, null);
        BigInteger expected = new BigInteger(bigInt);
        Assert.assertEquals(
                "Value bigger than max long was converted incorrectly",
                expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigInteger bd = new BigInteger("1000");
        String expected = "1.000";

        String converted = converter.convertToPresentation(bd, String.class,
                Locale.GERMAN);
        Assert.assertEquals(
                "Value with specific locale was converted incorrectly",
                expected, converted);
    }
}
