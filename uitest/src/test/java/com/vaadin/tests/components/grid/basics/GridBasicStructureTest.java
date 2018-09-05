package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;

public class GridBasicStructureTest extends GridBasicsTest {

    @Test
    public void testFreezingColumn() throws Exception {
        // Freeze column 1
        selectMenuPath("Component", "State", "Frozen column count", "1");

        WebElement cell = getGridElement().getCell(0, 0);
        assertTrue("First cell on a row should be frozen",
                cell.getAttribute("class").contains("frozen"));

        assertFalse("Second cell on a row should not be frozen",
                getGridElement().getCell(0, 1).getAttribute("class")
                        .contains("frozen"));

        int cellX = cell.getLocation().getX();
        scrollGridHorizontallyTo(100);
        assertEquals("First cell should not move when scrolling", cellX,
                cell.getLocation().getX());
    }

    @Test
    public void testHeightByRows() throws Exception {
        int initialHeight = getGridElement().getSize().getHeight();

        selectMenuPath("Component", "Size", "HeightMode Row");
        selectMenuPath("Component", "Size", "Height by Rows", "2.00 rows");

        TestBenchElement tableWrapper = getGridElement().getTableWrapper();
        int rowHeight = getGridElement().getRow(0).getSize().getHeight();

        assertTrue("Grid height was not 3 rows", Math
                .abs(rowHeight * 3 - tableWrapper.getSize().getHeight()) < 2);

        selectMenuPath("Component", "Size", "Height by Rows", "3.33 rows");

        assertTrue("Grid height was not 4.33 rows", Math.abs(
                rowHeight * 4.33 - tableWrapper.getSize().getHeight()) < 2);

        selectMenuPath("Component", "Size", "HeightMode Row");
        assertEquals("Grid should have returned to its original size",
                initialHeight, getGridElement().getSize().getHeight());
    }

    @Test
    public void testHeightModeChanges() throws Exception {
        selectMenuPath("Component", "Size", "Height by Rows", "2.00 rows");

        TestBenchElement tableWrapper = getGridElement().getTableWrapper();
        int rowHeight = getGridElement().getRow(0).getSize().getHeight();

        assertTrue("Grid height mode did not become ROW", Math
                .abs(rowHeight * 3 - tableWrapper.getSize().getHeight()) < 2);

        selectMenuPath("Component", "Size", "Height", "200px");

        assertEquals("Grid height mode did not become CSS", 200,
                getGridElement().getSize().getHeight());

    }
}
