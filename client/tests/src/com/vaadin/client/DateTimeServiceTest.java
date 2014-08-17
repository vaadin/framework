package com.vaadin.client;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class DateTimeServiceTest extends TestCase {

    final long MILLISECONDS_PER_DAY = 24 * 3600 * 1000;

    static Map<Date, Integer> isoWeekNumbers = new HashMap<Date, Integer>();
    static {
        isoWeekNumbers.put(getDate(2005, 02, 02), 5);

        isoWeekNumbers.put(getDate(2005, 1, 1), 53);
        isoWeekNumbers.put(getDate(2005, 1, 2), 53);
        isoWeekNumbers.put(getDate(2005, 1, 3), 1);
        isoWeekNumbers.put(getDate(2005, 1, 4), 1);
        isoWeekNumbers.put(getDate(2005, 1, 5), 1);
        isoWeekNumbers.put(getDate(2005, 1, 6), 1);
        isoWeekNumbers.put(getDate(2005, 1, 7), 1);
        isoWeekNumbers.put(getDate(2005, 1, 8), 1);
        isoWeekNumbers.put(getDate(2005, 1, 9), 1);
        isoWeekNumbers.put(getDate(2005, 1, 10), 2);
        isoWeekNumbers.put(getDate(2005, 12, 31), 52);
        isoWeekNumbers.put(getDate(2005, 12, 30), 52);
        isoWeekNumbers.put(getDate(2005, 12, 29), 52);
        isoWeekNumbers.put(getDate(2005, 12, 28), 52);
        isoWeekNumbers.put(getDate(2005, 12, 27), 52);
        isoWeekNumbers.put(getDate(2005, 12, 26), 52);
        isoWeekNumbers.put(getDate(2005, 12, 25), 51);
        isoWeekNumbers.put(getDate(2007, 1, 1), 1);
        isoWeekNumbers.put(getDate(2007, 12, 30), 52);
        isoWeekNumbers.put(getDate(2007, 12, 31), 1);
        isoWeekNumbers.put(getDate(2008, 1, 1), 1);
        isoWeekNumbers.put(getDate(2008, 12, 28), 52);
        isoWeekNumbers.put(getDate(2008, 12, 29), 1);
        isoWeekNumbers.put(getDate(2008, 12, 30), 1);
        isoWeekNumbers.put(getDate(2008, 12, 31), 1);
        isoWeekNumbers.put(getDate(2009, 1, 1), 1);
        isoWeekNumbers.put(getDate(2009, 12, 31), 53);
        isoWeekNumbers.put(getDate(2010, 1, 1), 53);
        isoWeekNumbers.put(getDate(2010, 1, 2), 53);
        isoWeekNumbers.put(getDate(2010, 1, 3), 53);
        isoWeekNumbers.put(getDate(2010, 1, 4), 1);
        isoWeekNumbers.put(getDate(2010, 1, 5), 1);
        isoWeekNumbers.put(getDate(2010, 10, 10), 40);
        isoWeekNumbers.put(getDate(2015, 3, 24), 13);
        isoWeekNumbers.put(getDate(2015, 3, 31), 14);
        isoWeekNumbers.put(getDate(2015, 10, 13), 42);
        isoWeekNumbers.put(getDate(2015, 10, 20), 43);
        isoWeekNumbers.put(getDate(2015, 10, 27), 44);
        isoWeekNumbers.put(getDate(2026, 3, 24), 13);
        isoWeekNumbers.put(getDate(2026, 3, 31), 14);
        isoWeekNumbers.put(getDate(2026, 10, 13), 42);
        isoWeekNumbers.put(getDate(2026, 10, 20), 43);
        isoWeekNumbers.put(getDate(2026, 10, 27), 44);

    }

    /**
     * Test all dates from 1990-1992 + some more and see that {@link Calendar}
     * calculates the ISO week number like we do.
     * 
     */
    public void testISOWeekNumbers() {
        Calendar c = Calendar.getInstance();
        c.set(1990, 1, 1);
        long start = c.getTimeInMillis();

        for (int i = 0; i < 1000; i++) {
            Date d = new Date(start + i * MILLISECONDS_PER_DAY);
            int expected = getCalendarISOWeekNr(d);
            int calculated = DateTimeService.getISOWeekNumber(d);
            assertEquals(d + " should be week " + expected, expected,
                    calculated);

        }
    }

    /**
     * Verify that special cases are handled correctly by us (and
     * {@link Calendar}).
     * 
     */
    public void testSampleISOWeekNumbers() {
        for (Date d : isoWeekNumbers.keySet()) {
            // System.out.println("Sample: " + d);
            int expected = isoWeekNumbers.get(d);
            int calculated = DateTimeService.getISOWeekNumber(d);
            assertEquals(d + " should be week " + expected
                    + " (Java Calendar is wrong?)", expected,
                    getCalendarISOWeekNr(d));
            assertEquals(d + " should be week " + expected, expected,
                    calculated);

        }
    }

    private int getCalendarISOWeekNr(Date d) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(4);
        c.setTime(d);

        return c.get(Calendar.WEEK_OF_YEAR);
    }

    private static Date getDate(int year, int month, int date) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, month - 1, date);
        return c.getTime();
    }

}
