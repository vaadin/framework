/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.LocaleService;
import com.vaadin.shared.data.date.VaadinDateTime;
import com.vaadin.shared.ui.datefield.DateTimeResolution;

/**
 * Represents a date-time selection component with a text field and a popup date
 * selector.
 *
 * @author Vaadin Ltd
 *
 * @since 8.0
 */
public class VPopupTimeCalendar extends
        VAbstractPopupCalendar<VDateTimeCalendarPanel, DateTimeResolution> {

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

    public static VaadinDateTime makeDate(Map<DateTimeResolution, Integer> dateValues) {
        if (dateValues.get(YEAR) == null) {
            return null;
        }
        int year = dateValues.getOrDefault(DateTimeResolution.YEAR,2000);
        int month = dateValues.getOrDefault(DateTimeResolution.MONTH,0);
        int day = dateValues.getOrDefault(DateTimeResolution.DAY,1);
        int hour = dateValues.getOrDefault(DateTimeResolution.HOUR,0);
        int minute = dateValues.getOrDefault(DateTimeResolution.MINUTE,0);
        int second = dateValues.getOrDefault(DateTimeResolution.SECOND,0);
        return new VaadinDateTime(year, month, day,hour,minute,second);
    }

    @Override
    public boolean isYear(DateTimeResolution resolution) {
        return YEAR.equals(resolution);
    }

    @Override
    protected VaadinDateTime getDate(Map<DateTimeResolution, Integer> dateValues) {
        return makeDate(dateValues);
    }

    @Override
    protected void updateBufferedResolutions() {
        super.updateBufferedResolutions();
        VaadinDateTime currentDate = getDate();
        if (currentDate != null) {
            DateTimeResolution resolution = getCurrentResolution();
            if (resolution.compareTo(MONTH) <= 0) {
                bufferedResolutions.put(MONTH, currentDate.getMonth() + 1);
            }
            if (resolution.compareTo(DAY) <= 0) {
                bufferedResolutions.put(DAY, currentDate.getDay());
            }
            if (resolution.compareTo(HOUR) <= 0) {
                bufferedResolutions.put(HOUR, currentDate.getHour());
            }
            if (resolution.compareTo(MINUTE) <= 0) {
                bufferedResolutions.put(MINUTE, currentDate.getMinute());
            }
            if (resolution.compareTo(SECOND) <= 0) {
                bufferedResolutions.put(SECOND, currentDate.getSec());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateValue(VaadinDateTime newDate) {
        VaadinDateTime currentDate = getCurrentDate();
        super.updateValue(newDate);
        DateTimeResolution resolution = getCurrentResolution();
        if(!Objects.equals(currentDate,newDate)) {
            if (resolution.compareTo(DAY) < 0) {
                bufferedResolutions.put(HOUR, newDate.getHour());
                if (resolution.compareTo(HOUR) < 0) {
                    bufferedResolutions.put(MINUTE, newDate.getMinute());
                    if (resolution.compareTo(MINUTE) < 0) {
                        bufferedResolutions.put(SECOND, newDate.getSec());
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
