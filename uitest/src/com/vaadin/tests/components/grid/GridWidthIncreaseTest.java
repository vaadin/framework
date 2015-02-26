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

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridWidthIncreaseTest extends MultiBrowserTest {

    private static int INCREASE_COUNT = 3;

    @Test
    public void testColumnsExpandWithGrid() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        int[] widths = new int[GridWidthIncrease.COLUMN_COUNT];
        for (int i = 0; i < INCREASE_COUNT; ++i) {
            int totalWidth = 0;
            $(ButtonElement.class).first().click();
            for (int c = 0; c < GridWidthIncrease.COLUMN_COUNT; ++c) {
                int width = grid.getCell(0, c).getSize().getWidth();
                totalWidth += width;
                widths[c] = width;
                if (c > 0) {
                    // check that columns are roughly the same width.
                    assertEquals("Difference in column widths", widths[c],
                            widths[c - 1], 1.0d);
                }
            }
            // Column widths should be the same as table wrapper size
            assertTrue(totalWidth == grid.getTableWrapper().getSize()
                    .getWidth());
        }
    }
}
