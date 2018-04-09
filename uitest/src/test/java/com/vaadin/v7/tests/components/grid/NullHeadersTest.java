package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NullHeadersTest extends SingleBrowserTest {

    @Test
    public void gridWithNullHeadersShouldBeRendered() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertEquals(1, grid.getHeaderCount());
        assertEquals(3, grid.getHeaderCells(0).size());
        for (int i = 0; i < 3; i++) {
            assertEquals("", grid.getHeaderCell(0, 0).getText());
        }
        assertRow(grid, 0, "Finland", "foo", "1");
        assertRow(grid, 1, "Swaziland", "bar", "2");
        assertRow(grid, 2, "Japan", "baz", "3");
    }

    private void assertRow(GridElement grid, int row, String... contents) {
        for (int col = 0; col < contents.length; col++) {
            assertEquals(contents[col], grid.getCell(row, col).getText());
        }

    }
}
