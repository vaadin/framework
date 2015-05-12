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

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

/**
 * Test for identical item captions in ComboBox.
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxIdenticalItemsTest extends MultiBrowserTest {

    @Test
    public void identicalItemsKeyboardTest() {
        openTestURL();
        int delay = BrowserUtil.isPhantomJS(getDesiredCapabilities()) ? 500 : 0;

        ComboBoxElement combobox = $(ComboBoxElement.class).first();

        combobox.sendKeys(delay, Keys.ARROW_DOWN, getReturn());
        waitUntilLogText("1. Item one-1 selected");

        Keys[] downDownEnter = new Keys[] { Keys.ARROW_DOWN, Keys.ARROW_DOWN,
                getReturn() };

        combobox.sendKeys(delay, downDownEnter);
        waitUntilLogText("2. Item one-2 selected");

        combobox.sendKeys(delay, downDownEnter);
        waitUntilLogText("3. Item two selected");

        combobox.sendKeys(delay, new Keys[] { Keys.ARROW_UP, Keys.ARROW_UP,
                Keys.ARROW_UP, getReturn() });
        waitUntilLogText("4. Item one-1 selected");
    }

    private Keys getReturn() {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            return Keys.ENTER;
        }
        return Keys.RETURN;
    }

    private void waitUntilLogText(final String expected) {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String text;

            @Override
            public Boolean apply(WebDriver input) {
                text = findElement(By.vaadin("PID_SLog_row_0")).getText();
                return text.equals(expected);
            }

            @Override
            public String toString() {
                return String.format(
                        "log content to update. Expected: '%s' (was: '%s')",
                        expected, text);
            }
        });
    }
}
