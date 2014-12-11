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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

public class GridEditorRowClientTest extends GridBasicClientFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "Editor row", "Enabled");
    }

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath("Component", "Editor row", "Edit row 5");
        assertNotNull(getEditorRow());

        selectMenuPath("Component", "Editor row", "Cancel edit");
        assertNull(getEditorRow());
        assertEquals("Row 5 edit cancelled",
                findElement(By.className("editor-row-log")).getText());
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath("Component", "Editor row", "Edit row 100");
        assertNotNull(getEditorRow());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath("Component", "Editor row", "Edit row 5");
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testKeyboardOpeningClosing() {

        getGridElement().getCell(4, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertNotNull(getEditorRow());

        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        assertNull(getEditorRow());
        assertEquals("Row 4 edit cancelled",
                findElement(By.className("editor-row-log")).getText());

        // Disable editor row
        selectMenuPath("Component", "Editor row", "Enabled");

        getGridElement().getCell(5, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertNull(getEditorRow());
    }

    @Test
    public void testWidgetBinding() throws Exception {
        selectMenuPath("Component", "Editor row", "Edit row 100");
        WebElement editorRow = getEditorRow();

        List<WebElement> widgets = editorRow.findElements(By
                .className("gwt-TextBox"));

        assertEquals(GridBasicFeatures.COLUMNS, widgets.size());

        assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));

        assertEquals("100", widgets.get(7).getAttribute("value"));
        assertEquals("<b>100</b>", widgets.get(9).getAttribute("value"));
    }

    @Test
    public void testWithSelectionColumn() throws Exception {
        selectMenuPath("Component", "State", "Selection mode", "multi");
        selectMenuPath("Component", "State", "Editor row", "Edit row 5");

        WebElement editorRow = getEditorRow();
        List<WebElement> selectorDivs = editorRow.findElements(By
                .cssSelector("div"));

        assertTrue("selector column cell should've been empty", selectorDivs
                .get(0).getAttribute("innerHTML").isEmpty());
        assertFalse("normal column cell shoul've had contents", selectorDivs
                .get(1).getAttribute("innerHTML").isEmpty());
    }

    @Test
    public void testSave() {
        selectMenuPath("Component", "Editor row", "Edit row 100");

        WebElement textField = getEditorRow().findElements(
                By.className("gwt-TextBox")).get(0);

        textField.sendKeys(" changed");

        WebElement saveButton = getEditorRow().findElement(
                By.className("v-editor-row-save"));

        saveButton.click();

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }

    @Test
    public void testProgrammaticSave() {
        selectMenuPath("Component", "Editor row", "Edit row 100");

        WebElement textField = getEditorRow().findElements(
                By.className("gwt-TextBox")).get(0);

        textField.sendKeys(" changed");

        selectMenuPath("Component", "Editor row", "Save");

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }
}
