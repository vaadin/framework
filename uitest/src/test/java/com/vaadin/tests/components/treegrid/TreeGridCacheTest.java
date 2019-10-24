package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridCacheTest extends MultiBrowserTest {

    @Test
    public void testGrid1() {
        openTestURL();
        $(ButtonElement.class).id("button1").click();
        TreeGridElement grid = $(TreeGridElement.class).first();
        grid.expandWithClick(1);
        grid.expandWithClick(5);
        GridCellElement cell = grid.getCell(10, 1);
        assertEquals("-4", cell.getText());

        cell.click();
        assertEquals(0, $(NotificationElement.class).all().size());
    }

    @Test
    public void testGrid2() {
        openTestURL();
        $(ButtonElement.class).id("button2").click();
        TreeGridElement grid = $(TreeGridElement.class).first();
        grid.expandWithClick(1);
        GridCellElement cell = grid.getCell(10, 1);
        assertEquals("leaf value8", cell.getText());

        cell.click();
        assertEquals(0, $(NotificationElement.class).all().size());
    }
}
