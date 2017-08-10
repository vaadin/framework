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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
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
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import com.vaadin.tests.components.grid.basicfeatures.element.CustomGridElement;

public class GridSelectionTest extends GridBasicFeaturesTest {

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

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // make sure the row is out of cache
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
        assertTrue("Selection event was not correct",
                logContainsText("Added 0, Removed none"));
        grid.getCell(0, 0).click();
        assertFalse("First row was not deselected.", getRow(0).isSelected());
        assertTrue("Deselection event was not correct",
                logContainsText("Added none, Removed 0"));

        grid.getCell(5, 0).click();
        assertTrue("Fifth row was not selected.", getRow(5).isSelected());
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Added 5, Removed none"));
        grid.getCell(0, 6).click();
        assertTrue("Selection event was not correct",
                logContainsText("Added 0, Removed 5"));
        toggleFirstRowSelection();
        assertTrue("Selection event was not correct",
                logContainsText("Added none, Removed 0"));
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertFalse("Fifth row was still selected.", getRow(5).isSelected());

        grid.scrollToRow(600);
        grid.getCell(595, 3).click();
        assertTrue("Row 595 was not selected.", getRow(595).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Added 595, Removed none"));
        toggleFirstRowSelection();
        assertFalse("Row 595 was still selected.", getRow(595).isSelected());
        assertTrue("First row was not selected.", getRow(0).isSelected());
        assertTrue("Selection event was not correct",
                logContainsText("Added 0, Removed 595"));
    }

    @Test
    public void testKeyboardSelection() {
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
    public void testSelectAllCheckbox() {
        openTestURL();

        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();

        for (int i = 0; i < GridBasicFeatures.ROWS; i += 100) {
            assertTrue("Row " + i + " was not selected.",
                    getGridElement().getRow(i).isSelected());
        }

        header.findElement(By.tagName("input")).click();
        assertFalse("Row 100 was still selected",
                getGridElement().getRow(100).isSelected());
    }

    @Test
    public void testSelectAllAndSort() {
        openTestURL();

        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        header.findElement(By.tagName("input")).click();

        getGridElement().getHeaderCell(0, 1).click();

        WebElement selectionBox = getGridElement().getCell(4, 0)
                .findElement(By.tagName("input"));
        selectionBox.click();
        selectionBox.click();

        assertFalse("Exception occured on row reselection.", logContainsText(
                "Exception occured, java.lang.IllegalStateException: No item id for key 101 found."));
    }

    @Test
    public void testSelectAllCheckboxWhenChangingModels() {
        openTestURL();

        GridCellElement header;
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(0, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        // Single selection model shouldn't have selection column to begin with
        assertFalse(
                "Selection columnn shouldn't have been in grid for Single Selection Model",
                getGridElement().getCell(0, 1)
                        .isElementPresent(By.tagName("input")));

        setSelectionModelNone();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

    }

    @Test
    public void testSelectAllCheckboxWithHeaderOperations() {
        openTestURL();

        setSelectionModelMulti();
        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");

        GridCellElement header = getGridElement().getHeaderCell(1, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

    }

    @Test
    public void testToggleDeselectAllowed() {
        openTestURL();

        setSelectionModelSingle();
        // Deselect allowed already enabled

        getGridElement().getCell(5, 1).click();
        getGridElement().getCell(5, 1).click();
        assertFalse("Row should be not selected after two clicks",
                getRow(5).isSelected());

        selectMenuPath("Component", "State", "Single select allow deselect");
        getGridElement().getCell(5, 1).click();
        getGridElement().getCell(5, 1).click();
        assertTrue("Row should be selected after two clicks",
                getRow(5).isSelected());

        selectMenuPath("Component", "State", "Single select allow deselect");
        getGridElement().getCell(5, 1).click();
        assertFalse("Row should be not selected after another click",
                getRow(5).isSelected());

        // Also verify that state is updated together with the model
        setSelectionModelNone();
        selectMenuPath("Component", "State", "Single select allow deselect");
        setSelectionModelSingle();

        getGridElement().getCell(5, 1).click();
        getGridElement().getCell(5, 1).click();

        assertTrue("Row should stay selected after two clicks",
                getRow(5).isSelected());
    }

    @Test
    public void testChangeSelectionModelUpdatesUI() {
        openTestURL();

        setSelectionModelSingle();

        getGridElement().getCell(5, 1).click();
        assertTrue("Row should be selected after clicking",
                getRow(5).isSelected());

        setSelectionModelNone();
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
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        WebElement selectAll = header.findElement(By.tagName("input"));

        selectMenuPath("Component", "State", "Select all");
        waitUntilCheckBoxValue(selectAll, true);
        assertTrue("Select all CheckBox wasn't selected as expected",
                selectAll.isSelected());

        selectMenuPath("Component", "State", "Select none");
        waitUntilCheckBoxValue(selectAll, false);
        assertFalse("Select all CheckBox was selected unexpectedly",
                selectAll.isSelected());

        selectMenuPath("Component", "State", "Select all");
        waitUntilCheckBoxValue(selectAll, true);
        getGridElement().getCell(5, 0).click();
        waitUntilCheckBoxValue(selectAll, false);
        assertFalse("Select all CheckBox was selected unexpectedly",
                selectAll.isSelected());
    }

    @Test
    public void testRemoveSelectedRow() {
        openTestURL();

        setSelectionModelSingle();
        getGridElement().getCell(0, 0).click();

        selectMenuPath("Component", "Body rows", "Remove selected rows");

        assertFalse(
                "Unexpected NullPointerException when removing selected rows",
                logContainsText(
                        "Exception occured, java.lang.NullPointerException: null"));
    }

    @Test
    public void testRemoveSelectedRowMulti() {
        openTestURL();

        setSelectionModelMulti();
        CustomGridElement grid = getGridElement();
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
        CustomGridElement grid = getGridElement();
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

    private void setSelectionModelMulti() {
        selectMenuPath("Component", "State", "Selection mode", "multi");
    }

    private void setSelectionModelSingle() {
        selectMenuPath("Component", "State", "Selection mode", "single");
    }

    private void setSelectionModelNone() {
        selectMenuPath("Component", "State", "Selection mode", "none");
    }

    private void toggleFirstRowSelection() {
        selectMenuPath("Component", "Body rows", "Select first row");
    }

    private void toggleUserSelectionAllowed() {
        selectMenuPath("Component", "State", "Allow user selection");
    }

    private GridRowElement getRow(int i) {
        return getGridElement().getRow(i);
    }

    @Test
    public void changeSelectionModelSingleToNone() throws Exception {
        openTestURL();

        setSelectionModelSingle();
        getGridElement().getCell(0, 0).click();
        assertTrue("row should become selected", getRow(0).isSelected());
        setSelectionModelNone();
        assertFalse("row should become deselected", getRow(0).isSelected());
        setSelectionModelSingle();
        assertFalse("row should still be deselected", getRow(0).isSelected());
        getGridElement().getCell(0, 0).click();
        assertTrue("row should become selected", getRow(0).isSelected());
    }

    @Test
    public void changeSelectionModelMultiToNone() throws Exception {
        openTestURL();

        setSelectionModelMulti();
        getGridElement().getCell(0, 0).click();
        getGridElement().getCell(1, 0).click();
        getGridElement().getCell(2, 0).click();
        assertTrue("row should become selected", getRow(0).isSelected());
        assertTrue("row should become selected", getRow(1).isSelected());
        assertTrue("row should become selected", getRow(2).isSelected());

        setSelectionModelNone();
        assertFalse("row should become deselected", getRow(0).isSelected());
        assertFalse("row should become deselected", getRow(1).isSelected());
        assertFalse("row should become deselected", getRow(2).isSelected());
        setSelectionModelMulti();
        assertFalse("row should still be deselected", getRow(0).isSelected());
        assertFalse("row should still be deselected", getRow(1).isSelected());
        assertFalse("row should still be deselected", getRow(2).isSelected());

        getGridElement().getCell(0, 0).click();
        assertTrue("row should become selected", getRow(0).isSelected());
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

    private WebElement getSelectAllCheckbox() {
        return getGridElement().getHeaderCell(0, 0)
                .findElement(By.tagName("input"));
    }

    private WebElement getSelectionCheckbox(int row) {
        return getGridElement().getCell(row, 0)
                .findElement(By.tagName("input"));
    }

}
