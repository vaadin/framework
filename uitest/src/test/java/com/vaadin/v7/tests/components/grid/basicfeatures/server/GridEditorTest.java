package com.vaadin.v7.tests.components.grid.basicfeatures.server;

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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public abstract class GridEditorTest extends GridBasicFeaturesTest {

    protected static final By BY_EDITOR_CANCEL = By
            .className("v-grid-editor-cancel");
    protected static final By BY_EDITOR_SAVE = By
            .className("v-grid-editor-save");
    protected static final String[] EDIT_ITEM_5 = { "Component", "Editor",
            "Edit item 5" };
    protected static final String[] EDIT_ITEM_100 = { "Component", "Editor",
            "Edit item 100" };
    protected static final String[] TOGGLE_EDIT_ENABLED = { "Component",
            "Editor", "Enabled" };

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
        boolean thrown = logContainsText(
                "Exception occurred, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testDisablingWhileOpen() {
        selectMenuPath(EDIT_ITEM_5);
        selectMenuPath(TOGGLE_EDIT_ENABLED);
        assertEditorOpen();
        boolean thrown = logContainsText(
                "Exception occurred, java.lang.IllegalStateException");
        assertTrue("IllegalStateException thrown", thrown);
    }

    @Test
    public void testProgrammaticOpeningWithScroll() {
        selectMenuPath(EDIT_ITEM_100);
        assertEditorOpen();
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
        assertEquals("Number of widgets", GridBasicFeatures.EDITABLE_COLUMNS,
                widgets.size());

        assertEquals("(100, 0)", widgets.get(0).getAttribute("value"));
        assertEquals("(100, 1)", widgets.get(1).getAttribute("value"));
        assertEquals("(100, 2)", widgets.get(2).getAttribute("value"));
        assertEquals("<b>100</b>", widgets.get(8).getAttribute("value"));
    }

    protected void assertEditorOpen() {
        assertEquals("Unexpected number of widgets",
                GridBasicFeatures.EDITABLE_COLUMNS, getEditorWidgets().size());
    }

    protected void assertEditorClosed() {
        assertNull("Editor is supposed to be closed", getEditor());
    }

    protected List<WebElement> getEditorWidgets() {
        assertNotNull("Editor is supposed to be open", getEditor());
        return getEditor().findElements(By.className("v-textfield"));

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
    public void testFocusOnProgrammaticOpenOnItemClick() {
        selectMenuPath("Component", "State", "EditorOpeningItemClickListener");

        GridCellElement cell = getGridElement().getCell(4, 2);

        cell.click();

        WebElement focused = getFocusedElement();

        assertEquals("", "input", focused.getTagName());
        assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testNoFocusOnProgrammaticOpen() {

        selectMenuPath(EDIT_ITEM_5);

        WebElement focused = getFocusedElement();

        assertEquals("Focus should remain in the menu", "menu",
                focused.getAttribute("id"));
    }

    @Test
    public void testUneditableColumn() {
        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();

        GridEditorElement editor = getGridElement().getEditor();
        assertFalse("Uneditable column should not have an editor widget",
                editor.isEditable(3));

        String classNames = editor
                .findElements(By.className("v-grid-editor-cells")).get(1)
                .findElements(By.xpath("./div")).get(3).getAttribute("class");

        assertTrue("Noneditable cell should contain not-editable classname",
                classNames.contains("not-editable"));

        assertTrue("Noneditable cell should contain v-grid-cell classname",
                classNames.contains("v-grid-cell"));

        assertNoErrorNotifications();
    }

    @Test
    public void testNoOpenFromHeaderOrFooter() {
        selectMenuPath("Component", "Footer", "Visible");

        getGridElement().getHeaderCell(0, 0).doubleClick();
        assertEditorClosed();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertEditorClosed();

        getGridElement().getFooterCell(0, 0).doubleClick();
        assertEditorClosed();

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertEditorClosed();
    }

    public void testEditorMoveOnResize() {
        selectMenuPath("Component", "Size", "Height", "500px");
        getGridElement().getCell(22, 0).doubleClick();
        assertEditorOpen();

        GridEditorElement editor = getGridElement().getEditor();
        TestBenchElement tableWrapper = getGridElement().getTableWrapper();

        int tableWrapperBottom = tableWrapper.getLocation().getY()
                + tableWrapper.getSize().getHeight();
        int editorBottom = editor.getLocation().getY()
                + editor.getSize().getHeight();

        assertTrue("Editor should not be initially outside grid",
                tableWrapperBottom - editorBottom <= 2);

        selectMenuPath("Component", "Size", "Height", "300px");
        assertEditorOpen();

        tableWrapperBottom = tableWrapper.getLocation().getY()
                + tableWrapper.getSize().getHeight();
        editorBottom = editor.getLocation().getY()
                + editor.getSize().getHeight();

        assertTrue("Editor should not be outside grid after resize",
                tableWrapperBottom - editorBottom <= 2);
    }

    public void testEditorDoesNotMoveOnResizeIfNotNeeded() {
        selectMenuPath("Component", "Size", "Height", "500px");

        selectMenuPath(EDIT_ITEM_5);
        assertEditorOpen();

        GridEditorElement editor = getGridElement().getEditor();

        int editorPos = editor.getLocation().getY();

        selectMenuPath("Component", "Size", "Height", "300px");
        assertEditorOpen();

        assertTrue("Editor should not have moved due to resize",
                editorPos == editor.getLocation().getY());
    }

    @Test
    public void testEditorClosedOnSort() {
        selectMenuPath(EDIT_ITEM_5);

        selectMenuPath("Component", "State", "Sort by column", "Column 0, ASC");

        assertEditorClosed();
    }

    @Test
    public void testEditorClosedOnFilter() {
        selectMenuPath(EDIT_ITEM_5);

        selectMenuPath("Component", "Filter", "Column 1 starts with \"(23\"");

        assertEditorClosed();
    }

    protected WebElement getSaveButton() {
        return getDriver().findElement(BY_EDITOR_SAVE);
    }

    protected WebElement getCancelButton() {
        return getDriver().findElement(BY_EDITOR_CANCEL);
    }
}
