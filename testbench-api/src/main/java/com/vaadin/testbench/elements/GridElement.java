/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * TestBench Element API for Grid
 * 
 * @since
 * @author Vaadin Ltd
 */
@ServerClass("com.vaadin.ui.Grid")
public class GridElement extends AbstractComponentElement {

    public static class GridCellElement extends AbstractElement {

        private static final String FOCUSED_CELL_CLASS_NAME = "-cell-focused";
        private static final String FROZEN_CLASS_NAME = "frozen";

        public boolean isFocused() {
            return getAttribute("class").contains(FOCUSED_CELL_CLASS_NAME);
        }

        public boolean isFrozen() {
            return getAttribute("class").contains(FROZEN_CLASS_NAME);
        }
    }

    public static class GridRowElement extends AbstractElement {

        private static final String FOCUSED_CLASS_NAME = "-row-focused";
        private static final String SELECTED_CLASS_NAME = "-row-selected";

        public boolean isFocused() {
            return getAttribute("class").contains(FOCUSED_CLASS_NAME);
        }

        @Override
        public boolean isSelected() {
            return getAttribute("class").contains(SELECTED_CLASS_NAME);
        }

        public GridCellElement getCell(int columnIndex) {
            TestBenchElement e = (TestBenchElement) findElement(
                    By.xpath("./td[" + (columnIndex + 1) + "]"));
            return e.wrap(GridCellElement.class);
        }
    }

    public static class GridEditorElement extends AbstractElement {

        private GridElement grid;

        private GridEditorElement setGrid(GridElement grid) {
            this.grid = grid;
            return this;
        }

        /**
         * Gets the editor field for column in given index.
         * 
         * @param colIndex
         *            the column index
         * @return the editor field for given location
         * 
         * @throws NoSuchElementException
         *             if {@code isEditable(colIndex) == false}
         */
        public TestBenchElement getField(int colIndex) {
            return grid.getSubPart("#editor[" + colIndex + "]");
        }

        /**
         * Gets whether the column with the given index is editable, that is,
         * has an associated editor field.
         * 
         * @param colIndex
         *            the column index
         * @return {@code true} if the column has an editor field, {@code false}
         *         otherwise
         */
        public boolean isEditable(int colIndex) {
            return grid
                    .isElementPresent(By.vaadin("#editor[" + colIndex + "]"));
        }

        /**
         * Checks whether a field is marked with an error.
         * 
         * @param colIndex
         *            column index
         * @return <code>true</code> iff the field is marked with an error
         */
        public boolean isFieldErrorMarked(int colIndex) {
            return getField(colIndex).getAttribute("class").contains("error");
        }

        /**
         * Saves the fields of this editor.
         * <p>
         * <em>Note:</em> that this closes the editor making this element
         * useless.
         */
        public void save() {
            findElement(By.className("v-grid-editor-save")).click();
        }

        /**
         * Cancels this editor.
         * <p>
         * <em>Note:</em> that this closes the editor making this element
         * useless.
         */
        public void cancel() {
            findElement(By.className("v-grid-editor-cancel")).click();
        }

        /**
         * Gets the error message text, or <code>null</code> if no message is
         * present.
         */
        public String getErrorMessage() {
            WebElement messageWrapper = findElement(By
                    .className("v-grid-editor-message"));
            List<WebElement> divs = messageWrapper.findElements(By
                    .tagName("div"));
            if (divs.isEmpty()) {
                return null;
            } else {
                return divs.get(0).getText();
            }
        }
    }

    /**
     * Scrolls Grid element so that wanted row is displayed
     * 
     * @param index
     *            Target row
     */
    public void scrollToRow(int index) {
        try {
            getSubPart("#cell[" + index + "]");
        } catch (NoSuchElementException e) {
            // Expected, ignore it.
        }
    }

    /**
     * Gets cell element with given row and column index.
     * 
     * @param rowIndex
     *            Row index
     * @param colIndex
     *            Column index
     * @return Cell element with given indices.
     */
    public GridCellElement getCell(int rowIndex, int colIndex) {
        scrollToRow(rowIndex);
        return getSubPart("#cell[" + rowIndex + "][" + colIndex + "]").wrap(
                GridCellElement.class);
    }

    /**
     * Gets row element with given row index.
     * 
     * @param index
     *            Row index
     * @return Row element with given index.
     */
    public GridRowElement getRow(int index) {
        scrollToRow(index);
        return getSubPart("#cell[" + index + "]").wrap(GridRowElement.class);
    }

    /**
     * Gets header cell element with given row and column index.
     * 
     * @param rowIndex
     *            Row index
     * @param colIndex
     *            Column index
     * @return Header cell element with given indices.
     */
    public GridCellElement getHeaderCell(int rowIndex, int colIndex) {
        return getSubPart("#header[" + rowIndex + "][" + colIndex + "]").wrap(
                GridCellElement.class);
    }

    /**
     * Gets footer cell element with given row and column index.
     * 
     * @param rowIndex
     *            Row index
     * @param colIndex
     *            Column index
     * @return Footer cell element with given indices.
     */
    public GridCellElement getFooterCell(int rowIndex, int colIndex) {
        return getSubPart("#footer[" + rowIndex + "][" + colIndex + "]").wrap(
                GridCellElement.class);
    }

    /**
     * Gets list of header cell elements on given row.
     * 
     * @param rowIndex
     *            Row index
     * @return Header cell elements on given row.
     */
    public List<GridCellElement> getHeaderCells(int rowIndex) {
        List<GridCellElement> headers = new ArrayList<GridCellElement>();
        for (TestBenchElement e : TestBenchElement.wrapElements(
                getSubPart("#header[" + rowIndex + "]").findElements(
                        By.xpath("./th")), getCommandExecutor())) {
            headers.add(e.wrap(GridCellElement.class));
        }
        return headers;
    }

    /**
     * Gets list of header cell elements on given row.
     * 
     * @param rowIndex
     *            Row index
     * @return Header cell elements on given row.
     */
    public List<GridCellElement> getFooterCells(int rowIndex) {
        List<GridCellElement> footers = new ArrayList<GridCellElement>();
        for (TestBenchElement e : TestBenchElement.wrapElements(
                getSubPart("#footer[" + rowIndex + "]").findElements(
                        By.xpath("./td")), getCommandExecutor())) {
            footers.add(e.wrap(GridCellElement.class));
        }
        return footers;
    }

    /**
     * Get header row count
     * 
     * @return Header row count
     */
    public int getHeaderCount() {
        return getSubPart("#header").findElements(By.xpath("./tr")).size();
    }

    /**
     * Get footer row count
     * 
     * @return Footer row count
     */
    public int getFooterCount() {
        return getSubPart("#footer").findElements(By.xpath("./tr")).size();
    }

    /**
     * Get a header row by index
     * 
     * @param rowIndex
     *            Row index
     * @return The th element of the row
     */
    public TestBenchElement getHeaderRow(int rowIndex) {
        return getSubPart("#header[" + rowIndex + "]");
    }

    /**
     * Get a footer row by index
     * 
     * @param rowIndex
     *            Row index
     * @return The tr element of the row
     */
    public TestBenchElement getFooterRow(int rowIndex) {
        return getSubPart("#footer[" + rowIndex + "]");
    }

    /**
     * Get the vertical scroll element
     * 
     * @return The element representing the vertical scrollbar
     */
    public TestBenchElement getVerticalScroller() {
        List<WebElement> rootElements = findElements(By.xpath("./div"));
        return (TestBenchElement) rootElements.get(0);
    }

    /**
     * Get the horizontal scroll element
     * 
     * @return The element representing the horizontal scrollbar
     */
    public TestBenchElement getHorizontalScroller() {
        List<WebElement> rootElements = findElements(By.xpath("./div"));
        return (TestBenchElement) rootElements.get(1);
    }

    /**
     * Get the header element
     * 
     * @return The thead element
     */
    public TestBenchElement getHeader() {
        return getSubPart("#header");
    }

    /**
     * Get the body element
     * 
     * @return the tbody element
     */
    public TestBenchElement getBody() {
        return getSubPart("#cell");
    }

    /**
     * Get the footer element
     * 
     * @return the tfoot element
     */
    public TestBenchElement getFooter() {
        return getSubPart("#footer");
    }

    /**
     * Get the element wrapping the table element
     * 
     * @return The element that wraps the table element
     */
    public TestBenchElement getTableWrapper() {
        List<WebElement> rootElements = findElements(By.xpath("./div"));
        return (TestBenchElement) rootElements.get(2);
    }

    public GridEditorElement getEditor() {
        return getSubPart("#editor").wrap(GridEditorElement.class)
                .setGrid(this);
    }

    /**
     * Helper function to get Grid subparts wrapped correctly
     * 
     * @param subPartSelector
     *            SubPart to be used in ComponentLocator
     * @return SubPart element wrapped in TestBenchElement class
     */
    private TestBenchElement getSubPart(String subPartSelector) {
        return (TestBenchElement) findElement(By.vaadin(subPartSelector));
    }

    /**
     * Gets the element that contains the details of a row.
     * 
     * @since
     * @param rowIndex
     *            the index of the row for the details
     * @return the element that contains the details of a row. <code>null</code>
     *         if no widget is defined for the details row
     * @throws NoSuchElementException
     *             if the given details row is currently not open
     */
    public TestBenchElement getDetails(int rowIndex)
            throws NoSuchElementException {
        return getSubPart("#details[" + rowIndex + "]");
    }

    /**
     * Gets the total number of data rows in the grid.
     *
     * @return the number of data rows in the grid,
     */
    public long getRowCount() {
        Long res = (Long) getCommandExecutor()
                .executeScript("return arguments[0].getBodyRowCount()", this);
        if (res == null) {
            throw new IllegalStateException("getBodyRowCount returned null");
        }

        return res.longValue();
    }

    /**
     * Gets all the data rows in the grid.
     * <p>
     * Returns an iterable which will lazily scroll rows into views and lazy
     * load data as needed.
     *
     * @return an iterable of all the data rows in the grid.
     */
    public Iterable<GridRowElement> getRows() {
        return new Iterable<GridElement.GridRowElement>() {
            public Iterator<GridRowElement> iterator() {
                return new Iterator<GridElement.GridRowElement>() {
                    int nextIndex = 0;

                    public GridRowElement next() {
                        return getRow(nextIndex++);
                    }

                    public boolean hasNext() {
                        try {
                            getRow(nextIndex);
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }

                    public void remove() {
                        throw new UnsupportedOperationException(
                                "remove not supported");
                    }

                };
            }
        };
    }
}
