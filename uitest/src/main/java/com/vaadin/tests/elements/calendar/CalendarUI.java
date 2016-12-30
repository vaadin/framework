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
