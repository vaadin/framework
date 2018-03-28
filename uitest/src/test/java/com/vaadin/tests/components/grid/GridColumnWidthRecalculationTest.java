package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridColumnWidthRecalculationTest extends SingleBrowserTest {

    private GridElement grid;

    @Before
    public void open() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void columnWidthAfterSwap() {
        int column0Width = getColumnWidth(0);
        int column1Width = getColumnWidth(1);
        Assert.assertTrue("Column 0 should be narrower than column 1 initially",
                column0Width < column1Width);

        $(ButtonElement.class).caption("Swap content").first().click();

        Assert.assertEquals(
                "Column 0 width should not change when swapping contents only",
                column0Width, getColumnWidth(0));
        Assert.assertEquals(
                "Column 1 width should not change when swapping contents only",
                column1Width, getColumnWidth(1));
    }

    @Test
    public void columnWidthAfterSwapAndRecalculate() {
        int column0Width = getColumnWidth(0);
        int column1Width = getColumnWidth(1);
        Assert.assertTrue("Column 0 should be narrower than column 1 initially",
                column0Width < column1Width);

        $(ButtonElement.class).caption("Swap content and recalculate columns")
                .first().click();

        column0Width = getColumnWidth(0);
        column1Width = getColumnWidth(1);

        Assert.assertTrue(
                "Column 1 should be narrower than column 0 after resize",
                column1Width < column0Width);
    }

    private int getColumnWidth(int columnIndex) {
        GridCellElement headerColumn = grid.getHeaderCells(0).get(columnIndex);
        Dimension column1Size = headerColumn.getSize();
        int columnWidth = column1Size.getWidth();
        return columnWidth;
    }

}
