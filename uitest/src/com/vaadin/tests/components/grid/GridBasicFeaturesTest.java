/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GridBasicFeaturesTest extends MultiBrowserTest {

    @Test
    public void testColumnHeaderCaptions() throws Exception {
        openTestURL();

        // Column headers should be visible
        List<WebElement> cells = getGridHeaderRowCells();
        assertEquals(10, cells.size());
        assertEquals("Column0", cells.get(0).getText());
        assertEquals("Column1", cells.get(1).getText());
        assertEquals("Column2", cells.get(2).getText());
    }

    @Test
    public void testColumnFooterCaptions() throws Exception {
        openTestURL();

        // footer row should by default be hidden
        List<WebElement> cells = getGridFooterRowCells();
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

        List<WebElement> cells = getGridHeaderRowCells();

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

        List<WebElement> cells = getGridFooterRowCells();
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
        List<WebElement> cells = getGridHeaderRowCells();
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
        List<WebElement> cells = getGridHeaderRowCells();
        assertEquals("Column0", cells.get(0).getText());

        // Hide column 0
        selectMenuPath("Component", "Columns", "Column0", "Remove");

        // Column 1 should now be the first cell
        cells = getGridHeaderRowCells();
        assertEquals("Column1", cells.get(0).getText());
    }

    @Test
    public void testFreezingColumn() throws Exception {
        openTestURL();

        // Freeze column 2
        selectMenuPath("Component", "Columns", "Column2", "Freeze");

        WebElement cell = getBodyCellByRowAndColumn(1, 1);
        assertTrue(cell.getAttribute("class").contains("frozen"));

        cell = getBodyCellByRowAndColumn(1, 2);
        assertTrue(cell.getAttribute("class").contains("frozen"));
    }

    @Test
    public void testInitialColumnWidths() throws Exception {
        openTestURL();

        // Default borders and margins implemented by escalator
        int cellBorder = 1 + 1;
        int cellMargin = 2 + 2;

        WebElement cell = getBodyCellByRowAndColumn(1, 1);
        assertEquals((100 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));

        cell = getBodyCellByRowAndColumn(1, 2);
        assertEquals((150 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));

        cell = getBodyCellByRowAndColumn(1, 3);
        assertEquals((200 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));
    }

    @Test
    public void testColumnWidths() throws Exception {
        openTestURL();

        // Default borders and margins implemented by escalator
        int cellBorder = 1 + 1;
        int cellMargin = 2 + 2;

        // Default column width is 100px
        WebElement cell = getBodyCellByRowAndColumn(1, 1);
        assertEquals((100 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));

        // Set first column to be 200px wide
        selectMenuPath("Component", "Columns", "Column0", "Column0 Width",
                "200px");

        cell = getBodyCellByRowAndColumn(1, 1);
        assertEquals((200 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));

        // Set second column to be 150px wide
        selectMenuPath("Component", "Columns", "Column1", "Column1 Width",
                "150px");
        cell = getBodyCellByRowAndColumn(1, 2);
        assertEquals((150 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));

        // Set first column to be auto sized (defaults to 100px currently)
        selectMenuPath("Component", "Columns", "Column0", "Column0 Width",
                "Auto");

        cell = getBodyCellByRowAndColumn(1, 1);
        assertEquals((100 - cellBorder - cellMargin) + "px",
                cell.getCssValue("width"));
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

        assertTrue("Unexpected initial state", !elementIsFound(newRow));

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Add row failed", elementIsFound(newRow));

        selectMenuPath("Component", "Body rows", "Remove first row");
        assertTrue("Remove row failed", !elementIsFound(newRow));
    }

    /**
     * Test that the current view is updated when a property's value is reflect
     * to the client, when the value is modified server-side.
     */
    @Test
    public void testPropertyValueChangeEvent() throws Exception {
        openTestURL();

        assertEquals("Unexpected cell initial state", "(0, 0)",
                getBodyCellByRowAndColumn(1, 1).getText());

        selectMenuPath("Component", "Body rows",
                "Modify first row (getItemProperty)");
        assertEquals("(First) modification with getItemProperty failed",
                "modified: 0", getBodyCellByRowAndColumn(1, 1).getText());

        selectMenuPath("Component", "Body rows",
                "Modify first row (getContainerProperty)");
        assertEquals("(Second) modification with getItemProperty failed",
                "modified: Column0", getBodyCellByRowAndColumn(1, 1).getText());
    }

    @Test
    public void testDataFetchingWorks() throws Exception {
        openTestURL();

        scrollGridVerticallyTo(200);

        /*
         * Give time for the data to be fetched.
         * 
         * TODO TestBench currently doesn't know when Grid's DOM structure is
         * stable. There are some plans regarding implementing support for this,
         * so this test case can (should) be modified once that's implemented.
         */
        sleep(1000);

        /*
         * TODO this screenshot comparison could be done on the DOM level, if
         * the DOM would be always in order. This could be amended once DOM
         * reordering is merged into the Grid branch.
         */
        compareScreen("dataHasBeenLoaded");
    }

    private boolean elementIsFound(By locator) {
        try {
            return driver.findElement(locator) != null;
        } catch (NoSuchElementException e) {
            return false;
        }
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
        return getDriver().findElement(
                By.xpath("//div[@id='testComponent']//tbody/tr[" + row
                        + "]/td[" + column + "]"));
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
        return getDriver().findElement(
                By.xpath("//div[@id='testComponent']/div[1]"));
    }

    private WebElement getHorizontalScroller() {
        return getDriver().findElement(
                By.xpath("//div[@id='testComponent']/div[2]"));
    }

    private WebElement getTableWrapper() {
        return getDriver().findElement(
                By.xpath("//div[@id='testComponent']/div[3]"));
    }

    private WebElement getGridElement() {
        return getDriver().findElement(By.id("testComponent"));
    }

    private List<WebElement> getGridHeaderRowCells() {
        return getDriver().findElements(
                By.xpath("//div[@id='testComponent']//thead//th"));
    }

    private List<WebElement> getGridFooterRowCells() {
        return getDriver().findElements(
                By.xpath("//div[@id='testComponent']//tfoot//td"));
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
