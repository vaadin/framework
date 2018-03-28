package com.vaadin.tests.components.grid;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridColumnShrinkSmallerThanContentsTest extends SingleBrowserTest {

    @Test
    public void scrollbarAndNoScrollbar() {
        openTestURL();
        GridElement noshrinkColumnGrid = $(GridElement.class).get(0);
        GridElement shrinkColumnGrid = $(GridElement.class).get(1);
        assertHorizontalScrollbar(noshrinkColumnGrid.getHorizontalScroller(),
                "Should have a horizontal scrollbar as column 2 should be wide");
        assertNoHorizontalScrollbar(shrinkColumnGrid.getHorizontalScroller(),
                "Should not have a horizontal scrollbar as column 2 should be narrow");
    }
}
