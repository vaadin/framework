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
        selectMenuPath(EDIT_ITEM_100);
        WebElement textField = getEditorWidgets().get(0);
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
        selectMenuPath(EDIT_ITEM_100);

        WebElement textField = getEditorWidgets().get(1);

        textField.click();
        // without this, the click in the middle of the field might not be after
        // the old text on some browsers
        new Actions(getDriver()).sendKeys(Keys.END).perform();

        textField.sendKeys(" changed");

        // Save from keyboard
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertEditorClosed();
        assertEquals("(100, 2) changed",
                getGridElement().getCell(100, 1).getText());
    }

    @Test
    public void testKeyboardSaveWithInvalidEdition() {
        makeInvalidEdition();

        GridEditorElement editor = getGridElement().getEditor();
        TestBenchElement field = editor.getField(7);

        field.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertEditorOpen();
        assertEquals("Column 7: Could not convert value to Integer",
                editor.getErrorMessage());
        assertTrue("Field 7 should have been marked with an error after error",
                editor.isFieldErrorMarked(7));

        editor.cancel();

        selectMenuPath(EDIT_ITEM_100);
        assertFalse("Exception should not exist",
                isElementPresent(NotificationElement.class));
        assertEquals("There should be no editor error message", null,
                getGridElement().getEditor().getErrorMessage());
    }

    @Test
    public void testSave() {
        selectMenuPath(EDIT_ITEM_100);

        WebElement textField = getEditorWidgets().get(0);

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
        selectMenuPath(EDIT_ITEM_100);

        WebElement textField = getEditorWidgets().get(0);

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

        assertEquals("Column 7: Could not convert value to Integer",
                editor.getErrorMessage());
        assertTrue("Field 7 should have been marked with an error after error",
                editor.isFieldErrorMarked(7));
        editor.cancel();

        selectMenuPath(EDIT_ITEM_100);
        assertFalse("Exception should not exist",
                isElementPresent(NotificationElement.class));
        assertEquals("There should be no editor error message", null,
                getGridElement().getEditor().getErrorMessage());
    }

    private void makeInvalidEdition() {
        selectMenuPath(EDIT_ITEM_5);
        assertFalse(logContainsText(
                "Exception occured, java.lang.IllegalStateException"));

        GridEditorElement editor = getGridElement().getEditor();

        assertFalse(
                "Field 7 should not have been marked with an error before error",
                editor.isFieldErrorMarked(7));

        WebElement intField = editor.getField(7);
        intField.clear();
        intField.sendKeys("banana phone");
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
                GridConstants.DEFAULT_CANCEL_CAPTION,
                getCancelButton().getText());

        selectMenuPath("Component", "Editor", "Change save caption");
        assertNotEquals(
                "Save button caption should've changed while editor is open",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());

        getCancelButton().click();

        selectMenuPath("Component", "Editor", "Change cancel caption");
        selectMenuPath(EDIT_ITEM_5);
        assertNotEquals(
                "Cancel button caption should've changed while editor is closed",
                GridConstants.DEFAULT_CANCEL_CAPTION,
                getCancelButton().getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath(EDIT_ITEM_5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testScrollDisabledOnProgrammaticOpen() {
        int originalScrollPos = getGridVerticalScrollPos();

        selectMenuPath(EDIT_ITEM_5);

        scrollGridVerticallyTo(100);
        assertEquals(
                "Grid shouldn't scroll vertically while editing in buffered mode",
                originalScrollPos, getGridVerticalScrollPos());
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
        selectMenuPath(EDIT_ITEM_5);

        getGridElement().getCell(4, 0).doubleClick();

        assertEquals("Editor should still edit row 5", "(5, 0)",
                getEditorWidgets().get(0).getAttribute("value"));
    }

    @Test
    public void testProgrammaticOpeningDisabledWhenOpen() {
        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();
        assertEquals("Editor should edit row 5", "(5, 0)",
                getEditorWidgets().get(0).getAttribute("value"));

        selectMenuPath(EDIT_ITEM_100);
        boolean thrown = logContainsText(
                "Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);

        assertEditorOpen();
        assertEquals("Editor should still edit row 5", "(5, 0)",
                getEditorWidgets().get(0).getAttribute("value"));
    }

    @Test
    public void testUserSortDisabledWhenOpen() {
        selectMenuPath(EDIT_ITEM_5);

        getGridElement().getHeaderCell(0, 0).click();

        assertEditorOpen();
        assertEquals("(2, 0)", getGridElement().getCell(2, 0).getText());
    }
}
