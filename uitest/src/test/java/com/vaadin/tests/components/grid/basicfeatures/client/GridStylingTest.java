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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;

public class GridStylingTest extends GridStaticSectionTest {

    @Test
    public void testGridPrimaryStyle() throws Exception {
        openTestURL();

        validateStylenames("v-grid");
    }

    @Test
    public void testChangingPrimaryStyleName() throws Exception {
        openTestURL();

        selectMenuPath("Component", "State", "Primary Stylename",
                "v-custom-style");

        validateStylenames("v-custom-style");
    }

    private void validateStylenames(String stylename) {

        String classNames = getGridElement().getAttribute("class");
        assertEquals(stylename, classNames);

        classNames = getGridElement().getVerticalScroller().getAttribute(
                "class");
        assertTrue(classNames.contains(stylename + "-scroller"));
        assertTrue(classNames.contains(stylename + "-scroller-vertical"));

        classNames = getGridElement().getHorizontalScroller().getAttribute(
                "class");
        assertTrue(classNames.contains(stylename + "-scroller"));
        assertTrue(classNames.contains(stylename + "-scroller-horizontal"));

        classNames = getGridElement().getTableWrapper().getAttribute("class");
        assertEquals(stylename + "-tablewrapper", classNames);

        classNames = getGridElement().getHeader().getAttribute("class");
        assertEquals(stylename + "-header", classNames);

        for (int row = 0; row < getGridElement().getHeaderCount(); row++) {
            classNames = getGridElement().getHeaderRow(row).getAttribute(
                    "class");
            assertEquals(stylename + "-row", classNames);

            for (int col = 0; col < GridBasicFeatures.COLUMNS; col++) {
                classNames = getGridElement().getHeaderCell(row, col)
                        .getAttribute("class");
                assertTrue(classNames.contains(stylename + "-cell"));
            }
        }

        classNames = getGridElement().getBody().getAttribute("class");
        assertEquals(stylename + "-body", classNames);

        int rowsInBody = getGridElement().getBody()
                .findElements(By.tagName("tr")).size();
        for (int row = 0; row < rowsInBody; row++) {
            classNames = getGridElement().getRow(row).getAttribute("class");
            assertTrue(classNames.contains(stylename + "-row"));
            assertTrue(classNames.contains(stylename + "-row-has-data"));

            for (int col = 0; col < GridBasicFeatures.COLUMNS; col++) {
                classNames = getGridElement().getCell(row, col).getAttribute(
                        "class");
                assertTrue(classNames.contains(stylename + "-cell"));

                if (row == 0 && col == 0) {
                    assertTrue(classNames.contains(stylename + "-cell-focused"));
                }
            }
        }

        classNames = getGridElement().getFooter().getAttribute("class");
        assertEquals(stylename + "-footer", classNames);

        for (int row = 0; row < getGridElement().getFooterCount(); row++) {
            classNames = getGridElement().getFooterRow(row).getAttribute(
                    "class");
            assertEquals(stylename + "-row", classNames);

            for (int col = 0; col < GridBasicFeatures.COLUMNS; col++) {
                classNames = getGridElement().getFooterCell(row, col)
                        .getAttribute("class");
                assertTrue(classNames.contains(stylename + "-cell"));
            }
        }
    }
}
