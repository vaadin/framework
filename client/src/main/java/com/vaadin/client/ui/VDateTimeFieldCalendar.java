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

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.ui.datefield.DateTimeResolution;

/**
 * A client side implementation for inline date/time field.
 *
 * @author Vaadin Ltd
 * @since 8.0
 *
 */
public class VDateTimeFieldCalendar extends
        VAbstractDateFieldCalendar<VDateTimeCalendarPanel, DateTimeResolution> {

    /**
     * Constructs a widget for the InlineDateTimeField component.
     */
    public VDateTimeFieldCalendar() {
        super(GWT.create(VDateTimeCalendarPanel.class), MINUTE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void updateBufferedValues() {
        // If field is invisible at the beginning, client can still be null when
        // this function is called.
        if (getClient() == null) {
            return;
        }

        Date date2 = calendarPanel.getDate();
        Date currentDate = getCurrentDate();
        DateTimeResolution resolution = getCurrentResolution();
        if (currentDate == null || date2.getTime() != currentDate.getTime()) {
            setCurrentDate((Date) date2.clone());
            bufferedResolutions.put(YEAR, date2.getYear() + 1900);
            if (resolution.compareTo(YEAR) < 0) {
                bufferedResolutions.put(MONTH, date2.getMonth() + 1);
                if (resolution.compareTo(MONTH) < 0) {
                    bufferedResolutions.put(DAY, date2.getDate());
                    if (resolution.compareTo(DAY) < 0) {
                        bufferedResolutions.put(HOUR, date2.getHours());
                        if (resolution.compareTo(HOUR) < 0) {
                            bufferedResolutions.put(MINUTE, date2.getMinutes());
                            if (resolution.compareTo(MINUTE) < 0) {
                                bufferedResolutions.put(SECOND,
                                        date2.getSeconds());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateValueFromPanel() {
        updateBufferedValues();
        if (bufferedResolutions != null) {
            sendBufferedValues();
        }
    }

    @Override
    public String resolutionAsString() {
        if (getCurrentResolution().compareTo(DAY) >= 0) {
            return getResolutionVariable(getCurrentResolution());
        }
        return "full";
    }

    @Override
    public boolean isYear(DateTimeResolution resolution) {
        return YEAR.equals(resolution);
    }

    @Override
    protected Date getDate(Map<DateTimeResolution, Integer> dateValues) {
        return VPopupTimeCalendar.makeDate(dateValues);
    }

    @Override
    protected DateTimeResolution[] doGetResolutions() {
        return DateTimeResolution.values();
    }

    @Override
    protected boolean supportsTime() {
        return true;
    }

}
