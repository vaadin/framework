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
package com.vaadin.v7.tests.components.calendar;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.shared.ui.calendar.CalendarState.EventSortOrder;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.event.BasicEvent;
import com.vaadin.v7.ui.components.calendar.event.CalendarEvent;
import com.vaadin.v7.ui.components.calendar.event.CalendarEventProvider;

/**
 * 
 * Test UI for event sorting in calendar month and week views.
 * 
 * @author Vaadin Ltd
 */
public class CalendarEventsSort extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getContent().setSizeFull();
        final Calendar calendar = new Calendar("Test calendar");

        toMonthView(calendar);
        calendar.setEventSortOrder(EventSortOrder.UNSORTED);

        calendar.setEventProvider(createEventProvider());
        addComponent(calendar);

        createSortByDateButton(calendar);
        createSortByDurationButton(calendar);
        createSortByProviderButton(calendar);
        createViewSwitchButton(calendar);
    }

    private void createViewSwitchButton(final Calendar calendar) {
        Button toWeek = new Button("Switch to week view", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                Button button = event.getButton();
                Boolean month = (Boolean) button.getData();
                button.setData(!month);
                if (month) {
                    button.setCaption("Switch to month view");
                    toWeekView(calendar);
                } else {
                    button.setCaption("Switch to week view");
                    toMonthView(calendar);
                }
            }

        });
        toWeek.addStyleName("view");
        toWeek.setData(true);
        addComponent(toWeek);
    }

    private Button createSortByProviderButton(final Calendar calendar) {
        Button byProvider = new Button("Sort by provider", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                calendar.setEventSortOrder(EventSortOrder.UNSORTED);
            }
        });
        byProvider.addStyleName("by-provider");
        addComponent(byProvider);
        return byProvider;
    }

    private void createSortByDurationButton(final Calendar calendar) {
        Button byDuration = new Button("Sort by duration DESC",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Button button = event.getButton();
                        EventSortOrder order = (EventSortOrder) button
                                .getData();
                        if (EventSortOrder.DURATION_DESC.equals(order)) {
                            order = EventSortOrder.DURATION_ASC;
                            button.setCaption("Sort by duration DESC");
                            addSortOrder(true, button);
                        } else {
                            order = EventSortOrder.DURATION_DESC;
                            button.setCaption("Sort by duration ASC");
                            addSortOrder(false, button);
                        }
                        button.setData(order);
                        calendar.setEventSortOrder(order);
                    }
                });
        byDuration.addStyleName("by-duration");
        byDuration.setData(EventSortOrder.DURATION_ASC);
        addComponent(byDuration);
    }

    private void createSortByDateButton(final Calendar calendar) {
        Button byStartDate = new Button("Sort by start date DESC",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Button button = event.getButton();
                        EventSortOrder order = (EventSortOrder) button
                                .getData();
                        if (EventSortOrder.START_DATE_DESC.equals(order)) {
                            order = EventSortOrder.START_DATE_ASC;
                            button.setCaption("Sort by start date DESC");
                            addSortOrder(true, button);
                        } else {
                            order = EventSortOrder.START_DATE_DESC;
                            button.setCaption("Sort by start date ASC");
                            addSortOrder(false, button);
                        }
                        button.setData(order);
                        calendar.setEventSortOrder(order);
                    }
                });
        byStartDate.setData(EventSortOrder.START_DATE_ASC);
        byStartDate.addStyleName("by-start-date");
        addComponent(byStartDate);
    }

    private CalendarEventProvider createEventProvider() {
        CalendarEventProvider provider = new CalendarEventProvider() {

            @Override
            public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(java.util.Calendar.HOUR_OF_DAY, 5);
                cal.set(java.util.Calendar.MINUTE, 0);
                cal.set(java.util.Calendar.SECOND, 0);
                cal.set(java.util.Calendar.MILLISECOND, 0);

                Date start = cal.getTime();
                cal.add(java.util.Calendar.HOUR_OF_DAY, 2);
                Date end = cal.getTime();

                CalendarEvent event1 = new BasicEvent("first", "descr1", start,
                        end);

                cal.set(java.util.Calendar.HOUR_OF_DAY, 2);
                start = cal.getTime();
                cal.add(java.util.Calendar.HOUR_OF_DAY, 4);
                end = cal.getTime();

                CalendarEvent event2 = new BasicEvent("second", "descr2", start,
                        end);

                cal.set(java.util.Calendar.HOUR_OF_DAY, 1);
                start = cal.getTime();
                cal.add(java.util.Calendar.HOUR_OF_DAY, 2);
                end = cal.getTime();

                CalendarEvent event3 = new BasicEvent("third", "descr2", start,
                        end);

                return Arrays.asList(event1, event2, event3);
            }

        };
        return provider;
    }

    private void addSortOrder(boolean ascending, Button button) {
        if (ascending) {
            button.addStyleName("asc");
            button.removeStyleName("desc");
        } else {
            button.removeStyleName("asc");
            button.addStyleName("desc");
        }
    }

    private void toMonthView(final Calendar calendar) {
        final java.util.Calendar cal = java.util.Calendar.getInstance();

        cal.add(java.util.Calendar.DAY_OF_YEAR, -2);
        calendar.setStartDate(cal.getTime());
        cal.add(java.util.Calendar.DAY_OF_YEAR, 14);
        calendar.setEndDate(cal.getTime());
    }

    private void toWeekView(final Calendar calendar) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, 2);
        calendar.setEndDate(cal.getTime());
    }

    @Override
    public String getDescription() {
        return "Make event sorting strategy customizable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14849;
    }
}