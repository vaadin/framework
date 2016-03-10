package com.vaadin.tests.data.converter;

import java.util.Locale;

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.ReverseConverter;
import com.vaadin.data.util.converter.StringToLongConverter;

public class StringToLongConverterTest extends TestCase {

    StringToLongConverter converter = new StringToLongConverter();
    Converter<Long, String> reverseConverter = new ReverseConverter<Long, String>(
            converter);

    public void testNullConversion() {
        assertEquals(null, converter.convertToModel(null, Long.class, null));
    }

    public void testReverseNullConversion() {
        assertEquals(null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", Long.class, null));
    }

    public void testValueConversion() {
        assertEquals(Long.valueOf(10),
                converter.convertToModel("10", Long.class, null));
    }

    public void testReverseValueConversion() {
        assertEquals(reverseConverter.convertToModel(10L, String.class, null),
                "10");
    }

    public void testExtremeLongValueConversion() {
        long l = converter.convertToModel("9223372036854775807", Long.class,
                null);
        Assert.assertEquals(Long.MAX_VALUE, l);
        l = converter.convertToModel("-9223372036854775808", Long.class, null);
        assertEquals(Long.MIN_VALUE, l);
    }

    public void testExtremeReverseLongValueConversion() {
        String str = reverseConverter.convertToModel(Long.MAX_VALUE,
                String.class, Locale.ENGLISH);
        Assert.assertEquals("9,223,372,036,854,775,807", str);
        str = reverseConverter.convertToModel(Long.MIN_VALUE, String.class,
                Locale.ENGLISH);
        Assert.assertEquals("-9,223,372,036,854,775,808", str);
    }

    public void testOutOfBoundsValueConversion() {
        // Long.MAX_VALUE+1 is converted to Long.MAX_VALUE
        long l = converter.convertToModel("9223372036854775808", Long.class,
                null);
        Assert.assertEquals(Long.MAX_VALUE, l);
        // Long.MIN_VALUE-1 is converted to Long.MIN_VALUE
        l = converter.convertToModel("-9223372036854775809", Long.class, null);
        assertEquals(Long.MIN_VALUE, l);

    }
}
