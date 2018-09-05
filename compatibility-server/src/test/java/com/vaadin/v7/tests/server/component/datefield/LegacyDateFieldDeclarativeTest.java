package com.vaadin.v7.tests.server.component.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.DateField;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 *
 * @author Vaadin Ltd
 * @since 7.4
 */
public class LegacyDateFieldDeclarativeTest
        extends DeclarativeTestBase<DateField> {

    private String getYearResolutionDesign() {
        return "<vaadin7-date-field resolution='year' value='2020'/>";
    }

    private DateField getYearResolutionExpected() {
        DateField df = new DateField();
        df.setResolution(Resolution.YEAR);
        df.setValue(new Date(2020 - 1900, 1 - 1, 1));
        return df;
    }

    private String getTimezoneDesign() {
        String timeZone = new SimpleDateFormat("Z")
                .format(new Date(2014 - 1900, 5 - 1, 5));
        return String.format(
                "<vaadin7-date-field range-start=\"2014-05-05 00:00:00%1$s\" range-end=\"2014-06-05 00:00:00%1$s\" date-out-of-range-message=\"Please select a sensible date\" date-format=\"yyyy-MM-dd\" lenient show-iso-week-numbers parse-error-message=\"You are doing it wrong\" time-zone=\"GMT+05:00\" value=\"2014-05-15 00:00:00%1$s\"/>",
                timeZone);
    }

    private DateField getTimezoneExpected() {
        DateField df = new DateField();

        df.setRangeStart(new Date(2014 - 1900, 5 - 1, 5));
        df.setRangeEnd(new Date(2014 - 1900, 6 - 1, 5));
        df.setDateOutOfRangeMessage("Please select a sensible date");
        df.setResolution(Resolution.DAY);
        df.setDateFormat("yyyy-MM-dd");
        df.setLenient(true);
        df.setShowISOWeekNumbers(true);
        df.setParseErrorMessage("You are doing it wrong");
        df.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        df.setValue(new Date(2014 - 1900, 5 - 1, 15));

        return df;
    }

    @Test
    public void readTimezone() {
        testRead(getTimezoneDesign(), getTimezoneExpected());
    }

    @Test
    public void writeTimezone() {
        testWrite(getTimezoneDesign(), getTimezoneExpected());
    }

    @Test
    public void readYearResolution() {
        testRead(getYearResolutionDesign(), getYearResolutionExpected());
    }

    @Test
    public void writeYearResolution() {
        // Writing is always done in full resolution..
        String timeZone = new SimpleDateFormat("Z")
                .format(new Date(2020 - 1900, 1 - 1, 1));
        testWrite(
                getYearResolutionDesign().replace("2020",
                        "2020-01-01 00:00:00" + timeZone),
                getYearResolutionExpected());
    }

    @Test
    public void testReadOnlyValue() {
        Date date = new Date(2020 - 1900, 1 - 1, 1);
        String timeZone = new SimpleDateFormat("Z").format(date);
        String design = "<vaadin7-date-field readonly resolution='year' value='2020-01-01 00:00:00"
                + timeZone + "'/>";
        DateField df = new DateField();
        df.setResolution(Resolution.YEAR);
        df.setValue(date);
        df.setReadOnly(true);

        testRead(design, df);
        testWrite(design, df);
    }

}
