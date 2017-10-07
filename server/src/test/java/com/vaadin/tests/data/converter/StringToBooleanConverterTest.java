package com.vaadin.tests.data.converter;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBooleanConverter;

public class StringToBooleanConverterTest extends AbstractStringConverterTest {

    @Override
    protected StringToBooleanConverter getConverter() {
        return new StringToBooleanConverter(getErrorMessage());
    }

    private StringToBooleanConverter yesNoConverter = new StringToBooleanConverter(
            getErrorMessage(), "yes", "no");
    private StringToBooleanConverter emptyTrueConverter = new StringToBooleanConverter(
            getErrorMessage(), "", "ABSENT");
    private final StringToBooleanConverter localeConverter = new StringToBooleanConverter(
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
        assertValue(true,
                getConverter().convertToModel("true", new ValueContext()));
        assertValue(false,
                getConverter().convertToModel("false", new ValueContext()));
    }

    @Test
    public void testYesNoValueConversion() {
        assertValue(true,
                yesNoConverter.convertToModel("yes", new ValueContext()));
        assertValue(false,
                yesNoConverter.convertToModel("no", new ValueContext()));

        assertEquals("yes",
                yesNoConverter.convertToPresentation(true, new ValueContext()));
        assertEquals("no", yesNoConverter.convertToPresentation(false,
                new ValueContext()));
    }

    @Test
    public void testEmptyTrueValueConversion() {
        assertValue(true,
                emptyTrueConverter.convertToModel("", new ValueContext()));
        assertValue(false, emptyTrueConverter.convertToModel("ABSENT",
                new ValueContext()));

        assertEquals("", emptyTrueConverter.convertToPresentation(true,
                new ValueContext()));
        assertEquals("ABSENT", emptyTrueConverter.convertToPresentation(false,
                new ValueContext()));
    }

    @Test
    public void testLocale() {
        assertEquals("May 18, 2033", localeConverter.convertToPresentation(true,
                new ValueContext(Locale.US)));
        assertEquals("January 24, 2065", localeConverter
                .convertToPresentation(false, new ValueContext(Locale.US)));

        assertEquals("18. Mai 2033", localeConverter.convertToPresentation(true,
                new ValueContext(Locale.GERMANY)));
        assertEquals("24. Januar 2065", localeConverter.convertToPresentation(
                false, new ValueContext(Locale.GERMANY)));
    }
}
