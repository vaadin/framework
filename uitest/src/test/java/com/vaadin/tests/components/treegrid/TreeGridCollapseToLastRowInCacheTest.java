package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridCollapseToLastRowInCacheTest extends SingleBrowserTest {

    @Before
    public void open() {
        // need to remove more rows than what is in cache when collapsing
        openTestURL("?restartApplication&debug&"
                + TreeGridScrolling.NODES_PARAMETER + "=50");
    }

    @Override
    protected Class<?> getUIClass() {
        return TreeGridScrolling.class;
    }

    // #8840
    @Test
    public void testCollapsingNode_removesLastRowFromGridCache_noInternalError() {
        TreeGridElement grid = $(TreeGridElement.class).first();

        grid.expandWithClick(0);
        grid.expandWithClick(1);

        assertNoErrorNotifications();

        assertEquals("0 | 0", grid.getCell(0, 0).getText());
        assertEquals("1 | 0", grid.getCell(1, 0).getText());
        assertEquals("2 | 0", grid.getCell(2, 0).getText());

        grid.collapseWithClick(0);

        assertEquals("0 | 0", grid.getCell(0, 0).getText());
        assertEquals("0 | 1", grid.getCell(1, 0).getText());

        assertNoErrorNotifications();
    }

}
