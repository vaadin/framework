/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.grid.basics;

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
import com.vaadin.testbench.TestBenchElement;
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
    public void testKeyboardSave() {
        editRow(100);

        WebElement textField = getEditor().getField(0);

        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new Actions(getDriver()).sendKeys(Keys.END).perform();

        textField.sendKeys(" changed");

        // Save from keyboard
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertEditorClosed();
        assertEquals("(100, 0) changed",
                getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testKeyboardSaveWithHiddenColumn() {
        selectMenuPath("Component", "Columns", "Column 0", "Hidden");
        editRow(100);

        WebElement textField = getEditor().getField(5);

        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new Actions(getDriver()).sendKeys(Keys.END).perform();

        textField.sendKeys(" changed");

        // Save from keyboard
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertEditorClosed();
        assertEquals("100 changed",
                getGridElement().getCell(100, 4).getText());
    }

    @Test
    public void testKeyboardSaveWithInvalidEdition() {
        makeInvalidEdition();

        GridEditorElement editor = getGridElement().getEditor();
        TestBenchElement field = editor.getField(7);

        field.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertEditorOpen();
        assertEquals(
                GridBasics.COLUMN_CAPTIONS[7]
                        + ": Could not convert value to Integer",
                editor.getErrorMessage());
        assertTrue("Field 7 should have been marked with an error after error",
                isEditorCellErrorMarked(7));

        editor.cancel();

        editRow(100);
        assertFalse("Exception should not exist",
                isElementPresent(NotificationElement.class));
        assertEquals("There should be no editor error message", null,
                getGridElement().getEditor().getErrorMessage());
    }

    @Test
    public void testSave() {
        editRow(100);

        WebElement textField = getEditor().getField(0);

        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new Actions(getDriver()).sendKeys(Keys.END).perform();

        textField.sendKeys(" changed");

        WebElement saveButton = getEditor()
                .findElement(By.className("v-grid-editor-save"));

        saveButton.click();

        assertEquals("(100, 0) changed",
                getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testProgrammaticSave() {
        editRow(100);

        WebElement textField = getEditor().getField(0);

        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new Actions(getDriver()).sendKeys(Keys.END).perform();

        textField.sendKeys(" changed");

        selectMenuPath("Component", "Editor", "Save");

        assertEquals("(100, 0) changed",
                getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testInvalidEdition() {
        makeInvalidEdition();

        GridEditorElement editor = getGridElement().getEditor();
        editor.save();

        assertEquals(
                GridBasics.COLUMN_CAPTIONS[7]
                        + ": Could not convert value to Integer",
                editor.getErrorMessage());
        assertTrue("Field 7 should have been marked with an error after error",
                isEditorCellErrorMarked(7));
        editor.cancel();

        editRow(100);
        assertFalse("Exception should not exist",
                isElementPresent(NotificationElement.class));
        assertEquals("There should be no editor error message", null,
                getGridElement().getEditor().getErrorMessage());
    }

    private void makeInvalidEdition() {
        editRow(5);
        assertFalse(logContainsText(
                "Exception occured, java.lang.IllegalStateException"));

        GridEditorElement editor = getGridElement().getEditor();

        assertFalse(
                "Field 7 should not have been marked with an error before error",
                editor.isFieldErrorMarked(7));

        WebElement intField = editor.getField(7);
        intField.clear();
        intField.sendKeys("banana phone");
        editor.getField(5).click();
    }

    @Test
    public void testEditorInDisabledGrid() {
        int originalScrollPos = getGridVerticalScrollPos();

        editRow(5);
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
        editRow(5);
        assertEquals("Save button caption should've been \""
                + GridConstants.DEFAULT_SAVE_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        assertEquals("Cancel button caption should've been \""
                + GridConstants.DEFAULT_CANCEL_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_CANCEL_CAPTION,
                getCancelButton().getText());

        selectMenuPath("Component", "Editor", "Change save caption");
        assertNotEquals(
                "Save button caption should've changed while editor is open",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());

        getCancelButton().click();

        selectMenuPath("Component", "Editor", "Change cancel caption");
        editRow(5);
        assertNotEquals(
                "Cancel button caption should've changed while editor is closed",
                GridConstants.DEFAULT_CANCEL_CAPTION,
                getCancelButton().getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        editRow(5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testScrollDisabledOnMouseOpen() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new Actions(getDriver()).doubleClick(cell_5_0).perform();

        scrollGridVerticallyTo(100);
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testScrollDisabledOnKeyboardOpen() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        cell_5_0.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        scrollGridVerticallyTo(100);
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
    }

    @Test
    public void testMouseOpeningClosing() {

        getGridElement().getCell(4, 0).doubleClick();
        assertEditorOpen();

        getCancelButton().click();
        assertEditorClosed();

        selectMenuPath(TOGGLE_EDIT_ENABLED);
        getGridElement().getCell(4, 0).doubleClick();
        assertEditorClosed();
    }

    @Test
    public void testMouseOpeningDisabledWhenOpen() {
        editRow(5);

        getGridElement().getCell(2, 0).doubleClick();

        assertEquals("Editor should still edit row 5", "(5, 0)",
                getEditor().getField(0).getAttribute("value"));
    }

    @Test
    public void testUserSortDisabledWhenOpen() {
        editRow(5);

        getGridElement().getHeaderCell(0, 0).click();

        assertEditorOpen();
        assertEquals("(2, 0)", getGridElement().getCell(2, 0).getText());
    }
}
