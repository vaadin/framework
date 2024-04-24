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
package com.vaadin.client.ui;

import static com.vaadin.shared.ui.datefield.DateTimeResolution.DAY;
import static com.vaadin.shared.ui.datefield.DateTimeResolution.HOUR;
import static com.vaadin.shared.ui.datefield.DateTimeResolution.MINUTE;
import static com.vaadin.shared.ui.datefield.DateTimeResolution.MONTH;
import static com.vaadin.shared.ui.datefield.DateTimeResolution.SECOND;
import static com.vaadin.shared.ui.datefield.DateTimeResolution.YEAR;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.LocaleService;
import com.vaadin.shared.ui.datefield.DateTimeResolution;

/**
 * Represents a date-time selection component with a text field and a pop-up
 * date-and-time selector.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
public class VPopupTimeCalendar extends
        VAbstractPopupCalendar<VDateTimeCalendarPanel, DateTimeResolution> {

    /**
     * Constructs a date-time selection component with a text field and a pop-up
     * date-and-time selector. Uses a {@link VDateTimeCalendarPanel} as the
     * pop-up content. Default resolution is {@link DateTimeResolution#MINUTE}.
     */
    public VPopupTimeCalendar() {
        super(GWT.create(VDateTimeCalendarPanel.class), MINUTE);
    }

    @Override
    protected DateTimeResolution[] doGetResolutions() {
        return DateTimeResolution.values();
    }

    @Override
    public String resolutionAsString() {
        if (getCurrentResolution().compareTo(DAY) >= 0) {
            return getResolutionVariable(getCurrentResolution());
        }
        return "full";
    }

    @Override
    public void setCurrentResolution(DateTimeResolution resolution) {
        super.setCurrentResolution(resolution == null ? MINUTE : resolution);
    }

    /**
     * Creates a date based on the provided date values map.
     *
     * @param dateValues
     *            a map with date values to convert into a date
     * @return the date based on the dateValues map
     */
    @SuppressWarnings("deprecation")
    public static Date makeDate(Map<DateTimeResolution, Integer> dateValues) {
        if (dateValues.get(YEAR) == null) {
            return null;
        }
        Date date = new Date(2000 - 1900, 0, 1);
        Integer year = dateValues.get(YEAR);
        if (year != null) {
            date.setYear(year - 1900);
        }
        Integer month = dateValues.get(MONTH);
        if (month != null) {
            date.setMonth(month - 1);
        }
        Integer day = dateValues.get(DAY);
        if (day != null) {
            date.setDate(day);
        }
        Integer hour = dateValues.get(HOUR);
        if (hour != null) {
            date.setHours(hour);
        }
        Integer minute = dateValues.get(MINUTE);
        if (minute != null) {
            date.setMinutes(minute);
        }
        Integer second = dateValues.get(SECOND);
        if (second != null) {
            date.setSeconds(second);
        }
        return date;
    }

    @Override
    public boolean isYear(DateTimeResolution resolution) {
        return YEAR.equals(resolution);
    }

    @Override
    protected Date getDate(Map<DateTimeResolution, Integer> dateValues) {
        return makeDate(dateValues);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void updateBufferedResolutions() {
        super.updateBufferedResolutions();
        Date currentDate = getDate();
        if (currentDate != null) {
            DateTimeResolution resolution = getCurrentResolution();
            if (resolution.compareTo(MONTH) <= 0) {
                bufferedResolutions.put(MONTH, currentDate.getMonth() + 1);
            }
            if (resolution.compareTo(DAY) <= 0) {
                bufferedResolutions.put(DAY, currentDate.getDate());
            }
            if (resolution.compareTo(HOUR) <= 0) {
                bufferedResolutions.put(HOUR, currentDate.getHours());
            }
            if (resolution.compareTo(MINUTE) <= 0) {
                bufferedResolutions.put(MINUTE, currentDate.getMinutes());
            }
            if (resolution.compareTo(SECOND) <= 0) {
                bufferedResolutions.put(SECOND, currentDate.getSeconds());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateValue(Date newDate) {
        Date currentDate = getCurrentDate();
        super.updateValue(newDate);
        DateTimeResolution resolution = getCurrentResolution();
        if (currentDate == null || newDate.getTime() != currentDate.getTime()) {
            if (resolution.compareTo(DAY) < 0) {
                bufferedResolutions.put(HOUR, newDate.getHours());
                if (resolution.compareTo(HOUR) < 0) {
                    bufferedResolutions.put(MINUTE, newDate.getMinutes());
                    if (resolution.compareTo(MINUTE) < 0) {
                        bufferedResolutions.put(SECOND, newDate.getSeconds());
                    }
                }
            }
        }
    }

    @Override
    protected String createFormatString() {
        if (isYear(getCurrentResolution())) {
            return "yyyy"; // force full year
        }
        try {
            String frmString = LocaleService.getDateFormat(currentLocale);
            frmString = cleanFormat(frmString);
            // String delim = LocaleService
            // .getClockDelimiter(currentLocale);
            if (getCurrentResolution().compareTo(HOUR) <= 0) {
                if (dts.isTwelveHourClock()) {
                    frmString += " hh";
                } else {
                    frmString += " HH";
                }
                if (getCurrentResolution().compareTo(MINUTE) <= 0) {
                    frmString += ":mm";
                    if (getCurrentResolution().compareTo(SECOND) <= 0) {
                        frmString += ":ss";
                    }
                }
                if (dts.isTwelveHourClock()) {
                    frmString += " aaa";
                }
            }

            return frmString;
        } catch (LocaleNotLoadedException e) {
            // TODO should die instead? Can the component survive
            // without format string?
            getLogger().log(Level.SEVERE,
                    e.getMessage() == null ? "" : e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected String cleanFormat(String format) {
        // Remove unnecessary d & M if resolution is too low
        if (getCurrentResolution().compareTo(DAY) > 0) {
            format = format.replaceAll("d", "");
        }
        if (getCurrentResolution().compareTo(MONTH) > 0) {
            format = format.replaceAll("M", "");
        }
        return super.cleanFormat(format);
    }

    @Override
    protected boolean supportsTime() {
        return true;
    }

    private static Logger getLogger() {
        return Logger.getLogger(VPopupTimeCalendar.class.getName());
    }
}
