/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.client.ui.datefield;

import java.util.Date;

import com.google.gwt.event.dom.client.DomEvent;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.ui.datefield.VCalendarPanel.FocusOutListener;
import com.vaadin.client.ui.datefield.VCalendarPanel.SubmitListener;
import com.vaadin.shared.ui.datefield.Resolution;

/**
 * A client side implementation for InlineDateField
 */
public class VDateFieldCalendar extends VDateField {

    protected final VCalendarPanel calendarPanel;

    public VDateFieldCalendar() {
        super();
        calendarPanel = new VCalendarPanel();
        calendarPanel.setParentField(this);
        add(calendarPanel);
        calendarPanel.setSubmitListener(new SubmitListener() {
            @Override
            public void onSubmit() {
                updateValueFromPanel();
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        });
        calendarPanel.setFocusOutListener(new FocusOutListener() {
            @Override
            public boolean onFocusOut(DomEvent<?> event) {
                updateValueFromPanel();
                return false;
            }
        });
    }
    
    /**
     * TODO refactor: almost same method as in VPopupCalendar.updateValue
     */
    @SuppressWarnings("deprecation")
    protected void updateValueFromPanel() {

        // If field is invisible at the beginning, client can still be null when
        // this function is called.
        if (getClient() == null) {
            return;
        }

        Date date2 = calendarPanel.getDate();
        Date currentDate = getCurrentDate();
        if (currentDate == null || date2.getTime() != currentDate.getTime()) {
            setCurrentDate((Date) date2.clone());
            getClient().updateVariable(getId(), "year", date2.getYear() + 1900,
                    false);
            if (getCurrentResolution().getCalendarField() > Resolution.YEAR
                    .getCalendarField()) {
                getClient().updateVariable(getId(), "month",
                        date2.getMonth() + 1, false);
                if (getCurrentResolution().getCalendarField() > Resolution.MONTH
                        .getCalendarField()) {
                    getClient().updateVariable(getId(), "day", date2.getDate(),
                            false);
                    if (getCurrentResolution().getCalendarField() > Resolution.DAY
                            .getCalendarField()) {
                        getClient().updateVariable(getId(), "hour",
                                date2.getHours(), false);
                        if (getCurrentResolution().getCalendarField() > Resolution.HOUR
                                .getCalendarField()) {
                            getClient().updateVariable(getId(), "min",
                                    date2.getMinutes(), false);
                            if (getCurrentResolution().getCalendarField() > Resolution.MINUTE
                                    .getCalendarField()) {
                                getClient().updateVariable(getId(), "sec",
                                        date2.getSeconds(), false);
                                if (getCurrentResolution().getCalendarField() > Resolution.SECOND
                                        .getCalendarField()) {
                                    getClient().updateVariable(
                                            getId(),
                                            "msec",
                                            DateTimeService
                                                    .getMilliseconds(date2),
                                            false);
                                }
                            }
                        }
                    }
                }
            }
            if (isImmediate()) {
                getClient().sendPendingVariableChanges();
            }
        }
    }
}
