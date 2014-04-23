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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

public class CalendarActionEventSource extends AbstractTestUI {

    private Calendar calendar;

    @Override
    protected void setup(VaadinRequest request) {
        calendar = new Calendar(new CalendarEventProvider() {

            @Override
            public List<com.vaadin.ui.components.calendar.event.CalendarEvent> getEvents(
                    Date startDate, Date endDate) {

                List<CalendarEvent> events = new ArrayList<CalendarEvent>();

                CalendarEvent event = null;
                try {
                    event = new BasicEvent("NAME", null, new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm").parse("2013-01-01 07:00"),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm")
                                    .parse("2013-01-01 11:00"));
                } catch (ParseException e) {
                    // Nothing to do
                }
                events.add(event);

                return events;
            }

        });
        try {
            calendar.setStartDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2013-01-01"));
            calendar.setEndDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2013-01-31"));
        } catch (ParseException e) {
            // Nothing to do
        }
        calendar.setImmediate(true);
        calendar.setFirstVisibleHourOfDay(6);
        calendar.setLastVisibleHourOfDay(22);
        calendar.setTimeFormat(TimeFormat.Format24H);
        calendar.setHandler((EventResizeHandler) null);

        setEnabled(true);
        calendar.addActionHandler(new Handler() {
            @Override
            public void handleAction(Action action, Object sender, Object target) {
                Label label1 = new Label(calendar.toString());
                label1.setId("calendarlabel");
                addComponent(label1);

                Label label2 = new Label(sender.toString());
                label2.setId("senderlabel");
                addComponent(label2);
            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new Action("ACTION") };
            }
        });
        addComponent(calendar);
        calendar.setSizeFull();
        setSizeFull();

    }

    @Override
    protected String getTestDescription() {
        return "Calendar action event source should be the calendar itself";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13191;
    }

}
