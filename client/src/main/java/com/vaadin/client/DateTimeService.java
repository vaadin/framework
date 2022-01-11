/*
 * Copyright 2000-2022 Vaadin Ltd.
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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.vaadin.shared.ui.datefield.DateResolution;

/**
 * This class provides date/time parsing services to all components on the
 * client side.
 *
 * @author Vaadin Ltd.
 *
 */
@SuppressWarnings("deprecation")
public class DateTimeService {

    private String locale;

    private static int[] maxDaysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31 };
    private int[] yearDays = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273,
            304, 334 };
    private int[] leapYearDays = { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274,
            305, 335};

    private static final long MILLISECONDS_PER_DAY = 24 * 3600 * 1000;

    /**
     * Creates a new date time service with the application default locale.
     */
    public DateTimeService() {
        locale = LocaleService.getDefaultLocale();
    }

    /**
     * Creates a new date time service with a given locale.
     *
     * @param locale
     *            e.g. {@code fi}, {@code en}, etc.
     * @throws LocaleNotLoadedException
     */
    public DateTimeService(String locale) throws LocaleNotLoadedException {
        setLocale(locale);
    }

    /**
     * Utility method to format positive int as zero-padded two-digits number.
     *
     * @param i
     *            the value
     * @return "00".."99"
     * @since 8.4
     */
    public static String asTwoDigits(int i) {
        return (i < 10 ? "0" : "") + i;
    }

    public void setLocale(String locale) throws LocaleNotLoadedException {
        if (!LocaleService.getAvailableLocales().contains(locale)) {
            throw new LocaleNotLoadedException(locale);
        }
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public String getMonth(int month) {
        try {
            return LocaleService.getMonthNames(locale)[month];
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in getMonth", e);
            return null;
        }
    }

    public String getShortMonth(int month) {
        try {
            return LocaleService.getShortMonthNames(locale)[month];
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in getShortMonth", e);
            return null;
        }
    }

    public String getDay(int day) {
        try {
            return LocaleService.getDayNames(locale)[day];
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in getDay", e);
            return null;
        }
    }

    /**
     * Returns the localized short name of the specified day.
     *
     * @param day
     *            the day, {@code 0} is {@code SUNDAY}
     * @return the localized short name
     */
    public String getShortDay(int day) {
        try {
            return LocaleService.getShortDayNames(locale)[day];
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in getShortDay", e);
            return null;
        }
    }

    /**
     * Returns the first day of the week, according to the used Locale.
     *
     * @return the localized first day of the week, {@code 0} is {@code SUNDAY}
     */
    public int getFirstDayOfWeek() {
        try {
            return LocaleService.getFirstDayOfWeek(locale);
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in getFirstDayOfWeek", e);
            return 0;
        }
    }

    /**
     * Returns whether the locale has twelve hour, or twenty four hour clock.
     *
     * @return {@code true} if the locale has twelve hour clock, {@code false}
     *         for twenty four clock
     */
    public boolean isTwelveHourClock() {
        try {
            return LocaleService.isTwelveHourClock(locale);
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in isTwelveHourClock", e);
            return false;
        }
    }

    public String getClockDelimeter() {
        try {
            return LocaleService.getClockDelimiter(locale);
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Error in getClockDelimiter", e);
            return ":";
        }
    }

    private static final String[] DEFAULT_AMPM_STRINGS = { "AM", "PM" };

    public String[] getAmPmStrings() {
        try {
            return LocaleService.getAmPmStrings(locale);
        } catch (final LocaleNotLoadedException e) {
            // TODO can this practically even happen? Should die instead?
            getLogger().log(Level.SEVERE,
                    "Locale not loaded, using fallback : AM/PM", e);
            return DEFAULT_AMPM_STRINGS;
        }
    }

    /**
     * Returns the first day of week of the specified {@code month}.
     *
     * @param month
     *            the month, not {@code null}
     * @return the first day of week,
     */
    public int getStartWeekDay(Date month) {
        final Date dateForFirstOfThisMonth = new Date(month.getYear(),
                month.getMonth(), 1);
        int firstDay;
        try {
            firstDay = LocaleService.getFirstDayOfWeek(locale);
        } catch (final LocaleNotLoadedException e) {
            getLogger().log(Level.SEVERE, "Locale not loaded, using fallback 0",
                    e);
            firstDay = 0;
        }
        int start = dateForFirstOfThisMonth.getDay() - firstDay;
        if (start < 0) {
            start += 7;
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
        if (month == 1 && isLeapYear(date)) {
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
            DateResolution resolution) {
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

        if (resolution == DateResolution.YEAR) {
            return (start <= target && end >= target);
        }
        start += s.getMonth() * 100000000l;
        end += e.getMonth() * 100000000l;
        target += date.getMonth() * 100000000l;
        if (resolution == DateResolution.MONTH) {
            return (start <= target && end >= target);
        }
        start += s.getDate() * 1000000l;
        end += e.getDate() * 1000000l;
        target += date.getDate() * 1000000l;
        if (resolution == DateResolution.DAY) {
            return (start <= target && end >= target);
        }
        start += s.getHours() * 10000l;
        end += e.getHours() * 10000l;
        target += date.getHours() * 10000l;
        start += s.getMinutes() * 100l;
        end += e.getMinutes() * 100l;
        target += date.getMinutes() * 100l;
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
        int dayOfWeek = date.getDay(); // 0 == sunday

        // ISO 8601 use weeks that start on monday so we use
        // mon=1,tue=2,...sun=7;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        // Find nearest thursday (defines the week in ISO 8601). The week number
        // for the nearest thursday is the same as for the target date.
        int nearestThursdayDiff = 4 - dayOfWeek; // 4 is thursday
        Date nearestThursday = new Date(
                date.getTime() + nearestThursdayDiff * MILLISECONDS_PER_DAY);

        Date firstOfJanuary = new Date(nearestThursday.getYear(), 0, 1);
        long timeDiff = nearestThursday.getTime() - firstOfJanuary.getTime();

        // Rounding the result, as the division doesn't result in an integer
        // when the given date is inside daylight saving time period.
        int daysSinceFirstOfJanuary = (int) Math
                .round((double) timeDiff / MILLISECONDS_PER_DAY);

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
     * @return
     */
    public String formatDate(Date date, String formatStr) {
        return formatDate(date, formatStr, null);
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
     * z is converted into the time zone name, using the specified
     * {@code timeZoneJSON}
     *
     * @param date
     *            The date to convert
     * @param formatStr
     *            The format string that might contain {@code MMM} or
     *            {@code MMMM}
     * @param timeZone
     *            The {@link TimeZone} used to replace {@code z}, can be
     *            {@code null}
     *
     * @return the formatted date string
     * @since 8.2
     */
    public String formatDate(Date date, String formatStr, TimeZone timeZone) {
        /*
         * Format week numbers
         */
        formatStr = formatWeekNumbers(date, formatStr);
        /*
         * Format month and day names separately when locale for the
         * DateTimeService is not the same as the browser locale
         */
        formatStr = formatTimeZone(date, formatStr, timeZone);
        formatStr = formatMonthNames(date, formatStr);
        formatStr = formatDayNames(date, formatStr);

        // Format uses the browser locale
        DateTimeFormat format = DateTimeFormat.getFormat(formatStr);

        String result = format.format(date);

        return result;
    }

    /*
     * Calculate number of the week in the year based on Date
     * Note, support for "ww" is missing GWT DateTimeFormat
     * and java.util.Calendar is not supported in GWT
     * Hence DIY method needed
     */
    private String getWeek(Date date) {
        int year = date.getYear()+1900;
        int month = date.getMonth();
        int day = date.getDate()+1;
        int weekDay = date.getDay();
        if (weekDay == 6) { 
            weekDay = 0;
        } else {
           weekDay = weekDay - 1;
        }
        boolean leap = false;
        if (((year % 4) == 0) && (((year % 100) != 0) || ((year % 400) == 0))) {
           leap = true;
        }
        int week;
        if (leap) {
            week = countWeek(leapYearDays, month, day, weekDay);
        } else {
            week = countWeek(yearDays, month, day, weekDay);
        }
        return ""+week;
    }

    private int countWeek(int[] days, int month, int day, int weekDay) {
        return ((days[month] + day) - (weekDay + 7) % 7 + 7) / 7;
    }

    private String formatWeekNumbers(Date date, String formatStr) {
        if (formatStr.contains("ww")) {
            String weekNumber = getWeek(date);
            formatStr = formatStr.replaceAll("ww", weekNumber);
        }
        return formatStr;
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
                formatStr = formatStr.replaceAll("[E]{4,}",
                        "'" + dayName + "'");
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
                formatStr = formatStr.replaceAll("[E]{3,}",
                        "'" + dayName + "'");
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
                formatStr = formatStr.replaceAll("[M]{4,}",
                        "'" + monthName + "'");
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
                formatStr = formatStr.replaceAll("[M]{3,}",
                        "'" + monthName + "'");
            }
        }

        return formatStr;
    }

    private String formatTimeZone(Date date, String formatStr,
            TimeZone timeZone) {
        // if 'z' is found outside quotes and timeZone is used
        if (getIndexOf(formatStr, 'z') != -1 && timeZone != null) {
            return replaceTimeZone(formatStr, timeZone.getShortName(date));
        }
        return formatStr;
    }

    /**
     * Replaces the {@code z} characters of the specified {@code formatStr} with
     * the given {@code timeZoneName}.
     *
     * @param formatStr
     *            The format string, which is the pattern describing the date
     *            and time format
     * @param timeZoneName
     *            the time zone name
     * @return the format string, with {@code z} replaced (if found)
     */
    private static String replaceTimeZone(String formatStr,
            String timeZoneName) {

        // search for 'z' outside the quotes (inside quotes is escaped)
        int start = getIndexOf(formatStr, 'z');
        if (start == -1) {
            return formatStr;
        }

        // if there are multiple consecutive 'z', treat them as one
        int end = start;
        while (end + 1 < formatStr.length()
                && formatStr.charAt(end + 1) == 'z') {
            end++;
        }
        return formatStr.substring(0, start) + "'" + timeZoneName + "'"
                + formatStr.substring(end + 1);
    }

    /**
     * Returns the first index of the specified {@code ch}, which is outside the
     * quotes.
     */
    private static int getIndexOf(String str, char ch) {
        boolean inQuote = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\'') {
                if (i + 1 < str.length() && str.charAt(i + 1) == '\'') {
                    i++;
                } else {
                    inQuote ^= true;
                }
            } else if (c == ch && !inQuote) {
                return i;
            }
        }
        return -1;
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
            throw new IllegalArgumentException(
                    "Parsing of '" + dateString + "' failed");
        }

        return date;
    }

    private static Logger getLogger() {
        return Logger.getLogger(DateTimeService.class.getName());
    }
}
