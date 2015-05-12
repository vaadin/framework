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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridMultiSortingTest extends GridBasicFeaturesTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return super.getBrowsersSupportingShiftClick();
    }

    @Test
    public void testUserMultiColumnSorting() {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 11", "Column 11 Width",
                "Auto");

        GridCellElement cell = getGridElement().getHeaderCell(0, 11);
        new Actions(driver).moveToElement(cell, 5, 5).click().perform();
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        new Actions(driver)
                .moveToElement(getGridElement().getHeaderCell(0, 0), 5, 5)
                .click().perform();
        new Actions(driver).keyUp(Keys.SHIFT).perform();

        String prev = getGridElement().getCell(0, 11).getAttribute("innerHTML");
        for (int i = 1; i <= 6; ++i) {
            assertEquals("Column 11 should contain same values.", prev,
                    getGridElement().getCell(i, 11).getAttribute("innerHTML"));
        }

        prev = getGridElement().getCell(0, 0).getText();
        for (int i = 1; i <= 6; ++i) {
            assertTrue(
                    "Grid is not sorted by column 0.",
                    prev.compareTo(getGridElement().getCell(i, 0).getText()) < 0);
        }
    }
}
