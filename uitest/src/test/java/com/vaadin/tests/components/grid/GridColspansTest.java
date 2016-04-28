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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridColspansTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);
    }

    @Test
    public void testHeaderColSpans() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("5", grid.getHeaderCell(0, 1).getAttribute("colspan"));
        assertEquals("2", grid.getHeaderCell(1, 1).getAttribute("colspan"));
        assertEquals("3", grid.getHeaderCell(1, 3).getAttribute("colspan"));
    }

    @Test
    public void testFooterColSpans() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("5", grid.getFooterCell(1, 1).getAttribute("colspan"));
        assertEquals("2", grid.getFooterCell(0, 1).getAttribute("colspan"));
        assertEquals("3", grid.getFooterCell(0, 3).getAttribute("colspan"));
    }

    @Test
    public void testHideFirstColumnOfColspan() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Failed initial condition.", "all the stuff", grid
                .getHeaderCell(0, 1).getText().toLowerCase());
        assertEquals("Failed initial condition.", "first name", grid
                .getHeaderCell(2, 1).getText().toLowerCase());
        $(ButtonElement.class).caption("Show/Hide firstName").first().click();
        assertEquals("Header text changed on column hide.", "all the stuff",
                grid.getHeaderCell(0, 1).getText().toLowerCase());
        assertEquals("Failed initial condition.", "last name", grid
                .getHeaderCell(2, 1).getText().toLowerCase());
    }

    @Test
    public void testSplittingMergedHeaders() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement headerCell = grid.getHeaderCell(1, 1);
        assertEquals("Failed initial condition.", "full name", headerCell
                .getText().toLowerCase());
        assertEquals("Failed initial condition.", "first name", grid
                .getHeaderCell(2, 1).getText().toLowerCase());
        $(ButtonElement.class).get(1).click();
        headerCell = grid.getHeaderCell(1, 1);
        assertEquals("Header text not changed on column reorder.", "address",
                headerCell.getText().toLowerCase());
        assertEquals("Unexpected colspan", "1",
                headerCell.getAttribute("colspan"));
        headerCell = grid.getHeaderCell(1, 2);
        assertEquals("Header text not changed on column reorder", "full name",
                headerCell.getText().toLowerCase());
        assertEquals("Unexpected colspan", "2",
                headerCell.getAttribute("colspan"));

        assertTrue("Error indicator not present",
                isElementPresent(By.className("v-errorindicator")));

    }
}
