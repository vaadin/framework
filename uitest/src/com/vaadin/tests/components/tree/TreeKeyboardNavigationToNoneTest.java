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
package com.vaadin.tests.components.tree;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for keyboard navigation in tree in case when there are no items to
 * navigate.
 * 
 * @author Vaadin Ltd
 */
public class TreeKeyboardNavigationToNoneTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();
    }

    @Test
    public void navigateUpForTheFirstItem() {
        sendKey(Keys.ARROW_UP);
        checkNotificationErrorAbsence("first");
    }

    @Test
    public void navigateDownForTheLastItem() {
        $(ButtonElement.class).first().click();
        sendKey(Keys.ARROW_DOWN);
        checkNotificationErrorAbsence("last");
    }

    private void checkNotificationErrorAbsence(String item) {
        Assert.assertFalse(
                "Notification is found after using keyboard for navigation "
                        + "from " + item + " tree item",
                isElementPresent(By.className("v-Notification")));
    }

    private void sendKey(Keys key) {
        Actions actions = new Actions(getDriver());
        actions.sendKeys(key).build().perform();
    }
}
