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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * When pressed down key, while positioned on the last item - should show next
 * page and focus on the first item of the next page.
 */
public class ComboBoxScrollingWithArrowsTest extends MultiBrowserTest {

    @Before
    public void openURL() {
        openTestURL();
    }

    @Test
    public void scrollDownArrowKeyTest() throws InterruptedException {
        final int ITEMS_PER_PAGE = 10;
        // Selenium is used instead of TestBench4, because there is no method to
        // access the popup of the combobox
        // The method ComboBoxElement.openPopup() opens the popup, but doesn't
        // provide any way to access the popup and send keys to it.
        // Ticket #13756
        WebElement dropDownComboBox = driver.findElement(By
                .className("v-filterselect-input"));
        // opens Lookup
        dropDownComboBox.sendKeys(Keys.DOWN);
        // go to the last item and then one more
        for (int i = 0; i < ITEMS_PER_PAGE + 1; i++) {
            dropDownComboBox.sendKeys(Keys.DOWN);
        }
        String expected = "item " + ITEMS_PER_PAGE;// item 10

        List<WebElement> items = driver.findElements(By
                .className("gwt-MenuItem-selected"));
        String actual = items.get(0).getText();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void scrollUpArrowKeyTest() throws InterruptedException {
        final int ITEMS_PER_PAGE = 10;
        WebElement dropDownComboBox = driver.findElement(By
                .className("v-filterselect-input"));
        // opens Lookup
        dropDownComboBox.sendKeys(Keys.DOWN);
        // go to the last item and then one more
        for (int i = 0; i < ITEMS_PER_PAGE + 1; i++) {
            dropDownComboBox.sendKeys(Keys.DOWN);
        }
        // move to one item up
        dropDownComboBox.sendKeys(Keys.UP);
        String expected = "item " + (ITEMS_PER_PAGE - 1);// item 9
        List<WebElement> items = driver.findElements(By
                .className("gwt-MenuItem-selected"));
        String actual = items.get(0).getText();
        Assert.assertEquals(expected, actual);
    }
}
