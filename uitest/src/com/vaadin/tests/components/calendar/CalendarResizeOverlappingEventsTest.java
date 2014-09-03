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

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.DndActionsTest;

/**
 * 
 * @author Vaadin Ltd
 */
public class CalendarResizeOverlappingEventsTest extends DndActionsTest {

    private int noOverlapWidth;
    private int oneOverlapWidth;
    private int twoOverlapsWidth;

    private WebElement firstEvent;
    private WebElement secondEvent;
    private WebElement thirdEvent;

    private WebElement firstEventBottomResize;
    private WebElement secondEventBottomResize;
    private WebElement thirdEventBottomResize;

    @Test
    public void testCalendarResizeOverlappingEvents()
            throws InterruptedException, IOException {

        openTestURL();
        initParams();
        doTest();
    }

    private void doTest() {
        assertWidths(noOverlapWidth, noOverlapWidth, noOverlapWidth);

        dragAndDrop(firstEventBottomResize, 240);
        assertWidths(oneOverlapWidth, oneOverlapWidth, oneOverlapWidth);

        dragAndDrop(secondEventBottomResize, 240);
        assertWidths(twoOverlapsWidth, twoOverlapsWidth, twoOverlapsWidth);

        dragAndDrop(secondEventBottomResize, -240);
        dragAndDrop(firstEventBottomResize, -240);
        assertWidths(noOverlapWidth, noOverlapWidth, noOverlapWidth);

    }

    private void assertWidths(int firstEventExpectedWidth,
            int secondEventExpectedWidth, int thirdEventExpectedWidth) {
        int widthTolerance = 5;
        String errorMessage = "Wrong event width after resizing, expected [%d] (+/-%d), obtained [%d]";

        int actualWidth = firstEvent.getSize().getWidth();
        int expectedWidth = firstEventExpectedWidth;
        Assert.assertTrue(String.format(errorMessage, expectedWidth,
                widthTolerance, actualWidth),
                isAproximateWidth(actualWidth, expectedWidth, widthTolerance));

        actualWidth = secondEvent.getSize().getWidth();
        expectedWidth = secondEventExpectedWidth;
        Assert.assertTrue(String.format(errorMessage, expectedWidth,
                widthTolerance, actualWidth),
                isAproximateWidth(actualWidth, expectedWidth, widthTolerance));

        actualWidth = thirdEvent.getSize().getWidth();
        expectedWidth = thirdEventExpectedWidth;
        Assert.assertTrue(String.format(errorMessage, expectedWidth,
                widthTolerance, actualWidth),
                isAproximateWidth(actualWidth, expectedWidth, widthTolerance));
    }

    private boolean isAproximateWidth(int actualWidth, int expectedWidth,
            int tolerance) {
        return Math.abs(expectedWidth - actualWidth) <= tolerance;
    }

    private void dragAndDrop(WebElement element, int yOffset) {
        dragAndDrop(element, 0, yOffset);
    }

    private void initParams() {
        WebElement dateSlot = getDriver().findElement(
                By.className("v-datecellslot"));
        int dateSlotWidth = dateSlot.getSize().getWidth();
        noOverlapWidth = dateSlotWidth;
        oneOverlapWidth = dateSlotWidth / 2;
        twoOverlapsWidth = dateSlotWidth / 3;

        Comparator<WebElement> startTimeComparator = new Comparator<WebElement>() {
            @Override
            public int compare(WebElement e1, WebElement e2) {
                int e1Top = e1.getLocation().getY();
                int e2Top = e2.getLocation().getY();
                return e1Top - e2Top;
            }
        };

        List<WebElement> eventElements = getDriver().findElements(
                By.className("v-calendar-event-content"));
        Collections.sort(eventElements, startTimeComparator);
        firstEvent = eventElements.get(0);
        secondEvent = eventElements.get(1);
        thirdEvent = eventElements.get(2);

        List<WebElement> resizeBottomElements = getDriver().findElements(
                By.className("v-calendar-event-resizebottom"));
        Collections.sort(resizeBottomElements, startTimeComparator);
        firstEventBottomResize = resizeBottomElements.get(0);
        secondEventBottomResize = resizeBottomElements.get(1);
        thirdEventBottomResize = resizeBottomElements.get(2);
    }
}
