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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests opening and closing of calendar notifications.
 * 
 * @author Vaadin Ltd
 */
public class CalendarNotificationsTestIE extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return CalendarNotifications.class;
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected boolean usePersistentHoverForIE() {
        return false;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getIEBrowsersOnly();
    }

    @Test
    public void notificationTest() throws Exception {
        openTestURL();

        WebElement day = findElements(By.className("v-calendar-day-number"))
                .get(2);
        // IE8 requires you to click on the text part to fire the event
        new Actions(getDriver())
                .moveToElement(day, day.getSize().getWidth() - 3,
                        day.getSize().getHeight() / 2).click().perform();

        // check that a notification was opened, this is done with a log instead
        // of a screenshot or element presence check due to problems with IE
        // webdriver
        String text = findElement(By.id("Log")).findElement(
                By.className("v-label")).getText();
        Assert.assertTrue("Notification should've opened",
                "1. Opening a notification".equals(text));

        // move the mouse around a bit
        new Actions(getDriver()).moveByOffset(5, 5).moveByOffset(100, 100)
                .perform();

        // wait until the notification has animated out
        sleep(1000);

        Assert.assertFalse("There should be no notification on the page",
                $(NotificationElement.class).exists());
    }
}
