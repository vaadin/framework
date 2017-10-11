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

    public VDateTimeFieldCalendar() {
        super(GWT.create(VDateTimeCalendarPanel.class),
                DateTimeResolution.MINUTE);
    }

    @Override
    public void updateValueFromPanel() {
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
            addBufferedResolution(DateTimeResolution.YEAR,
                    date2.getYear() + 1900);
            if (resolution.compareTo(DateTimeResolution.YEAR) < 0) {
                addBufferedResolution(DateTimeResolution.MONTH,
                        date2.getMonth() + 1);
                if (resolution.compareTo(DateTimeResolution.MONTH) < 0) {
                    addBufferedResolution(DateTimeResolution.DAY,
                            date2.getDate());
                    if (resolution.compareTo(DateTimeResolution.DAY) < 0) {
                        addBufferedResolution(DateTimeResolution.HOUR,
                                date2.getHours());
                        if (resolution.compareTo(DateTimeResolution.HOUR) < 0) {
                            addBufferedResolution(DateTimeResolution.MINUTE,
                                    date2.getMinutes());
                            if (resolution
                                    .compareTo(DateTimeResolution.MINUTE) < 0) {
                                addBufferedResolution(DateTimeResolution.SECOND,
                                        date2.getSeconds());
                            }
                        }
                    }
                }
            }
            sendBufferedValues();
        }
    }

    private void addBufferedResolution(DateTimeResolution resolution,
            Integer value) {
        bufferedResolutions.put(getResolutionVariable(resolution), value);
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
    public boolean isYear(DateTimeResolution resolution) {
        return DateTimeResolution.YEAR.equals(resolution);
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
