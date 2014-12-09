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

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridSelectionTest extends GridBasicFeaturesTest {

    @Test
    public void testSelectOnOff() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected", getRow(0)
                .isSelected());
        toggleFirstRowSelection();
        assertTrue("row should become selected", getRow(0).isSelected());
        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", getRow(0).isSelected());
    }

    @Test
    public void testSelectOnScrollOffScroll() throws Exception {
        openTestURL();

        setSelectionModelMulti();

        assertFalse("row shouldn't start out as selected", getRow(0)
                .isSelected());
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

        assertFalse("row shouldn't start out as selected", getRow(0)
                .isSelected());

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

        assertFalse("row shouldn't start out as selected", getRow(0)
                .isSelected());

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // make sure the row is out of cache
        assertFalse("row shouldn't be selected when scrolling "
                + "back into view", getRow(0).isSelected());
    }

    @Test
    public void testSingleSelectionUpdatesFromServer() {
        openTestURL();
        setSelectionModelSingle();

        GridElement grid = getGridElement();
        assertFalse("First row was selected from start", grid.getRow(0)
                .isSelected());
        toggleFirstRowSelection();
        assertTrue("First row was not selected.", getRow(0).isSelected());
        grid.getCell(5, 0).click();
        assertTrue("Fifth row was not selected.", getRow(5).isSelected());
        assertFalse("First row was still selected.", getRow(0).isSelected());
        grid.getCell(0, 6).click();
        toggleFirstRowSelection();
        assertFalse("First row was still selected.", getRow(0).isSelected());
        assertFalse("Fifth row was still selected.", getRow(5).isSelected());

        grid.scrollToRow(600);
        grid.getCell(595, 3).click();
        assertTrue("Row 595 was not selected.", getRow(595).isSelected());
        toggleFirstRowSelection();
        assertFalse("Row 595 was still selected.", getRow(595).isSelected());
        assertTrue("First row was not selected.", getRow(0).isSelected());
    }

    @Test
    public void testKeyboardSelection() {
        openTestURL();
        setSelectionModelMulti();

        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();
        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.", grid
                .getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.", !grid
                .getRow(3).isSelected());

        grid.scrollToRow(500);

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.", grid
                .getRow(3).isSelected());
    }

    @Test
    public void testKeyboardWithSingleSelection() {
        openTestURL();
        setSelectionModelSingle();

        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();

        assertTrue("Grid row 3 was not selected with clicking.", grid.getRow(3)
                .isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.", !grid
                .getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not selected with space key.", grid
                .getRow(3).isSelected());

        grid.scrollToRow(500);

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Grid row 3 was not deselected with space key.", !grid
                .getRow(3).isSelected());
    }

    @Test
    public void testSelectAllCheckbox() {
        openTestURL();

        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();

        for (int i = 0; i < GridBasicFeatures.ROWS; i += 100) {
            assertTrue("Row " + i + " was not selected.", getGridElement()
                    .getRow(i).isSelected());
        }

        header.findElement(By.tagName("input")).click();
        assertFalse("Row 100 was still selected", getGridElement().getRow(100)
                .isSelected());
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
                getGridElement().getCell(0, 1).isElementPresent(
                        By.tagName("input")));

        setSelectionModelNone();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

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

    private GridRowElement getRow(int i) {
        return getGridElement().getRow(i);
    }
}
