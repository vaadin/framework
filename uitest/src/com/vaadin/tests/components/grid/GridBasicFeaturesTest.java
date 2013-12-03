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
        List<WebElement> cells = getDriver()
                .findElements(
                        By.xpath("//thead[contains(@class, 'v-escalator-header')]//th"));
        assertEquals(10, cells.size());
        assertEquals("Column0", cells.get(0).getText());
        assertEquals("Column1", cells.get(1).getText());
        assertEquals("Column2", cells.get(2).getText());
    }

    @Test
    public void testColumnFooterCaptions() throws Exception {
        openTestURL();

        String footerCellPath = "//tfoot[contains(@class, 'v-escalator-footer')]"
                + "//td[contains(@class, 'v-escalator-cell')]";

        // footer row should by default be hidden
        assertEquals(0, getDriver().findElements(By.xpath(footerCellPath))
                .size());

        // Open footer row
        selectMenuPath("Component", "Footers", "Visible");

        // Footers should now be visible
        List<WebElement> cells = getDriver().findElements(
                By.xpath(footerCellPath));
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

        String headerCellPath = "//thead[contains(@class, 'v-escalator-header')]//th";

        // header row should be empty
        assertEquals(0, getDriver().findElements(By.xpath(headerCellPath))
                .size());

        // add a group row
        selectMenuPath("Component", "Column groups", "Add group row");

        // Empty group row cells should be present
        assertEquals(10, getDriver().findElements(By.xpath(headerCellPath))
                .size());

        // Group columns 0 & 1
        selectMenuPath("Component", "Column groups", "Column group row 1",
                "Group Column 0 & 1");

        List<WebElement> cells = getDriver().findElements(
                By.xpath(headerCellPath));
        assertEquals("Column 0 & 1", cells.get(0).getText());
        assertEquals("Column 0 & 1", cells.get(1).getText());
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

        String footerCellPath = "//tfoot[contains(@class, 'v-escalator-footer')]"
                + "//td[contains(@class, 'v-escalator-cell')]";

        List<WebElement> cells = getDriver().findElements(
                By.xpath(footerCellPath));
        assertEquals("Column 0 & 1", cells.get(0).getText());
        assertEquals("Column 0 & 1", cells.get(1).getText());
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
        String headerCellPath = "//thead[contains(@class, 'v-escalator-header')]//th";
        List<WebElement> cells = getDriver().findElements(
                By.xpath(headerCellPath));
        assertEquals("Column0", cells.get(0).getText());

        // Hide column 0
        selectMenuPath("Component", "Columns", "Column0", "Visible");

        // Column 1 should now be the first cell
        cells = getDriver().findElements(By.xpath(headerCellPath));
        assertEquals("Column1", cells.get(0).getText());
    }

    @Test
    public void testRemovingColumn() throws Exception {
        openTestURL();

        // Column 0 should be visible
        String headerCellPath = "//thead[contains(@class, 'v-escalator-header')]//th";
        List<WebElement> cells = getDriver().findElements(
                By.xpath(headerCellPath));
        assertEquals("Column0", cells.get(0).getText());

        // Hide column 0
        selectMenuPath("Component", "Columns", "Column0", "Remove");

        // Column 1 should now be the first cell
        cells = getDriver().findElements(By.xpath(headerCellPath));
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

    private WebElement getBodyCellByRowAndColumn(int row, int column) {
        return getDriver().findElement(
                By.xpath("//tbody[contains(@class, 'v-escalator-body')]/tr["
                        + row + "]/td[" + column + "]"));
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
}
