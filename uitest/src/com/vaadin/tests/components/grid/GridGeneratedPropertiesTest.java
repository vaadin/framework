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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridGeneratedPropertiesTest extends MultiBrowserTest {

    @Test
    public void testMilesColumnExists() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertEquals("Miles header wasn't present.", "miles", grid
                .getHeaderCell(0, 2).getText().toLowerCase());
    }

    @Test
    public void testUnsortableGeneratedProperty() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Overwritten foo property should not be sortable
        GridCellElement fooHeader = grid.getHeaderCell(0, 0);
        fooHeader.click();
        assertFalse("Column foo was unexpectedly sorted.", fooHeader
                .getAttribute("class").contains("sort"));

        // Generated property miles is not sortable
        GridCellElement milesHeader = grid.getHeaderCell(0, 2);
        milesHeader.click();
        assertFalse("Column miles was unexpectedly sorted.", milesHeader
                .getAttribute("class").contains("sort"));
    }

    @Test
    public void testSortableGeneratedProperty() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Generated property baz is sortable
        GridCellElement bazHeader = grid.getHeaderCell(0, 3);
        bazHeader.click();
        assertTrue("Column baz was not sorted ascending", bazHeader
                .getAttribute("class").contains("sort-asc"));
        bazHeader.click();
        assertTrue("Column baz was not sorted descending", bazHeader
                .getAttribute("class").contains("sort-desc"));
    }

    @Test
    public void testInitialSorting() {
        // Grid is sorted in this case by one visible and one nonexistent
        // column. There should be no sort indicator.
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        GridCellElement kmHeader = grid.getHeaderCell(0, 1);
        assertFalse("Column km was unexpectedly sorted",
                kmHeader.getAttribute("class").contains("sort-asc")
                        || kmHeader.getAttribute("class").contains("sort-desc"));
        assertFalse("Unexpected client-side exception was visible",
                isElementPresent(NotificationElement.class));
    }
}
