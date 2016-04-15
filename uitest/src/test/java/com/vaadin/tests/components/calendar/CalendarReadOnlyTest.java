package com.vaadin.tests.components.calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.CalendarElement;

public class CalendarReadOnlyTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL("restartApplication&readonly");
    }

    @Test
    public void weekViewCanBeOpenedFromMonthView() {
        openWeekView();

        assertTrue("Calendar wasn't in week view.", getCalendar().hasWeekView());
    }

    @Test
    public void dayViewCanBeOpenedFromMonthView() {
        openDayView();

        assertTrue("Calendar wasn't in day view.", getCalendar().hasDayView());
    }

    @Test
    public void dayViewCanBeOpenedFromWeekView() {
        openWeekView();

        getCalendar().getDayHeaders().get(0).click();

        assertTrue("Calendar wasn't in day view.", getCalendar().hasDayView());
    }

    @Test
    public void weekViewCanBeBrowsedForwards() {
        openWeekView();

        String firstDayOfCurrentWeek = getVisibleFirstDay();
        getCalendar().next();

        String firstDayOfNextWeek = getVisibleFirstDay();

        assertThat("Week didn't change.", firstDayOfCurrentWeek,
                is(not(firstDayOfNextWeek)));
    }

    @Test
    public void weekViewCanBeBrowsedBackwards() {
        openWeekView();

        String firstDayOfCurrentWeek = getVisibleFirstDay();
        getCalendar().back();

        String firstDayOfPreviousWeek = getVisibleFirstDay();

        assertThat("Week didn't change.", firstDayOfCurrentWeek,
                is(not(firstDayOfPreviousWeek)));
    }

    @Test
    public void dayViewCanBeBrowsedForwards() {
        openDayView();

        String currentDay = getVisibleFirstDay();
        getCalendar().next();

        String nextDay = getVisibleFirstDay();

        assertThat("Day didn't change.", currentDay, is(not(nextDay)));
    }

    @Test
    public void dayViewCanBeBrowsedBackwards() {
        openDayView();

        String currentDay = getVisibleFirstDay();
        getCalendar().back();

        String previousDay = getVisibleFirstDay();

        assertThat("Day didn't change.", currentDay, is(not(previousDay)));
    }

    @Test
    public void hiddenEventsCanBeExpanded() {
        WebElement dayWithEvents = getFirstDayWithEvents();

        assertThat("Incorrect event count.", getVisibleEvents(dayWithEvents)
                .size(), is(2));

        toggleExpandEvents(dayWithEvents).click();
        assertThat("Incorrect event count.", getVisibleEvents(dayWithEvents)
                .size(), is(4));

        toggleExpandEvents(dayWithEvents).click();
        assertThat("Incorrect event count.", getVisibleEvents(dayWithEvents)
                .size(), is(2));
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

    private WebElement toggleExpandEvents(WebElement dayWithEvents) {
        return dayWithEvents.findElement(By
                .className("v-calendar-bottom-spacer"));
    }

    private List<WebElement> getVisibleEvents(WebElement dayWithEvents) {
        return dayWithEvents.findElements(By.className("v-calendar-event"));
    }
}
