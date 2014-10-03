package com.vaadin.tests.components.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Calendar;

public class CalendarWeekSelection extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.US);

        try {
            calendar.setStartDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2013-12-15"));
            calendar.setEndDate(new SimpleDateFormat("yyyy-MM-dd")
                    .parse("2014-01-15"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        addComponent(calendar);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14783;
    }

    @Override
    protected String getTestDescription() {
        return "December 2013 - January 2014. Clicking the week 1 "
                + "should open the week view for the first week of 2014.";
    }
}
