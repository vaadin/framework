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
package com.vaadin.client.ui.datefield;

import java.util.Date;

import com.vaadin.client.DateTimeService;
import com.vaadin.client.ui.VDateTimeCalendarPanel;
import com.vaadin.client.ui.VDateTimeCalendarPanel.TimeChangeListener;
import com.vaadin.client.ui.VPopupTimeCalendar;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.shared.ui.datefield.LocalDateTimeFieldState;
import com.vaadin.ui.AbstractLocalDateTimeField;

/**
 * The client-side connector for AbstractLocalDateTimeField.
 * 
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(AbstractLocalDateTimeField.class)
public class DateTimeFieldConnector extends
        TextualDateConnector<VDateTimeCalendarPanel, DateTimeResolution> {

    @Override
    protected boolean isResolutionMonthOrHigher() {
        return getWidget().getCurrentResolution()
                .compareTo(DateTimeResolution.MONTH) >= 0;
    }

    @Override
    public VPopupTimeCalendar getWidget() {
        return (VPopupTimeCalendar) super.getWidget();
    }

    @Override
    public LocalDateTimeFieldState getState() {
        return (LocalDateTimeFieldState) super.getState();
    }

    @Override
    protected void updateListeners() {
        super.updateListeners();
        if (getWidget().getCurrentResolution()
                .compareTo(DateTimeResolution.DAY) < 0) {
            getWidget().calendar
                    .setTimeChangeListener(new TimeChangeListener() {
                        @Override
                        public void changed(int hour, int min, int sec,
                                int msec) {
                            Date d = getWidget().getDate();
                            if (d == null) {
                                // date currently null, use the value from
                                // calendarPanel
                                // (~ client time at the init of the widget)
                                d = (Date) getWidget().calendar.getDate()
                                        .clone();
                            }
                            d.setHours(hour);
                            d.setMinutes(min);
                            d.setSeconds(sec);
                            DateTimeService.setMilliseconds(d, msec);

                            // Always update time changes to the server
                            getWidget().updateValue(d);

                            // Update text field
                            getWidget().buildDate();
                        }
                    });
        }
    }
}
