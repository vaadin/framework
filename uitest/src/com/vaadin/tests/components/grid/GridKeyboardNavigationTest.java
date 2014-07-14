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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridKeyboardNavigationTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return GridBasicFeatures.class;
    }

    @Test
    public void testCellActiveOnClick() {
        openTestURL();

        GridElement grid = getGridElement();
        assertTrue("Body cell 0, 0 is not active on init.",
                cellIsActive(grid, 0, 0));
        grid.getCell(5, 2).click();
        assertFalse("Body cell 0, 0 was still active after clicking",
                cellIsActive(grid, 0, 0));
        assertTrue("Body cell 5, 2 is not active after clicking",
                cellIsActive(grid, 5, 2));
    }

    @Test
    public void testCellNotActiveWhenRendererHandlesEvent() {
        openTestURL();

        GridElement grid = getGridElement();
        assertTrue("Body cell 0, 0 is not active on init.",
                cellIsActive(grid, 0, 0));
        grid.getHeaderCell(0, 3).click();
        assertTrue("Body cell 0, 0 is not active after click on header.",
                cellIsActive(grid, 0, 0));
    }

    private boolean cellIsActive(GridElement grid, int row, int col) {
        return grid.getCell(row, col).getAttribute("class")
                .contains("-cell-active");
    }

    private GridElement getGridElement() {
        return $(GridElement.class).first();
    }
}
