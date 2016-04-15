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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSortIndicatorTest extends MultiBrowserTest {

    @Test
    public void testIndicators() throws InterruptedException {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        // Clicking the left header cell should set ascending sort order for
        // both columns.
        grid.getHeaderCell(0, 0).click();
        assertTrue(grid.getHeaderCell(0, 0).getAttribute("class")
                .contains("sort-asc"));
        assertTrue(grid.getHeaderCell(0, 1).getAttribute("class")
                .contains("sort-asc"));
        // Click the left column to change the sort direction.
        grid.getHeaderCell(0, 0).click();
        assertTrue(grid.getHeaderCell(0, 0).getAttribute("class")
                .contains("sort-desc"));
        assertTrue(grid.getHeaderCell(0, 1).getAttribute("class")
                .contains("sort-desc"));
        // Clicking on the right column should have no effect.
        grid.getHeaderCell(0, 1).click();
        assertTrue(grid.getHeaderCell(0, 0).getAttribute("class")
                .contains("sort-desc"));
        assertTrue(grid.getHeaderCell(0, 1).getAttribute("class")
                .contains("sort-desc"));
    }
}