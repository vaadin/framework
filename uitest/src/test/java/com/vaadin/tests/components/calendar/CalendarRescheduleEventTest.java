package com.vaadin.tests.components.calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.DndActionsTest;

/**
 * Test to check ability to reschedule events unlimited times.
 *
 * @author Vaadin Ltd
 */
public class CalendarRescheduleEventTest extends DndActionsTest {

    @Test
    public void rescheduleEventSeveralTimes() {
        openTestURL();

        // Reschedule event for the first time
        int y = rescheduleEvent(20);

        WebElement startElement = getDriver()
                .findElement(By.className("start"));
        WebElement endElement = getDriver().findElement(By.className("end"));

        long start = Long.parseLong(startElement.getText());
        long end = Long.parseLong(endElement.getText());

        long duration = end - start;

        // Reschedule event for the second time
        int yNew = rescheduleEvent(20);

        startElement = getDriver().findElement(By.className("start"));
        endElement = getDriver().findElement(By.className("end"));

        long newStart = Long.parseLong(startElement.getText());
        long newEnd = Long.parseLong(endElement.getText());

        Assert.assertTrue(
                "Second rescheduling did not change the event start time",
                newStart > start);
        Assert.assertEquals(
                "Duration of the event after second rescheduling has been changed",
                duration, newEnd - newStart);
        Assert.assertTrue(
                "Second rescheduling did not change the event Y coordinate",
                yNew > y);
    }

    /*
     * DnD event by Y axis
     */
    private int rescheduleEvent(int yOffset) {
        WebElement eventCaption = getDriver()
                .findElement(By.className("v-calendar-event-caption"));

        dragAndDrop(eventCaption, 0, yOffset);

        eventCaption = getDriver()
                .findElement(By.className("v-calendar-event-caption"));
        return eventCaption.getLocation().getY();
    }

}
