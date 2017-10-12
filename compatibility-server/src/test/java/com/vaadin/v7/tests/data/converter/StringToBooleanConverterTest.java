package com.vaadin.v7.tests.data.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.v7.data.util.converter.StringToBooleanConverter;

public class StringToBooleanConverterTest {

    StringToBooleanConverter converter = new StringToBooleanConverter();
    StringToBooleanConverter yesNoConverter = new StringToBooleanConverter(
            "yes", "no");
    StringToBooleanConverter localeConverter = new StringToBooleanConverter() {
        @Override
        public String getFalseString(Locale locale) {
            Date d = new Date(3000000000000L);
            return SimpleDateFormat
                    .getDateInstance(SimpleDateFormat.LONG, locale)
                    .format(d.getTime()
                            + (d.getTimezoneOffset() + 120) * 60 * 1000L);
        }

        @Override
        public String getTrueString(Locale locale) {
            Date d = new Date(2000000000000L);
            return SimpleDateFormat
                    .getDateInstance(SimpleDateFormat.LONG, locale)
                    .format(d.getTime()
                            + (d.getTimezoneOffset() + 120) * 60 * 1000L);
        }
    };

    @Test
    public void testNullConversion() {
        assertNull(converter.convertToModel(null, Boolean.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        assertNull(converter.convertToModel("", Boolean.class, null));
    }

    @Test
    public void testValueConversion() {
        assertTrue(converter.convertToModel("true", Boolean.class, null));
        assertFalse(converter.convertToModel("false", Boolean.class, null));
    }

    @Test
    public void testYesNoValueConversion() {
        assertTrue(yesNoConverter.convertToModel("yes", Boolean.class, null));
        assertFalse(yesNoConverter.convertToModel("no", Boolean.class, null));

        assertEquals("yes",
                yesNoConverter.convertToPresentation(true, String.class, null));
        assertEquals("no", yesNoConverter.convertToPresentation(false,
                String.class, null));
    }

    @Test
    public void testLocale() {
        assertEquals("May 18, 2033", localeConverter.convertToPresentation(true,
                String.class, Locale.US));
        assertEquals("January 24, 2065", localeConverter
                .convertToPresentation(false, String.class, Locale.US));

        assertEquals("18. Mai 2033", localeConverter.convertToPresentation(true,
                String.class, Locale.GERMANY));
        assertEquals("24. Januar 2065", localeConverter
                .convertToPresentation(false, String.class, Locale.GERMANY));
    }
}
