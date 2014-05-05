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
package com.vaadin.tests.components.notification;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Unit test class for Notification with middle left and middle right positions.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class MiddleNotificationPositionTest extends MultiBrowserTest {

    @Test
    public void testMiddleLeft() {
        openTestURL();

        WebElement webElement = driver.findElement(By
                .className("show-middle-left"));
        webElement.click();

        WebElement notification = driver.findElement(By
                .className("v-Notification"));

        Assert.assertNotNull(notification);
        String left = notification.getCssValue("left");
        Assert.assertEquals(
                "Left position of notification element should be 0px", "0px",
                left);
        Point location = notification.getLocation();
        Assert.assertEquals("X coordinate of notifiation element should be 0",
                0, location.getX());

        WebElement body = driver.findElement(By.tagName("body"));
        int height = body.getSize().height;

        Assert.assertTrue("Y coordinate of notification element is too small",
                height / 2 - notification.getSize().height / 2 - 1 <= location
                        .getY());
        Assert.assertTrue("Y coordinate of notification element is too big",
                height / 2 + 1 >= location.getY());
    }

    @Test
    public void testMiddleRight() {
        openTestURL();

        WebElement webElement = driver.findElement(By
                .className("show-middle-right"));
        webElement.click();

        WebElement notification = driver.findElement(By
                .className("v-Notification"));

        Assert.assertNotNull(notification);
        String right = notification.getCssValue("right");
        Assert.assertEquals(
                "Right position of notification element should be 0px", "0px",
                right);

        WebElement body = driver.findElement(By.tagName("body"));
        int height = body.getSize().height;
        int width = body.getSize().width;

        Point location = notification.getLocation();
        Assert.assertTrue(
                "Notification right border should be in the rightmost position",
                width - 1 <= location.getX()
                        + notification.getSize().getWidth());

        Assert.assertTrue("Y coordinate of notification element is too small",
                height / 2 - notification.getSize().height / 2 - 1 <= location
                        .getY());
        Assert.assertTrue("Y coordinate of notification element is too big",
                height / 2 + 1 >= location.getY());
    }

}
