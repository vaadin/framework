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
import com.vaadin.shared.ui.datefield.DateResolution;

/**
 * Represents a date selection component with a text field and a popup date
 * selector.
 *
 * @author Vaadin Ltd
 *
 */
public class VPopupCalendar
        extends VAbstractPopupCalendar<VDateCalendarPanel, DateResolution> {

    public VPopupCalendar() {
        super(GWT.create(VDateCalendarPanel.class), DateResolution.YEAR);
    }

    @Override
    protected DateResolution[] doGetResolutions() {
        return DateResolution.values();
    }

    @Override
    public String resolutionAsString() {
        return getResolutionVariable(getCurrentResolution());
    }

    @Override
    public void setCurrentResolution(DateResolution resolution) {
        super.setCurrentResolution(
                resolution == null ? DateResolution.YEAR : resolution);
    }

    public static Date makeDate(Map<DateResolution, Integer> dateValues) {
        if (dateValues.get(DateResolution.YEAR) == null) {
            return null;
        }
        Date date = new Date(2000 - 1900, 0, 1);
        Integer year = dateValues.get(DateResolution.YEAR);
        if (year != null) {
            date.setYear(year - 1900);
        }
        Integer month = dateValues.get(DateResolution.MONTH);
        if (month != null) {
            date.setMonth(month - 1);
        }
        Integer day = dateValues.get(DateResolution.DAY);
        if (day != null) {
            date.setDate(day);
        }
        return date;
    }

    @Override
    public boolean isYear(DateResolution resolution) {
        return DateResolution.YEAR.equals(resolution);
    }

    @Override
    protected Date getDate(Map<DateResolution, Integer> dateValues) {
        return makeDate(dateValues);
    }

    @Override
    protected void updateDateVariables() {
        super.updateDateVariables();
        DateResolution resolution = getCurrentResolution();
        // Update variables
        // (only the smallest defining resolution needs to be
        // immediate)
        Date currentDate = getDate();
        if (resolution.compareTo(DateResolution.MONTH) <= 0) {
            bufferedResolutions.put(DateResolution.MONTH.name(),
                    currentDate != null ? currentDate.getMonth() + 1 : null);
        }
        if (resolution.compareTo(DateResolution.DAY) <= 0) {
            bufferedResolutions.put(DateResolution.DAY.name(),
                    currentDate != null ? currentDate.getDate() : null);
        }
        sendBufferedValues();
    }

    @Override
    protected String cleanFormat(String format) {
        // Remove unnecessary d & M if resolution is too low
        if (getCurrentResolution().compareTo(DateResolution.DAY) > 0) {
            format = format.replaceAll("d", "");
        }
        if (getCurrentResolution().compareTo(DateResolution.MONTH) > 0) {
            format = format.replaceAll("M", "");
        }
        return super.cleanFormat(format);
    }

    @Override
    protected boolean supportsTime() {
        return false;
    }

}
