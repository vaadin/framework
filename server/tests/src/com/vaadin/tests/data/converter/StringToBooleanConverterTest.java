package com.vaadin.tests.data.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToBooleanConverter;

public class StringToBooleanConverterTest {

    StringToBooleanConverter converter = new StringToBooleanConverter();
    StringToBooleanConverter yesNoConverter = new StringToBooleanConverter("yes","no");
    StringToBooleanConverter localeConverter = new StringToBooleanConverter() {
        @Override
        public String getFalseString(Locale locale) {
            Date d = new Date(3000000000000L);
            return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG,
                    locale).format(
                    d.getTime() + (d.getTimezoneOffset() + 120) * 60 * 1000L);
        }

        @Override
        public String getTrueString(Locale locale) {
            Date d = new Date(2000000000000L);
            return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG,
                    locale).format(
                    d.getTime() + (d.getTimezoneOffset() + 120) * 60 * 1000L);
        }
    };

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, Boolean.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null,
                converter.convertToModel("", Boolean.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertTrue(converter.convertToModel("true", Boolean.class, null));
        Assert.assertFalse(converter.convertToModel("false", Boolean.class,
                null));
    }

    @Test
    public void testYesNoValueConversion() {
        Assert.assertTrue(yesNoConverter.convertToModel("yes", Boolean.class,
                null));
        Assert.assertFalse(yesNoConverter.convertToModel("no", Boolean.class,
                null));

        Assert.assertEquals("yes",
                yesNoConverter.convertToPresentation(true, String.class, null));
        Assert.assertEquals("no",
                yesNoConverter.convertToPresentation(false, String.class, null));
    }

    @Test
    public void testLocale() {
        Assert.assertEquals("May 18, 2033", localeConverter
                .convertToPresentation(true, String.class, Locale.US));
        Assert.assertEquals("January 24, 2065", localeConverter
                .convertToPresentation(false, String.class, Locale.US));

        Assert.assertEquals("18. Mai 2033", localeConverter
                .convertToPresentation(true, String.class, Locale.GERMANY));
        Assert.assertEquals("24. Januar 2065", localeConverter
                .convertToPresentation(false, String.class, Locale.GERMANY));
    }

}
