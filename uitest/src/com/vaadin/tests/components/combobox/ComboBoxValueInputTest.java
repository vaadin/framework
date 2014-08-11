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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests ComboBox inputs and selections.
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxValueInputTest extends MultiBrowserTest {

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    };

    @Test
    public void testOverall() {
        List<ComboBoxElement> comboBoxes = $(ComboBoxElement.class).all();
        assertEquals("unexpected amount of comboboxes found", 6,
                comboBoxes.size());
    }

    @Test
    public void testFirstComboBoxInputs() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).all().get(0);

        // check the initial selection
        assertEquals("correct selection not found", "",
                comboBox.findElement(By.tagName("input")).getAttribute("value"));

        // ensure the combobox has suggested values
        assertFalse("suggestions not found even if there should be several",
                comboBox.getPopupSuggestions().isEmpty());

        // check that the found items were correct ones
        WebElement popup = driver.findElement(By
                .className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 4,
                filteredItems.size());
        assertEquals("wrong filtering result", " ", filteredItems.get(0)
                .getText());
        assertEquals("wrong filtering result", "Value 1", filteredItems.get(1)
                .getText());
        assertEquals("wrong filtering result", "Value 3", filteredItems.get(3)
                .getText());

        // select the second last item
        filteredItems.get(2).click();

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));

        // check the updated selection
        assertEquals("correct selection not found", "Value 2", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));
    }

    @Test
    public void testSecondComboBoxInputs() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).all().get(1);

        // check the input prompt
        assertEquals("correct input prompt not found", "Please select",
                comboBox.findElement(By.tagName("input")).getAttribute("value"));

        // focus the combobox
        comboBox.findElement(By.tagName("input")).click();

        // check the input prompt has disappeared
        assertEquals("correct input prompt not found", "", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // ensure the combobox has suggested values
        assertFalse("suggestions not found even if there should be several",
                comboBox.getPopupSuggestions().isEmpty());

        // check that the found items were correct ones
        WebElement popup = driver.findElement(By
                .className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 4,
                filteredItems.size());
        assertEquals("wrong filtering result", " ", filteredItems.get(0)
                .getText());
        assertEquals("wrong filtering result", "Value 1", filteredItems.get(1)
                .getText());
        assertEquals("wrong filtering result", "Value 3", filteredItems.get(3)
                .getText());

        // select the last item
        filteredItems.get(3).click();

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));

        // check the updated selection
        assertEquals("correct selection not found", "Value 3", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));
    }

    @Test
    public void testThirdComboBoxInputs() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).all().get(2);

        // check the null selection
        assertEquals("correct selection not found", "Null item", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // ensure the combobox has suggested values
        assertFalse("suggestions not found even if there should be several",
                comboBox.getPopupSuggestions().isEmpty());

        // check that the found items were correct ones
        WebElement popup = driver.findElement(By
                .className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 4,
                filteredItems.size());
        assertEquals("wrong filtering result", "Null item", filteredItems
                .get(0).getText());
        assertEquals("wrong filtering result", "Value 1", filteredItems.get(1)
                .getText());
        assertEquals("wrong filtering result", "Value 3", filteredItems.get(3)
                .getText());

        // ensure the null item is marked as selected
        assertEquals("wrong selection", "Null item",
                popup.findElement(By.className("v-filterselect-suggestmenu"))
                        .findElement(By.className("gwt-MenuItem-selected"))
                        .getText());

        // select the second last item
        filteredItems.get(2).click();

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));

        // check the updated selection
        assertEquals("correct selection not found", "Value 2", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // open the popup again
        comboBox.openPopup();

        // check that the found items were correct ones
        popup = driver.findElement(By.className("v-filterselect-suggestpopup"));
        filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 4,
                filteredItems.size());
        assertEquals("wrong filtering result", "Null item", filteredItems
                .get(0).getText());
        assertEquals("wrong filtering result", "Value 1", filteredItems.get(1)
                .getText());
        assertEquals("wrong filtering result", "Value 3", filteredItems.get(3)
                .getText());

        // ensure the selected item is marked as selected
        assertEquals("wrong selection", "Value 2",
                popup.findElement(By.className("v-filterselect-suggestmenu"))
                        .findElement(By.className("gwt-MenuItem-selected"))
                        .getText());

        // select the first item again
        filteredItems.get(0).click();

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));

        // check the displayed value didn't change
        assertEquals("correct selection not found", "Null item", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // enter a new filtering value
        comboBox.findElement(By.tagName("input")).clear();
        comboBox.findElement(By.tagName("input")).sendKeys("value");

        // ensure there's a popup again
        assertTrue("popup not found when there should be one",
                comboBox.isElementPresent(By.vaadin("#popup")));

        // check that the found items were correct ones
        popup = driver.findElement(By.className("v-filterselect-suggestpopup"));
        filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 3,
                filteredItems.size());
        assertEquals("wrong filtering result", "Value 1", filteredItems.get(0)
                .getText());
        assertEquals("wrong filtering result", "Value 3", filteredItems.get(2)
                .getText());

        // check the displayed value updated
        assertEquals("correct selection not found", "value", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        comboBox.findElement(By.tagName("input")).sendKeys(Keys.ARROW_DOWN);

        // ensure the first item is marked as selected
        assertEquals("wrong selection", "Value 1",
                popup.findElement(By.className("v-filterselect-suggestmenu"))
                        .findElement(By.className("gwt-MenuItem-selected"))
                        .getText());

        // navigate with keys to the last item
        comboBox.findElement(By.tagName("input")).sendKeys(Keys.ARROW_DOWN);
        comboBox.findElement(By.tagName("input")).sendKeys(Keys.ARROW_DOWN);

        // TODO: remove this limit once #14402 has been fixed
        if (getBrowsersExcludingIE().contains(getDesiredCapabilities())) {
            // ensure the last item is marked as selected
            assertEquals(
                    "wrong selection",
                    "Value 3",
                    popup.findElement(
                            By.className("v-filterselect-suggestmenu"))
                            .findElement(By.className("gwt-MenuItem-selected"))
                            .getText());
        }

        // select the last item
        comboBox.findElement(By.tagName("input")).sendKeys(Keys.RETURN);

        // TODO: remove this limit once #14402 has been fixed
        if (getBrowsersExcludingIE().contains(getDesiredCapabilities())) {
            // check the updated selection
            assertEquals("correct selection not found", "Value 3", comboBox
                    .findElement(By.tagName("input")).getAttribute("value"));
        }

        // TODO: remove this altogether once #14402 has been fixed
        if (Browser.PHANTOMJS.getDesiredCapabilities().equals(
                getDesiredCapabilities())) {
            // toggle popup closed since the RETURN didn't do it
            comboBox.openPopup();
        }

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));
    }

    @Test
    public void testFourthComboBoxInputs() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).all().get(3);

        // check the combobox displays the null selection instead of the input
        // prompt
        assertEquals("correct value not displayed", "Null item", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // ensure the combobox has suggested values
        assertFalse("suggestions not found even if there should be several",
                comboBox.getPopupSuggestions().isEmpty());

        // check that the found items were correct ones
        WebElement popup = driver.findElement(By
                .className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 4,
                filteredItems.size());
        assertEquals("wrong filtering result", "Null item", filteredItems
                .get(0).getText());
        assertEquals("wrong filtering result", "Value 1", filteredItems.get(1)
                .getText());
        assertEquals("wrong filtering result", "Value 3", filteredItems.get(3)
                .getText());

        // ensure the null item is marked as selected
        assertEquals("wrong selection", "Null item",
                popup.findElement(By.className("v-filterselect-suggestmenu"))
                        .findElement(By.className("gwt-MenuItem-selected"))
                        .getText());

        // select the second item
        filteredItems.get(1).click();

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));

        // check the updated selection
        assertEquals("correct selection not found", "Value 1", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // open the popup again
        comboBox.openPopup();
        waitForElementPresent(By.className("v-filterselect-suggestmenu"));
        popup = driver.findElement(By.className("v-filterselect-suggestpopup"));

        // ensure the full selection remains
        filteredItems = popup.findElement(
                By.className("v-filterselect-suggestmenu")).findElements(
                By.className("gwt-MenuItem"));
        assertEquals("unexpected amount of suggestions found", 4,
                filteredItems.size());

        // ensure the selected item is marked as selected
        assertEquals("wrong selection", "Value 1",
                popup.findElement(By.className("v-filterselect-suggestmenu"))
                        .findElement(By.className("gwt-MenuItem-selected"))
                        .getText());

        // select the null selection item
        filteredItems.get(0).click();

        // ensure there's no more popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));
    }

    @Test
    public void testFifthComboBoxInputs() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).all().get(4);

        // check the initial selection
        assertEquals("correct selection not found", "Null item", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // check the disabled status
        assertEquals("correct selection not found", "true", comboBox
                .findElement(By.tagName("input")).getAttribute("disabled"));

        comboBox.openPopup();

        // ensure there's no popup
        assertFalse("popup found when there should be none",
                comboBox.isElementPresent(By.vaadin("#popup")));
    }

    @Test
    public void testSixthComboBoxInputs() {
        ComboBoxElement comboBox = $(ComboBoxElement.class).all().get(5);

        // check the initial selection
        assertEquals("correct selection not found", "Null item", comboBox
                .findElement(By.tagName("input")).getAttribute("value"));

        // check the readonly status
        assertEquals("correct selection not found", "true", comboBox
                .findElement(By.tagName("input")).getAttribute("readonly"));

        assertFalse("button visible on readonly combobox", comboBox
                .findElement(By.vaadin("#button")).isDisplayed());
    }
}
