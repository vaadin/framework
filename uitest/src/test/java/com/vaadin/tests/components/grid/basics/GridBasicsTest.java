package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.customelements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest.CellSide;

/**
 * Base class for all {@link GridBasics} UI tests
 */
public abstract class GridBasicsTest extends MultiBrowserTest {

    /* Identical List of test data */
    private List<DataObject> testData;

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Most tests are run with only one browser.
        return getBrowserCapabilities(Browser.PHANTOMJS);
    }

    @Override
    protected Class<?> getUIClass() {
        return GridBasics.class;
    }

    @Before
    public void setUp() {
        openTestURL();
        testData = DataObject.generateObjects();
    }

    protected GridElement getGridElement() {
        return $(GridElement.class).first();
    }

    protected List<TestBenchElement> getGridHeaderRowCells() {
        List<TestBenchElement> headerCells = new ArrayList<>();
        for (int i = 0; i < getGridElement().getHeaderCount(); ++i) {
            headerCells.addAll(getGridElement().getHeaderCells(i));
        }
        return headerCells;
    }

    protected Stream<DataObject> getTestData() {
        return testData.stream();
    }

    protected void scrollGridVerticallyTo(double px) {
        executeScript("arguments[0].scrollTop = " + px,
                getGridVerticalScrollbar());
    }

    protected void scrollGridHorizontallyTo(double px) {
        executeScript("arguments[0].scrollLeft = " + px,
                getGridHorizontalScrollbar());
    }

    protected WebElement getGridVerticalScrollbar() {
        return getDriver().findElement(By.xpath(
                "//div[contains(@class, \"v-grid-scroller-vertical\")]"));
    }

    protected WebElement getGridHorizontalScrollbar() {
        return getDriver().findElement(By.xpath(
                "//div[contains(@class, \"v-grid-scroller-horizontal\")]"));
    }

    protected GridCellElement getDefaultColumnHeader(int index) {
        List<GridCellElement> headerRowCells = getGridElement()
                .getHeaderCells(0);
        return headerRowCells.get(index);
    }

    protected void dragAndDropDefaultColumnHeader(int draggedColumnHeaderIndex,
            int onTopOfColumnHeaderIndex, CellSide cellSide) {
        GridCellElement columnHeader = getDefaultColumnHeader(
                onTopOfColumnHeaderIndex);
        new Actions(getDriver())
                .clickAndHold(getDefaultColumnHeader(draggedColumnHeaderIndex))
                .moveToElement(columnHeader, getHorizontalOffsetForDragAndDrop(
                        columnHeader, cellSide), 0)
                .release().perform();
    }

    protected void dragAndDropColumnHeader(int headerRow,
            int draggedColumnHeaderIndex, int onTopOfColumnHeaderIndex,
            CellSide cellSide) {
        GridCellElement headerCell = getGridElement().getHeaderCell(headerRow,
                onTopOfColumnHeaderIndex);
        new Actions(getDriver())
                .clickAndHold(getGridElement().getHeaderCell(headerRow,
                        draggedColumnHeaderIndex))
                .moveToElement(headerCell,
                        getHorizontalOffsetForDragAndDrop(headerCell, cellSide),
                        0)
                .release().perform();
    }

    protected void dragAndDropColumnHeader(int headerRow,
            int draggedColumnHeaderIndex, int onTopOfColumnHeaderIndex,
            int horizontalOffset) {
        GridCellElement headerCell = getGridElement().getHeaderCell(headerRow,
                onTopOfColumnHeaderIndex);
        new Actions(getDriver())
                .clickAndHold(getGridElement().getHeaderCell(headerRow,
                        draggedColumnHeaderIndex))
                .moveToElement(headerCell, horizontalOffset, 0).release()
                .perform();
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

    protected void toggleColumnReorder() {
        selectMenuPath("Component", "State", "Column Reordering");
    }

    protected void assertColumnHeaderOrder(int... indices) {
        List<TestBenchElement> headers = getGridHeaderRowCells();
        for (int i = 0; i < indices.length; i++) {
            assertColumnHeader("HEADER CELL " + indices[i], headers.get(i));
        }
    }

    protected void assertColumnHeader(String expectedHeaderCaption,
            TestBenchElement testBenchElement) {
        assertEquals(expectedHeaderCaption.toLowerCase(),
                testBenchElement.getText().toLowerCase());
    }

    protected void assertColumnIsSorted(int index) {
        WebElement columnHeader = getDefaultColumnHeader(index);
        assertTrue(columnHeader.getAttribute("class").contains("sort"));
    }

    protected void assertFocusedCell(int row, int column) {
        assertTrue(getGridElement().getCell(row, column).getAttribute("class")
                .contains("focused"));
    }

    protected void focusCell(int row, int column) {
        getGridElement().getCell(row, column).click();
    }

}
