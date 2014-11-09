package com.vaadin.tests.components.calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.DndActionsTest;

public class NullEventMoveHandlerTest extends DndActionsTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void eventIsNotMovableInMonthView() {
        assertEventCannotBeMoved();
    }

    @Test
    public void eventIsNotMovableInWeekView() {
        openWeekView();
        assertEventCannotBeMoved();
    }

    private void assertEventCannotBeMoved() {
        int originalPosition = getEventXPosition();

        moveEventToNextDay();

        assertThat("Event position changed.", getEventXPosition(),
                is(originalPosition));
    }

    private void openWeekView() {
        getDriver().findElement(By.className("v-calendar-week-number")).click();
    }

    private void moveEventToNextDay() {
        WebElement event = getEvent();
        dragAndDrop(event, event.getSize().getWidth() + 5, 0);
    }

    private int getEventXPosition() {
        return getEvent().getLocation().getX();
    }

    private WebElement getEvent() {
        return getDriver().findElement(By.className("v-calendar-event"));
    }
}