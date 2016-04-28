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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSubPixelProblemWrappingTest extends MultiBrowserTest {

    @Test
    public void addedRowShouldNotWrap() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        // Cells in first row should be at the same y coordinate as the row
        assertRowAndCellTops(grid, 0);

        // Add a row
        $(ButtonElement.class).first().click();

        // Cells in the first row should be at the same y coordinate as the row
        assertRowAndCellTops(grid, 0);
        // Cells in the second row should be at the same y coordinate as the row
        assertRowAndCellTops(grid, 1);
    }

    private void assertRowAndCellTops(GridElement grid, int rowIndex) {
        GridRowElement row = grid.getRow(rowIndex);
        int rowTop = row.getLocation().y;

        int cell0Top = grid.getCell(rowIndex, 0).getLocation().y;
        int cell1Top = grid.getCell(rowIndex, 1).getLocation().y;
        Assert.assertEquals(rowTop, cell0Top);
        Assert.assertEquals(rowTop, cell1Top);
    }
}
