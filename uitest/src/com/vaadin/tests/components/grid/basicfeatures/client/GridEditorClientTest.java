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
import static org.junit.Assert.assertNotEquals;
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

import com.vaadin.shared.ui.grid.GridConstants;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

public class GridEditorClientTest extends GridBasicClientFeaturesTest {

    private static final String[] EDIT_ROW_100 = new String[] { "Component",
            "Editor", "Edit row 100" };
    private static final String[] EDIT_ROW_5 = new String[] { "Component",
            "Editor", "Edit row 5" };

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "Editor", "Enabled");
    }

    @Test
    public void testProgrammaticOpeningClosing() {
        selectMenuPath(EDIT_ROW_5);
        assertNotNull(getEditor());

        selectMenuPath("Component", "Editor", "Cancel edit");
        assertNull(getEditor());
        assertEquals("Row 5 edit cancelled",
                findElement(By.className("grid-editor-log")).getText());
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath(EDIT_ROW_100);
        assertNotNull(getEditor());
    }

    @Test(expected = NoSuchElementException.class)
    public void testVerticalScrollLocking() {
        selectMenuPath(EDIT_ROW_5);
        getGridElement().getCell(200, 0);
    }

    @Test
    public void testMouseOpeningClosing() {

        getGridElement().getCell(4, 0).doubleClick();
        assertNotNull(getEditor());

        getCancelButton().click();
        assertNull(getEditor());

        // Disable editor
        selectMenuPath("Component", "Editor", "Enabled");

        getGridElement().getCell(4, 0).doubleClick();
        assertNull(getEditor());
    }

    @Test
    public void testKeyboardOpeningClosing() {

        getGridElement().getCell(4, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertNotNull(getEditor());

        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();
        assertNull(getEditor());
        assertEquals("Row 4 edit cancelled",
                findElement(By.className("grid-editor-log")).getText());

        // Disable editor
        selectMenuPath("Component", "Editor", "Enabled");

        getGridElement().getCell(5, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertNull(getEditor());
    }

    @Test
    public void testWidgetBinding() throws Exception {
        selectMenuPath(EDIT_ROW_100);
        WebElement editor = getEditor();

        List<WebElement> widgets = editor.findElements(By
                .className("gwt-TextBox"));

        assertEquals(GridBasicFeatures.EDITABLE_COLUMNS, widgets.size());

        assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));

        assertEquals("100", widgets.get(6).getAttribute("value"));
        assertEquals("<b>100</b>", widgets.get(8).getAttribute("value"));
    }

    @Test
    public void testWithSelectionColumn() throws Exception {
        selectMenuPath("Component", "State", "Selection mode", "multi");
        selectMenuPath("Component", "State", "Frozen column count",
                "-1 columns");
        selectMenuPath(EDIT_ROW_5);

        WebElement editorCells = findElements(
                By.className("v-grid-editor-cells")).get(1);
        List<WebElement> selectorDivs = editorCells.findElements(By
                .cssSelector("div"));

        assertTrue("selector column cell should've been empty", selectorDivs
                .get(0).getAttribute("innerHTML").isEmpty());
        assertFalse("normal column cell shoul've had contents", selectorDivs
                .get(1).getAttribute("innerHTML").isEmpty());
    }

    @Test
    public void testSave() {
        selectMenuPath(EDIT_ROW_100);

        WebElement textField = getEditor().findElements(
                By.className("gwt-TextBox")).get(0);

        textField.clear();
        textField.sendKeys("Changed");

        WebElement saveButton = getEditor().findElement(
                By.className("v-grid-editor-save"));

        saveButton.click();

        assertEquals("Changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testProgrammaticSave() {
        selectMenuPath(EDIT_ROW_100);

        WebElement textField = getEditor().findElements(
                By.className("gwt-TextBox")).get(0);

        textField.clear();
        textField.sendKeys("Changed");

        selectMenuPath("Component", "Editor", "Save");

        assertEquals("Changed", getGridElement().getCell(100, 0).getText());
    }

    @Test
    public void testCaptionChange() {
        selectMenuPath(EDIT_ROW_5);
        assertEquals("Save button caption should've been \""
                + GridConstants.DEFAULT_SAVE_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());
        assertEquals("Cancel button caption should've been \""
                + GridConstants.DEFAULT_CANCEL_CAPTION + "\" to begin with",
                GridConstants.DEFAULT_CANCEL_CAPTION, getCancelButton()
                        .getText());

        selectMenuPath("Component", "Editor", "Change Save Caption");
        assertNotEquals(
                "Save button caption should've changed while editor is open",
                GridConstants.DEFAULT_SAVE_CAPTION, getSaveButton().getText());

        getCancelButton().click();

        selectMenuPath("Component", "Editor", "Change Cancel Caption");
        selectMenuPath(EDIT_ROW_5);
        assertNotEquals(
                "Cancel button caption should've changed while editor is closed",
                GridConstants.DEFAULT_CANCEL_CAPTION, getCancelButton()
                        .getText());
    }

    @Test
    public void testUneditableColumn() {
        selectMenuPath("Component", "Editor", "Edit row 5");

        assertFalse("Uneditable column should not have an editor widget",
                getGridElement().getEditor().isEditable(3));
    }

    @Test
    public void testErrorField() {
        selectMenuPath(EDIT_ROW_5);

        GridEditorElement editor = getGridElement().getEditor();

        assertTrue("No errors should be present",
                editor.findElements(By.className("error")).isEmpty());
        assertEquals("No error message should be present", null,
                editor.getErrorMessage());

        selectMenuPath("Component", "Editor", "Toggle second editor error");
        getSaveButton().click();

        assertEquals("Unexpected amount of error fields", 1, editor
                .findElements(By.className("error")).size());
        assertEquals(
                "Unexpedted error message",
                "Syntethic fail of editor in column 2. "
                        + "This message is so long that it doesn't fit into its box",
                editor.getErrorMessage());
    }

    @Test
    public void testFocusOnMouseOpen() {

        GridCellElement cell = getGridElement().getCell(4, 2);

        cell.doubleClick();

        WebElement focused = getFocusedElement();

        assertEquals("", "input", focused.getTagName());
        assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testFocusOnKeyboardOpen() {

        GridCellElement cell = getGridElement().getCell(4, 2);

        cell.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        WebElement focused = getFocusedElement();

        assertEquals("", "input", focused.getTagName());
        assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testNoFocusOnProgrammaticOpen() {

        selectMenuPath(EDIT_ROW_5);

        WebElement focused = getFocusedElement();

        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            assertEquals("Focus should be in html", "html",
                    focused.getTagName());
        } else if (BrowserUtil.isIE(getDesiredCapabilities())) {
            assertEquals("Focus should be nowhere", null, focused);
        } else {
            // GWT menubar loses focus after clicking a menuitem
            assertEquals("Focus should be in body", "body",
                    focused.getTagName());
        }
    }

    protected WebElement getSaveButton() {
        return getEditor().findElement(By.className("v-grid-editor-save"));
    }

    protected WebElement getCancelButton() {
        return getEditor().findElement(By.className("v-grid-editor-cancel"));
    }
}
