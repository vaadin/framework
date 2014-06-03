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

/**
 * 
 */
package com.vaadin.server;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Logger;

import com.vaadin.shared.ui.ui.UIState.LocaleData;
import com.vaadin.shared.ui.ui.UIState.LocaleServiceState;
import com.vaadin.ui.UI;

/**
 * Server side service which handles locale and the transmission of locale date
 * to the client side LocaleService.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public class LocaleService implements Serializable {

    private UI ui;

    private LocaleServiceState state;

    /**
     * Creates a LocaleService bound to the given UI
     * 
     * @since 7.1
     * @param ui
     *            The UI which owns the LocaleService
     */
    public LocaleService(UI ui, LocaleServiceState state) {
        this.ui = ui;
        this.state = state;
    }

    /**
     * Retrieves the UI this service is bound to
     * 
     * @since 7.1
     * @return the UI for this service
     */
    public UI getUI() {
        return ui;
    }

    /**
     * Adds a locale to be sent to the client (browser) for date and time entry
     * etc. All locale specific information is derived from server-side
     * {@link Locale} instances and sent to the client when needed, eliminating
     * the need to use the {@link Locale} class and all the framework behind it
     * on the client.
     * 
     * @param locale
     *            The locale which is required on the client side
     */
    public void addLocale(Locale locale) {
        for (LocaleData data : getState(false).localeData) {
            if (data.name.equals(locale.toString())) {
                // Already there
                return;
            }
        }

        getState(true).localeData.add(createLocaleData(locale));
    }

    /**
     * Returns the state for this service
     * <p>
     * The state is transmitted inside the UI state rather than as an individual
     * entity.
     * </p>
     * 
     * @since 7.1
     * @param markAsDirty
     *            true to mark the state as dirty
     * @return a LocaleServiceState object that can be read in any case and
     *         modified if markAsDirty is true
     */
    private LocaleServiceState getState(boolean markAsDirty) {
        if (markAsDirty) {
            getUI().markAsDirty();
        }

        return state;
    }

    /**
     * Creates a LocaleData instance for transportation to the client
     * 
     * @since 7.1
     * @param locale
     *            The locale for which to create a LocaleData object
     * @return A LocaleData object with information about the given locale
     */
    protected LocaleData createLocaleData(Locale locale) {
        LocaleData localeData = new LocaleData();
        localeData.name = locale.toString();

        final DateFormatSymbols dfs = new DateFormatSymbols(locale);
        localeData.shortMonthNames = dfs.getShortMonths();
        localeData.monthNames = dfs.getMonths();
        // Client expects 0 based indexing, DateFormatSymbols use 1 based
        localeData.shortDayNames = new String[7];
        localeData.dayNames = new String[7];
        String[] sDayNames = dfs.getShortWeekdays();
        String[] lDayNames = dfs.getWeekdays();
        for (int i = 0; i < 7; i++) {
            localeData.shortDayNames[i] = sDayNames[i + 1];
            localeData.dayNames[i] = lDayNames[i + 1];
        }

        /*
         * First day of week (0 = sunday, 1 = monday)
         */
        final java.util.Calendar cal = new GregorianCalendar(locale);
        localeData.firstDayOfWeek = cal.getFirstDayOfWeek() - 1;

        /*
         * Date formatting (MM/DD/YYYY etc.)
         */

        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT, locale);
        if (!(dateFormat instanceof SimpleDateFormat)) {
            getLogger().warning(
                    "Unable to get default date pattern for locale "
                            + locale.toString());
            dateFormat = new SimpleDateFormat();
        }
        final String df = ((SimpleDateFormat) dateFormat).toPattern();

        int timeStart = df.indexOf("H");
        if (timeStart < 0) {
            timeStart = df.indexOf("h");
        }
        final int ampm_first = df.indexOf("a");
        // E.g. in Korean locale AM/PM is before h:mm
        // TODO should take that into consideration on client-side as well,
        // now always h:mm a
        if (ampm_first > 0 && ampm_first < timeStart) {
            timeStart = ampm_first;
        }
        // Hebrew locale has time before the date
        final boolean timeFirst = timeStart == 0;
        String dateformat;
        if (timeFirst) {
            int dateStart = df.indexOf(' ');
            if (ampm_first > dateStart) {
                dateStart = df.indexOf(' ', ampm_first);
            }
            dateformat = df.substring(dateStart + 1);
        } else {
            dateformat = df.substring(0, timeStart - 1);
        }

        localeData.dateFormat = dateformat.trim();

        /*
         * Time formatting (24 or 12 hour clock and AM/PM suffixes)
         */
        final String timeformat = df.substring(timeStart, df.length());
        /*
         * Doesn't return second or milliseconds.
         * 
         * We use timeformat to determine 12/24-hour clock
         */
        final boolean twelve_hour_clock = timeformat.indexOf("a") > -1;
        // TODO there are other possibilities as well, like 'h' in french
        // (ignore them, too complicated)
        final String hour_min_delimiter = timeformat.indexOf(".") > -1 ? "."
                : ":";
        // outWriter.print("\"tf\":\"" + timeformat + "\",");
        localeData.twelveHourClock = twelve_hour_clock;
        localeData.hourMinuteDelimiter = hour_min_delimiter;
        if (twelve_hour_clock) {
            final String[] ampm = dfs.getAmPmStrings();
            localeData.am = ampm[0];
            localeData.pm = ampm[1];
        }

        return localeData;
    }

    private static Logger getLogger() {
        return Logger.getLogger(LocaleService.class.getName());
    }

}
