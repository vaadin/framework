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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
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
        editRow(5);

        assertEditorOpen();

        assertFalse("Save button should not be visible in unbuffered mode.",
                isElementPresent(BY_EDITOR_SAVE));

        assertFalse("Cancel button should not be visible in unbuffered mode.",
                isElementPresent(BY_EDITOR_CANCEL));
    }

    @Test
    public void testToggleEditorUnbufferedWhileOpen() {
        editRow(5);
        assertEditorOpen();
        selectMenuPath(TOGGLE_EDITOR_BUFFERED);
        boolean thrown = logContainsText(
                "Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException was not thrown", thrown);
    }

    @Test
    public void testEditorMoveWithMouse() {
        editRow(5);

        assertEditorOpen();

        String firstFieldValue = getEditor().getField(0).getAttribute("value");
        assertEquals("Editor should be at row 5", "(5, 0)", firstFieldValue);

        getGridElement().getCell(6, 0).click();
        firstFieldValue = getEditor().getField(0).getAttribute("value");

        assertEquals("Editor should be at row 6", "(6, 0)", firstFieldValue);
    }

    @Test
    public void testEditorMoveWithKeyboard() throws InterruptedException {
        editRow(100);

        getEditor().getField(0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        String firstFieldValue = getEditor().getField(0).getAttribute("value");
        assertEquals("Editor should move to row 101", "(101, 0)",
                firstFieldValue);

        for (int i = 0; i < 10; i++) {
            new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.ENTER)
                    .keyUp(Keys.SHIFT).perform();

            firstFieldValue = getEditor().getField(0).getAttribute("value");
            int row = 100 - i;
            assertEquals("Editor should move to row " + row, "(" + row + ", 0)",
                    firstFieldValue);
        }
    }

    @Test
    public void testValidationErrorPreventsMove() throws InterruptedException {
        editRow(5);

        getEditor().getField(7).click();
        String faultyInt = "not a number";
        getEditor().getField(7).sendKeys(faultyInt);

        getGridElement().getCell(6, 7).click();

        assertEquals("Editor should not move from row 5", "(5, 0)",
                getEditor().getField(0).getAttribute("value"));

        getEditor().getField(7).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        getEditor().getField(7).sendKeys("4");

        getGridElement().getCell(7, 0).click();

        assertEquals("Editor should move to row 7", "(7, 0)",
                getEditor().getField(0).getAttribute("value"));

    }

    @Test
    public void testErrorMessageWrapperHidden() {
        editRow(5);

        assertEditorOpen();

        WebElement editorFooter = getEditor()
                .findElement(By.className("v-grid-editor-footer"));

        assertTrue("Editor footer should not be visible when there's no error",
                editorFooter.getCssValue("display").equalsIgnoreCase("none"));
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
        editRow(5);

        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();

        assertTrue("Editor text field should be disabled",
                null != getEditor().getField(0).getAttribute("disabled"));

        selectMenuPath("Component", "State", "Enabled");
        assertEditorOpen();

        assertFalse("Editor text field should not be disabled",
                null != getEditor().getField(0).getAttribute("disabled"));
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

    @Ignore("Needs refresh item functionality")
    @Test
    public void testExternalValueChangePassesToEditor() {
        editRow(5);
        assertEditorOpen();

        selectMenuPath("Component", "State", "ReactiveValueChanger");

        getEditor().getField(0).click();
        getEditor().getField(0).sendKeys("changing value");

        // Focus another field to cause the value to be sent to the server
        getEditor().getField(3).click();

        assertEquals("Value of Column 2 in the editor was not changed",
                "Modified", getEditor().getField(5).getAttribute("value"));
    }

    @Test
    public void testEditorClosedOnUserSort() {
        editRow(5);

        getGridElement().getHeaderCell(0, 0).click();

        assertEditorClosed();
    }

    @Test
    public void testEditorSaveOnRowChange() {
        // Double click sets the focus programmatically
        getGridElement().getCell(5, 0).doubleClick();

        TestBenchElement editor = getGridElement().getEditor().getField(0);
        editor.clear();
        // Click to ensure IE focus...
        editor.click(5, 5);
        editor.sendKeys("Foo", Keys.ENTER);

        assertEquals("Editor did not move.", "(6, 0)",
                getGridElement().getEditor().getField(0).getAttribute("value"));
        assertEquals("Editor field value did not update from server.", "6",
                getGridElement().getEditor().getField(3).getAttribute("value"));

        assertEquals("Edited value was not saved.", "Foo",
                getGridElement().getCell(5, 0).getText());
    }
}
