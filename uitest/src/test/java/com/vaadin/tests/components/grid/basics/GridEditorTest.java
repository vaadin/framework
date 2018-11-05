package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;

public abstract class GridEditorTest extends GridBasicsTest {

    protected static final org.openqa.selenium.By BY_EDITOR_CANCEL = By
            .className("v-grid-editor-cancel");
    protected static final org.openqa.selenium.By BY_EDITOR_SAVE = By
            .className("v-grid-editor-save");
    protected static final String[] TOGGLE_EDIT_ENABLED = { "Component",
            "Editor", "Enabled" };

    @Override
    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();

        minimizeDebugWindow();

        selectMenuPath(TOGGLE_EDIT_ENABLED);
    }

    @Test
    public void testProgrammaticClosing() {
        editRow(5);
        assertEditorOpen();

        selectMenuPath("Component", "Editor", "Cancel edit");
        assertEditorClosed();
    }

    public void testEditorReopenAfterHide() {
        editRow(5);
        assertEditorOpen();
        selectMenuPath("Component", "Editor", "Hide grid");
        selectMenuPath("Component", "Editor", "Show grid");
        assertEditorClosed();
        editRow(5);
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

    protected void assertEditorOpen() {
        waitUntil(driver -> getGridElement()
                .isElementPresent(By.vaadin("#editor")));
    }

    protected void assertEditorClosed() {
        assertFalse("Editor is supposed to be closed",
                getGridElement().isElementPresent(By.vaadin("#editor")));
    }

    @Test
    public void testFocusOnMouseOpen() {

        GridCellElement cell = getGridElement().getCell(4, 0);

        cell.doubleClick();

        WebElement focused = getFocusedElement();

        assertEquals("", "input", focused.getTagName());
        assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testFocusOnKeyboardOpen() {

        GridCellElement cell = getGridElement().getCell(4, 0);

        cell.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        WebElement focused = getFocusedElement();

        assertEquals("", "input", focused.getTagName());
        assertEquals("", cell.getText(), focused.getAttribute("value"));
    }

    @Test
    public void testUneditableColumn() {
        editRow(5);
        assertEditorOpen();

        GridEditorElement editor = getGridElement().getEditor();
        assertFalse("Uneditable column should not have an editor widget",
                editor.isEditable(2));

        String classNames = editor
                .findElements(By.className("v-grid-editor-cells")).get(1)
                .findElements(By.xpath("./div")).get(2).getAttribute("class");

        assertTrue("Noneditable cell should contain not-editable classname",
                classNames.contains("not-editable"));

        assertTrue("Noneditable cell should contain v-grid-cell classname",
                classNames.contains("v-grid-cell"));

        assertNoErrorNotifications();
    }

    @Test
    public void testNoOpenFromHeaderOrFooter() {
        selectMenuPath("Component", "Footer", "Append footer row");

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

        editRow(5);
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
        editRow(5);

        selectMenuPath("Component", "Columns", "Column 0", "Sort ASC");

        assertEditorClosed();
    }

    @Ignore("Needs programmatic filtering")
    @Test
    public void testEditorClosedOnFilter() {
        editRow(5);

        selectMenuPath("Component", "Filter", "Column 1 starts with \"(23\"");

        assertEditorClosed();
    }

    @Test
    public void testEditorOpeningFromServer() {
        selectMenuPath("Component", "Editor", "Edit row 5");
        assertEditorOpen();

        Assert.assertEquals("Unexpected editor field content", "5",
                getEditor().getField(3).getAttribute("value"));
        Assert.assertEquals("Unexpected not-editable column content", "(5, 1)",
                getEditor().findElement(By.className("not-editable"))
                        .getText());
    }

    @Test
    public void testEditorOpenWithScrollFromServer() {
        selectMenuPath("Component", "Editor", "Edit last row");
        assertEditorOpen();

        Assert.assertEquals("Unexpected editor field content", "999",
                getEditor().getField(3).getAttribute("value"));
        Assert.assertEquals("Unexpected not-editable column content",
                "(999, 1)", getEditor()
                        .findElement(By.className("not-editable")).getText());
    }

    @Test
    public void testEditorCancelOnOpen() {
        editRow(2);
        getGridElement().sendKeys(Keys.ESCAPE);

        selectMenuPath("Component", "Editor", "Cancel next edit");
        getGridElement().getCell(2, 0).doubleClick();
        assertEditorClosed();

        editRow(2);
        assertNoErrorNotifications();
    }

    protected WebElement getSaveButton() {
        return getDriver().findElement(BY_EDITOR_SAVE);
    }

    protected WebElement getCancelButton() {
        return getDriver().findElement(BY_EDITOR_CANCEL);
    }

    protected void editRow(int rowIndex) {
        getGridElement().getCell(rowIndex, 0).doubleClick();
        assertEditorOpen();
    }

    protected boolean isEditorCellErrorMarked(int colIndex) {
        WebElement editorCell = getGridElement().getEditor()
                .findElement(By.xpath("./div/div[" + (colIndex + 1) + "]"));
        return editorCell.getAttribute("class").contains("error");
    }
}
