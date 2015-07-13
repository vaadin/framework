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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridClientSelectionTest extends GridBasicClientFeaturesTest {

    @Test
    public void testChangeSelectionMode() {
        openTestURL();

        setSelectionModelNone();
        assertTrue("First column was selection column", getGridElement()
                .getCell(0, 0).getText().equals("(0, 0)"));
        setSelectionModelMulti();
        assertTrue("First column was not selection column", getGridElement()
                .getCell(0, 1).getText().equals("(0, 0)"));
    }

    @Test
    public void testSelectAllCheckbox() {
        openTestURL();

        setSelectionModelMulti();
        selectMenuPath("Component", "DataSource", "Reset with 100 rows of Data");
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();

        for (int i = 0; i < 100; i += 10) {
            assertTrue("Row " + i + " was not selected.", getGridElement()
                    .getRow(i).isSelected());
        }

        header.findElement(By.tagName("input")).click();
        assertFalse("Row 52 was still selected", getGridElement().getRow(52)
                .isSelected());
    }

    @Test
    public void testSelectAllCheckboxWhenChangingModels() {
        openTestURL();

        GridCellElement header;
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(0, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelSingle(true);
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelNone();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

    }

    @Test
    public void testDeselectAllowedMouseInput() {
        openTestURL();

        setSelectionModelSingle(true);

        getGridElement().getCell(5, 1).click();

        assertTrue("Row 5 should be selected after clicking", isRowSelected(5));

        getGridElement().getCell(7, 1).click();

        assertFalse("Row 5 should be deselected after clicking another row",
                isRowSelected(5));
        assertTrue("Row 7 should be selected after clicking", isRowSelected(7));

        getGridElement().getCell(7, 1).click();

        assertFalse("Row should be deselected after clicking again",
                isRowSelected(7));
    }

    @Test
    public void testDeselectAllowedKeyboardInput() {
        openTestURL();

        setSelectionModelSingle(true);

        getGridElement().getHeaderCell(0, 0).click();

        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Row 0 should be selected after pressing space",
                isRowSelected(0));

        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertFalse(
                "Row 0 should be deselected after pressing space another row",
                isRowSelected(0));
        assertTrue("Row 1 should be selected after pressing space",
                isRowSelected(1));

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertFalse("Row should be deselected after pressing space again",
                isRowSelected(1));
    }

    @Test
    public void testDeselectNotAllowedMouseInput() {
        openTestURL();

        setSelectionModelSingle(false);

        getGridElement().getCell(5, 1).click();

        assertTrue("Row 5 should be selected after clicking", isRowSelected(5));

        getGridElement().getCell(7, 1).click();

        assertFalse("Row 5 should be deselected after clicking another row",
                isRowSelected(5));
        assertTrue("Row 7 should be selected after clicking", isRowSelected(7));

        getGridElement().getCell(7, 1).click();

        assertTrue("Row should remain selected after clicking again",
                isRowSelected(7));
    }

    @Test
    public void testDeselectNotAllowedKeyboardInput() {
        openTestURL();

        setSelectionModelSingle(false);

        getGridElement().getHeaderCell(0, 0).click();
        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Row 0 should be selected after pressing space",
                isRowSelected(0));

        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertFalse(
                "Row 0 should be deselected after pressing space another row",
                isRowSelected(0));
        assertTrue("Row 1 should be selected after pressing space",
                isRowSelected(1));

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertTrue("Row should remain selected after pressing space again",
                isRowSelected(1));
    }

    @Test
    public void testChangeSelectionModelUpdatesUI() {
        openTestURL();

        setSelectionModelSingle(true);
        getGridElement().getCell(5, 1).click();
        assertTrue("Row 5 should be selected after clicking", isRowSelected(5));
        setSelectionModelNone();
        assertFalse(
                "Row 5 should not be selected after changing selection model",
                isRowSelected(5));

    }
}
