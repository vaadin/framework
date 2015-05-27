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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.elements.NotificationElement;

public class GridEditorBufferedTest extends GridEditorTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
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

    @Test
    public void testInvalidEdition() {
        selectMenuPath(EDIT_ITEM_5);
        assertFalse(logContainsText("Exception occured, java.lang.IllegalStateException"));

        GridEditorElement editor = getGridElement().getEditor();

        assertFalse(
                "Field 7 should not have been marked with an error before error",
                editor.isFieldErrorMarked(7));

        WebElement intField = editor.getField(7);
        intField.clear();
        intField.sendKeys("banana phone");
        editor.save();

        assertEquals("Column 7: Could not convert value to Integer",
                editor.getErrorMessage());
        assertTrue("Field 7 should have been marked with an error after error",
                editor.isFieldErrorMarked(7));
        editor.cancel();

        selectMenuPath(EDIT_ITEM_100);
        assertFalse("Exception should not exist",
                isElementPresent(NotificationElement.class));
        assertEquals("There should be no editor error message", null,
                editor.getErrorMessage());
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
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testCaptionChange() {
        selectMenuPath(EDIT_ITEM_5);
        assertEquals("Save button caption should've been \""
                + GridConstants.DEFAULT_SAVE_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        assertEquals("Cancel button caption should've been \""
                + GridConstants.DEFAULT_CANCEL_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_CANCEL_CAPTION, getCancelButton()
                        .getText());

        selectMenuPath("Component", "Editor", "Change save caption");
        assertNotEquals(
                "Save button caption should've changed while editor is open",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());

        getCancelButton().click();

        selectMenuPath("Component", "Editor", "Change cancel caption");
        selectMenuPath(EDIT_ITEM_5);
        assertNotEquals(
                "Cancel button caption should've changed while editor is closed",
                GridConstants.DEFAULT_CANCEL_CAPTION, getCancelButton()
                        .getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath(EDIT_ITEM_5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testNoScrollAfterEditByAPI() {
        int originalScrollPos = getGridVerticalScrollPos();

        selectMenuPath(EDIT_ITEM_5);

        scrollGridVerticallyTo(100);
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testNoScrollAfterEditByMouse() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new Actions(getDriver()).doubleClick(cell_5_0).perform();

        scrollGridVerticallyTo(100);
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testNoScrollAfterEditByKeyboard() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        cell_5_0.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        scrollGridVerticallyTo(100);
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
    }
}
