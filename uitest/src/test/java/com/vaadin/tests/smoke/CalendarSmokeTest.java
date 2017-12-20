package com.vaadin.tests.smoke;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CalendarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CalendarSmokeTest extends MultiBrowserTest {

    @Test
    public void calendarSmokeTest() {
        openTestURL();

        smokeTest();
    }

    @Test
    public void readOnlyCalendarSmokeTest() {
        openTestURL("restartApplication&readonly");

        smokeTest();
    }

    private void smokeTest() {
        openWeekView();

        assertTrue("Calendar wasn't in week view.",
                getCalendar().hasWeekView());
        reload();

        openDayView();
        assertTrue("Calendar wasn't in day view.", getCalendar().hasDayView());
        reload();

        openWeekView();
        getCalendar().getDayHeaders().get(0).click();
        assertTrue("Calendar wasn't in day view.", getCalendar().hasDayView());
        reload();

        openWeekView();
        String firstDayOfCurrentWeek = getVisibleFirstDay();
        getCalendar().next();
        String firstDayOfNextWeek = getVisibleFirstDay();
        assertNotEquals("Week didn't change.", firstDayOfNextWeek,
                firstDayOfCurrentWeek);
        reload();

        openDayView();
        String currentDay = getVisibleFirstDay();
        getCalendar().next();
        String nextDay = getVisibleFirstDay();
        assertNotEquals("Day didn't change.", nextDay, currentDay);
        reload();

        openDayView();
        currentDay = getVisibleFirstDay();
        getCalendar().back();
        String previousDay = getVisibleFirstDay();
        assertNotEquals("Day didn't change.", previousDay, currentDay);
        reload();

        WebElement dayWithEvents = getFirstDayWithEvents();
        assertEquals("Incorrect event count.", 2,
                getVisibleEvents(dayWithEvents).size());
        toggleExpandEvents(dayWithEvents).click();
        assertEquals("Incorrect event count.", 4,
                getVisibleEvents(dayWithEvents).size());
        toggleExpandEvents(dayWithEvents).click();
        assertEquals("Incorrect event count.", 2,
                getVisibleEvents(dayWithEvents).size());
    }

    private CalendarElement getCalendar() {
        return $(CalendarElement.class).first();
    }

    private void openDayView() {
        getCalendar().getDayNumbers().get(0).click();
    }

    private void openWeekView() {
        getCalendar().getWeekNumbers().get(0).click();
    }

    private String getVisibleFirstDay() {
        return getCalendar().getDayHeaders().get(0).getText();
    }

    private WebElement getFirstDayWithEvents() {
        for (WebElement monthDay : getCalendar().getMonthDays()) {
            if (!getVisibleEvents(monthDay).isEmpty()) {
                return monthDay;
            }
        }

        return null;
    }

    private void reload() {
        getDriver().navigate().refresh();
    }

    private WebElement toggleExpandEvents(WebElement dayWithEvents) {
        return dayWithEvents
                .findElement(By.className("v-calendar-bottom-spacer"));
    }

    private List<WebElement> getVisibleEvents(WebElement dayWithEvents) {
        return dayWithEvents.findElements(By.className("v-calendar-event"));
    }
}
