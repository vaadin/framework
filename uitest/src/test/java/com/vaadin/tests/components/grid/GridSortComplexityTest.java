package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridSortComplexityTest extends MultiBrowserTest {

    @Test
    public void testOperationCountUponSorting() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertEquals("2. FETCH", getLogRow(0));
        assertEquals("1. SIZE", getLogRow(1));

        grid.getHeaderCell(0, 0).click();
        assertEquals("5. FETCH", getLogRow(0));
        assertEquals("4. ON SORT: ASCENDING", getLogRow(1));
        assertEquals("3. SIZE", getLogRow(2));
    }
}
