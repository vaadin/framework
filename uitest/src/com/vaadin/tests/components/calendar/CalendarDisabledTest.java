package com.vaadin.tests.components.calendar;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.CalendarElement;

public class CalendarDisabledTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return CalendarReadOnly.class;
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL("restartApplication&disabled");
    }

    private CalendarElement getCalendar() {
        return $(CalendarElement.class).first();
    }

    @Test
    public void weekViewCannotBeOpenedFromMonthView() {
        tryOpenWeekView();
        assertCalendarInMonthView();
    }

    @Test
    public void dayViewCannotBeOpenedFromMonthView() {
        tryOpenDayView();
        assertCalendarInMonthView();
    }

    private void tryOpenDayView() {
        getCalendar().getDayNumbers().get(0).click();
    }

    private void tryOpenWeekView() {
        getCalendar().getWeekNumbers().get(0).click();
    }

    private void assertCalendarInMonthView() {
        assertTrue("Calendar wasn't in month view.", getCalendar()
                .hasMonthView());
    }
}
