package com.vaadin.tests.components.calendar;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Theme("tests-calendar")
public class TestHideTimeAndSeparator extends AbstractTestUI {

    class GenericEvent implements CalendarEvent {
        private final Date start;
        private final Date end;
        private final String caption;
        private final boolean hideTime;

        public GenericEvent(Date start, Date end, String caption,
                boolean hideTime) {
            this.start = start;
            this.end = end;
            this.caption = caption;
            this.hideTime = hideTime;
        }

        @Override
        public Date getStart() {
            return start;
        }

        @Override
        public Date getEnd() {
            return end;
        }

        @Override
        public String getCaption() {
            return caption;
        }

        @Override
        public String getDescription() {
            return "This is a " + caption;
        }

        @Override
        public String getStyleName() {
            return hideTime ? "hide-time" : null;
        }

        @Override
        public boolean isAllDay() {
            return false;
        }

    }

    CalendarEvent shortEventHidden = new GenericEvent(
            makeDate(2013, 1, 2, 8, 0), makeDate(2013, 1, 2, 8, 30),
            "Short event", true);
    CalendarEvent longEventHidden = new GenericEvent(
            makeDate(2013, 1, 2, 10, 0), makeDate(2013, 1, 2, 12, 0),
            "Long event", true);
    CalendarEvent shortEvent = new GenericEvent(makeDate(2013, 1, 3, 8, 0),
            makeDate(2013, 1, 3, 8, 30), "Short event", false);
    CalendarEvent longEvent = new GenericEvent(makeDate(2013, 1, 3, 10, 0),
            makeDate(2013, 1, 3, 12, 0), "Long event", false);

    @Override
    protected void setup(VaadinRequest request) {
        Calendar cal = new Calendar();
        cal.setWidth("100%");
        cal.setHeight("500px");

        cal.setLocale(Locale.US);

        cal.addEvent(shortEventHidden);
        cal.addEvent(longEventHidden);
        cal.addEvent(shortEvent);
        cal.addEvent(longEvent);

        cal.setStartDate(makeDate(2013, 1, 1));
        cal.setEndDate(makeDate(2013, 1, 7));
        cal.setFirstVisibleHourOfDay(7);

        addComponent(cal);
    }

    @Override
    protected String getTestDescription() {
        return "The time should be hideable by CSS";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12460;
    }

    private Date makeDate(int year, int month, int day, int hour, int minute) {
        java.util.Calendar juc = java.util.Calendar.getInstance();
        juc.set(year, month, day, hour, minute);
        return juc.getTime();
    }

    private Date makeDate(int year, int month, int day) {
        java.util.Calendar juc = java.util.Calendar.getInstance();
        juc.set(year, month, day);
        return juc.getTime();
    }
}
