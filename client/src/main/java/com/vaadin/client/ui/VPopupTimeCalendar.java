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
        if (dateValues.get(DateTimeResolution.YEAR) == null) {
            return null;
        }
        Date date = new Date(2000 - 1900, 0, 1);
        Integer year = dateValues.get(DateTimeResolution.YEAR);
        if (year != null) {
            date.setYear(year - 1900);
        }
        Integer month = dateValues.get(DateTimeResolution.MONTH);
        if (month != null) {
            date.setMonth(month - 1);
        }
        Integer day = dateValues.get(DateTimeResolution.DAY);
        if (day != null) {
            date.setDate(day);
        }
        Integer hour = dateValues.get(DateTimeResolution.HOUR);
        if (hour != null) {
            date.setHours(hour);
        }
        Integer minute = dateValues.get(DateTimeResolution.MINUTE);
        if (minute != null) {
            date.setMinutes(minute);
        }
        Integer second = dateValues.get(DateTimeResolution.SECOND);
        if (second != null) {
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
        DateTimeResolution resolution = getCurrentResolution();
        // (only the smallest defining resolution needs to be
        // immediate)
        Date currentDate = getDate();
        if (resolution.compareTo(DateTimeResolution.MONTH) <= 0) {
            addBufferedResolution(DateTimeResolution.MONTH,
                    currentDate != null ? currentDate.getMonth() + 1 : null);
        }
        if (resolution.compareTo(DateTimeResolution.DAY) <= 0) {
            addBufferedResolution(DateTimeResolution.DAY,
                    currentDate != null ? currentDate.getDate() : null);
        }
        if (resolution.compareTo(DateTimeResolution.HOUR) <= 0) {
            addBufferedResolution(DateTimeResolution.HOUR,
                    currentDate != null ? currentDate.getHours() : null);
        }
        if (resolution.compareTo(DateTimeResolution.MINUTE) <= 0) {
            addBufferedResolution(DateTimeResolution.MINUTE,
                    currentDate != null ? currentDate.getMinutes() : null);
        }
        if (resolution.compareTo(DateTimeResolution.SECOND) <= 0) {
            addBufferedResolution(DateTimeResolution.SECOND,
                    currentDate != null ? currentDate.getSeconds() : null);
        }
        sendBufferedValues();
    }

    private void addBufferedResolution(DateTimeResolution resolutionToAdd,
            Integer value) {
        bufferedResolutions.put(resolutionToAdd.name(), value);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateValue(Date newDate) {
        Date currentDate = getCurrentDate();
        super.updateValue(newDate);
        DateTimeResolution resolution = getCurrentResolution();
        if (currentDate == null || newDate.getTime() != currentDate.getTime()) {
            if (resolution.compareTo(DateTimeResolution.DAY) < 0) {
                bufferedResolutions.put(DateTimeResolution.HOUR.name(),
                        newDate.getHours());
                if (resolution.compareTo(DateTimeResolution.HOUR) < 0) {
                    bufferedResolutions.put(DateTimeResolution.MINUTE.name(),
                            newDate.getMinutes());
                    if (resolution.compareTo(DateTimeResolution.MINUTE) < 0) {
                        bufferedResolutions.put(
                                DateTimeResolution.SECOND.name(),
                                newDate.getSeconds());
                    }
                }
            }
        }
    }

    @Override
    protected String createFormatString() {
        if (isYear(getCurrentResolution())) {
            return "yyyy"; // force full year
        } else {

            try {
                String frmString = LocaleService.getDateFormat(currentLocale);
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
                        if (getCurrentResolution()
                                .compareTo(DateTimeResolution.SECOND) <= 0) {
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
                VConsole.error(e);
                return null;
            }
        }
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

    @Override
    protected boolean supportsTime() {
        return true;
    }

}
