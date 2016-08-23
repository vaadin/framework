package com.vaadin.tests.data.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToBooleanConverter;

public class StringToBooleanConverterTest extends AbstractConverterTest {

    @Override
    protected StringToBooleanConverter getConverter() {
        return new StringToBooleanConverter();
    }

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
    public void testEmptyStringConversion() {
        assertResult(null, getConverter().convertToModel("", null));
    }

    @Test
    public void testValueConversion() {
        assertResult(true, getConverter().convertToModel("true", null));
        assertResult(false, getConverter().convertToModel("false", null));
    }

    @Test
    public void testYesNoValueConversion() {
        assertResult(true, yesNoConverter.convertToModel("yes", null));
        assertResult(false, yesNoConverter.convertToModel("no", null));

        Assert.assertEquals("yes",
                yesNoConverter.convertToPresentation(true, null));
        Assert.assertEquals("no",
                yesNoConverter.convertToPresentation(false, null));
    }

    @Test
    public void testLocale() {
        Assert.assertEquals("May 18, 2033",
                localeConverter.convertToPresentation(true, Locale.US));
        Assert.assertEquals("January 24, 2065",
                localeConverter.convertToPresentation(false, Locale.US));

        Assert.assertEquals("18. Mai 2033",
                localeConverter.convertToPresentation(true, Locale.GERMANY));
        Assert.assertEquals("24. Januar 2065",
                localeConverter.convertToPresentation(false, Locale.GERMANY));
    }
}
