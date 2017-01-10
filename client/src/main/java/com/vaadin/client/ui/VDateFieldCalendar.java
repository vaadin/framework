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

import com.google.gwt.core.shared.GWT;
import com.vaadin.shared.ui.datefield.DateResolution;

/**
 * A client side implementation for InlineDateField.
 * 
 * @author Vaadin Ltd
 *
 */
public class VDateFieldCalendar
        extends VAbstractDateFieldCalendar<VDateCalendarPanel, DateResolution> {

    public VDateFieldCalendar() {
        super(GWT.create(VDateCalendarPanel.class), DateResolution.YEAR);
    }

    /**
     * TODO refactor: almost same method as in VPopupCalendar.updateValue
     * <p>
     * For internal use only. May be removed or replaced in the future.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void updateValueFromPanel() {
        // If field is invisible at the beginning, client can still be null when
        // this function is called.
        if (getClient() == null) {
            return;
        }

        Date date2 = calendarPanel.getDate();
        Date currentDate = getCurrentDate();
        if (currentDate == null || date2.getTime() != currentDate.getTime()) {
            setCurrentDate((Date) date2.clone());
            getClient().updateVariable(getId(),
                    getResolutionVariable(DateResolution.YEAR),
                    // Java Date uses the year aligned to 1900 (no to zero).
                    // So we should add 1900 to get a correct year aligned to 0.
                    date2.getYear() + 1900, false);
            if (getCurrentResolution().compareTo(DateResolution.YEAR) < 0) {
                getClient().updateVariable(getId(),
                        getResolutionVariable(DateResolution.MONTH),
                        date2.getMonth() + 1, false);
                if (getCurrentResolution()
                        .compareTo(DateResolution.MONTH) < 0) {
                    getClient().updateVariable(getId(),
                            getResolutionVariable(DateResolution.DAY),
                            date2.getDate(), false);
                }
            }
            getClient().sendPendingVariableChanges();
        }
    }

    @Override
    public void setCurrentResolution(DateResolution resolution) {
        super.setCurrentResolution(
                resolution == null ? DateResolution.YEAR : resolution);
    }

    @Override
    public String resolutionAsString() {
        return getResolutionVariable(getCurrentResolution());
    }

    @Override
    public boolean isYear(DateResolution resolution) {
        return DateResolution.YEAR.equals(resolution);
    }

    @Override
    protected DateResolution[] doGetResolutions() {
        return DateResolution.values();
    }

    @Override
    protected Date getDate(Map<DateResolution, Integer> dateVaules) {
        return VPopupCalendar.makeDate(dateVaules);
    }

}
