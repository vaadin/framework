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
import com.vaadin.client.ui.VDateTimeFieldCalendar;
import com.vaadin.shared.data.date.VaadinDateTime;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.InlineDateTimeField;

/**
 * The client-side connector for InlineDateTimeField.
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
@Connect(InlineDateTimeField.class)
public class InlineDateTimeFieldConnector extends
        AbstractInlineDateFieldConnector<VDateTimeCalendarPanel, DateTimeResolution> {

    @Override
    protected boolean isResolutionMonthOrHigher() {
        return getWidget().getCurrentResolution()
                .compareTo(DateTimeResolution.MONTH) >= 0;
    }

    @Override
    public VDateTimeFieldCalendar getWidget() {
        return (VDateTimeFieldCalendar) super.getWidget();
    }

    @Override
    protected void updateListeners() {
        super.updateListeners();
        if (getWidget().getCurrentResolution()
                .compareTo(DateTimeResolution.DAY) < 0) {
            getWidget().calendarPanel
                    .setTimeChangeListener((hour, min, sec, msec) -> {
                        VaadinDateTime d = getWidget().getDate();
                        if (d == null) {
                            // date currently null, use the value from
                            // calendarPanel
                            // (~ client time at the init of the widget)
                            d = getWidget().calendarPanel.getDate();
                        }
                        VaadinDateTime newDateTime = new
                                VaadinDateTime(d,hour,min,sec);
                        // Always update time changes to the server
                        getWidget().calendarPanel.setDate(newDateTime);
                        getWidget().updateValueFromPanel();
                    });
        }
    }
}
