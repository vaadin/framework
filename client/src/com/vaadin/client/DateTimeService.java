/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.vaadin.shared.ui.datefield.Resolution;

/**
 * This class provides date/time parsing services to all components on the
 * client side.
 * 
 * @author Vaadin Ltd.
 * 
 */
@SuppressWarnings("deprecation")
public class DateTimeService {

    private String currentLocale;

    private static int[] maxDaysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31 };

    /**
     * Creates a new date time service with the application default locale.
     */
    public DateTimeService() {
        currentLocale = LocaleService.getDefaultLocale();
    }

    /**
     * Creates a new date time service with a given locale.
     * 
     * @param locale
     *            e.g. fi, en etc.
     * @throws LocaleNotLoadedException
     */
    public DateTimeService(String locale) throws LocaleNotLoadedException {
        setLocale(locale);
    }

    public void setLocale(String locale) throws LocaleNotLoadedException {
        if (LocaleService.getAvailableLocales().contains(locale)) {
            currentLocale = locale;
        } else {
            throw new LocaleNotLoadedException(locale);
        }
    }

    public String getLocale() {
        return currentLocale;
    }

    public String getMonth(int month) {
        try {
            return LocaleService.getMonthNames(currentLocale)[month];
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return null;
        }
    }

    public String getShortMonth(int month) {
        try {
            return LocaleService.getShortMonthNames(currentLocale)[month];
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return null;
        }
    }

    public String getDay(int day) {
        try {
            return LocaleService.getDayNames(currentLocale)[day];
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return null;
        }
    }

    public String getShortDay(int day) {
        try {
            return LocaleService.getShortDayNames(currentLocale)[day];
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return null;
        }
    }

    public int getFirstDayOfWeek() {
        try {
            return LocaleService.getFirstDayOfWeek(currentLocale);
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return 0;
        }
    }

    public boolean isTwelveHourClock() {
        try {
            return LocaleService.isTwelveHourClock(currentLocale);
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return false;
        }
    }

    public String getClockDelimeter() {
        try {
            return LocaleService.getClockDelimiter(currentLocale);
        } catch (final LocaleNotLoadedException e) {
            VConsole.error(e);
            return ":";
        }
    }

    private static final String[] DEFAULT_AMPM_STRINGS = { "AM", "PM" };

    public String[] getAmPmStrings() {
        try {
            return LocaleService.getAmPmStrings(currentLocale);
        } catch (final LocaleNotLoadedException e) {
            // TODO can this practically even happen? Should die instead?
            VConsole.error("Locale not loaded, using fallback : AM/PM");
            VConsole.error(e);
            return DEFAULT_AMPM_STRINGS;
        }
    }

    public int getStartWeekDay(Date date) {
        final Date dateForFirstOfThisMonth = new Date(date.getYear(),
                date.getMonth(), 1);
        int firstDay;
        try {
            firstDay = LocaleService.getFirstDayOfWeek(currentLocale);
        } catch (final LocaleNotLoadedException e) {
            VConsole.error("Locale not loaded, using fallback 0");
            VConsole.error(e);
            firstDay = 0;
        }
        int start = dateForFirstOfThisMonth.getDay() - firstDay;
        if (start < 0) {
            start = 6;
        }
        return start;
    }

    public static void setMilliseconds(Date date, int ms) {
        date.setTime(date.getTime() / 1000 * 1000 + ms);
    }

    public static int getMilliseconds(Date date) {
        if (date == null) {
            return 0;
        }

        return (int) (date.getTime() - date.getTime() / 1000 * 1000);
    }

    public static int getNumberOfDaysInMonth(Date date) {
        final int month = date.getMonth();
        if (month == 1 && true == isLeapYear(date)) {
            return 29;
        }
        return maxDaysInMonth[month];
    }

    public static boolean isLeapYear(Date date) {
        // Instantiate the date for 1st March of that year
        final Date firstMarch = new Date(date.getYear(), 2, 1);

        // Go back 1 day
        final long firstMarchTime = firstMarch.getTime();
        final long lastDayTimeFeb = firstMarchTime - (24 * 60 * 60 * 1000); // NUM_MILLISECS_A_DAY

        // Instantiate new Date with this time
        final Date febLastDay = new Date(lastDayTimeFeb);

        // Check for date in this new instance
        return (29 == febLastDay.getDate()) ? true : false;
    }

    public static boolean isSameDay(Date d1, Date d2) {
        return (getDayInt(d1) == getDayInt(d2));
    }

    public static boolean isInRange(Date date, Date rangeStart, Date rangeEnd,
            Resolution resolution) {
        Date s;
        Date e;
        if (rangeStart.after(rangeEnd)) {
            s = rangeEnd;
            e = rangeStart;
        } else {
            e = rangeEnd;
            s = rangeStart;
        }
        long start = s.getYear() * 10000000000l;
        long end = e.getYear() * 10000000000l;
        long target = date.getYear() * 10000000000l;

        if (resolution == Resolution.YEAR) {
            return (start <= target && end >= target);
        }
        start += s.getMonth() * 100000000l;
        end += e.getMonth() * 100000000l;
        target += date.getMonth() * 100000000l;
        if (resolution == Resolution.MONTH) {
            return (start <= target && end >= target);
        }
        start += s.getDate() * 1000000l;
        end += e.getDate() * 1000000l;
        target += date.getDate() * 1000000l;
        if (resolution == Resolution.DAY) {
            return (start <= target && end >= target);
        }
        start += s.getHours() * 10000l;
        end += e.getHours() * 10000l;
        target += date.getHours() * 10000l;
        if (resolution == Resolution.HOUR) {
            return (start <= target && end >= target);
        }
        start += s.getMinutes() * 100l;
        end += e.getMinutes() * 100l;
        target += date.getMinutes() * 100l;
        if (resolution == Resolution.MINUTE) {
            return (start <= target && end >= target);
        }
        start += s.getSeconds();
        end += e.getSeconds();
        target += date.getSeconds();
        return (start <= target && end >= target);

    }

    private static int getDayInt(Date date) {
        final int y = date.getYear();
        final int m = date.getMonth();
        final int d = date.getDate();

        return ((y + 1900) * 10000 + m * 100 + d) * 1000000000;
    }

    /**
     * Returns the ISO-8601 week number of the given date.
     * 
     * @param date
     *            The date for which the week number should be resolved
     * @return The ISO-8601 week number for {@literal date}
     */
    public static int getISOWeekNumber(Date date) {
        final long MILLISECONDS_PER_DAY = 24 * 3600 * 1000;
        int dayOfWeek = date.getDay(); // 0 == sunday

        // ISO 8601 use weeks that start on monday so we use
        // mon=1,tue=2,...sun=7;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        // Find nearest thursday (defines the week in ISO 8601). The week number
        // for the nearest thursday is the same as for the target date.
        int nearestThursdayDiff = 4 - dayOfWeek; // 4 is thursday
        Date nearestThursday = new Date(date.getTime() + nearestThursdayDiff
                * MILLISECONDS_PER_DAY);

        Date firstOfJanuary = new Date(nearestThursday.getYear(), 0, 1);
        long timeDiff = nearestThursday.getTime() - firstOfJanuary.getTime();

        // Rounding the result, as the division doesn't result in an integer
        // when the given date is inside daylight saving time period.
        int daysSinceFirstOfJanuary = (int) Math.round((double) timeDiff
                / MILLISECONDS_PER_DAY);

        int weekNumber = (daysSinceFirstOfJanuary) / 7 + 1;

        return weekNumber;
    }

    /**
     * Check if format contains the month name. If it does we manually convert
     * it to the month name since DateTimeFormat.format always uses the current
     * locale and will replace the month name wrong if current locale is
     * different from the locale set for the DateField.
     * 
     * MMMM is converted into long month name, MMM is converted into short month
     * name. '' are added around the name to avoid that DateTimeFormat parses
     * the month name as a pattern.
     * 
     * @param date
     *            The date to convert
     * @param formatStr
     *            The format string that might contain MMM or MMMM
     * @param dateTimeService
     *            Reference to the Vaadin DateTimeService
     * @return
     */
    public String formatDate(Date date, String formatStr) {
        /*
         * Format month and day names separately when locale for the
         * DateTimeService is not the same as the browser locale
         */
        formatStr = formatMonthNames(date, formatStr);
        formatStr = formatDayNames(date, formatStr);

        // Format uses the browser locale
        DateTimeFormat format = DateTimeFormat.getFormat(formatStr);

        String result = format.format(date);

        return result;
    }

    private String formatDayNames(Date date, String formatStr) {
        if (formatStr.contains("EEEE")) {
            String dayName = getDay(date.getDay());

            if (dayName != null) {
                /*
                 * Replace 4 or more E:s with the quoted day name. Also
                 * concatenate generated string with any other string prepending
                 * or following the EEEE pattern, i.e. 'EEEE'ta ' becomes 'DAYta
                 * ' and not 'DAY''ta ', 'ab'EEEE becomes 'abDAY', 'x'EEEE'y'
                 * becomes 'xDAYy'.
                 */
                formatStr = formatStr.replaceAll("'([E]{4,})'", dayName);
                formatStr = formatStr.replaceAll("([E]{4,})'", "'" + dayName);
                formatStr = formatStr.replaceAll("'([E]{4,})", dayName + "'");
                formatStr = formatStr
                        .replaceAll("[E]{4,}", "'" + dayName + "'");
            }
        }

        if (formatStr.contains("EEE")) {

            String dayName = getShortDay(date.getDay());

            if (dayName != null) {
                /*
                 * Replace 3 or more E:s with the quoted month name. Also
                 * concatenate generated string with any other string prepending
                 * or following the EEE pattern, i.e. 'EEE'ta ' becomes 'DAYta '
                 * and not 'DAY''ta ', 'ab'EEE becomes 'abDAY', 'x'EEE'y'
                 * becomes 'xDAYy'.
                 */
                formatStr = formatStr.replaceAll("'([E]{3,})'", dayName);
                formatStr = formatStr.replaceAll("([E]{3,})'", "'" + dayName);
                formatStr = formatStr.replaceAll("'([E]{3,})", dayName + "'");
                formatStr = formatStr
                        .replaceAll("[E]{3,}", "'" + dayName + "'");
            }
        }

        return formatStr;
    }

    private String formatMonthNames(Date date, String formatStr) {
        if (formatStr.contains("MMMM")) {
            String monthName = getMonth(date.getMonth());

            if (monthName != null) {
                /*
                 * Replace 4 or more M:s with the quoted month name. Also
                 * concatenate generated string with any other string prepending
                 * or following the MMMM pattern, i.e. 'MMMM'ta ' becomes
                 * 'MONTHta ' and not 'MONTH''ta ', 'ab'MMMM becomes 'abMONTH',
                 * 'x'MMMM'y' becomes 'xMONTHy'.
                 */
                formatStr = formatStr.replaceAll("'([M]{4,})'", monthName);
                formatStr = formatStr.replaceAll("([M]{4,})'", "'" + monthName);
                formatStr = formatStr.replaceAll("'([M]{4,})", monthName + "'");
                formatStr = formatStr.replaceAll("[M]{4,}", "'" + monthName
                        + "'");
            }
        }

        if (formatStr.contains("MMM")) {

            String monthName = getShortMonth(date.getMonth());

            if (monthName != null) {
                /*
                 * Replace 3 or more M:s with the quoted month name. Also
                 * concatenate generated string with any other string prepending
                 * or following the MMM pattern, i.e. 'MMM'ta ' becomes 'MONTHta
                 * ' and not 'MONTH''ta ', 'ab'MMM becomes 'abMONTH', 'x'MMM'y'
                 * becomes 'xMONTHy'.
                 */
                formatStr = formatStr.replaceAll("'([M]{3,})'", monthName);
                formatStr = formatStr.replaceAll("([M]{3,})'", "'" + monthName);
                formatStr = formatStr.replaceAll("'([M]{3,})", monthName + "'");
                formatStr = formatStr.replaceAll("[M]{3,}", "'" + monthName
                        + "'");
            }
        }

        return formatStr;
    }

    /**
     * Replaces month names in the entered date with the name in the current
     * browser locale.
     * 
     * @param enteredDate
     *            Date string e.g. "5 May 2010"
     * @param formatString
     *            Format string e.g. "d M yyyy"
     * @return The date string where the month names have been replaced by the
     *         browser locale version
     */
    private String parseMonthName(String enteredDate, String formatString) {
        LocaleInfo browserLocale = LocaleInfo.getCurrentLocale();
        if (browserLocale.getLocaleName().equals(getLocale())) {
            // No conversion needs to be done when locales match
            return enteredDate;
        }
        String[] browserMonthNames = browserLocale.getDateTimeConstants()
                .months();
        String[] browserShortMonthNames = browserLocale.getDateTimeConstants()
                .shortMonths();

        if (formatString.contains("MMMM")) {
            // Full month name
            for (int i = 0; i < 12; i++) {
                enteredDate = enteredDate.replaceAll(getMonth(i),
                        browserMonthNames[i]);
            }
        }
        if (formatString.contains("MMM")) {
            // Short month name
            for (int i = 0; i < 12; i++) {
                enteredDate = enteredDate.replaceAll(getShortMonth(i),
                        browserShortMonthNames[i]);
            }
        }

        return enteredDate;
    }

    /**
     * Parses the given date string using the given format string and the locale
     * set in this DateTimeService instance.
     * 
     * @param dateString
     *            Date string e.g. "1 February 2010"
     * @param formatString
     *            Format string e.g. "d MMMM yyyy"
     * @param lenient
     *            true to use lenient parsing, false to use strict parsing
     * @return A Date object representing the dateString. Never returns null.
     * @throws IllegalArgumentException
     *             if the parsing fails
     * 
     */
    public Date parseDate(String dateString, String formatString,
            boolean lenient) throws IllegalArgumentException {
        /* DateTimeFormat uses the browser's locale */
        DateTimeFormat format = DateTimeFormat.getFormat(formatString);

        /*
         * Parse month names separately when locale for the DateTimeService is
         * not the same as the browser locale
         */
        dateString = parseMonthName(dateString, formatString);

        Date date;

        if (lenient) {
            date = format.parse(dateString);
        } else {
            date = format.parseStrict(dateString);
        }

        // Some version of Firefox sets the timestamp to 0 if parsing fails.
        if (date != null && date.getTime() == 0) {
            throw new IllegalArgumentException("Parsing of '" + dateString
                    + "' failed");
        }

        return date;

    }

}
