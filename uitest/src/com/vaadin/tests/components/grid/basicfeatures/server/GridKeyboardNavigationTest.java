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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridKeyboardNavigationTest extends GridBasicFeaturesTest {

    @Test
    public void testCellFocusOnClick() {
        openTestURL();

        GridElement grid = getGridElement();
        assertTrue("Body cell 0, 0 is not focused on init.", grid.getCell(0, 0)
                .isFocused());
        grid.getCell(5, 2).click();
        assertFalse("Body cell 0, 0 was still focused after clicking", grid
                .getCell(0, 0).isFocused());
        assertTrue("Body cell 5, 2 is not focused after clicking", grid
                .getCell(5, 2).isFocused());
    }

    @Test
    public void testCellNotFocusedWhenRendererHandlesEvent() {
        openTestURL();

        GridElement grid = getGridElement();
        assertTrue("Body cell 0, 0 is not focused on init.", grid.getCell(0, 0)
                .isFocused());
        grid.getHeaderCell(0, 3).click();
        assertFalse("Body cell 0, 0 is focused after click on header.", grid
                .getCell(0, 0).isFocused());
        assertTrue("Header cell 0, 3 is not focused after click on header.",
                grid.getHeaderCell(0, 3).isFocused());
    }

    @Test
    public void testSimpleKeyboardNavigation() {
        openTestURL();

        GridElement grid = getGridElement();
        grid.getCell(0, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        assertTrue("Body cell 1, 0 is not focused after keyboard navigation.",
                grid.getCell(1, 0).isFocused());

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT).perform();
        assertTrue("Body cell 1, 1 is not focused after keyboard navigation.",
                grid.getCell(1, 1).isFocused());

        int i;
        for (i = 1; i < 40; ++i) {
            new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        }

        assertFalse("Grid has not scrolled with cell focus",
                isElementPresent(By.xpath("//td[text() = '(0, 0)']")));
        assertTrue("Cell focus is not visible",
                isElementPresent(By.xpath("//td[text() = '(" + i + ", 0)']")));
        assertTrue("Body cell " + i + ", 1 is not focused", grid.getCell(i, 1)
                .isFocused());
    }

    @Test
    public void testNavigateFromHeaderToBody() {
        openTestURL();

        GridElement grid = getGridElement();
        grid.scrollToRow(300);
        new Actions(driver).moveToElement(grid.getHeaderCell(0, 7)).click()
                .perform();
        grid.scrollToRow(280);

        assertTrue("Header cell is not focused.", grid.getHeaderCell(0, 7)
                .isFocused());
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        assertTrue("Body cell 280, 7 is not focused", grid.getCell(280, 7)
                .isFocused());
    }

    @Test
    public void testNavigationFromFooterToBody() {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");

        GridElement grid = getGridElement();
        grid.scrollToRow(300);
        grid.getFooterCell(0, 2).click();

        assertTrue("Footer cell does not have focus.", grid.getFooterCell(0, 2)
                .isFocused());
        new Actions(getDriver()).sendKeys(Keys.ARROW_UP).perform();
        assertTrue("Body cell 300, 2 does not have focus.", grid
                .getCell(300, 2).isFocused());
    }

    @Test
    public void testNavigateBetweenHeaderAndBodyWithTab() {
        openTestURL();

        GridElement grid = getGridElement();
        grid.getCell(10, 2).click();

        assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2)
                .isFocused());
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .keyUp(Keys.SHIFT).perform();
        assertTrue("Header cell 0, 2 does not have focus",
                grid.getHeaderCell(0, 2).isFocused());
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2)
                .isFocused());

        // Navigate out of the Grid and try to navigate with arrow keys.
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .sendKeys(Keys.TAB).keyUp(Keys.SHIFT).sendKeys(Keys.ARROW_DOWN)
                .perform();
        assertTrue("Header cell 0, 2 does not have focus",
                grid.getHeaderCell(0, 2).isFocused());
    }

    @Test
    public void testNavigateBetweenFooterAndBodyWithTab() {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");

        GridElement grid = getGridElement();
        grid.getCell(10, 2).click();

        assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2)
                .isFocused());
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertTrue("Footer cell 0, 2 does not have focus",
                grid.getFooterCell(0, 2).isFocused());
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .keyUp(Keys.SHIFT).perform();
        assertTrue("Body cell 10, 2 does not have focus", grid.getCell(10, 2)
                .isFocused());

        // Navigate out of the Grid and try to navigate with arrow keys.
        new Actions(getDriver()).sendKeys(Keys.TAB).sendKeys(Keys.TAB)
                .sendKeys(Keys.ARROW_UP).perform();
        assertTrue("Footer cell 0, 2 does not have focus",
                grid.getFooterCell(0, 2).isFocused());
    }

    @Test
    public void testHomeEnd() throws Exception {
        openTestURL();

        getGridElement().getCell(100, 2).click();

        new Actions(getDriver()).sendKeys(Keys.HOME).perform();
        assertTrue("First row is not visible", getGridElement().getCell(0, 2)
                .isDisplayed());

        new Actions(getDriver()).sendKeys(Keys.END).perform();
        assertTrue("Last row cell not visible",
                getGridElement().getCell(GridBasicFeatures.ROWS - 1, 2)
                        .isDisplayed());
    }

    @Test
    public void testPageUpPageDown() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Size", "HeightMode Row");

        getGridElement().getCell(9, 2).click();
        new Actions(getDriver()).sendKeys(Keys.PAGE_DOWN).perform();
        assertTrue("Row 17 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(17, 2)']")));

        new Actions(getDriver()).sendKeys(Keys.PAGE_DOWN).perform();
        assertTrue("Row 25 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(25, 2)']")));
        checkFocusedCell(29, 2, 4);

        getGridElement().getCell(41, 2).click();
        new Actions(getDriver()).sendKeys(Keys.PAGE_UP).perform();
        assertTrue("Row 33 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(33, 2)']")));

        new Actions(getDriver()).sendKeys(Keys.PAGE_UP).perform();
        assertTrue("Row 25 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(25, 2)']")));
        checkFocusedCell(21, 2, 4);
    }

    private void checkFocusedCell(int row, int column, int rowTolerance) {
        WebElement focusedCell = getGridElement().findElement(
                By.className("v-grid-cell-focused"));
        String cellContents = focusedCell.getText();
        String[] rowAndCol = cellContents.replaceAll("[()\\s]", "").split(",");
        int focusedRow = Integer.parseInt(rowAndCol[0].trim());
        int focusedColumn = Integer.parseInt(rowAndCol[1].trim());
        // rowTolerance is the maximal allowed difference from the expected
        // focused row. It is required because scrolling using page up/down
        // may not move the position by exactly the visible height of the grid.
        assertTrue("The wrong cell is focused. Expected (" + row + "," + column
                + "), was " + cellContents,
                column == focusedColumn
                        && Math.abs(row - focusedRow) <= rowTolerance);
    }
}