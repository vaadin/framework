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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Verifies SlowComboBox filtering works when user inputs text. Also verifies
 * pagination works when the matching results number more than those that can be
 * displayed.
 * 
 * @author Vaadin Ltd
 */
public class ComboBoxSlowTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testZeroMatches() throws InterruptedException {
        clickComboBoxTextArea();
        sleep(250);
        typeString("1");
        assertEquals(0, getNumberOfSuggestions());
    }

    @Test
    public void testElevenMatchesAndPaging() throws InterruptedException {
        clickComboBoxTextArea();
        sleep(250);
        typeString("Item 12");

        final WebElement popup = driver.findElement(By
                .className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = getFilteredItems(popup);
        assertEquals("unexpected amount of suggestions found on first page",
                10, filteredItems.size());
        assertEquals("wrong filtering result", "Item 12", filteredItems.get(0)
                .getText());
        assertEquals("wrong filtering result", "Item 128", filteredItems.get(9)
                .getText());

        assertTrue(isPagingActive());
        goToNextPage();

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                List<WebElement> filteredItems = getFilteredItems(popup);
                return filteredItems.size() == 1
                        && "Item 129".equals(filteredItems.get(0).getText());
            }
        });
    }

    @Test
    public void testTwoMatchesNoPaging() {
        clickComboBoxTextArea();
        typeString("Item 100");
        assertFalse(isPagingActive());

        WebElement popup = driver.findElement(By
                .className("v-filterselect-suggestpopup"));
        List<WebElement> filteredItems = getFilteredItems(popup);
        assertEquals("unexpected amount of suggestions found", 2,
                filteredItems.size());
        assertEquals("wrong filtering result", "Item 100", filteredItems.get(0)
                .getText());
        assertEquals("wrong filtering result", "Item 1000", filteredItems
                .get(1).getText());
    }

    private void clickComboBoxTextArea() {
        WebElement cb = getDriver().findElement(
                By.className("v-filterselect-input"));
        cb.click();
    }

    private void typeString(String s) {
        Actions action = new Actions(getDriver());
        action.sendKeys(s);
        action.build().perform();
    }

    private int getNumberOfSuggestions() {

        List<WebElement> elements = getDriver().findElements(
                By.className("gwt-MenuItem"));
        return elements.size();
    }

    private boolean isPagingActive() {
        List<WebElement> elements = getDriver().findElements(
                By.className("v-filterselect-nextpage"));
        return elements.size() == 1;
    }

    private void goToNextPage() {
        WebElement nextPage = getDriver().findElement(
                By.className("v-filterselect-nextpage"));
        nextPage.click();
    }

    /*
     * Gets the list of filtered items from the combobox popup.
     */
    private List<WebElement> getFilteredItems(final WebElement popup) {
        return popup.findElement(By.className("v-filterselect-suggestmenu"))
                .findElements(By.className("gwt-MenuItem"));
    }
}
