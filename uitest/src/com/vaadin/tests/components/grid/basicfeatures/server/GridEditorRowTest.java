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
package com.vaadin.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridEditorRowTest extends GridBasicFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "Editor row", "Enabled");
    }

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath("Component", "Editor row", "Edit item 5");
        assertNotNull(getEditorRow());

        selectMenuPath("Component", "Editor row", "Cancel edit");
        assertNull(getEditorRow());
    }

    @Test
    public void testProgrammaticOpeningWhenDisabled() {
        selectMenuPath("Component", "Editor row", "Enabled");
        selectMenuPath("Component", "Editor row", "Edit item 5");
        assertNull(getEditorRow());
        assertEquals(
                "4. Exception occured, java.lang.IllegalStateExceptionThis EditorRow is not enabled",
                getLogRow(0));
    }

    @Test
    public void testDisablingWhileOpen() {
        selectMenuPath("Component", "Editor row", "Edit item 5");
        selectMenuPath("Component", "Editor row", "Enabled");
        assertNotNull(getEditorRow());
        assertEquals(
                "4. Exception occured, java.lang.IllegalStateExceptionCannot disable the editor row while an item (5) is being edited.",
                getLogRow(0));

    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath("Component", "Editor row", "Edit item 100");
        assertNotNull(getEditorRow());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath("Component", "Editor row", "Edit item 5");
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testKeyboardOpeningClosing() {

        getGridElement().getCell(4, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertNotNull(getEditorRow());

        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        assertNull(getEditorRow());

        // Disable editor row
        selectMenuPath("Component", "Editor row", "Enabled");

        getGridElement().getCell(5, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertNull(getEditorRow());
    }

    @Test
    public void testComponentBinding() {
        selectMenuPath("Component", "State", "Editor row", "Edit item 100");

        List<WebElement> widgets = getEditorRow().findElements(
                By.className("v-widget"));

        assertEquals(GridBasicFeatures.COLUMNS, widgets.size());

        assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));
        assertEquals("<b>100</b>", widgets.get(9).getAttribute("value"));
    }

    @Test
    public void testCommit() {
        selectMenuPath("Component", "Editor row", "Edit item 100");

        List<WebElement> widgets = getEditorRow().findElements(
                By.className("v-textfield"));

        widgets.get(0).click();

        widgets.get(0).sendKeys(" changed");

        WebElement saveButton = getEditorRow().findElement(
                By.className("v-editor-row-save"));

        saveButton.click();

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }

    @Test
    public void testDiscard() {
        selectMenuPath("Component", "Editor row", "Edit item 100");

        List<WebElement> widgets = getEditorRow().findElements(
                By.className("v-textfield"));

        widgets.get(0).sendKeys(" changed");

        selectMenuPath("Component", "Editor row", "Discard");

        assertEquals("(100, 0)", getGridElement().getCell(100, 0).getText());
    }
}
