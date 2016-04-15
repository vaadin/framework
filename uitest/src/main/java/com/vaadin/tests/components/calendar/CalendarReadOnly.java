package com.vaadin.tests.components.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class CalendarReadOnly extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Calendar calendar = new Calendar();

        if (request.getParameter("readonly") != null) {
            calendar.setReadOnly(true);
        }

        if (request.getParameter("disabled") != null) {
            calendar.setEnabled(false);
        }

        calendar.setFirstVisibleHourOfDay(8);
        calendar.setLastVisibleHourOfDay(16);

        calendar.setTimeFormat(Calendar.TimeFormat.Format24H);
        calendar.setHandler((CalendarComponentEvents.EventResizeHandler) null);

        calendar.setSizeFull();

        try {
            calendar.setStartDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2013-09-01"));
            calendar.setEndDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2013-09-30"));

            BasicEvent event = new BasicEvent("EVENT NAME 1",
                    "EVENT TOOLTIP 1",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .parse("2013-09-05 15:30"), new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm").parse("2013-09-05 22:20"));
            event.setStyleName("color1");

            calendar.addEvent(event);
            calendar.addEvent(event);
            calendar.addEvent(event);
            calendar.addEvent(event);

        } catch (ParseException e) {

        }

        addComponent(calendar);
    }

    @Override
    protected Integer getTicketNumber() {
        return 16523;
    }

    @Override
    protected String getTestDescription() {
        return "When set to readonly, you should still be able to navigate through the calendar.";
    }
}
