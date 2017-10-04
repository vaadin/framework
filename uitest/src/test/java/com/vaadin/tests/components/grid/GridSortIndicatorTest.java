/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridSortIndicatorTest extends SingleBrowserTest {

    @Test
    public void testIndicators() throws InterruptedException {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        $(ButtonElement.class).caption("Sort both").first().click();
        assertTrue("First column should be sorted ascending",
                grid.getHeaderCell(0, 0).getAttribute("class")
                        .contains("sort-asc"));
        assertEquals("First column should have aria-sort other", "other",
                grid.getHeaderCell(0, 0).getAttribute("aria-sort"));
        assertEquals("First column should be first in sort order", "1",
                grid.getHeaderCell(0, 0).getAttribute("sort-order"));
        assertTrue("Second column should be sorted ascending",
                grid.getHeaderCell(0, 1).getAttribute("class")
                        .contains("sort-asc"));
        assertEquals("Second column should have aria-sort other", "other",
                grid.getHeaderCell(0, 1).getAttribute("aria-sort"));
        assertEquals("Second column should be also sorted", "2",
                grid.getHeaderCell(0, 1).getAttribute("sort-order"));

        $(ButtonElement.class).caption("Sort first").first().click();
        assertEquals("First column should have aria-sort ascending",
                "ascending",
                grid.getHeaderCell(0, 0).getAttribute("aria-sort"));
        assertTrue("First column should be sorted ascending",
                grid.getHeaderCell(0, 0).getAttribute("class")
                        .contains("sort-asc"));
        assertEquals("Second column should have aria-sort none", "none",
                grid.getHeaderCell(0, 1).getAttribute("aria-sort"));
        assertFalse("Second column should not be sorted",
                grid.getHeaderCell(0, 1).getAttribute("class")
                        .contains("sort-asc"));
    }
}
