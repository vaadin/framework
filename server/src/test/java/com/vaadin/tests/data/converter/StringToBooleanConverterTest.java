package com.vaadin.tests.data.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.StringToBooleanConverter;

public class StringToBooleanConverterTest extends AbstractStringConverterTest {

    @Override
    protected StringToBooleanConverter getConverter() {
        return new StringToBooleanConverter(getErrorMessage());
    }

    private StringToBooleanConverter yesNoConverter = new StringToBooleanConverter(
            getErrorMessage(), "yes", "no");
    private StringToBooleanConverter emptyTrueConverter = new StringToBooleanConverter(
            getErrorMessage(), "", "ABSENT");
    private StringToBooleanConverter localeConverter = new StringToBooleanConverter(
            getErrorMessage()) {
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
    public void testValueConversion() {
        assertValue(true, getConverter().convertToModel("true", null));
        assertValue(false, getConverter().convertToModel("false", null));
    }

    @Test
    public void testYesNoValueConversion() {
        assertValue(true, yesNoConverter.convertToModel("yes", null));
        assertValue(false, yesNoConverter.convertToModel("no", null));

        Assert.assertEquals("yes",
                yesNoConverter.convertToPresentation(true, null));
        Assert.assertEquals("no",
                yesNoConverter.convertToPresentation(false, null));
    }

    @Test
    public void testEmptyTrueValueConversion() {
        assertValue(true, emptyTrueConverter.convertToModel("", null));
        assertValue(false, emptyTrueConverter.convertToModel("ABSENT", null));

        Assert.assertEquals("",
                emptyTrueConverter.convertToPresentation(true, null));
        Assert.assertEquals("ABSENT",
                emptyTrueConverter.convertToPresentation(false, null));
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
