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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

public class GridHeaderTest extends GridStaticSectionTest {

    @Test
    public void testDefaultHeader() throws Exception {
        openTestURL();

        assertHeaderCount(1);
        assertHeaderTexts(0, 0);
    }

    @Test
    public void testHeaderVisibility() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Header", "Visible");

        assertHeaderCount(0);

        selectMenuPath("Component", "Header", "Append row");

        assertHeaderCount(0);

        selectMenuPath("Component", "Header", "Visible");

        assertHeaderCount(2);
    }

    @Test
    public void testHeaderCaptions() throws Exception {
        openTestURL();

        assertHeaderTexts(0, 0);
    }

    @Test
    public void testAddRows() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Header", "Append row");

        assertHeaderCount(2);
        assertHeaderTexts(0, 0);
        assertHeaderTexts(1, 1);

        selectMenuPath("Component", "Header", "Prepend row");

        assertHeaderCount(3);
        assertHeaderTexts(2, 0);
        assertHeaderTexts(0, 1);
        assertHeaderTexts(1, 2);

        selectMenuPath("Component", "Header", "Append row");

        assertHeaderCount(4);
        assertHeaderTexts(2, 0);
        assertHeaderTexts(0, 1);
        assertHeaderTexts(1, 2);
        assertHeaderTexts(3, 3);
    }

    @Test
    public void testRemoveRows() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Append row");

        selectMenuPath("Component", "Header", "Remove top row");

        assertHeaderCount(2);
        assertHeaderTexts(0, 0);
        assertHeaderTexts(2, 1);

        selectMenuPath("Component", "Header", "Remove bottom row");
        assertHeaderCount(1);
        assertHeaderTexts(0, 0);
    }

    @Test
    public void testDefaultRow() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Sortable");

        GridCellElement headerCell = getGridElement().getHeaderCell(0, 0);

        headerCell.click();

        assertTrue(hasClassName(headerCell, "sort-asc"));

        headerCell.click();

        assertFalse(hasClassName(headerCell, "sort-asc"));
        assertTrue(hasClassName(headerCell, "sort-desc"));

        selectMenuPath("Component", "Header", "Prepend row");
        selectMenuPath("Component", "Header", "Default row", "Top");

        assertFalse(hasClassName(headerCell, "sort-desc"));
        headerCell = getGridElement().getHeaderCell(0, 0);
        assertTrue(hasClassName(headerCell, "sort-desc"));

        selectMenuPath("Component", "Header", "Default row", "Unset");

        assertFalse(hasClassName(headerCell, "sort-desc"));
    }

    @Test
    public void joinHeaderColumnsByCells() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Header", "Append row");

        selectMenuPath("Component", "Header", "Row 2", "Join column cells 0, 1");

        GridCellElement spannedCell = getGridElement().getHeaderCell(1, 0);
        assertTrue(spannedCell.isDisplayed());
        assertEquals("2", spannedCell.getAttribute("colspan"));

        // TestBench returns the spanned cell for all spanned columns
        GridCellElement hiddenCell = getGridElement().getHeaderCell(1, 1);
        assertEquals(spannedCell.getText(), hiddenCell.getText());
    }

    @Test
    public void joinHeaderColumnsByColumns() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Header", "Append row");

        selectMenuPath("Component", "Header", "Row 2", "Join columns 1, 2");

        GridCellElement spannedCell = getGridElement().getHeaderCell(1, 1);
        assertTrue(spannedCell.isDisplayed());
        assertEquals("2", spannedCell.getAttribute("colspan"));

        // TestBench returns the spanned cell for all spanned columns
        GridCellElement hiddenCell = getGridElement().getHeaderCell(1, 2);
        assertEquals(spannedCell.getText(), hiddenCell.getText());
    }

    @Test
    public void joinAllColumnsInHeaderRow() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Header", "Append row");

        selectMenuPath("Component", "Header", "Row 2", "Join all columns");

        GridCellElement spannedCell = getGridElement().getHeaderCell(1, 0);
        assertTrue(spannedCell.isDisplayed());
        assertEquals("" + GridBasicFeatures.COLUMNS,
                spannedCell.getAttribute("colspan"));

        for (int columnIndex = 1; columnIndex < GridBasicFeatures.COLUMNS; columnIndex++) {
            // TestBench returns the spanned cell for all spanned columns
            GridCellElement hiddenCell = getGridElement().getHeaderCell(1,
                    columnIndex);
            assertEquals(spannedCell.getText(), hiddenCell.getText());
        }
    }

    @Test
    public void testInitialCellTypes() throws Exception {
        openTestURL();

        GridCellElement textCell = getGridElement().getHeaderCell(0, 0);

        /*
         * Reindeer has a CSS text transformation that changes the casing so
         * that we can't rely on it being what we set
         */
        assertEquals("header (0,0)", textCell.getText().toLowerCase());

        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 1);
        assertTrue(widgetCell.isElementPresent(By.className("gwt-HTML")));

        GridCellElement htmlCell = getGridElement().getHeaderCell(0, 2);
        assertHTML("<b>Header (0,2)</b>", htmlCell);
    }

    @Test
    public void testDynamicallyChangingCellType() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Header Type",
                "Widget Header");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        assertTrue(widgetCell.isElementPresent(By.className("gwt-Button")));

        selectMenuPath("Component", "Columns", "Column 1", "Header Type",
                "HTML Header");
        GridCellElement htmlCell = getGridElement().getHeaderCell(0, 1);
        assertHTML("<b>HTML Header</b>", htmlCell);

        selectMenuPath("Component", "Columns", "Column 2", "Header Type",
                "Text Header");
        GridCellElement textCell = getGridElement().getHeaderCell(0, 2);

        /*
         * Reindeer has a CSS text transformation that changes the casing so
         * that we can't rely on it being what we set
         */
        assertEquals("text header", textCell.getText().toLowerCase());
    }

    @Test
    public void testCellWidgetInteraction() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Header Type",
                "Widget Header");
        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        WebElement button = widgetCell.findElement(By.className("gwt-Button"));

        new Actions(getDriver()).moveToElement(button, 5, 5).click().perform();

        assertEquals("clicked", button.getText().toLowerCase());
    }

    @Test
    public void widgetInSortableCellInteraction() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 0", "Header Type",
                "Widget Header");

        selectMenuPath("Component", "Columns", "Column 0", "Sortable");

        GridCellElement widgetCell = getGridElement().getHeaderCell(0, 0);
        WebElement button = widgetCell.findElement(By.className("gwt-Button"));

        assertNotEquals("clicked", button.getText().toLowerCase());

        new Actions(getDriver()).moveToElement(button, 5, 5).click().perform();

        assertEquals("clicked", button.getText().toLowerCase());
    }

    private void assertHeaderCount(int count) {
        assertEquals("header count", count, getGridElement().getHeaderCount());
    }

    private boolean hasClassName(TestBenchElement element, String name) {
        return Arrays.asList(element.getAttribute("class").split(" "))
                .contains(name);
    }
}
