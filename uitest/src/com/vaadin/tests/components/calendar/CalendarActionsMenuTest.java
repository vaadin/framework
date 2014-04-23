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
import com.vaadin.ui.Notification;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.MoveEvent;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

public class CalendarActionsMenuTest extends AbstractTestUI {

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
                    event = new BasicEvent("NAME", "TOOLTIP",
                            new SimpleDateFormat("yyyy-MM-dd hh:mm")
                                    .parse("2013-01-01 07:00"),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm")
                                    .parse("2013-01-01 11:00"));
                } catch (ParseException e) {
                    // Nothing to do
                }
                events.add(event);

                try {
                    event = new BasicEvent("NAME 2", "TOOLTIP2",
                            new SimpleDateFormat("yyyy-MM-dd hh:mm")
                                    .parse("2013-01-03 07:00"),
                            new SimpleDateFormat("yyyy-MM-dd hh:mm")
                                    .parse("2013-01-04 11:00"));
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
                Notification.show("ACTION CLICKED");

            }

            @Override
            public Action[] getActions(Object target, Object sender) {
                return new Action[] { new Action("ACTION") };
            }
        });
        calendar.setHandler(new EventClickHandler() {
            private static final long serialVersionUID = -177173530105538438L;

            @Override
            public void eventClick(EventClick event) {
                Notification.show("EVENT CLICKED");
            }
        });

        calendar.setHandler(new EventMoveHandler() {
            @Override
            public void eventMove(MoveEvent event) {
                Notification.show("EVENT MOVED");
            }
        });
        addComponent(calendar);
        calendar.setSizeFull();
        setSizeFull();

    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return "The context should appear if you right click on a calendar event regardless of view mode";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return 12181;
    }

}
