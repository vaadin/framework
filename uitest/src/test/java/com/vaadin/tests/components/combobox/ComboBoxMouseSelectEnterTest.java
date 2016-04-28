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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class ComboBoxMouseSelectEnterTest extends MultiBrowserTest {

    private ComboBoxElement comboBoxElement;

    @Override
    public void setup() throws Exception {

        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        comboBoxElement = $(ComboBoxElement.class).first();
    }

    @Test
    public void enterSetsValueSelectedByMouseOver() {
        comboBoxElement.openPopup();
        comboBoxElement.sendKeys(Keys.DOWN, Keys.DOWN);
        String selectedItemText = findElement(
                By.className("gwt-MenuItem-selected")).getText();
        assertThat("Item selected by arrows should be a1", selectedItemText,
                is("a1"));
        new Actions(driver).moveToElement(getWebElementForItem("a5")).build()
                .perform();
        comboBoxElement.sendKeys(getReturn());
        assertThat("Item selected by mouse should be a5",
                comboBoxElement.getText(), is("a5"));
        checkLabelValue("a5");
    }

    private WebElement getWebElementForItem(String wantedText) {
        WebElement wantedItem = null;
        List<WebElement> items = findElements(By.className("gwt-MenuItem"));
        for (WebElement item : items) {
            if (item.getText().equals(wantedText)) {
                wantedItem = item;
                break;
            }
        }
        return wantedItem;
    }

    private Keys getReturn() {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            return Keys.ENTER;
        } else {
            return Keys.RETURN;
        }
    }

    private void checkLabelValue(final String expectedValue) {

        waitUntil(new ExpectedCondition<Boolean>() {
            private String actualValue;

            @Override
            public Boolean apply(WebDriver input) {
                actualValue = $(LabelElement.class).id("value").getText();
                return actualValue.equals(expectedValue);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("Label value to match '%s' (was: '%s')",
                        expectedValue, actualValue);
            }
        });
    }

}