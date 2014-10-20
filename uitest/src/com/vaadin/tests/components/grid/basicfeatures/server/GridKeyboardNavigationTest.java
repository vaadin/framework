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
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.components.grid.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridKeyboardNavigationTest extends GridBasicFeaturesTest {

    @Test
    public void testCellActiveOnClick() {
        openTestURL();

        GridElement grid = getGridElement();
        assertTrue("Body cell 0, 0 is not active on init.", grid.getCell(0, 0)
                .isActive());
        grid.getCell(5, 2).click();
        assertFalse("Body cell 0, 0 was still active after clicking", grid
                .getCell(0, 0).isActive());
        assertTrue("Body cell 5, 2 is not active after clicking",
                grid.getCell(5, 2).isActive());
    }

    @Test
    public void testCellNotActiveWhenRendererHandlesEvent() {
        openTestURL();

        GridElement grid = getGridElement();
        assertTrue("Body cell 0, 0 is not active on init.", grid.getCell(0, 0)
                .isActive());
        grid.getHeaderCell(0, 3).click();
        assertFalse("Body cell 0, 0 is active after click on header.", grid
                .getCell(0, 0).isActive());
        assertTrue("Header cell 0, 3 is not active after click on header.",
                grid.getHeaderCell(0, 3).isActive());
    }

    @Test
    public void testSimpleKeyboardNavigation() {
        openTestURL();

        GridElement grid = getGridElement();
        grid.getCell(0, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        assertTrue("Body cell 1, 0 is not active after keyboard navigation.",
                grid.getCell(1, 0).isActive());

        new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT).perform();
        assertTrue("Body cell 1, 1 is not active after keyboard navigation.",
                grid.getCell(1, 1).isActive());

        int i;
        for (i = 1; i < 40; ++i) {
            new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        }

        assertFalse("Grid has not scrolled with active cell",
                isElementPresent(By.xpath("//td[text() = '(0, 0)']")));
        assertTrue("Active cell is not visible",
                isElementPresent(By.xpath("//td[text() = '(" + i + ", 0)']")));
        assertTrue("Body cell " + i + ", 1 is not active", grid.getCell(i, 1)
                .isActive());
    }

    @Test
    public void testNavigateFromHeaderToBody() {
        openTestURL();

        GridElement grid = getGridElement();
        grid.scrollToRow(300);
        new Actions(driver).moveToElement(grid.getHeaderCell(0, 7)).click()
                .perform();
        grid.scrollToRow(280);

        assertTrue("Header cell is not active.", grid.getHeaderCell(0, 7)
                .isActive());
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        assertTrue("Body cell 280, 7 is not active", grid.getCell(280, 7)
                .isActive());
    }

    @Test
    public void testNavigationFromFooterToBody() {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");

        GridElement grid = getGridElement();
        grid.scrollToRow(300);
        grid.getFooterCell(0, 2).click();

        assertTrue("Footer cell is not active.", grid.getFooterCell(0, 2)
                .isActive());
        new Actions(getDriver()).sendKeys(Keys.ARROW_UP).perform();
        assertTrue("Body cell 300, 2 is not active", grid.getCell(300, 2)
                .isActive());
    }

    @Test
    public void testNavigateBetweenHeaderAndBodyWithTab() {
        openTestURL();

        GridElement grid = getGridElement();
        grid.getCell(10, 2).click();

        assertTrue("Body cell 10, 2 is not active", grid.getCell(10, 2)
                .isActive());
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .keyUp(Keys.SHIFT).perform();
        assertTrue("Header cell 0, 2 is not active", grid.getHeaderCell(0, 2)
                .isActive());
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertTrue("Body cell 10, 2 is not active", grid.getCell(10, 2)
                .isActive());

        // Navigate out of the Grid and try to navigate with arrow keys.
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .sendKeys(Keys.TAB).keyUp(Keys.SHIFT).sendKeys(Keys.ARROW_DOWN)
                .perform();
        assertTrue("Header cell 0, 2 is not active", grid.getHeaderCell(0, 2)
                .isActive());
    }

    @Test
    public void testNavigateBetweenFooterAndBodyWithTab() {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");

        GridElement grid = getGridElement();
        grid.getCell(10, 2).click();

        assertTrue("Body cell 10, 2 is not active", grid.getCell(10, 2)
                .isActive());
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertTrue("Footer cell 0, 2 is not active", grid.getFooterCell(0, 2)
                .isActive());
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .keyUp(Keys.SHIFT).perform();
        assertTrue("Body cell 10, 2 is not active", grid.getCell(10, 2)
                .isActive());

        // Navigate out of the Grid and try to navigate with arrow keys.
        new Actions(getDriver()).sendKeys(Keys.TAB).sendKeys(Keys.TAB)
                .sendKeys(Keys.ARROW_UP).perform();
        assertTrue("Footer cell 0, 2 is not active", grid.getFooterCell(0, 2)
                .isActive());
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

        getGridElement().getCell(5, 2).click();

        new Actions(getDriver()).sendKeys(Keys.PAGE_DOWN).perform();
        assertTrue("Row 20 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(20, 2)']")));

        new Actions(getDriver()).sendKeys(Keys.PAGE_DOWN).perform();
        assertTrue("Row 30 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(30, 2)']")));

        assertTrue("Original active cell is no longer active", getGridElement()
                .getCell(5, 2).isActive());

        getGridElement().getCell(50, 2).click();

        new Actions(getDriver()).sendKeys(Keys.PAGE_UP).perform();
        assertTrue("Row 31 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(31, 2)']")));

        new Actions(getDriver()).sendKeys(Keys.PAGE_UP).perform();
        assertTrue("Row 21 did not become visible",
                isElementPresent(By.xpath("//td[text() = '(21, 2)']")));

        assertTrue("Original active cell is no longer active", getGridElement()
                .getCell(50, 2).isActive());
    }

    @Test
    public void testActiveCellOffsetWhileInDifferentSection() {
        openTestURL();
        getGridElement().getCell(0, 0).click();
        new Actions(getDriver()).sendKeys(Keys.UP).perform();
        assertTrue("Header 0,0 should've become active", getGridElement()
                .getHeaderCell(0, 0).isActive());

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Header 0,0 should've remained active", getGridElement()
                .getHeaderCell(0, 0).isActive());
    }

    @Test
    public void testActiveCellOffsetWhileInSameSectionAndInsertedAbove() {
        openTestURL();
        assertTrue("Body 0,0 should've been", getGridElement().getCell(0, 0)
                .isActive());

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Body 1,0 should've become active", getGridElement()
                .getCell(1, 0).isActive());
    }

    @Test
    public void testActiveCellOffsetWhileInSameSectionAndInsertedBelow() {
        openTestURL();
        assertTrue("Body 0,0 should've been active",
                getGridElement().getCell(0, 0).isActive());

        selectMenuPath("Component", "Body rows", "Add second row");
        assertTrue("Body 0,0 should've remained active", getGridElement()
                .getCell(0, 0).isActive());
    }
}
