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

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * Test to check notification delay.
 * 
 * @author Vaadin Ltd
 */
public class NotificationDelayTest extends MultiBrowserTest {

    @Test
    public void testDelay() throws InterruptedException {
        openTestURL();

        Assert.assertTrue("No notification found", hasNotification());

        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                new Actions(getDriver()).moveByOffset(10, 10).perform();

                return !hasNotification();
            }
        });
    }

    private boolean hasNotification() {
        return isElementPresent(By.className("v-Notification"));
    }

}
