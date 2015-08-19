package com.vaadin.tests.components.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.event.BasicEvent;

public class NullEventMoveHandler extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        Calendar calendar = getCalendar();

        calendar.setHandler((CalendarComponentEvents.EventMoveHandler) null);
        calendar.setHandler(new EventClickHandler() {

            @Override
            public void eventClick(EventClick event) {
                log("Clicked on " + event.getCalendarEvent().getCaption());

            }
        });

        addComponent(calendar);
    }

    private Calendar getCalendar() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.US);

        try {
            calendar.setStartDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2014-06-01"));
            calendar.setEndDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2014-06-30"));

            BasicEvent event = new BasicEvent("foo", "bar",
                    new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-01"));

            calendar.addEvent(event);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    @Override
    protected Integer getTicketNumber() {
        return 15174;
    }

    @Override
    protected String getTestDescription() {
        return "Events should not be movable when EventMoveHandler is null.";
    }
}
