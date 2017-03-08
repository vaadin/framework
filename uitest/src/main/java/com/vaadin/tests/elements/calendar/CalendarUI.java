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
package com.vaadin.tests.elements.calendar;

import static java.util.Calendar.DAY_OF_MONTH;

import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Calendar;

public class CalendarUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Calendar calendar = new Calendar();
        calendar.setWidth("100%");

        Button monthView = new Button("Month view");
        monthView.setId("month-view");
        monthView.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                java.util.Calendar javaCalendar = java.util.Calendar
                        .getInstance(Locale.ENGLISH);
                javaCalendar.set(DAY_OF_MONTH, 1);
                calendar.setStartDate(javaCalendar.getTime());
                javaCalendar.set(DAY_OF_MONTH,
                        javaCalendar.getActualMaximum(DAY_OF_MONTH));
                calendar.setEndDate(javaCalendar.getTime());
            }
        });

        addComponents(monthView, calendar);
    }

    @Override
    protected String getTestDescription() {
        return "UI used to validate Calendar element API";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
