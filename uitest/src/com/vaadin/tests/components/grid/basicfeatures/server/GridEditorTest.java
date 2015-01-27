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

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridEditorTest extends GridBasicFeaturesTest {

    private static final String[] EDIT_ITEM_5 = new String[] { "Component",
            "Editor", "Edit item 5" };
    private static final String[] EDIT_ITEM_100 = new String[] { "Component",
            "Editor", "Edit item 100" };
    private static final String[] TOGGLE_EDIT_ENABLED = new String[] {
            "Component", "Editor", "Enabled" };

    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();
        selectMenuPath(TOGGLE_EDIT_ENABLED);
    }

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();

        selectMenuPath("Component", "Editor", "Cancel edit");
        assertEditorClosed();
    }

    @Test
    public void testProgrammaticOpeningWhenDisabled() {
        selectMenuPath(TOGGLE_EDIT_ENABLED);
        selectMenuPath(EDIT_ITEM_5);
        assertEditorClosed();
        boolean thrown = logContainsText("Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testDisablingWhileOpen() {
        selectMenuPath(EDIT_ITEM_5);
        selectMenuPath(TOGGLE_EDIT_ENABLED);
        assertEditorOpen();
        boolean thrown = logContainsText("Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath(EDIT_ITEM_100);
        assertEditorOpen();
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath(EDIT_ITEM_5);
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
        selectMenuPath(TOGGLE_EDIT_ENABLED);
        getGridElement().getCell(5, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertEditorClosed();
    }

    @Test
    public void testComponentBinding() {
        selectMenuPath(EDIT_ITEM_100);

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
        selectMenuPath(EDIT_ITEM_100);

        WebElement textField = getEditorWidgets().get(0);

        textField.click();

        textField.sendKeys(" changed");

        WebElement saveButton = getEditor().findElement(
                By.className("v-grid-editor-save"));

        saveButton.click();

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }

    @Test
    public void testProgrammaticSave() {
        selectMenuPath(EDIT_ITEM_100);

        WebElement textField = getEditorWidgets().get(0);

        textField.click();

        textField.sendKeys(" changed");

        selectMenuPath("Component", "Editor", "Save");

        assertEquals("(100, 0) changed", getGridElement().getCell(100, 0)
                .getText());
    }

    private void assertEditorOpen() {
        assertNotNull("Editor is supposed to be open", getEditor());
        assertEquals("Unexpected number of widgets", GridBasicFeatures.COLUMNS,
                getEditorWidgets().size());
    }

    private void assertEditorClosed() {
        assertNull("Editor is supposed to be closed", getEditor());
    }

    private List<WebElement> getEditorWidgets() {
        assertNotNull(getEditor());
        return getEditor().findElements(By.className("v-textfield"));

    }

    @Test
    public void testInvalidEdition() {
        selectMenuPath(EDIT_ITEM_5);
        assertFalse(logContainsText("Exception occured, java.lang.IllegalStateException"));

        GridEditorElement editor = getGridElement().getEditor();

        WebElement intField = editor.getField(7);
        intField.clear();
        intField.sendKeys("banana phone");
        editor.save();
        WebElement n = $(NotificationElement.class).first();
        assertEquals("Column 7: Could not convert value to Integer",
                n.getText());
        n.click();
        editor.cancel();

        selectMenuPath(EDIT_ITEM_100);
        assertFalse("Exception should not exist",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void testNoScrollAfterEditByAPI() {
        int originalScrollPos = getGridVerticalScrollPos();

        selectMenuPath(EDIT_ITEM_5);

        scrollGridVerticallyTo(100);
        assertEquals("Grid shouldn't scroll vertically while editing",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testNoScrollAfterEditByMouse() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new Actions(getDriver()).doubleClick(cell_5_0).perform();

        scrollGridVerticallyTo(100);
        assertEquals("Grid shouldn't scroll vertically while editing",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testNoScrollAfterEditByKeyboard() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        cell_5_0.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        scrollGridVerticallyTo(100);
        assertEquals("Grid shouldn't scroll vertically while editing",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testEditorInDisabledGrid() {
        int originalScrollPos = getGridVerticalScrollPos();

        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();

        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();

        GridEditorElement editor = getGridElement().getEditor();
        editor.save();
        assertEditorOpen();

        editor.cancel();
        assertEditorOpen();

        selectMenuPath("Component", "State", "Enabled");

        scrollGridVerticallyTo(100);
        assertEquals("Grid shouldn't scroll vertically while editing",
                originalScrollPos, getGridVerticalScrollPos());
    }
}
