/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.VCalendarPanel.FocusChangeListener;
import com.vaadin.client.ui.VCalendarPanel.TimeChangeListener;
import com.vaadin.client.ui.VDateFieldCalendar;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.datefield.InlineDateFieldState;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.InlineDateField;

@Connect(InlineDateField.class)
public class InlineDateFieldConnector extends AbstractDateFieldConnector {

    @Override
    @SuppressWarnings("deprecation")
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().calendarPanel.setShowISOWeekNumbers(getWidget()
                .isShowISOWeekNumbers());
        getWidget().calendarPanel.setDateTimeService(getWidget()
                .getDateTimeService());
        getWidget().calendarPanel.setResolution(getWidget()
                .getCurrentResolution());
        Date currentDate = getWidget().getCurrentDate();
        if (currentDate != null) {
            getWidget().calendarPanel.setDate(new Date(currentDate.getTime()));
        } else {
            getWidget().calendarPanel.setDate(null);
        }

        if (getWidget().getCurrentResolution().getCalendarField() > Resolution.DAY
                .getCalendarField()) {
            getWidget().calendarPanel
                    .setTimeChangeListener(new TimeChangeListener() {
                        @Override
                        public void changed(int hour, int min, int sec, int msec) {
                            Date d = getWidget().getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) getWidget().calendarPanel.getDate()
                                        .clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            getWidget().calendarPanel.setDate(d);
                            getWidget().updateValueFromPanel();
                        }
                    });
        }

        if (getWidget().getCurrentResolution().getCalendarField() <= Resolution.MONTH
                .getCalendarField()) {
            getWidget().calendarPanel
                    .setFocusChangeListener(new FocusChangeListener() {
                        @Override
                        public void focusChanged(Date date) {
                            Date date2 = new Date();
                            if (getWidget().calendarPanel.getDate() != null) {
                                date2.setTime(getWidget().calendarPanel
                                        .getDate().getTime());
                            }
                            /*
                             * Update the value of calendarPanel
                             */
                            date2.setYear(date.getYear());
                            date2.setMonth(date.getMonth());
                            getWidget().calendarPanel.setDate(date2);
                            /*
                             * Then update the value from panel to server
                             */
                            getWidget().updateValueFromPanel();
                        }
                    });
        } else {
            getWidget().calendarPanel.setFocusChangeListener(null);
        }

        // Update possible changes
        getWidget().calendarPanel.renderCalendar();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        getWidget().setTabIndex(getState().tabIndex);
        getWidget().calendarPanel.setRangeStart(getState().rangeStart);
        getWidget().calendarPanel.setRangeEnd(getState().rangeEnd);
    }

    @Override
    public VDateFieldCalendar getWidget() {
        return (VDateFieldCalendar) super.getWidget();
    }

    @Override
    public InlineDateFieldState getState() {
        return (InlineDateFieldState) super.getState();
    }
}
