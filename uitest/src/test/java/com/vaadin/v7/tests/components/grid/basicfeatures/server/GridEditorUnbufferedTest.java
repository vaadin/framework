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
package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;

public class GridEditorUnbufferedTest extends GridEditorTest {

    private static final String[] TOGGLE_EDITOR_BUFFERED = new String[] {
            "Component", "Editor", "Buffered mode" };
    private static final String[] CANCEL_EDIT = new String[] { "Component",
            "Editor", "Cancel edit" };

    @Override
    @Before
    public void setUp() {
        super.setUp();
        selectMenuPath(TOGGLE_EDITOR_BUFFERED);
    }

    @Test
    public void testEditorShowsNoButtons() {
        selectMenuPath(EDIT_ITEM_5);

        assertEditorOpen();

        assertFalse("Save button should not be visible in unbuffered mode.",
                isElementPresent(BY_EDITOR_SAVE));

        assertFalse("Cancel button should not be visible in unbuffered mode.",
                isElementPresent(BY_EDITOR_CANCEL));
    }

    @Test
    public void testToggleEditorUnbufferedWhileOpen() {
        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();
        selectMenuPath(TOGGLE_EDITOR_BUFFERED);
        boolean thrown = logContainsText(
                "Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testEditorMoveWithMouse() {
        selectMenuPath(EDIT_ITEM_5);

        assertEditorOpen();

        String firstFieldValue = getEditorWidgets().get(0)
                .getAttribute("value");
        assertEquals("Editor should be at row 5", "(5, 0)", firstFieldValue);

        getGridElement().getCell(10, 0).click();
        firstFieldValue = getEditorWidgets().get(0).getAttribute("value");

        assertEquals("Editor should be at row 10", "(10, 0)", firstFieldValue);
    }

    @Test
    public void testEditorMoveWithKeyboard() throws InterruptedException {
        selectMenuPath(EDIT_ITEM_100);

        assertEditorOpen();

        getEditorWidgets().get(0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        String firstFieldValue = getEditorWidgets().get(0)
                .getAttribute("value");
        assertEquals("Editor should move to row 101", "(101, 0)",
                firstFieldValue);

        for (int i = 0; i < 10; i++) {
            new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.ENTER)
                    .keyUp(Keys.SHIFT).perform();

            firstFieldValue = getEditorWidgets().get(0).getAttribute("value");
            int row = 100 - i;
            assertEquals("Editor should move to row " + row, "(" + row + ", 0)",
                    firstFieldValue);
        }
    }

    @Test
    public void testValidationErrorPreventsMove() throws InterruptedException {
        // Because of "out of view" issues, we need to move this for easy access
        selectMenuPath("Component", "Columns", "Column 7", "Column 7 Width",
                "50px");
        for (int i = 0; i < 6; ++i) {
            selectMenuPath("Component", "Columns", "Column 7", "Move left");
        }

        selectMenuPath(EDIT_ITEM_5);

        getEditorWidgets().get(1).click();
        String faultyInt = "not a number";
        getEditorWidgets().get(1).sendKeys(faultyInt);

        getGridElement().getCell(10, 0).click();

        assertEquals("Editor should not move from row 5", "(5, 0)",
                getEditorWidgets().get(0).getAttribute("value"));

        getEditorWidgets().get(1).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        getEditorWidgets().get(1).sendKeys("5");
        // FIXME: Needs to trigger one extra validation round-trip for now
        getGridElement().sendKeys(Keys.ENTER);

        getGridElement().getCell(10, 0).click();

        assertEquals("Editor should move to row 10", "(10, 0)",
                getEditorWidgets().get(0).getAttribute("value"));

    }

    @Test
    public void testErrorMessageWrapperHidden() {
        selectMenuPath(EDIT_ITEM_5);

        assertEditorOpen();

        WebElement editorFooter = getEditor()
                .findElement(By.className("v-grid-editor-footer"));

        assertTrue("Editor footer should not be visible when there's no error",
                editorFooter.getCssValue("display").equalsIgnoreCase("none"));
    }

    @Test
    public void testScrollEnabledOnProgrammaticOpen() {
        int originalScrollPos = getGridVerticalScrollPos();

        selectMenuPath(EDIT_ITEM_5);

        scrollGridVerticallyTo(100);
        assertGreater(
                "Grid should scroll vertically while editing in unbuffered mode",
                getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testScrollEnabledOnMouseOpen() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new Actions(getDriver()).doubleClick(cell_5_0).perform();

        scrollGridVerticallyTo(100);
        assertGreater(
                "Grid should scroll vertically while editing in unbuffered mode",
                getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testScrollEnabledOnKeyboardOpen() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        cell_5_0.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        scrollGridVerticallyTo(100);
        assertGreater(
                "Grid should scroll vertically while editing in unbuffered mode",
                getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testEditorInDisabledGrid() {
        selectMenuPath(EDIT_ITEM_5);

        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();

        assertTrue("Editor text field should be disabled",
                null != getEditorWidgets().get(2).getAttribute("disabled"));

        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();

        assertFalse("Editor text field should not be disabled",
                null != getEditorWidgets().get(2).getAttribute("disabled"));
    }

    @Test
    public void testMouseOpeningClosing() {

        getGridElement().getCell(4, 0).doubleClick();
        assertEditorOpen();

        selectMenuPath(CANCEL_EDIT);
        selectMenuPath(TOGGLE_EDIT_ENABLED);

        getGridElement().getCell(4, 0).doubleClick();
        assertEditorClosed();
    }

    @Test
    public void testProgrammaticOpeningWhenOpen() {
        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();
        assertEquals("Editor should edit row 5", "(5, 0)",
                getEditorWidgets().get(0).getAttribute("value"));

        selectMenuPath(EDIT_ITEM_100);
        assertEditorOpen();
        assertEquals("Editor should edit row 100", "(100, 0)",
                getEditorWidgets().get(0).getAttribute("value"));
    }

    @Test
    public void testExternalValueChangePassesToEditor() {
        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();

        selectMenuPath("Component", "State", "ReactiveValueChanger");

        getEditorWidgets().get(0).click();
        getEditorWidgets().get(0).sendKeys("changing value");

        // Focus another field to cause the value to be sent to the server
        getEditorWidgets().get(2).click();

        assertEquals("Value of Column 2 in the editor was not changed",
                "Modified", getEditorWidgets().get(2).getAttribute("value"));
    }

    @Test
    public void testEditorClosedOnUserSort() {
        selectMenuPath(EDIT_ITEM_5);

        getGridElement().getHeaderCell(0, 0).click();

        assertEditorClosed();
    }

    @Test
    public void testEditorSaveOnRowChange() {
        // Double click sets the focus programmatically
        getGridElement().getCell(5, 2).doubleClick();

        TestBenchElement editor = getGridElement().getEditor().getField(2);
        editor.clear();
        // Click to ensure IE focus...
        editor.click(5, 5);
        editor.sendKeys("Foo", Keys.ENTER);

        assertEquals("Editor did not move.", "(6, 0)",
                getGridElement().getEditor().getField(0).getAttribute("value"));
        assertEquals("Editor field value did not update from server.", "(6, 2)",
                getGridElement().getEditor().getField(2).getAttribute("value"));

        assertEquals("Edited value was not saved.", "Foo",
                getGridElement().getCell(5, 2).getText());
    }
}
