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
