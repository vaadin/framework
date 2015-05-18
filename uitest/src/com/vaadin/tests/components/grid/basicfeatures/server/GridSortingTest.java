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
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridSortingTest extends GridBasicFeaturesTest {

    private static class SortInfo {
        public final int sortOrder;
        public final SortDirection sortDirection;

        private SortInfo(int sortOrder, SortDirection sortDirection) {
            this.sortOrder = sortOrder;
            this.sortDirection = sortDirection;
        }
    }

    private static class SortInfoWithColumn extends SortInfo {
        public final int columnIndex;

        private SortInfoWithColumn(int columnIndex, int sortOrder,
                SortDirection sortDirection) {
            super(sortOrder, sortDirection);
            this.columnIndex = columnIndex;
        }
    }

    private static SortInfo _(int sortOrder, SortDirection sortDirection) {
        return new SortInfo(sortOrder, sortDirection);
    }

    private static SortInfoWithColumn _(int columnIndex, int sortOrder,
            SortDirection sortDirection) {
        return new SortInfoWithColumn(columnIndex, sortOrder, sortDirection);
    }

    @Test
    public void testProgrammaticSorting() throws Exception {
        openTestURL();

        // Sorting by column 9 is sorting by row index that is represented as a
        // String.
        // First cells for first 3 rows are (9, 0), (99, 0) and (999, 0)
        sortBy("Column 9, DESC");
        assertLastSortIsUserOriginated(false);

        // Verify that programmatic sorting calls are identified as originating
        // from API
        assertColumnsAreSortedAs(_(9, 1, SortDirection.DESCENDING));

        String row = "";
        for (int i = 0; i < 3; ++i) {
            row += "9";
            String expected = "(" + row + ", 0)";
            String cellValue = getGridElement().getCell(i, 0).getText();
            assertEquals("Grid is not sorted by Column 9 "
                    + "using descending direction.", expected, cellValue);
        }

        // Column 10 is random numbers from Random with seed 13334
        sortBy("Column 10, ASC");

        assertFalse("Column 9 should no longer have the sort-desc stylename",
                getGridElement().getHeaderCell(0, 9).getAttribute("class")
                        .contains("sort-desc"));

        assertColumnsAreSortedAs(_(10, 1, SortDirection.ASCENDING));

        for (int i = 0; i < 5; ++i) {
            Integer firstRow = Integer.valueOf(getGridElement().getCell(i + 1,
                    10).getText());
            Integer secondRow = Integer.valueOf(getGridElement().getCell(i, 10)
                    .getText());
            assertGreater("Grid is not sorted by Column 10 using"
                    + " ascending direction", firstRow, secondRow);

        }

        // Column 7 is row index as a number. Last three row are original rows
        // 2, 1 and 0.
        sortBy("Column 7, DESC");
        for (int i = 0; i < 3; ++i) {
            String expected = "(" + i + ", 0)";
            String cellContent = getGridElement().getCell(
                    GridBasicFeatures.ROWS - (i + 1), 0).getText();
            assertEquals("Grid is not sorted by Column 7 using "
                    + "descending direction", expected, cellContent);
        }

        assertFalse("Column 10 should no longer have the sort-asc stylename",
                getGridElement().getHeaderCell(0, 10).getAttribute("class")
                        .contains("sort-asc"));

        assertColumnsAreSortedAs(_(7, 1, SortDirection.DESCENDING));
    }

    @Test
    public void testMouseSorting() throws Exception {
        setDebug(true);
        openTestURL();

        GridElement grid = getGridElement();

        selectMenuPath("Component", "Columns", "Column 9", "Column 9 Width",
                "Auto");

        // Sorting by column 9 is sorting by row index that is represented as a
        // String.

        // Click header twice to sort descending
        clickHeader(grid.getHeaderCell(0, 9));

        assertLastSortIsUserOriginated(true);

        assertColumnsAreSortedAs(_(9, 1, SortDirection.ASCENDING));
        clickHeader(grid.getHeaderCell(0, 9));
        assertColumnsAreSortedAs(_(9, 1, SortDirection.DESCENDING));

        // First cells for first 3 rows are (9, 0), (99, 0) and (999, 0)
        String row = "";
        for (int i = 0; i < 3; ++i) {
            row += "9";
            String expected = "(" + row + ", 0)";
            String actual = grid.getCell(i, 0).getText();
            assertEquals("Grid is not sorted by Column 9"
                    + " using descending direction.", expected, actual);
        }

        selectMenuPath("Component", "Columns", "Column 10", "Column 10 Width",
                "Auto");
        // Column 10 is random numbers from Random with seed 13334
        // Click header to sort ascending
        clickHeader(grid.getHeaderCell(0, 10));
        assertColumnsAreSortedAs(_(10, 1, SortDirection.ASCENDING));

        for (int i = 0; i < 5; ++i) {
            Integer firstRow = Integer.valueOf(grid.getCell(i + 1, 10)
                    .getText());
            Integer secondRow = Integer.valueOf(grid.getCell(i, 10).getText());
            assertGreater(
                    "Grid is not sorted by Column 10 using ascending direction",
                    firstRow, secondRow);

        }

        selectMenuPath("Component", "Columns", "Column 7", "Column 7 Width",
                "Auto");
        // Column 7 is row index as a number. Last three row are original rows
        // 2, 1 and 0.
        // Click header twice to sort descending
        clickHeader(grid.getHeaderCell(0, 7));
        assertColumnsAreSortedAs(_(7, 1, SortDirection.ASCENDING));
        clickHeader(grid.getHeaderCell(0, 7));
        assertColumnsAreSortedAs(_(7, 1, SortDirection.DESCENDING));

        for (int i = 0; i < 3; ++i) {
            assertEquals(
                    "Grid is not sorted by Column 7 using descending direction",
                    "(" + i + ", 0)",
                    grid.getCell(GridBasicFeatures.ROWS - (i + 1), 0).getText());
        }

    }

    private void clickHeader(GridCellElement headerCell) {
        new Actions(getDriver()).moveToElement(headerCell, 5, 5).click()
                .perform();
    }

    private void sendKey(Keys seq) {
        new Actions(getDriver()).sendKeys(seq).perform();
    }

    private void holdKey(Keys key) {
        new Actions(getDriver()).keyDown(key).perform();
    }

    private void releaseKey(Keys key) {
        new Actions(getDriver()).keyUp(key).perform();
    }

    @Test
    public void testKeyboardSortingMultipleHeaders() {
        openTestURL();
        selectMenuPath("Component", "Header", "Append row");

        // Sort according to first column by clicking
        getGridElement().getHeaderCell(0, 0).click();
        assertColumnIsSorted(0);

        // Try to sort according to second column by pressing enter on the new
        // header
        sendKey(Keys.ARROW_RIGHT);
        sendKey(Keys.ARROW_DOWN);
        sendKey(Keys.ENTER);

        // Should not have sorted
        assertColumnIsSorted(0);

        // Sort using default header
        sendKey(Keys.ARROW_UP);
        sendKey(Keys.ENTER);

        // Should have sorted
        assertColumnIsSorted(1);

    }

    @Test
    public void testKeyboardSorting() {
        openTestURL();

        /*
         * We can't click on the header directly, since it will sort the header
         * immediately. We need to focus some other column first, and only then
         * navigate there.
         */
        getGridElement().getCell(0, 0).click();
        sendKey(Keys.ARROW_UP);

        // Sort ASCENDING on first column
        sendKey(Keys.ENTER);
        assertLastSortIsUserOriginated(true);
        assertColumnsAreSortedAs(_(1, SortDirection.ASCENDING));

        // Move to next column
        sendKey(Keys.RIGHT);

        // Add this column to the existing sorting group
        holdKey(Keys.SHIFT);
        sendKey(Keys.ENTER);
        releaseKey(Keys.SHIFT);
        assertColumnsAreSortedAs(_(1, SortDirection.ASCENDING),
                _(2, SortDirection.ASCENDING));

        // Move to next column
        sendKey(Keys.RIGHT);

        // Add a third column to the sorting group
        holdKey(Keys.SHIFT);
        sendKey(Keys.ENTER);
        releaseKey(Keys.SHIFT);
        assertColumnsAreSortedAs(_(1, SortDirection.ASCENDING),
                _(2, SortDirection.ASCENDING), _(3, SortDirection.ASCENDING));

        // Move back to the second column
        sendKey(Keys.LEFT);

        // Change sort direction of the second column to DESCENDING
        holdKey(Keys.SHIFT);
        sendKey(Keys.ENTER);
        releaseKey(Keys.SHIFT);
        assertColumnsAreSortedAs(_(1, SortDirection.ASCENDING),
                _(2, SortDirection.DESCENDING), _(3, SortDirection.ASCENDING));

        // Move back to the third column
        sendKey(Keys.RIGHT);

        // Set sorting to third column, ASCENDING
        sendKey(Keys.ENTER);
        assertColumnsAreSortedAs(_(2, 1, SortDirection.ASCENDING));

        // Move to the fourth column
        sendKey(Keys.RIGHT);

        // Make sure that single-column sorting also works as expected
        sendKey(Keys.ENTER);
        assertColumnsAreSortedAs(_(3, 1, SortDirection.ASCENDING));

    }

    private void assertColumnsAreSortedAs(SortInfoWithColumn... sortInfos) {
        for (SortInfoWithColumn sortInfo : sortInfos) {
            assertSort(sortInfo, sortInfo.columnIndex,
                    onlyOneColumnIsSorted(sortInfos));
        }
    }

    /**
     * @param sortDirections
     *            <code>null</code> if not interested in that index, otherwise a
     *            direction that the column needs to be sorted as
     */
    private void assertColumnsAreSortedAs(SortInfo... sortInfos) {
        for (int column = 0; column < sortInfos.length; column++) {
            SortInfo sortInfo = sortInfos[column];
            assertSort(sortInfo, column, onlyOneColumnIsSorted(sortInfos));
        }
    }

    private void assertSort(SortInfo sortInfo, int column,
            boolean onlyOneColumnIsSorted) {
        if (sortInfo == null) {
            return;
        }

        GridCellElement headerCell = getGridElement().getHeaderCell(0, column);
        String classValue = headerCell.getAttribute("class");

        boolean isSortedAscending = sortInfo.sortDirection == SortDirection.ASCENDING
                && classValue.contains("sort-asc");
        boolean isSortedDescending = sortInfo.sortDirection == SortDirection.DESCENDING
                && classValue.contains("sort-desc");

        if (isSortedAscending || isSortedDescending) {
            String sortOrderAttribute = headerCell.getAttribute("sort-order");

            if (sortOrderAttribute == null) {
                if (!(sortInfo.sortOrder == 1 && onlyOneColumnIsSorted)) {
                    fail("missing sort-order element attribute from column "
                            + column);
                }
            } else {
                assertEquals("sort order was not as expected",
                        String.valueOf(sortInfo.sortOrder), sortOrderAttribute);
            }
        } else {
            fail("column index " + column + " was not sorted as "
                    + sortInfo.sortDirection + " (class: " + classValue + ")");
        }
    }

    private static boolean onlyOneColumnIsSorted(SortInfo[] sortInfos) {

        boolean foundSortedColumn = false;
        for (SortInfo sortInfo : sortInfos) {
            if (sortInfo == null) {
                continue;
            }

            if (!foundSortedColumn) {
                foundSortedColumn = true;
            } else {
                // two columns were sorted
                return false;
            }
        }
        return foundSortedColumn;
    }

    private void sortBy(String column) {
        selectMenuPath("Component", "State", "Sort by column", column);
    }

    private void assertLastSortIsUserOriginated(boolean isUserOriginated) {
        List<WebElement> userOriginatedMessages = getDriver()
                .findElements(
                        By.xpath("//*[contains(text(),'SortOrderChangeEvent: isUserOriginated')]"));

        Collections.sort(userOriginatedMessages, new Comparator<WebElement>() {
            @Override
            public int compare(WebElement o1, WebElement o2) {
                return o1.getText().compareTo(o2.getText());
            }
        });

        String newestEntry = userOriginatedMessages.get(
                userOriginatedMessages.size() - 1).getText();

        String[] parts = newestEntry.split(" ");
        boolean wasUserOriginated = Boolean
                .parseBoolean(parts[parts.length - 1]);
        if (isUserOriginated) {
            assertTrue("expected the sort to be user originated, but wasn't",
                    wasUserOriginated);
        } else {
            assertFalse(
                    "expected the sort not to be user originated, but it was",
                    wasUserOriginated);
        }
    }
}
