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
import static org.junit.Assert.assertTrue;

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

public class GridEditorTest extends GridBasicFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "Editor", "Enabled");
    }

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath("Component", "Editor", "Edit item 5");
        assertEditorOpen();

        selectMenuPath("Component", "Editor", "Cancel edit");
        assertEditorClosed();
    }

    @Test
    public void testProgrammaticOpeningWhenDisabled() {
        selectMenuPath("Component", "Editor", "Enabled");
        selectMenuPath("Component", "Editor", "Edit item 5");
        assertEditorClosed();
        boolean thrown = getLogRow(0).startsWith(
                "5. Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testDisablingWhileOpen() {
        selectMenuPath("Component", "Editor", "Edit item 5");
        selectMenuPath("Component", "Editor", "Enabled");
        assertEditorOpen();
        boolean thrown = getLogRow(0).startsWith(
                "5. Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath("Component", "Editor", "Edit item 100");
        assertEditorOpen();
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath("Component", "Editor", "Edit item 5");
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testKeyboardOpeningClosing() {

        getGridElement().getCell(4, 0).click();
        assertEditorClosed();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertEditorOpen();

        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        assertEditorClosed();

        // Disable Editor
        selectMenuPath("Component", "Editor", "Enabled");
        getGridElement().getCell(5, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertEditorClosed();
    }

    @Test
    public void testComponentBinding() {
        selectMenuPath("Component", "State", "Editor", "Edit item 100");

        List<WebElement> widgets = getEditorWidgets();
        assertEquals("Number of widgets", GridBasicFeatures.COLUMNS,
                widgets.size());

        assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));
        assertEquals("<b>100</b>", widgets.get(9).getAttribute("value"));
    }

    @Test
    public void testSave() {
        selectMenuPath("Component", "Editor", "Edit item 100");

        WebElement textField = getEditorWidgets().get(0);

        textField.click();

        textField.sendKeys(" changed");

        WebElement saveButton = getEditor().findElement(
                By.className("v-editor-row-save"));

        saveButton.click();

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }

    @Test
    public void testProgrammaticSave() {
        selectMenuPath("Component", "Editor", "Edit item 100");

        WebElement textField = getEditorWidgets().get(0);

        textField.click();

        textField.sendKeys(" changed");

        selectMenuPath("Component", "Editor", "Save");

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }

    private void assertEditorOpen() {
        assertNotNull("Editor open", getEditor());
        assertEquals("Number of widgets", GridBasicFeatures.COLUMNS,
                getEditorWidgets().size());
    }

    private void assertEditorClosed() {
        assertNull("Editor closed", getEditor());
    }

    private List<WebElement> getEditorWidgets() {
        assertNotNull(getEditor());
        return getEditor().findElements(By.className("v-textfield"));

    }
}
