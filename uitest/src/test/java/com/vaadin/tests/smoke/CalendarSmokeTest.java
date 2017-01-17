package com.vaadin.tests.smoke;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
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
        assertThat("Week didn't change.", firstDayOfCurrentWeek,
                is(not(firstDayOfNextWeek)));
        reload();

        openDayView();
        String currentDay = getVisibleFirstDay();
        getCalendar().next();
        String nextDay = getVisibleFirstDay();
        assertThat("Day didn't change.", currentDay, is(not(nextDay)));
        reload();

        openDayView();
        currentDay = getVisibleFirstDay();
        getCalendar().back();
        String previousDay = getVisibleFirstDay();
        assertThat("Day didn't change.", currentDay, is(not(previousDay)));
        reload();

        WebElement dayWithEvents = getFirstDayWithEvents();
        assertThat("Incorrect event count.",
                getVisibleEvents(dayWithEvents).size(), is(2));
        toggleExpandEvents(dayWithEvents).click();
        assertThat("Incorrect event count.",
                getVisibleEvents(dayWithEvents).size(), is(4));
        toggleExpandEvents(dayWithEvents).click();
        assertThat("Incorrect event count.",
                getVisibleEvents(dayWithEvents).size(), is(2));
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
            if (getVisibleEvents(monthDay).size() > 0) {
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
