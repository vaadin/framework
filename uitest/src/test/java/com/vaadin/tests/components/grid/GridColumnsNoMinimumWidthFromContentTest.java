package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridColumnsNoMinimumWidthFromContentTest extends MultiBrowserTest {

    @Test
    public void testResizing() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        WebElement hScrollbar = grid
                .findElement(By.className("v-grid-scroller-horizontal"));

        // initial state, should have no scrollbar
        GridCellElement lastColumn = grid.getHeaderCell(0, 19);
        ensureScrollbarVisibility(hScrollbar, false);
        ensureNoGap(grid, lastColumn);

        // resize small enough to get a scrollbar
        getDriver().manage().window().setSize(new Dimension(810, 800));
        ensureScrollbarVisibility(hScrollbar, true);

        // resize just enough to lose the scrollbar
        getDriver().manage().window().setSize(new Dimension(840, 800));
        ensureScrollbarVisibility(hScrollbar, false);
        ensureNoGap(grid, lastColumn);

        int lastColumnWidth = lastColumn.getSize().getWidth();
        assertGreater("Unexpected last column width: " + lastColumnWidth
                + " (should be over 30)", lastColumnWidth, 30);
    }

    private void ensureNoGap(GridElement grid, GridCellElement lastColumn) {
        int gridRightEdge = grid.getLocation().getX()
                + grid.getSize().getWidth();
        int lastColumnRightEdge = lastColumn.getLocation().getX()
                + lastColumn.getSize().getWidth();
        assertThat("Unexpected positioning.", (double) gridRightEdge,
                closeTo(lastColumnRightEdge, 1d));
    }

    private void ensureScrollbarVisibility(WebElement scrollbar,
            boolean displayed) {
        assertEquals(displayed ? "block" : "none",
                scrollbar.getCssValue("display"));
    }
}
