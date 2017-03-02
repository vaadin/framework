package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.components.grid.basics.GridBasicsTest;

public class GridSelectionTest extends GridBasicsTest {
    @Test
    public void testSelectOnOff() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                getRow(0).isSelected());
        toggleFirstRowSelection();
        assertTrue("row should become selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", getRow(0).isSelected());

        toggleFirstRowSelection();
        assertTrue("row should become selected", getRow(0).isSelected());
        getGridElement().getCell(0, 0).click();
        assertFalse("row shouldn't remain selected", getRow(0).isSelected());
    }

    @Test
    public void testSelectOnScrollOffScroll() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                getRow(0).isSelected());
        toggleFirstRowSelection();
        assertTrue("row should become selected", getRow(0).isSelected());

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        scrollGridVerticallyTo(0); // scroll it back into view

        assertTrue("row should still be selected when scrolling "
                + "back into view", getRow(0).isSelected());
    }

    @Test
    public void testSelectScrollOnScrollOff() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                getRow(0).isSelected());

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // scroll it back into view
        assertTrue("row should still be selected when scrolling "
                + "back into view", getRow(0).isSelected());

        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", getRow(0).isSelected());
    }

    @Test
    public void testSelectScrollOnOffScroll() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected",
                getRow(0).isSelected());

        toggleFirstRowSelection();
        assertTrue("row should be selected", getRow(0).isSelected());
        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0);
        assertFalse(
                "row shouldn't be selected when scrolling " + "back into view",
                getRow(0).isSelected());
    }

    @Test
    public void testSingleSelectionUpdatesFromServer() {
        openTestURL();
        setSelectionModelSingle();

        GridElement grid = getGridElement();
        assertFalse("First row was selected from start",
                grid.getRow(0).isSelected());
        toggleFirstRowSelection();
        assertTrue("First row was not selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct", logContainsText(
                "SingleSelectionEvent: Selected: DataObject[0]"));
        grid.getCell(0, 0).click();
        assertFalse("First row was not deselected.", getRow(0).isSelected());
        assertTrue("Deselection event was not correct",
                logContainsText("SingleSelectionEvent: Selected: none"));

        grid.getCell(5, 0).click();
        assertTrue("Fifth row was not selected.", getRow(5).isSelected());
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct", logContainsText(
                "SingleSelectionEvent: Selected: DataObject[5]"));
        grid.getCell(0, 6).click();
        assertTrue("Selection event was not correct", logContainsText(
                "SingleSelectionEvent: Selected: DataObject[0]"));
        toggleFirstRowSelection();
        assertTrue("Selection event was not correct",
                logContainsText("SingleSelectionEvent: Selected: none"));
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertFalse("Fifth row was still selected.", getRow(5).isSelected());

        grid.scrollToRow(600);
        grid.getCell(595, 4).click();
        assertTrue("Row 595 was not selected.", getRow(595).isSelected());
        assertTrue("Selection event was not correct", logContainsText(
                "SingleSelectionEvent: Selected: DataObject[595]"));
        toggleFirstRowSelection();
        assertFalse("Row 595 was still selected.", getRow(595).isSelected());
        assertTrue("First row was not selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct", logContainsText(
                "SingleSelectionEvent: Selected: DataObject[0]"));
    }

    @Test
    public void testKeyboardWithMultiSelection() {
        openTestURL();
        setSelectionModelMulti();

        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();
        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.",
                grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.",
                !grid.getRow(3).isSelected());

        grid.scrollToRow(500);

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.",
                grid.getRow(3).isSelected());
    }

    @Test
    public void testKeyboardWithSingleSelection() {
        openTestURL();
        setSelectionModelSingle();

        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();

        assertTrue("Grid row 3 was not selected with clicking.",
                grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.",
                !grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.",
                grid.getRow(3).isSelected());

        grid.scrollToRow(500);

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.",
                !grid.getRow(3).isSelected());
    }

    @Test
    public void testChangeSelectionModelUpdatesUI() {
        openTestURL();

        setSelectionModelMulti();

        getGridElement().getCell(5, 0).click();
        assertTrue("Row should be selected after clicking",
                getRow(5).isSelected());

        setSelectionModelSingle();
        assertFalse("Row should not be selected after changing selection model",
                getRow(5).isSelected());
    }

    @Test
    public void testSelectionCheckBoxesHaveStyleNames() {
        openTestURL();

        setSelectionModelMulti();

        assertTrue(
                "Selection column CheckBox should have the proper style name set",
                getGridElement().getCell(0, 0).findElement(By.tagName("span"))
                        .getAttribute("class")
                        .contains("v-grid-selection-checkbox"));

        GridCellElement header = getGridElement().getHeaderCell(0, 0);
        assertTrue("Select all CheckBox should have the proper style name set",
                header.findElement(By.tagName("span")).getAttribute("class")
                        .contains("v-grid-select-all-checkbox"));
    }

    @Test
    public void testServerSideSelectTogglesSelectAllCheckBox() {
        openTestURL();

        setSelectionModelMulti();
        assertFalse("Select all CheckBox should not be selected",
                getSelectAllCheckbox().isSelected());

        selectAll();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), true);
        assertTrue("Select all CheckBox wasn't selected as expected",
                getSelectAllCheckbox().isSelected());

        deselectAll();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), false);
        assertFalse("Select all CheckBox was selected unexpectedly",
                getSelectAllCheckbox().isSelected());

        selectAll();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), true);
        getGridElement().getCell(5, 0).click();
        waitUntilCheckBoxValue(getSelectAllCheckbox(), false);
        assertFalse("Select all CheckBox was selected unexpectedly",
                getSelectAllCheckbox().isSelected());
    }

    @Test
    public void testRemoveSelectedRow() {
        openTestURL();

        setSelectionModelSingle();
        getGridElement().getCell(0, 0).click();

        selectMenuPath("Component", "Body rows", "Deselect all");

        assertFalse(
                "Unexpected NullPointerException when removing selected rows",
                logContainsText(
                        "Exception occured, java.lang.NullPointerException: null"));
    }

    @Test
    public void singleSelectUserSelectionDisallowedSpaceSelectionNoOp() {
        openTestURL();
        setSelectionModelSingle();
        getGridElement().focus();
        getGridElement().sendKeys(Keys.DOWN, Keys.SPACE);
        assertTrue("row was selected when selection was allowed",
                getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(Keys.SPACE);
        assertTrue("deselect disallowed", getRow(1).isSelected());
        getGridElement().sendKeys(Keys.DOWN, Keys.SPACE);
        assertFalse("select disallowed", getRow(2).isSelected());
        assertTrue("old selection remains", getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(Keys.SPACE);
        assertTrue("select allowed again", getRow(2).isSelected());
        assertFalse("old selection removed", getRow(1).isSelected());

    }

    @Test
    public void singleSelectUserSelectionDisallowedClickSelectionNoOp() {
        openTestURL();
        setSelectionModelSingle();
        getGridElement().getCell(1, 0).click();
        assertTrue("selection allowed, should have been selected",
                getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().getCell(1, 0).click();
        assertTrue("deselect disallowed, should remain selected",
                getRow(1).isSelected());
        getGridElement().getCell(2, 0).click();
        assertFalse("select disallowed, should not have been selected",
                getRow(2).isSelected());
        assertTrue("select disallowed, old selection should have remained",
                getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().getCell(2, 0).click();
        assertTrue("select allowed again, row should have been selected",
                getRow(2).isSelected());
        assertFalse("old selection removed", getRow(1).isSelected());

    }

    @Test
    public void multiSelectUserSelectionDisallowedSpaceSelectionNoOp() {
        openTestURL();
        setSelectionModelMulti();
        getGridElement().focus();
        getGridElement().sendKeys(Keys.DOWN, Keys.SPACE);
        assertTrue("selection allowed, should have been selected",
                getRow(1).isSelected());
        toggleUserSelectionAllowed();
        getGridElement().sendKeys(Keys.SPACE);
        assertTrue("deselect disallowed, should remain selected",
                getRow(1).isSelected());
        getGridElement().sendKeys(Keys.DOWN, Keys.SPACE);
        assertFalse("select disallowed, should not have been selected",
                getRow(2).isSelected());
        assertTrue("select disallowed, old selection should have remained",
                getRow(1).isSelected());

        toggleUserSelectionAllowed();
        getGridElement().sendKeys(Keys.SPACE);
        assertTrue("select allowed again, row should have been selected",
                getRow(2).isSelected());
        assertTrue(
                "select allowed again but old selection should have remained",
                getRow(1).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedCheckboxSelectionNoOp() {
        openTestURL();
        setSelectionModelMulti();
        assertTrue(getSelectionCheckbox(0).isEnabled());
        toggleUserSelectionAllowed();
        assertFalse(getSelectionCheckbox(0).isEnabled());

        // Select by clicking on checkbox (should always fail as it is disabled)
        getSelectionCheckbox(0).click();
        assertFalse(getGridElement().getRow(0).isSelected());
        // Select by clicking on cell (should fail)
        getGridElement().getCell(0, 0).click();
        assertFalse(getGridElement().getRow(0).isSelected());

        toggleUserSelectionAllowed();
        assertTrue(getSelectionCheckbox(0).isEnabled());
        getSelectionCheckbox(0).click();
        assertTrue(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedCheckboxSelectAllNoOp() {
        openTestURL();
        setSelectionModelMulti();

        assertTrue(getSelectAllCheckbox().isEnabled());
        toggleUserSelectionAllowed();
        assertFalse(getSelectAllCheckbox().isEnabled());

        // Select all by clicking on checkbox (should not select)
        getSelectAllCheckbox().click();
        assertFalse(getSelectAllCheckbox().isSelected());
        assertFalse(getGridElement().getRow(0).isSelected());
        assertFalse(getGridElement().getRow(10).isSelected());

        // Select all by clicking on header cell (should not select)
        getGridElement().getHeaderCell(0, 0).click();
        assertFalse(getSelectAllCheckbox().isSelected());
        assertFalse(getGridElement().getRow(0).isSelected());
        assertFalse(getGridElement().getRow(10).isSelected());

        // Select all by press SPACE on the header cell (should not select)
        getGridElement().getHeaderCell(0, 0).sendKeys(Keys.SPACE);
        assertFalse(getSelectAllCheckbox().isSelected());
        assertFalse(getGridElement().getRow(0).isSelected());
        assertFalse(getGridElement().getRow(10).isSelected());

        toggleUserSelectionAllowed();

        assertTrue(getSelectAllCheckbox().isEnabled());
        getSelectAllCheckbox().click();
        assertTrue(getGridElement().getRow(0).isSelected());
        assertTrue(getGridElement().getRow(10).isSelected());
    }

    @Test
    public void singleSelectUserSelectionDisallowedServerSelect() {
        openTestURL();
        setSelectionModelSingle();
        toggleUserSelectionAllowed();

        toggleFirstRowSelection();
        assertTrue(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void multiSelectUserSelectionDisallowedServerSelect() {
        openTestURL();
        setSelectionModelMulti();
        toggleUserSelectionAllowed();

        toggleFirstRowSelection();
        assertTrue(getGridElement().getRow(0).isSelected());
    }

    @Test
    @Ignore("Removing rows is not implemented in the UI")
    public void testRemoveSelectedRowMulti() {
        openTestURL();

        setSelectionModelMulti();
        GridElement grid = getGridElement();
        grid.getCell(5, 0).click();

        selectMenuPath("Component", "Body rows", "Remove selected rows");
        assertSelected();
        grid.getCell(5, 0).click();
        assertSelected(5);
        grid.getCell(6, 0).click();
        assertSelected(5, 6);
        grid.getCell(5, 0).click();
        assertSelected(6);
        grid.getCell(5, 0).click();
        grid.getCell(4, 0).click();
        selectMenuPath("Component", "Body rows", "Remove selected rows");
        assertSelected();
        grid.getCell(0, 0).click();
        assertSelected(0);
        grid.getCell(5, 0).click();
        assertSelected(0, 5);
        grid.getCell(6, 0).click();
        assertSelected(0, 5, 6);

    }

    private void assertSelected(Integer... selected) {
        GridElement grid = getGridElement();
        HashSet<Integer> expected = new HashSet<Integer>(
                Arrays.asList(selected));
        for (int i = 0; i < 10; i++) {
            boolean rowSelected = grid.getRow(i).isSelected();
            if (expected.contains(i)) {
                Assert.assertTrue("Expected row " + i + " to be selected",
                        rowSelected);
            } else {
                Assert.assertFalse("Expected row " + i + " not to be selected",
                        rowSelected);
            }
        }

    }

    private void toggleUserSelectionAllowed() {
        selectMenuPath("Component", "State", "Disallow user selection");
    }

    private WebElement getSelectionCheckbox(int row) {
        return getGridElement().getCell(row, 0)
                .findElement(By.tagName("input"));
    }

    private void waitUntilCheckBoxValue(final WebElement checkBoxElememnt,
            final boolean expectedValue) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return expectedValue ? checkBoxElememnt.isSelected()
                        : !checkBoxElememnt.isSelected();
            }
        }, 5);
    }

    private GridRowElement getRow(int i) {
        return getGridElement().getRow(i);
    }
}
