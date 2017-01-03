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

import com.vaadin.shared.ui.datefield.DateResolution;

/**
 * @author Vaadin Ltd
 *
 */
public class VTextualDate extends VAbstractTextualDate<DateResolution> {

    public VTextualDate() {
        super(DateResolution.YEAR);
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

    public static Date makeDate(Map<DateResolution, Integer> dateVaules) {
        if (dateVaules.get(DateResolution.YEAR) == -1) {
            return null;
        }
        Date date = new Date(2000 - 1900, 0, 1);
        int year = dateVaules.get(DateResolution.YEAR);
        if (year >= 0) {
            date.setYear(year - 1900);
        }
        int month = dateVaules.get(DateResolution.MONTH);
        if (month >= 0) {
            date.setMonth(month - 1);
        }
        int day = dateVaules.get(DateResolution.DAY);
        if (day >= 0) {
            date.setDate(day);
        }
        return date;
    }

    @Override
    protected Date getDate(Map<DateResolution, Integer> dateVaules) {
        return makeDate(dateVaules);
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
    protected void updateDateVariables() {
        // Update variables
        // (only the smallest defining resolution needs to be
        // immediate)
        super.updateDateVariables();
        updateDateVaraibles(this);
    }

    @Override
    protected DateResolution[] doGetResolutions() {
        return DateResolution.values();
    }

    @Override
    public boolean isYear(DateResolution resolution) {
        return DateResolution.YEAR.equals(resolution);
    }

    static void updateDateVaraibles(VDateField<DateResolution> dateField) {
        Date currentDate = dateField.getDate();
        if (dateField.getCurrentResolution()
                .compareTo(DateResolution.MONTH) <= 0) {
            dateField.getClient().updateVariable(dateField.getId(),
                    dateField.getResolutionVariable(DateResolution.MONTH),
                    currentDate != null ? currentDate.getMonth() + 1 : -1,
                    dateField.getCurrentResolution() == DateResolution.MONTH);
        }
        if (dateField.getCurrentResolution()
                .compareTo(DateResolution.DAY) <= 0) {
            dateField.getClient().updateVariable(dateField.getId(),
                    dateField.getResolutionVariable(DateResolution.DAY),
                    currentDate != null ? currentDate.getDate() : -1,
                    dateField.getCurrentResolution() == DateResolution.DAY);
        }
    }

}
