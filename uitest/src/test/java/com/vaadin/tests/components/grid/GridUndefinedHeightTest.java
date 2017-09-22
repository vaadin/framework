package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridUndefinedHeightTest extends SingleBrowserTest {

    @Before
    public void before() {
        setDebug(true);
        openTestURL();
    }

    @Test
    public void grid_undefined_height() {
        GridElement grid = $(GridElement.class).first();
        int oneRow = grid.getRow(0).getSize().getHeight();
        int gridHeight = grid.getSize().getHeight();
        int rows = 4; // Header Row + 3 Body Rows

        Assert.assertEquals("Grid height mismatch", oneRow * rows, gridHeight);

        assertNoErrorNotifications();
    }

    @Test
    public void gridv7_undefined_height() {
        GridElement grid = $(GridElement.class).get(1);
        int oneRow = grid.getRow(0).getSize().getHeight();
        int gridHeight = grid.getSize().getHeight();
        int rows = 4; // Header Row + 3 Body Rows

        Assert.assertEquals("Grid height mismatch", oneRow * rows, gridHeight);

        assertNoErrorNotifications();
    }

    @Test
    public void grid_undefined_height_add_header() {
        // Add header row to Grid
        $(ButtonElement.class).first().click();

        GridElement grid = $(GridElement.class).first();
        int oneRow = grid.getRow(0).getSize().getHeight();
        int gridHeight = grid.getSize().getHeight();
        int rows = 5; // 2 Header Rows + 3 Body Rows

        Assert.assertEquals("Grid height mismatch", oneRow * rows, gridHeight);

        assertNoErrorNotifications();
    }

    @Test
    public void gridv7_undefined_height_add_header() {
        // Add header row to Grid
        $(ButtonElement.class).first().click();

        GridElement grid = $(GridElement.class).get(1);
        int oneRow = grid.getRow(0).getSize().getHeight();
        int gridHeight = grid.getSize().getHeight();
        int rows = 5; // 2 Header Rows + 3 Body Rows

        Assert.assertEquals("Grid height mismatch", oneRow * rows, gridHeight);

        assertNoErrorNotifications();
    }
}
