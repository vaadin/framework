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
package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

/**
 * TestBench Element API for Grid
 * 
 * @since
 * @author Vaadin Ltd
 */
@ServerClass("com.vaadin.ui.Grid")
public class GridElement extends AbstractComponentElement {

    public static class GridCellElement extends AbstractElement {

        // TODO static final?
        // TODO rename "active" to "focused" once Valo CSS is merged
        private String FOCUSED_CELL_CLASS_NAME = "-cell-active";
        private String FROZEN_CLASS_NAME = "frozen";

        public boolean isFocused() {
            return getAttribute("class").contains(FOCUSED_CELL_CLASS_NAME);
        }

        public boolean isFrozen() {
            return getAttribute("class").contains(FROZEN_CLASS_NAME);
        }
    }

    public static class GridRowElement extends AbstractElement {

        // TODO static final?
        // TODO rename "active" to "focused" once Valo CSS is merged
        private String FOCUSED_CLASS_NAME = "-row-active";
        private String SELECTED_CLASS_NAME = "-row-selected";

        public boolean isFocused() {
            return getAttribute("class").contains(FOCUSED_CLASS_NAME);
        }

        @Override
        public boolean isSelected() {
            return getAttribute("class").contains(SELECTED_CLASS_NAME);
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
    public WebElement getHeaderRow(int rowIndex) {
        return getSubPart("#header[" + rowIndex + "]");
    }

    /**
     * Get a footer row by index
     * 
     * @param rowIndex
     *            Row index
     * @return The tr element of the row
     */
    public WebElement getFooterRow(int rowIndex) {
        return getSubPart("#footer[" + rowIndex + "]");
    }

    /**
     * Get the vertical scroll element
     * 
     * @return The element representing the vertical scrollbar
     */
    public WebElement getVerticalScroller() {
        List<WebElement> rootElements = findElements(By.xpath("./div"));
        return rootElements.get(0);
    }

    /**
     * Get the horizontal scroll element
     * 
     * @return The element representing the horizontal scrollbar
     */
    public WebElement getHorizontalScroller() {
        List<WebElement> rootElements = findElements(By.xpath("./div"));
        return rootElements.get(1);
    }

    /**
     * Get the header element
     * 
     * @return The thead element
     */
    public WebElement getHeader() {
        return getSubPart("#header");
    }

    /**
     * Get the body element
     * 
     * @return the tbody element
     */
    public WebElement getBody() {
        return getSubPart("#cell");
    }

    /**
     * Get the footer element
     * 
     * @return the tfoot element
     */
    public WebElement getFooter() {
        return getSubPart("#footer");
    }

    /**
     * Get the element wrapping the table element
     * 
     * @return The element that wraps the table element
     */
    public WebElement getTableWrapper() {
        List<WebElement> rootElements = findElements(By.xpath("./div"));
        return rootElements.get(2);
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

}
