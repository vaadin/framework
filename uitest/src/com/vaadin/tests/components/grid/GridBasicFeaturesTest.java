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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridBasicFeaturesTest extends MultiBrowserTest {

    @Test
    public void testColumnHeaderCaptions() throws Exception {
        openTestURL();

        // Column headers should be visible
        List<TestBenchElement> cells = getGridHeaderRowCells();
        assertEquals(10, cells.size());
        assertEquals("Column0", cells.get(0).getText());
        assertEquals("Column1", cells.get(1).getText());
        assertEquals("Column2", cells.get(2).getText());
    }

    @Test
    public void testColumnFooterCaptions() throws Exception {
        openTestURL();

        // footer row should by default be hidden
        List<TestBenchElement> cells = getGridFooterRowCells();
        assertEquals(0, cells.size());

        // Open footer row
        selectMenuPath("Component", "Footers", "Visible");

        // Footers should now be visible
        cells = getGridFooterRowCells();
        assertEquals(10, cells.size());
        assertEquals("Footer 0", cells.get(0).getText());
        assertEquals("Footer 1", cells.get(1).getText());
        assertEquals("Footer 2", cells.get(2).getText());
    }

    @Test
    public void testColumnGroupHeaders() throws Exception {
        openTestURL();

        // Hide column headers for this test
        selectMenuPath("Component", "Headers", "Visible");

        List<TestBenchElement> cells = getGridHeaderRowCells();

        // header row should be empty
        assertEquals(0, cells.size());

        // add a group row
        selectMenuPath("Component", "Column groups", "Add group row");

        // Empty group row cells should be present
        cells = getGridHeaderRowCells();
        assertEquals(10, cells.size());

        // Group columns 0 & 1
        selectMenuPath("Component", "Column groups", "Column group row 1",
                "Group Column 0 & 1");

        cells = getGridHeaderRowCells();
        assertEquals("Column 0 & 1", cells.get(0).getText());
    }

    @Test
    public void testColumnGroupFooters() throws Exception {
        openTestURL();

        // add a group row
        selectMenuPath("Component", "Column groups", "Add group row");

        // Set footer visible
        selectMenuPath("Component", "Column groups", "Column group row 1",
                "Footer Visible");

        // Group columns 0 & 1
        selectMenuPath("Component", "Column groups", "Column group row 1",
                "Group Column 0 & 1");

        List<TestBenchElement> cells = getGridFooterRowCells();
        assertEquals("Column 0 & 1", cells.get(0).getText());
    }

    @Test
    public void testGroupingSameColumnsOnRowThrowsException() throws Exception {
        openTestURL();

        // add a group row
        selectMenuPath("Component", "Column groups", "Add group row");

        // Group columns 0 & 1
        selectMenuPath("Component", "Column groups", "Column group row 1",
                "Group Column 0 & 1");

        // Group columns 1 & 2 shoud fail
        selectMenuPath("Component", "Column groups", "Column group row 1",
                "Group Column 1 & 2");

        assertTrue(getLogRow(0)
                .contains(
                        "Exception occured, java.lang.IllegalArgumentExceptionColumn Column1 already belongs to another group."));
    }

    @Test
    public void testHidingColumn() throws Exception {
        openTestURL();

        // Column 0 should be visible
        List<TestBenchElement> cells = getGridHeaderRowCells();
        assertEquals("Column0", cells.get(0).getText());

        // Hide column 0
        selectMenuPath("Component", "Columns", "Column0", "Visible");

        // Column 1 should now be the first cell
        cells = getGridHeaderRowCells();
        assertEquals("Column1", cells.get(0).getText());
    }

    @Test
    public void testRemovingColumn() throws Exception {
        openTestURL();

        // Column 0 should be visible
        List<TestBenchElement> cells = getGridHeaderRowCells();
        assertEquals("Column0", cells.get(0).getText());

        // Hide column 0
        selectMenuPath("Component", "Columns", "Column0", "Remove");

        // Column 1 should now be the first cell
        cells = getGridHeaderRowCells();
        assertEquals("Column1", cells.get(0).getText());
    }

    @Test
    public void testDataLoadingAfterRowRemoval() throws Exception {
        openTestURL();

        // Remove columns 2,3,4
        selectMenuPath("Component", "Columns", "Column2", "Remove");
        selectMenuPath("Component", "Columns", "Column3", "Remove");
        selectMenuPath("Component", "Columns", "Column4", "Remove");

        // Scroll so new data is lazy loaded
        scrollGridVerticallyTo(1000);

        // Let lazy loading do its job
        sleep(1000);

        // Check that row is loaded
        assertThat(getBodyCellByRowAndColumn(11, 0).getText(), not("..."));
    }

    @Test
    public void testFreezingColumn() throws Exception {
        openTestURL();

        // Freeze column 2
        selectMenuPath("Component", "Columns", "Column2", "Freeze");

        WebElement cell = getBodyCellByRowAndColumn(0, 0);
        assertTrue(cell.getAttribute("class").contains("frozen"));

        cell = getBodyCellByRowAndColumn(0, 1);
        assertTrue(cell.getAttribute("class").contains("frozen"));
    }

    @Test
    public void testInitialColumnWidths() throws Exception {
        openTestURL();

        WebElement cell = getBodyCellByRowAndColumn(0, 0);
        assertEquals(100, cell.getSize().getWidth());

        cell = getBodyCellByRowAndColumn(0, 1);
        assertEquals(150, cell.getSize().getWidth());

        cell = getBodyCellByRowAndColumn(0, 2);
        assertEquals(200, cell.getSize().getWidth());
    }

    @Test
    public void testColumnWidths() throws Exception {
        openTestURL();

        // Default column width is 100px
        WebElement cell = getBodyCellByRowAndColumn(0, 0);
        assertEquals(100, cell.getSize().getWidth());

        // Set first column to be 200px wide
        selectMenuPath("Component", "Columns", "Column0", "Column0 Width",
                "200px");

        cell = getBodyCellByRowAndColumn(0, 0);
        assertEquals(200, cell.getSize().getWidth());

        // Set second column to be 150px wide
        selectMenuPath("Component", "Columns", "Column1", "Column1 Width",
                "150px");
        cell = getBodyCellByRowAndColumn(0, 1);
        assertEquals(150, cell.getSize().getWidth());

        // Set first column to be auto sized (defaults to 100px currently)
        selectMenuPath("Component", "Columns", "Column0", "Column0 Width",
                "Auto");

        cell = getBodyCellByRowAndColumn(0, 0);
        assertEquals(100, cell.getSize().getWidth());
    }

    @Test
    public void testPrimaryStyleNames() throws Exception {
        openTestURL();

        // v-grid is default primary style namea
        assertPrimaryStylename("v-grid");

        selectMenuPath("Component", "State", "Primary style name",
                "v-escalator");
        assertPrimaryStylename("v-escalator");

        selectMenuPath("Component", "State", "Primary style name", "my-grid");
        assertPrimaryStylename("my-grid");

        selectMenuPath("Component", "State", "Primary style name", "v-grid");
        assertPrimaryStylename("v-grid");
    }

    /**
     * Test that the current view is updated when a server-side container change
     * occurs (without scrolling back and forth)
     */
    @Test
    public void testItemSetChangeEvent() throws Exception {
        openTestURL();

        final By newRow = By.xpath("//td[text()='newcell: 0']");

        assertTrue("Unexpected initial state", !isElementPresent(newRow));

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Add row failed", isElementPresent(newRow));

        selectMenuPath("Component", "Body rows", "Remove first row");
        assertTrue("Remove row failed", !isElementPresent(newRow));
    }

    /**
     * Test that the current view is updated when a property's value is reflect
     * to the client, when the value is modified server-side.
     */
    @Test
    public void testPropertyValueChangeEvent() throws Exception {
        openTestURL();

        assertEquals("Unexpected cell initial state", "(0, 0)",
                getBodyCellByRowAndColumn(0, 0).getText());

        selectMenuPath("Component", "Body rows",
                "Modify first row (getItemProperty)");
        assertEquals("(First) modification with getItemProperty failed",
                "modified: 0", getBodyCellByRowAndColumn(0, 0).getText());

        selectMenuPath("Component", "Body rows",
                "Modify first row (getContainerProperty)");
        assertEquals("(Second) modification with getItemProperty failed",
                "modified: Column0", getBodyCellByRowAndColumn(0, 0).getText());
    }

    @Test
    public void testSelectOnOff() throws Exception {
        openTestURL();

        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));
        toggleFirstRowSelection();
        assertTrue("row should become selected", isSelected(getRow(0)));
        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", isSelected(getRow(0)));
    }

    @Test
    public void testSelectOnScrollOffScroll() throws Exception {
        openTestURL();
        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));
        toggleFirstRowSelection();
        assertTrue("row should become selected", isSelected(getRow(0)));

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        scrollGridVerticallyTo(0); // scroll it back into view

        assertTrue("row should still be selected when scrolling "
                + "back into view", isSelected(getRow(0)));
    }

    @Test
    public void testSelectScrollOnScrollOff() throws Exception {
        openTestURL();
        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // scroll it back into view
        assertTrue("row should still be selected when scrolling "
                + "back into view", isSelected(getRow(0)));

        toggleFirstRowSelection();
        assertFalse("row shouldn't remain selected", isSelected(getRow(0)));
    }

    @Test
    public void testSelectScrollOnOffScroll() throws Exception {
        openTestURL();
        assertFalse("row shouldn't start out as selected",
                isSelected(getRow(0)));

        scrollGridVerticallyTo(10000); // make sure the row is out of cache
        toggleFirstRowSelection();
        toggleFirstRowSelection();

        scrollGridVerticallyTo(0); // make sure the row is out of cache
        assertFalse("row shouldn't be selected when scrolling "
                + "back into view", isSelected(getRow(0)));
    }

    private void toggleFirstRowSelection() {
        selectMenuPath("Component", "Body rows", "Select first row");
    }

    @SuppressWarnings("static-method")
    private boolean isSelected(TestBenchElement row) {
        /*
         * FIXME We probably should get a GridRow instead of a plain
         * TestBenchElement, that has an "isSelected" thing integrated. (henrik
         * paul 26.6.2014)
         */
        return row.getAttribute("class").contains("-row-selected");
    }

    private TestBenchElement getRow(int i) {
        return getGridElement().getRow(i);
    }

    private void assertPrimaryStylename(String stylename) {
        assertTrue(getGridElement().getAttribute("class").contains(stylename));

        String tableWrapperStyleName = getTableWrapper().getAttribute("class");
        assertTrue(tableWrapperStyleName.contains(stylename + "-tablewrapper"));

        String hscrollStyleName = getHorizontalScroller().getAttribute("class");
        assertTrue(hscrollStyleName.contains(stylename + "-scroller"));
        assertTrue(hscrollStyleName
                .contains(stylename + "-scroller-horizontal"));

        String vscrollStyleName = getVerticalScroller().getAttribute("class");
        assertTrue(vscrollStyleName.contains(stylename + "-scroller"));
        assertTrue(vscrollStyleName.contains(stylename + "-scroller-vertical"));
    }

    private WebElement getBodyCellByRowAndColumn(int row, int column) {
        return getGridElement().getCell(row, column);
    }

    private void selectSubMenu(String menuCaption) {
        selectMenu(menuCaption);
        new Actions(getDriver()).moveByOffset(100, 0).build().perform();
    }

    private void selectMenu(String menuCaption) {
        getDriver().findElement(
                By.xpath("//span[text() = '" + menuCaption + "']")).click();
    }

    private void selectMenuPath(String... menuCaptions) {
        selectMenu(menuCaptions[0]);
        for (int i = 1; i < menuCaptions.length; i++) {
            selectSubMenu(menuCaptions[i]);
        }
    }

    private WebElement getVerticalScroller() {
        return getGridElement().findElement(By.xpath("./div[1]"));
    }

    private WebElement getHorizontalScroller() {
        return getGridElement().findElement(By.xpath("./div[2]"));
    }

    private WebElement getTableWrapper() {
        return getGridElement().findElement(By.xpath("./div[3]"));
    }

    private GridElement getGridElement() {
        return $(GridElement.class).id("testComponent");
    }

    private List<TestBenchElement> getGridHeaderRowCells() {
        List<TestBenchElement> headerCells = new ArrayList<TestBenchElement>();
        for (int i = 0; i < getGridElement().getHeaderCount(); ++i) {
            headerCells.addAll(getGridElement().getHeaderCells(i));
        }
        return headerCells;
    }

    private List<TestBenchElement> getGridFooterRowCells() {
        List<TestBenchElement> footerCells = new ArrayList<TestBenchElement>();
        for (int i = 0; i < getGridElement().getFooterCount(); ++i) {
            footerCells.addAll(getGridElement().getFooterCells(i));
        }
        return footerCells;
    }

    private void scrollGridVerticallyTo(double px) {
        executeScript("arguments[0].scrollTop = " + px,
                getGridVerticalScrollbar());
    }

    private Object executeScript(String script, WebElement element) {
        @SuppressWarnings("hiding")
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

    private WebElement getGridVerticalScrollbar() {
        return getDriver()
                .findElement(
                        By.xpath("//div[contains(@class, \"v-grid-scroller-vertical\")]"));
    }
}
