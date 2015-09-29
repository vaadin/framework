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

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

@TestCategory("grid")
public class GridColumnResizeTest extends GridBasicFeaturesTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testResizeHandlesPresentInDefaultHeader() {
        for (int i = 0; i < GridBasicFeatures.COLUMNS; ++i) {
            assertResizable(i, true);
        }
    }

    @Test
    public void testResizeHandlesNotInNonDefaultHeader() {
        selectMenuPath("Component", "Header", "Prepend row");

        for (int i = 0; i < GridBasicFeatures.COLUMNS; ++i) {
            assertResizable(getGridElement().getHeaderCell(0, i), false);
            assertResizable(getGridElement().getHeaderCell(1, i), true);
        }
    }

    @Test
    public void testResizeHandlesNotInFooter() {
        selectMenuPath("Component", "Footer", "Visible");
        for (int i = 0; i < GridBasicFeatures.COLUMNS; ++i) {
            assertResizable(getGridElement().getFooterCell(0, i), false);
        }
    }

    @Test
    public void testToggleSetResizable() {
        selectMenuPath("Component", "Columns", "Column 1", "Resizable");

        for (int i = 0; i < GridBasicFeatures.COLUMNS; ++i) {
            assertResizable(i, i != 1);
        }

        selectMenuPath("Component", "Columns", "Column 1", "Resizable");

        for (int i = 0; i < GridBasicFeatures.COLUMNS; ++i) {
            assertResizable(i, true);
        }
    }

    private void assertResizable(int columnIndex, boolean resizable) {
        assertResizable(getGridElement().getHeaderCell(0, columnIndex),
                resizable);
    }

    private void assertResizable(GridCellElement cell, boolean resizable) {
        assertEquals("Header resize handle present", resizable,
                cell.isElementPresent(By
                        .cssSelector("div.v-grid-column-resize-handle")));
    }
}
