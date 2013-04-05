/**
 * Copyright 2013 Vaadin Ltd.
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
import java.util.Locale;

import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.event.BasicEvent;

public class CalendarActionsUI extends UI {

    @SuppressWarnings("deprecation")
    @Override
    protected void init(VaadinRequest request) {
        GridLayout content = new GridLayout(1, 2);
        content.setSizeFull();
        setContent(content);

        final Calendar calendar = new Calendar();
        calendar.setLocale(new Locale("fi", "FI"));

        calendar.setSizeFull();
        calendar.setStartDate(new Date(100, 1, 1));
        calendar.setEndDate(new Date(100, 2, 1));

        calendar.addActionHandler(new Action.Handler() {

            public final Action NEW_EVENT = new Action("Add event");
            public final Action EDIT_EVENT = new Action("Edit event");
            public final Action REMOVE_EVENT = new Action("Remove event");

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.vaadin.event.Action.Handler#handleAction(com.vaadin.event
             * .Action, java.lang.Object, java.lang.Object)
             */
            @Override
            public void handleAction(Action action, Object sender, Object target) {
                Date date = (Date) target;
                if (action == NEW_EVENT) {
                    BasicEvent event = new BasicEvent("New event",
                            "Hello world", date, date);
                    calendar.addEvent(event);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object,
             * java.lang.Object)
             */
            @Override
            public Action[] getActions(Object target, Object sender) {
                CalendarDateRange date = (CalendarDateRange) target;

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(2000, 1, 1, 12, 0, 0);

                if (date.inRange(cal.getTime())) {
                    return new Action[] { NEW_EVENT, };
                }

                cal.add(java.util.Calendar.DAY_OF_WEEK, 1);

                if (date.inRange(cal.getTime())) {
                    return new Action[] { REMOVE_EVENT };
                }

                return null;
            }
        });

        content.addComponent(calendar);

        content.addComponent(new Button("Set week view",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        calendar.setEndDate(new Date(100, 1, 7));
                    }
                }));

        content.setRowExpandRatio(0, 1);

    }
}
