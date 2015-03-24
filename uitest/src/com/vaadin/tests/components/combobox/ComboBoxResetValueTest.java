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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxResetValueTest extends MultiBrowserTest {

    static final String FILTER_STRING = "filter";

    @Test
    public void testNullSelectionAllowedAndSetNullSelectionItemId() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.NULL_SELECTION_ALLOWED_WITH_SET_NULL_SELECTION_ITEM_ID);
        clickResetButton();

        openPopup(comboBoxWebElement);

        assertEquals("There should be selected: "
                + ComboBoxResetValue.EMPTY_VALUE,
                ComboBoxResetValue.EMPTY_VALUE, getSelectedInPopupValue());
    }

    @Test
    public void testFilterNullSelectionAllowedAndSetNullSelectionItemId() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.NULL_SELECTION_ALLOWED_WITH_SET_NULL_SELECTION_ITEM_ID);
        clickResetButton();
        printFilterAndRemoveIt(getComboBoxInput(comboBoxWebElement));

        assertEquals("There should be " + ComboBoxResetValue.EMPTY_VALUE,
                ComboBoxResetValue.EMPTY_VALUE,
                getComboBoxValue(comboBoxWebElement));
    }

    @Test
    public void testNullSelectionAllowedWithoutNullSelectionItemId() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.NULL_SELECTION_ALLOWED_WITHOUT_NULL_SELECTION_ITEM_ID);
        clickResetButton();

        openPopup(comboBoxWebElement);

        // not sure about expected result here.. Should be first empty string
        // selected or not after reseting..
        assertEquals("There should be no selection", null,
                getSelectedInPopupValue());
    }

    @Test
    public void testFilterNullSelectionAllowedWithoutNullSelectionItemId() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.NULL_SELECTION_ALLOWED_WITHOUT_NULL_SELECTION_ITEM_ID);
        clickResetButton();
        printFilterAndRemoveIt(getComboBoxInput(comboBoxWebElement));

        assertEquals("There should be empty value", "",
                getComboBoxValue(comboBoxWebElement));
    }

    @Test
    public void testNullSelectionNotAllowed() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class).id(
                ComboBoxResetValue.NULL_SELECTION_NOT_ALLOWED);
        clickResetButton();

        openPopup(comboBoxWebElement);

        assertEquals("There should be no selection", null,
                getSelectedInPopupValue());
    }

    @Test
    public void testFilterNullSelectionNotAllowed() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class).id(
                ComboBoxResetValue.NULL_SELECTION_NOT_ALLOWED);
        clickResetButton();
        printFilterAndRemoveIt(getComboBoxInput(comboBoxWebElement));

        assertEquals("There should be empty value", "",
                getComboBoxValue(comboBoxWebElement));
    }

    private void openPopup(ComboBoxElement comboBox) {
        if (!isElementPresent(By.vaadin("#popup"))) {
            comboBox.openPopup();
        }
    }

    private String getSelectedInPopupValue() {
        try {
            WebElement selectedSpan = driver.findElement(By
                    .cssSelector(".gwt-MenuItem-selected span"));
            return selectedSpan.getText();
        } catch (NoSuchElementException e) {
            return null;
        } catch (WebDriverException e) {
            if (e.getMessage() != null
                    && e.getMessage().contains("Unable to find element")) {
                return null;
            }
            throw e;
        }
    }

    private void clickResetButton() {
        ButtonElement resetButton = $(ButtonElement.class).first();
        // workaround because of IE10 that doesn't always respond to click
        resetButton.focus();
        resetButton.sendKeys(Keys.ENTER);
    }

    private void printFilterAndRemoveIt(WebElement target) {
        Actions actions = new Actions(getDriver());
        actions.click(target).perform();
        actions.sendKeys(Keys.chord(Keys.CONTROL, "a")).perform(); // Select all
        actions.sendKeys(FILTER_STRING);
        actions.sendKeys(Keys.ENTER).sendKeys(Keys.ESCAPE).sendKeys(Keys.TAB); // hack
        actions.perform();
    }

    private String getComboBoxValue(ComboBoxElement comboBox) {
        return getComboBoxInput(comboBox).getAttribute("value");
    }

    private WebElement getComboBoxInput(ComboBoxElement comboBox) {
        return comboBox.findElement(By.tagName("input"));
    }
}
