package com.vaadin.tests.components.calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.PrivateTB3Configuration;

/**
 * Test that calendar action event source is the calendar, not a private nested
 * class in it.
 *
 * The related code is not browser dependent so only running on a single
 * browser.
 *
 * @author Vaadin Ltd
 */
public class CalendarActionEventSourceTest extends PrivateTB3Configuration {
    @Test
    public void testActionEventSourceIsCalendarForEmptyCell() throws Exception {
        openTestURL();

        // perform action on empty cell
        WebElement element = getDriver()
                .findElement(By.className("v-calendar-spacer"));
        performAction(element);

        checkEventSourceIsCalendar();
    }

    @Test
    public void testActionEventSourceIsCalendarForEvent() throws Exception {
        openTestURL();

        // perform action on calendar event
        WebElement element = getDriver()
                .findElement(By.className("v-calendar-event"));
        performAction(element);

        checkEventSourceIsCalendar();
    }

    private void performAction(WebElement element) {
        // right click
        new Actions(getDriver()).contextClick(element).perform();
        WebElement menuItem = getDriver()
                .findElement(By.className("gwt-MenuItem"));
        menuItem.click();
    }

    private void checkEventSourceIsCalendar() {
        String calendarObject = getDriver().findElement(By.id("calendarlabel"))
                .getText();
        String actionSourceObject = getDriver()
                .findElement(By.id("senderlabel")).getText();
        Assert.assertEquals(
                "Calendar action event source must be the calendar itself",
                calendarObject, actionSourceObject);
    }

    @Override
    protected Class<?> getUIClass() {
        return CalendarActionEventSource.class;
    }

}
