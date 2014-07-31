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
package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class ComboBoxIdenticalItemsTest extends MultiBrowserTest {

    private WebElement select;

    /* This test has been directly ported from a TB2 test */
    @Test
    public void identicalItemsKeyboardTest() throws Exception {
        openTestURL();

        // wait for the UI to be fully loaded
        waitForElementVisible(By.className("v-filterselect"));
        waitForElementVisible(By.id("Log"));

        select = findElement(By
                .vaadin("/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[1]/VFilterSelect[0]/domChild[0]"));
        select.click();

        Keys[] downDownEnter = new Keys[] { Keys.ARROW_DOWN, Keys.ARROW_DOWN,
                Keys.ENTER };
        sendKeys(downDownEnter);
        assertLogText("1. Item one-1 selected");

        sendKeys(downDownEnter);
        assertLogText("2. Item one-2 selected");

        sendKeys(downDownEnter);
        assertLogText("3. Item two selected");

        sendKeys(new Keys[] { Keys.ARROW_UP, Keys.ARROW_UP, Keys.ARROW_UP,
                Keys.ENTER });
        assertLogText("4. Item one-1 selected");
    }

    private void assertLogText(String expected) throws Exception {
        String text = findElement(By.vaadin("PID_SLog_row_0")).getText();
        Assert.assertTrue("Expected '" + expected + "' found '" + text + "'",
                text.equals(expected));
    }

    private void sendKeys(Keys[] keys) throws Exception {
        for (Keys key : keys) {
            select.sendKeys(key);
            // wait a while between the key presses, at least PhantomJS fails if
            // they are sent too fast
            sleep(10);
        }
    }
}
