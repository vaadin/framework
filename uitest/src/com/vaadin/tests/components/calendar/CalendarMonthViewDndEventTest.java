/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.calendar;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.DndActionsTest;

/**
 * Test to check how DnD works for regular (not all day event) in calendar month
 * view.
 * 
 * @author Vaadin Ltd
 */
public class CalendarMonthViewDndEventTest extends DndActionsTest {

    @Test
    public void dragAndDropEventToNextDay() {
        openTestURL();

        WebElement event = getDriver().findElement(
                By.className("v-calendar-event"));
        int x = event.getLocation().getX();
        int width = event.getSize().getWidth();

        WebElement cell = getDriver().findElement(
                By.className("v-calendar-month-day"));

        int cellY = cell.getLocation().getY();
        int cellHeight = cell.getSize().getHeight();

        long origStart = getTime("start");
        long origEnd = getTime("end");

        dragAndDrop(event, event.getSize().getWidth() + 5, 0);

        event = getDriver().findElement(By.className("v-calendar-event"));
        int newX = event.getLocation().getX();
        int newY = event.getLocation().getY();

        Assert.assertTrue(
                "Moved event has wrong Y position (not the same row)",
                newY >= cellY && newY < cellY + cellHeight);
        Assert.assertTrue(
                "Moved event has wrong X position (not after original event)",
                newX >= x + width - 1);

        long start = getTime("start");
        long end = getTime("end");

        int day = 24 * 3600000;
        Assert.assertEquals(
                "Start date of moved event is not next day, same time",
                origStart + day, start);
        Assert.assertEquals(
                "End date of moved event is not next day, same time", origEnd
                        + day, end);
    }

    private long getTime(String style) {
        WebElement start = getDriver().findElement(By.className(style));
        return Long.parseLong(start.getText());
    }

}
