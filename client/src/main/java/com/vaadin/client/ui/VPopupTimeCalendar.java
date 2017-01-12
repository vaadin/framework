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

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.vaadin.client.LocaleNotLoadedException;
import com.vaadin.client.LocaleService;
import com.vaadin.client.VConsole;
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
        super(GWT.create(VDateTimeCalendarPanel.class),
                DateTimeResolution.MINUTE);
    }

    @Override
    protected DateTimeResolution[] doGetResolutions() {
        return DateTimeResolution.values();
    }

    @Override
    public String resolutionAsString() {
        if (getCurrentResolution().compareTo(DateTimeResolution.DAY) >= 0) {
            return getResolutionVariable(getCurrentResolution());
        } else {
            return "full";
        }
    }

    @Override
    public void setCurrentResolution(DateTimeResolution resolution) {
        super.setCurrentResolution(
                resolution == null ? DateTimeResolution.MINUTE : resolution);
    }

    public static Date makeDate(Map<DateTimeResolution, Integer> dateValues) {
        if (dateValues.get(DateTimeResolution.YEAR) == -1) {
            return null;
        }
        Date date = new Date(2000 - 1900, 0, 1);
        int year = dateValues.get(DateTimeResolution.YEAR);
        if (year >= 0) {
            date.setYear(year - 1900);
        }
        int month = dateValues.get(DateTimeResolution.MONTH);
        if (month >= 0) {
            date.setMonth(month - 1);
        }
        int day = dateValues.get(DateTimeResolution.DAY);
        if (day >= 0) {
            date.setDate(day);
        }
        int hour = dateValues.get(DateTimeResolution.HOUR);
        if (hour >= 0) {
            date.setHours(hour);
        }
        int minute = dateValues.get(DateTimeResolution.MINUTE);
        if (minute >= 0) {
            date.setMinutes(minute);
        }
        int second = dateValues.get(DateTimeResolution.SECOND);
        if (second >= 0) {
            date.setSeconds(second);
        }
        return date;
    }

    @Override
    public boolean isYear(DateTimeResolution resolution) {
        return DateTimeResolution.YEAR.equals(resolution);
    }

    @Override
    protected Date getDate(Map<DateTimeResolution, Integer> dateValues) {
        return makeDate(dateValues);
    }

    @Override
    protected void updateDateVariables() {
        super.updateDateVariables();
        // Update variables
        // (only the smallest defining resolution needs to be
        // immediate)
        Date currentDate = getDate();
        if (getCurrentResolution().compareTo(DateTimeResolution.MONTH) <= 0) {
            getClient().updateVariable(getId(),
                    getResolutionVariable(DateTimeResolution.MONTH),
                    currentDate != null ? currentDate.getMonth() + 1 : -1,
                    getCurrentResolution() == DateTimeResolution.MONTH);
        }
        if (getCurrentResolution().compareTo(DateTimeResolution.DAY) <= 0) {
            getClient().updateVariable(getId(),
                    getResolutionVariable(DateTimeResolution.DAY),
                    currentDate != null ? currentDate.getDate() : -1,
                    getCurrentResolution() == DateTimeResolution.DAY);
        }
        if (getCurrentResolution().compareTo(DateTimeResolution.HOUR) <= 0) {
            getClient().updateVariable(getId(),
                    getResolutionVariable(DateTimeResolution.HOUR),
                    currentDate != null ? currentDate.getHours() : -1,
                    getCurrentResolution() == DateTimeResolution.HOUR);
        }
        if (getCurrentResolution().compareTo(DateTimeResolution.MINUTE) <= 0) {
            getClient().updateVariable(getId(),
                    getResolutionVariable(DateTimeResolution.MINUTE),
                    currentDate != null ? currentDate.getMinutes() : -1,
                    getCurrentResolution() == DateTimeResolution.MINUTE);
        }
        if (getCurrentResolution().compareTo(DateTimeResolution.SECOND) <= 0) {
            getClient().updateVariable(getId(),
                    getResolutionVariable(DateTimeResolution.SECOND),
                    currentDate != null ? currentDate.getSeconds() : -1,
                    getCurrentResolution() == DateTimeResolution.SECOND);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateValue(Date newDate) {
        Date currentDate = getCurrentDate();
        super.updateValue(newDate);
        if (currentDate == null || newDate.getTime() != currentDate.getTime()) {
            if (getCurrentResolution().compareTo(DateTimeResolution.DAY) < 0) {
                getClient().updateVariable(getId(),
                        getResolutionVariable(DateTimeResolution.HOUR),
                        newDate.getHours(), false);
                if (getCurrentResolution()
                        .compareTo(DateTimeResolution.HOUR) < 0) {
                    getClient().updateVariable(getId(),
                            getResolutionVariable(DateTimeResolution.MINUTE),
                            newDate.getMinutes(), false);
                    if (getCurrentResolution()
                            .compareTo(DateTimeResolution.MINUTE) < 0) {
                        getClient().updateVariable(getId(),
                                getResolutionVariable(
                                        DateTimeResolution.SECOND),
                                newDate.getSeconds(), false);
                    }
                }
            }
        }
    }

    @Override
    protected String getFormatString() {
        if (formatStr == null) {
            if (isYear(getCurrentResolution())) {
                formatStr = "yyyy"; // force full year
            } else {

                try {
                    String frmString = LocaleService
                            .getDateFormat(currentLocale);
                    frmString = cleanFormat(frmString);
                    // String delim = LocaleService
                    // .getClockDelimiter(currentLocale);
                    if (getCurrentResolution()
                            .compareTo(DateTimeResolution.HOUR) <= 0) {
                        if (dts.isTwelveHourClock()) {
                            frmString += " hh";
                        } else {
                            frmString += " HH";
                        }
                        if (getCurrentResolution()
                                .compareTo(DateTimeResolution.MINUTE) <= 0) {
                            frmString += ":mm";
                            if (getCurrentResolution().compareTo(
                                    DateTimeResolution.SECOND) <= 0) {
                                frmString += ":ss";
                            }
                        }
                        if (dts.isTwelveHourClock()) {
                            frmString += " aaa";
                        }

                    }

                    formatStr = frmString;
                } catch (LocaleNotLoadedException e) {
                    // TODO should die instead? Can the component survive
                    // without format string?
                    VConsole.error(e);
                }
            }
        }
        return formatStr;
    }

    @Override
    protected String cleanFormat(String format) {
        // Remove unnecessary d & M if resolution is too low
        if (getCurrentResolution().compareTo(DateTimeResolution.DAY) > 0) {
            format = format.replaceAll("d", "");
        }
        if (getCurrentResolution().compareTo(DateTimeResolution.MONTH) > 0) {
            format = format.replaceAll("M", "");
        }
        return super.cleanFormat(format);
    }

}
