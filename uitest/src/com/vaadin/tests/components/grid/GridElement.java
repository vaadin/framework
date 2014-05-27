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
package com.vaadin.tests.components.grid;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.ServerClass;

/**
 * TestBench Element API for Grid
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
@ServerClass("com.vaadin.ui.components.grid.Grid")
public class GridElement extends AbstractComponentElement {

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
    public TestBenchElement getCell(int rowIndex, int colIndex) {
        scrollToRow(rowIndex);
        return getSubPart("#cell[" + rowIndex + "][" + colIndex + "]");
    }

    /**
     * Gets row element with given row index.
     * 
     * @param index
     *            Row index
     * @return Row element with given index.
     */
    public TestBenchElement getRow(int index) {
        scrollToRow(index);
        return getSubPart("#cell[" + index + "]");
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
    public TestBenchElement getHeaderCell(int rowIndex, int colIndex) {
        return getSubPart("#header[" + rowIndex + "][" + colIndex + "]");
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
    public TestBenchElement getFooterCell(int rowIndex, int colIndex) {
        return getSubPart("#footer[" + rowIndex + "][" + colIndex + "]");
    }

    /**
     * Gets list of header cell elements on given row.
     * 
     * @param rowIndex
     *            Row index
     * @return Header cell elements on given row.
     */
    public List<TestBenchElement> getHeaderCells(int rowIndex) {
        return TestBenchElement.wrapElements(
                getSubPart("#header[" + rowIndex + "]").findElements(
                        By.xpath("./th")), getTestBenchCommandExecutor());
    }

    /**
     * Gets list of header cell elements on given row.
     * 
     * @param rowIndex
     *            Row index
     * @return Header cell elements on given row.
     */
    public List<TestBenchElement> getFooterCells(int rowIndex) {
        return TestBenchElement.wrapElements(
                getSubPart("#footer[" + rowIndex + "]").findElements(
                        By.xpath("./td")), getTestBenchCommandExecutor());
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
