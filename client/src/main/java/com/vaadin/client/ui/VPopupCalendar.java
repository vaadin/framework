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

import static com.vaadin.shared.ui.datefield.DateResolution.DAY;
import static com.vaadin.shared.ui.datefield.DateResolution.MONTH;
import static com.vaadin.shared.ui.datefield.DateResolution.YEAR;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.data.date.VaadinDateTime;
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
        super(GWT.create(VDateCalendarPanel.class), YEAR);
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
        super.setCurrentResolution(resolution == null ? YEAR : resolution);
    }

    public static VaadinDateTime makeDate(Map<DateResolution, Integer> dateValues) {
        if (dateValues.get(YEAR) == null) {
            return null;
        }
        int year = dateValues.getOrDefault(YEAR,2000);
        int month = dateValues.getOrDefault(MONTH,0);
        int day = dateValues.getOrDefault(DAY,1);
        return new VaadinDateTime(year, month, day);
    }

    @Override
    public boolean isYear(DateResolution resolution) {
        return YEAR.equals(resolution);
    }

    @Override
    protected VaadinDateTime getDate(Map<DateResolution, Integer> dateValues) {
        return makeDate(dateValues);
    }

    @Override
    protected void updateBufferedResolutions() {
        super.updateBufferedResolutions();
        VaadinDateTime currentDate = getDate();
        if (currentDate != null) {
            DateResolution resolution = getCurrentResolution();
            if (resolution.compareTo(MONTH) <= 0) {
                bufferedResolutions.put(MONTH, currentDate.getMonth() + 1);
            }
            if (resolution.compareTo(DAY) <= 0) {
                bufferedResolutions.put(DAY, currentDate.getDay());
            }
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
        return false;
    }

}
