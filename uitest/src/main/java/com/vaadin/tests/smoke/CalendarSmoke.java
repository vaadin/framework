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
package com.vaadin.tests.smoke;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.v7.ui.components.calendar.event.BasicEvent;

public class CalendarSmoke extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Calendar calendar = new Calendar();

        if (request.getParameter("readonly") != null) {
            calendar.setReadOnly(true);
        }

        calendar.setFirstVisibleHourOfDay(8);
        calendar.setLastVisibleHourOfDay(16);

        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        calendar.setHandler((CalendarComponentEvents.EventResizeHandler) null);

        calendar.setSizeFull();

        try {
            calendar.setStartDate(
                    new SimpleDateFormat("yyyy-MM-dd").parse("2013-09-01"));
            calendar.setEndDate(
                    new SimpleDateFormat("yyyy-MM-dd").parse("2013-09-30"));

            BasicEvent event = new BasicEvent("EVENT NAME 1", "EVENT TOOLTIP 1",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .parse("2013-09-05 15:30"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .parse("2013-09-05 22:20"));
            event.setStyleName("color1");

            calendar.addEvent(event);
            calendar.addEvent(event);
            calendar.addEvent(event);
            calendar.addEvent(event);

        } catch (ParseException e) {

        }

        addComponent(calendar);
    }

}
