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
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests if long event which began before the view period is shown (#15242)
 */
public class BeanItemContainerLongEventTest extends MultiBrowserTest {

    @Override
    protected String getDeploymentPath() {
        return "/run/BeanItemContainerTestUI?restartApplication";
    }

    @Test
    public void testEventDisplayedInWeekView() {
        openTestURL();
        WebElement target = driver.findElements(
                By.className("v-calendar-week-number")).get(1);
        target.click();
        target = driver.findElement(By.className("v-calendar-event"));
        Assert.assertEquals("Wrong event name", "Long event", target.getText());
    }

    @Test
    public void testEventDisplayedInDayView() {
        openTestURL();
        WebElement target = driver.findElements(
                By.className("v-calendar-day-number")).get(5);
        target.click();
        target = driver.findElement(By.className("v-calendar-event"));
        Assert.assertEquals("Wrong event name", "Long event", target.getText());
    }

}
