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
package com.vaadin.tests.components.calendar;

import java.util.Date;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.BasicEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider.EventSetChangeEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider.EventSetChangeListener;

/**
 * Test UI to check ability to reschedule events unlimited times.
 * 
 * @author Vaadin Ltd
 */
public class CalendarRescheduleEvent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Calendar calendar = new Calendar("Test calendar");
        final java.util.Calendar cal = getAdjustedCalendar(1);

        Date from = cal.getTime();

        cal.add(java.util.Calendar.HOUR, 1);
        Date to = cal.getTime();

        final BasicEvent basicEvent = new BasicEvent("event", "description",
                from, to);

        HorizontalLayout info = new HorizontalLayout();
        info.setSpacing(true);
        info.setMargin(true);
        addComponent(info);
        final Label startLbl = new Label();
        startLbl.addStyleName("start");
        info.addComponent(startLbl);

        final Label endLbl = new Label();
        endLbl.addStyleName("end");
        info.addComponent(endLbl);

        BasicEventProvider provider = new BasicEventProvider();
        provider.addEvent(basicEvent);
        calendar.setEventProvider(provider);
        provider.addEventSetChangeListener(new EventSetChangeListener() {

            @Override
            public void eventSetChange(EventSetChangeEvent event) {
                List<CalendarEvent> events = event.getProvider().getEvents(
                        new Date(0), new Date(Long.MAX_VALUE));
                CalendarEvent calEvent = events.get(0);
                Date startEvent = calEvent.getStart();
                Date endEvent = calEvent.getEnd();

                startLbl.setValue(String.valueOf(startEvent.getTime()));
                endLbl.setValue(String.valueOf(endEvent.getTime()));
            }
        });

        addComponent(calendar);
    }

    @Override
    protected String getTestDescription() {
        return "It should be possible to reschedule events unlimited times.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13233;
    }

    private java.util.Calendar getAdjustedCalendar(int hour) {
        final java.util.Calendar cal = java.util.Calendar.getInstance();

        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal;
    }

}
