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

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridWidthIncreaseTest extends MultiBrowserTest {

    private static int INCREASE_COUNT = 3;

    @Test
    public void testColumnsExpandWithGrid() throws IOException {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        double accuracy = 1.0d;
        DesiredCapabilities cap = getDesiredCapabilities();
        if (BrowserUtil.isIE(cap, 8) || BrowserUtil.isIE(cap, 9)
                || BrowserUtil.isPhantomJS(cap)) {
            accuracy = 2.0d;
        }

        for (int i = 0; i < INCREASE_COUNT; ++i) {
            $(ButtonElement.class).first().click();
            int prevWidth = 0;
            for (int c = 0; c < GridWidthIncrease.COLUMN_COUNT; ++c) {
                int width = grid.getCell(0, c).getSize().getWidth();
                if (c > 0) {
                    // check that columns are roughly the same width.
                    assertEquals("Difference in column widths", prevWidth,
                            width, accuracy);
                }
                prevWidth = width;
            }
            /*
             * Column widths should be the same as table wrapper size. Since
             * Selenium doesn't support subpixels correctly, we use a rough
             * estimation.
             */
            assertEquals(grid.getRow(0).getSize().getWidth(), grid
                    .getTableWrapper().getSize().getWidth(), accuracy);
        }
    }
}
