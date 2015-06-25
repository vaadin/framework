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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

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
        boolean thrown = logContainsText("Exception occured, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testEditorMove() {
        selectMenuPath(EDIT_ITEM_5);

        assertEditorOpen();

        String firstFieldValue = getEditorWidgets().get(0)
                .getAttribute("value");
        assertTrue("Editor is not at correct row index (5)",
                "(5, 0)".equals(firstFieldValue));

        getGridElement().getCell(10, 0).click();
        firstFieldValue = getEditorWidgets().get(0).getAttribute("value");

        assertTrue("Editor is not at correct row index (10)",
                "(10, 0)".equals(firstFieldValue));
    }

    @Test
    public void testErrorMessageWrapperHidden() {
        selectMenuPath(EDIT_ITEM_5);

        assertEditorOpen();

        WebElement editorFooter = getEditor().findElement(
                By.className("v-grid-editor-footer"));

        assertTrue("Editor footer should not be visible when there's no error",
                editorFooter.getCssValue("display").equalsIgnoreCase("none"));
    }

    @Test
    public void testScrollAfterEditByAPI() {
        int originalScrollPos = getGridVerticalScrollPos();

        selectMenuPath(EDIT_ITEM_5);

        scrollGridVerticallyTo(100);
        assertGreater(
                "Grid should scroll vertically while editing in unbuffered mode",
                getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testScrollAfterEditByMouse() {
        int originalScrollPos = getGridVerticalScrollPos();

        GridCellElement cell_5_0 = getGridElement().getCell(5, 0);
        new Actions(getDriver()).doubleClick(cell_5_0).perform();

        scrollGridVerticallyTo(100);
        assertGreater(
                "Grid should scroll vertically while editing in unbuffered mode",
                getGridVerticalScrollPos(), originalScrollPos);
    }

    @Test
    public void testScrollAfterEditByKeyboard() {
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
        assertEquals("Editor should edit row 5", "(5, 0)", getEditorWidgets()
                .get(0).getAttribute("value"));

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

}