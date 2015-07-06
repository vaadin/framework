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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.components.grid.basicfeatures.element.CustomGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public abstract class GridBasicFeaturesTest extends MultiBrowserTest {

    public enum CellSide {
        LEFT, RIGHT;
    }

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    protected Class<?> getUIClass() {
        return GridBasicFeatures.class;
    }

    protected CustomGridElement getGridElement() {
        return ((TestBenchElement) findElement(By.id("testComponent")))
                .wrap(CustomGridElement.class);
    }

    protected void scrollGridVerticallyTo(double px) {
        executeScript("arguments[0].scrollTop = " + px,
                getGridVerticalScrollbar());
    }

    protected void scrollGridHorizontallyTo(double px) {
        executeScript("arguments[0].scrollLeft = " + px,
                getGridHorizontalScrollbar());
    }

    protected int getGridVerticalScrollPos() {
        return ((Number) executeScript("return arguments[0].scrollTop",
                getGridVerticalScrollbar())).intValue();
    }

    protected List<TestBenchElement> getGridHeaderRowCells() {
        List<TestBenchElement> headerCells = new ArrayList<TestBenchElement>();
        for (int i = 0; i < getGridElement().getHeaderCount(); ++i) {
            headerCells.addAll(getGridElement().getHeaderCells(i));
        }
        return headerCells;
    }

    protected List<TestBenchElement> getGridFooterRowCells() {
        List<TestBenchElement> footerCells = new ArrayList<TestBenchElement>();
        for (int i = 0; i < getGridElement().getFooterCount(); ++i) {
            footerCells.addAll(getGridElement().getFooterCells(i));
        }
        return footerCells;
    }

    protected WebElement getEditor() {
        List<WebElement> elems = getGridElement().findElements(
                By.className("v-grid-editor"));

        assertLessThanOrEqual("number of editors", elems.size(), 1);

        return elems.isEmpty() ? null : elems.get(0);
    }

    private Object executeScript(String script, WebElement element) {
        final WebDriver driver = getDriver();
        if (driver instanceof JavascriptExecutor) {
            final JavascriptExecutor je = (JavascriptExecutor) driver;
            return je.executeScript(script, element);
        } else {
            throw new IllegalStateException("current driver "
                    + getDriver().getClass().getName() + " is not a "
                    + JavascriptExecutor.class.getSimpleName());
        }
    }

    protected WebElement getGridVerticalScrollbar() {
        return getDriver()
                .findElement(
                        By.xpath("//div[contains(@class, \"v-grid-scroller-vertical\")]"));
    }

    protected WebElement getGridHorizontalScrollbar() {
        return getDriver()
                .findElement(
                        By.xpath("//div[contains(@class, \"v-grid-scroller-horizontal\")]"));
    }

    /**
     * Reloads the page without restartApplication. This occasionally breaks
     * stuff.
     */
    protected void reopenTestURL() {
        String testUrl = getTestUrl();
        testUrl = testUrl.replace("?restartApplication", "?");
        testUrl = testUrl.replace("?&", "?");
        driver.get(testUrl);
    }

    protected void focusCell(int row, int column) {
        getGridElement().getCell(row, column).click();
    }

    protected void setFrozenColumns(int numberOfFrozenColumns) {
        selectMenuPath("Component", "State", "Frozen column count",
                Integer.toString(numberOfFrozenColumns));
    }

    protected void assertColumnHeaderOrder(int... indices) {
        List<TestBenchElement> headers = getGridHeaderRowCells();
        for (int i = 0; i < indices.length; i++) {
            assertColumnHeader("Column " + indices[i], headers.get(i));
        }
    }

    protected void assertColumnHeader(String expectedHeaderCaption,
            TestBenchElement testBenchElement) {
        assertEquals(expectedHeaderCaption.toLowerCase(), testBenchElement
                .getText().toLowerCase());
    }

    protected GridCellElement getDefaultColumnHeader(int index) {
        List<GridCellElement> headerRowCells = getGridElement().getHeaderCells(
                0);
        return headerRowCells.get(index);
    }

    protected void dragAndDropDefaultColumnHeader(int draggedColumnHeaderIndex,
            int onTopOfColumnHeaderIndex, CellSide cellSide) {
        GridCellElement columnHeader = getDefaultColumnHeader(onTopOfColumnHeaderIndex);
        new Actions(getDriver())
                .clickAndHold(getDefaultColumnHeader(draggedColumnHeaderIndex))
                .moveToElement(
                        columnHeader,
                        getHorizontalOffsetForDragAndDrop(columnHeader,
                                cellSide), 0).release().perform();
    }

    private int getHorizontalOffsetForDragAndDrop(GridCellElement columnHeader,
            CellSide cellSide) {
        if (cellSide == CellSide.LEFT) {
            return 5;
        } else {
            int half = columnHeader.getSize().getWidth() / 2;
            return half + (half / 2);
        }
    }

    protected void dragAndDropColumnHeader(int headerRow,
            int draggedColumnHeaderIndex, int onTopOfColumnHeaderIndex,
            CellSide cellSide) {
        GridCellElement headerCell = getGridElement().getHeaderCell(headerRow,
                onTopOfColumnHeaderIndex);
        new Actions(getDriver())
                .clickAndHold(
                        getGridElement().getHeaderCell(headerRow,
                                draggedColumnHeaderIndex))
                .moveToElement(
                        headerCell,
                        getHorizontalOffsetForDragAndDrop(headerCell, cellSide),
                        0).release().perform();
    }

    protected void dragAndDropColumnHeader(int headerRow,
            int draggedColumnHeaderIndex, int onTopOfColumnHeaderIndex,
            int horizontalOffset) {
        GridCellElement headerCell = getGridElement().getHeaderCell(headerRow,
                onTopOfColumnHeaderIndex);
        new Actions(getDriver())
                .clickAndHold(
                        getGridElement().getHeaderCell(headerRow,
                                draggedColumnHeaderIndex))
                .moveToElement(headerCell, horizontalOffset, 0).release()
                .perform();
    }

    protected void assertColumnIsSorted(int index) {
        WebElement columnHeader = getDefaultColumnHeader(index);
        assertTrue(columnHeader.getAttribute("class").contains("sort"));
    }

    protected void assertFocusedCell(int row, int column) {
        assertTrue(getGridElement().getCell(row, column).getAttribute("class")
                .contains("focused"));
    }

    protected WebElement getSidebar() {
        List<WebElement> elements = findElements(By.className("v-grid-sidebar"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    protected WebElement getSidebarOpenButton() {
        List<WebElement> elements = findElements(By
                .className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    /**
     * Returns the toggle inside the sidebar for hiding the column at the given
     * index, or null if not found.
     */
    protected WebElement getColumnHidingToggle(int columnIndex) {
        WebElement sidebar = getSidebar();
        List<WebElement> elements = sidebar.findElements(By
                .className("column-hiding-toggle"));
        for (WebElement e : elements) {
            if ((e.getText().toLowerCase()).startsWith("column " + columnIndex)) {
                return e;
            }
        }
        return null;
    }
}
