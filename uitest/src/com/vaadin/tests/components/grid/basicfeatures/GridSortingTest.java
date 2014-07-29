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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.components.grid.GridElement;

public class GridSortingTest extends GridBasicFeaturesTest {

    @Test
    public void testProgrammaticSorting() throws IOException {
        openTestURL();

        GridElement grid = getGridElement();

        // Sorting by column 9 is sorting by row index that is represented as a
        // String.
        // First cells for first 3 rows are (9, 0), (99, 0) and (999, 0)
        sortBy("Column 9, DESC");

        assertTrue("Column 9 should have the sort-desc stylename", grid
                .getHeaderCell(0, 9).getAttribute("class")
                .contains("sort-desc"));

        String row = "";
        for (int i = 0; i < 3; ++i) {
            row += "9";
            assertEquals(
                    "Grid is not sorted by Column 9 using descending direction.",
                    "(" + row + ", 0)", grid.getCell(i, 0).getText());
        }

        // Column 10 is random numbers from Random with seed 13334
        sortBy("Column 10, ASC");

        assertFalse(
                "Column 9 should no longer have the sort-desc stylename",
                grid.getHeaderCell(0, 9).getAttribute("class")
                        .contains("sort-desc"));
        assertTrue("Column 10 should have the sort-asc stylename", grid
                .getHeaderCell(0, 10).getAttribute("class")
                .contains("sort-asc"));

        // Not cleaning up correctly causes exceptions when scrolling.
        grid.scrollToRow(50);
        assertFalse("Scrolling caused and exception when shuffled.",
                getLogRow(0).contains("Exception"));

        for (int i = 0; i < 5; ++i) {
            assertGreater(
                    "Grid is not sorted by Column 10 using ascending direction",
                    Integer.parseInt(grid.getCell(i + 1, 10).getText()),
                    Integer.parseInt(grid.getCell(i, 10).getText()));

        }

        // Column 7 is row index as a number. Last three row are original rows
        // 2, 1 and 0.
        sortBy("Column 7, DESC");
        for (int i = 0; i < 3; ++i) {
            assertEquals(
                    "Grid is not sorted by Column 7 using descending direction",
                    "(" + i + ", 0)",
                    grid.getCell(GridBasicFeatures.ROWS - (i + 1), 0).getText());
        }

        assertFalse(
                "Column 10 should no longer have the sort-asc stylename",
                grid.getHeaderCell(0, 10).getAttribute("class")
                        .contains("sort-asc"));
        assertTrue("Column 7 should have the sort-desc stylename", grid
                .getHeaderCell(0, 7).getAttribute("class")
                .contains("sort-desc"));

    }

    @Test
    public void testUserSorting() throws InterruptedException {
        openTestURL();

        GridElement grid = getGridElement();

        // Sorting by column 9 is sorting by row index that is represented as a
        // String.
        // First cells for first 3 rows are (9, 0), (99, 0) and (999, 0)

        // Click header twice to sort descending
        grid.getHeaderCell(0, 9).click();
        grid.getHeaderCell(0, 9).click();
        String row = "";
        for (int i = 0; i < 3; ++i) {
            row += "9";
            assertEquals(
                    "Grid is not sorted by Column 9 using descending direction.",
                    "(" + row + ", 0)", grid.getCell(i, 0).getText());
        }

        assertEquals("2. Sort order: [Column 9 ASCENDING]", getLogRow(2));
        assertEquals("4. Sort order: [Column 9 DESCENDING]", getLogRow(0));

        // Column 10 is random numbers from Random with seed 13334
        // Click header to sort ascending
        grid.getHeaderCell(0, 10).click();

        assertEquals("6. Sort order: [Column 10 ASCENDING]", getLogRow(0));

        // Not cleaning up correctly causes exceptions when scrolling.
        grid.scrollToRow(50);
        assertFalse("Scrolling caused and exception when shuffled.",
                getLogRow(0).contains("Exception"));

        for (int i = 0; i < 5; ++i) {
            assertGreater(
                    "Grid is not sorted by Column 10 using ascending direction",
                    Integer.parseInt(grid.getCell(i + 1, 10).getText()),
                    Integer.parseInt(grid.getCell(i, 10).getText()));

        }

        // Column 7 is row index as a number. Last three row are original rows
        // 2, 1 and 0.
        // Click header twice to sort descending
        grid.getHeaderCell(0, 7).click();
        grid.getHeaderCell(0, 7).click();
        for (int i = 0; i < 3; ++i) {
            assertEquals(
                    "Grid is not sorted by Column 7 using descending direction",
                    "(" + i + ", 0)",
                    grid.getCell(GridBasicFeatures.ROWS - (i + 1), 0).getText());
        }

        assertEquals("9. Sort order: [Column 7 ASCENDING]", getLogRow(3));
        assertEquals("11. Sort order: [Column 7 DESCENDING]", getLogRow(1));
    }

    @Test
    public void testUserMultiColumnSorting() {
        openTestURL();

        getGridElement().getHeaderCell(0, 0).click();
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        getGridElement().getHeaderCell(0, 11).click();
        new Actions(driver).keyUp(Keys.SHIFT).perform();

        String prev = getGridElement().getCell(0, 11).getAttribute("innerHTML");
        for (int i = 1; i <= 6; ++i) {
            assertEquals("Column 11 should contain same values.", prev,
                    getGridElement().getCell(i, 11).getAttribute("innerHTML"));
        }

        prev = getGridElement().getCell(0, 0).getText();
        for (int i = 1; i <= 6; ++i) {
            assertTrue(
                    "Grid is not sorted by column 0.",
                    prev.compareTo(getGridElement().getCell(i, 0).getText()) < 0);
        }

    }

    private void sortBy(String column) {
        selectMenuPath("Component", "State", "Sort by column", column);
    }
}
