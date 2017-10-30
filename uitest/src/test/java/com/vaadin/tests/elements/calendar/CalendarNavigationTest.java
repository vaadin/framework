package com.vaadin.tests.elements.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CalendarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CalendarNavigationTest extends MultiBrowserTest {

    private CalendarElement calendarElement;

    @Override
    protected Class<?> getUIClass() {
        return CalendarUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        calendarElement = $(CalendarElement.class).first();
    }

    @Test
    public void calendarNavigation_backAndForwardInWeekView_navigationWorks() {
        assertTrue(calendarElement.hasWeekView());
        String originalFirstDay = calendarElement.getDayHeaders().get(0)
                .getText();

        calendarElement.back();
        calendarElement.waitForVaadin();
        assertNotEquals(originalFirstDay,
                calendarElement.getDayHeaders().get(0).getText());

        calendarElement.next();
        calendarElement.waitForVaadin();

        assertEquals(originalFirstDay,
                calendarElement.getDayHeaders().get(0).getText());
    }

    @Test(expected = IllegalStateException.class)
    public void calendarNavigation_navigationInMonthView_exceptionThrown() {
        $(ButtonElement.class).get(0).click();
        calendarElement.waitForVaadin();

        assertTrue(calendarElement.hasMonthView());

        calendarElement.next();
    }
}
