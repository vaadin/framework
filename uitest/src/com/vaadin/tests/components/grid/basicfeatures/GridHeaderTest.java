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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;

public class GridHeaderTest extends GridBasicClientFeaturesTest {

    @Test
    public void testHeaderVisibility() throws Exception {
        openTestURL();

        // Column headers should be visible by default
        List<TestBenchElement> cells = getGridHeaderRowCells();
        assertEquals(GridBasicFeatures.COLUMNS, cells.size());
    }

    @Test
    public void testHeaderCaptions() throws Exception {
        openTestURL();

        List<TestBenchElement> cells = getGridHeaderRowCells();

        int i = 0;
        for (TestBenchElement cell : cells) {
            assertText("Column " + i, cell);
            i++;
        }
    }

    @Test
    public void testHeadersWithInvisibleColumns() throws Exception {
        openTestURL();

        selectMenuPath("Component", "Columns", "Column 1", "Visible");
        selectMenuPath("Component", "Columns", "Column 3", "Visible");

        List<TestBenchElement> cells = getGridHeaderRowCells();
        assertEquals(GridBasicFeatures.COLUMNS - 2, cells.size());

        assertText("Column 0", cells.get(0));
        assertText("Column 2", cells.get(1));
        assertText("Column 4", cells.get(2));

        selectMenuPath("Component", "Columns", "Column 3", "Visible");

        cells = getGridHeaderRowCells();
        assertEquals(GridBasicFeatures.COLUMNS - 1, cells.size());

        assertText("Column 0", cells.get(0));
        assertText("Column 2", cells.get(1));
        assertText("Column 3", cells.get(2));
        assertText("Column 4", cells.get(3));
    }

    private static void assertText(String text, TestBenchElement e) {
        // TBE.getText returns "" if the element is scrolled out of view
        assertEquals(text, e.getAttribute("innerHTML"));
    }
}
