package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.customelements.GridElement;
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
