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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

public class GridFooterTest extends GridStaticSectionTest {

    @Test
    public void testDefaultFooter() {
        openTestURL();

        // Footer should have zero rows by default
        assertFooterCount(0);
    }

    @Test
    public void testFooterVisibility() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Visible");

        assertFooterCount(0);

        selectMenuPath("Component", "Footer", "Append row");

        assertFooterCount(0);

        selectMenuPath("Component", "Footer", "Visible");

        assertFooterCount(1);
    }

    @Test
    public void testAddRows() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        assertFooterCount(1);
        assertFooterTexts(0, 0);

        selectMenuPath("Component", "Footer", "Prepend row");

        assertFooterCount(2);
        assertFooterTexts(1, 0);
        assertFooterTexts(0, 1);

        selectMenuPath("Component", "Footer", "Append row");

        assertFooterCount(3);
        assertFooterTexts(1, 0);
        assertFooterTexts(0, 1);
        assertFooterTexts(2, 2);
    }

    @Test
    public void testRemoveRows() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Prepend row");
        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Footer", "Remove top row");

        assertFooterCount(1);
        assertFooterTexts(1, 0);

        selectMenuPath("Component", "Footer", "Remove bottom row");
        assertFooterCount(0);
    }

    @Test
    public void joinColumnsByCells() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Footer", "Row 1", "Join column cells 0, 1");

        GridCellElement spannedCell = getGridElement().getFooterCell(0, 0);
        assertTrue(spannedCell.isDisplayed());
        assertEquals("2", spannedCell.getAttribute("colspan"));

        // TestBench returns the spanned cell for all columns
        assertEquals(spannedCell.getText(), getGridElement()
                .getFooterCell(0, 1).getText());
    }

    @Test
    public void joinColumnsByColumns() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Footer", "Row 1", "Join columns 1, 2");

        GridCellElement spannedCell = getGridElement().getFooterCell(0, 1);
        assertTrue(spannedCell.isDisplayed());
        assertEquals("2", spannedCell.getAttribute("colspan"));

        // TestBench returns the spanned cell for all columns
        assertEquals(spannedCell.getText(), getGridElement()
                .getFooterCell(0, 2).getText());
    }

    @Test
    public void joinAllColumnsInRow() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Footer", "Row 1", "Join all columns");

        GridCellElement spannedCell = getGridElement().getFooterCell(0, 0);
        assertTrue(spannedCell.isDisplayed());
        assertEquals("" + GridBasicFeatures.COLUMNS,
                spannedCell.getAttribute("colspan"));

        for (int columnIndex = 1; columnIndex < GridBasicFeatures.COLUMNS; columnIndex++) {
            GridCellElement hiddenCell = getGridElement().getFooterCell(0,
                    columnIndex);
            // TestBench returns the spanned cell for all columns
            assertEquals(spannedCell.getText(), hiddenCell.getText());
        }
    }

    @Test
    public void testInitialCellTypes() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        GridCellElement textCell = getGridElement().getFooterCell(0, 0);
        /*
         * Reindeer has a CSS text transformation that changes the casing so
         * that we can't rely on it being what we set
         */
        assertEquals("footer (0,0)", textCell.getText().toLowerCase());

        GridCellElement widgetCell = getGridElement().getFooterCell(0, 1);
        assertTrue(widgetCell.isElementPresent(By.className("gwt-HTML")));

        GridCellElement htmlCell = getGridElement().getFooterCell(0, 2);
        assertHTML("<b>Footer (0,2)</b>", htmlCell);
    }

    @Test
    public void testDynamicallyChangingCellType() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Columns", "Column 0", "Footer Type",
                "Widget Footer");
        GridCellElement widgetCell = getGridElement().getFooterCell(0, 0);
        assertTrue(widgetCell.isElementPresent(By.className("gwt-Button")));

        selectMenuPath("Component", "Columns", "Column 1", "Footer Type",
                "HTML Footer");
        GridCellElement htmlCell = getGridElement().getFooterCell(0, 1);
        assertHTML("<b>HTML Footer</b>", htmlCell);

        selectMenuPath("Component", "Columns", "Column 2", "Footer Type",
                "Text Footer");
        GridCellElement textCell = getGridElement().getFooterCell(0, 2);

        /*
         * Reindeer has a CSS text transformation that changes the casing so
         * that we can't rely on it being what we set
         */
        assertEquals("text footer", textCell.getText().toLowerCase());
    }

    @Test
    public void testCellWidgetInteraction() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Footer", "Append row");

        selectMenuPath("Component", "Columns", "Column 0", "Footer Type",
                "Widget Footer");
        GridCellElement widgetCell = getGridElement().getFooterCell(0, 0);
        WebElement button = widgetCell.findElement(By.className("gwt-Button"));

        assertNotEquals("clicked", button.getText().toLowerCase());

        new Actions(getDriver()).moveToElement(button, 5, 5).click().perform();

        assertEquals("clicked", button.getText().toLowerCase());
    }

    private void assertFooterCount(int count) {
        assertEquals("footer count", count, getGridElement().getFooterCount());
    }
}
