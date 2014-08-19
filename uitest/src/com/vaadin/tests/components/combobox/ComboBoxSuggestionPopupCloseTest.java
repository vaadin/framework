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

import static org.junit.Assert.assertFalse;
import static org.openqa.selenium.Keys.ARROW_DOWN;
import static org.openqa.selenium.Keys.ENTER;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class ComboBoxSuggestionPopupCloseTest extends MultiBrowserTest {

    private WebElement selectTextbox;

    @Test
    public void closeSuggestionPopupTest() throws Exception {
        openTestURL();

        waitForElementVisible(By.className("v-filterselect"));

        selectTextbox = $(ComboBoxElement.class).first().findElement(
                By.vaadin("#textbox"));
        selectTextbox.click();

        // open popup and select first element
        sendKeys(new Keys[] { ARROW_DOWN, ARROW_DOWN, ENTER });

        // open popup and hit enter to close it
        sendKeys(new Keys[] { ARROW_DOWN, ENTER });

        assertFalse(isElementPresent(By.className("v-filterselect-suggestmenu")));

    }

    private void sendKeys(Keys[] keys) throws Exception {
        for (Keys key : keys) {
            selectTextbox.sendKeys(key);
            // wait a while between the key presses, at least PhantomJS fails if
            // they are sent too fast
            sleep(10);
        }
    }
};
