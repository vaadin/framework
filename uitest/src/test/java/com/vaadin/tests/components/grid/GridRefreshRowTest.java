package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridRefreshRowTest extends SingleBrowserTest {

    private GridElement grid;

    @Test
    public void refreshRow() {
        openTestURL();
        grid = $(GridElement.class).first();
        update(0);
        update(1);
        update(2);
        style(1);
        style(2);

        assertEquals("Lisa", grid.getCell(0, 1).getText());
        assertEquals("Joshua", grid.getCell(1, 1).getText());
        assertEquals("Marge", grid.getCell(2, 1).getText());

        assertFalse(hasCssClass(grid.getRow(0), "rowstyle"));
        assertFalse(hasCssClass(grid.getRow(1), "rowstyle"));
        assertFalse(hasCssClass(grid.getRow(2), "rowstyle"));
        assertFalse(hasCssClass(grid.getCell(0, 0), "cellstyle"));
        assertFalse(hasCssClass(grid.getCell(1, 0), "cellstyle"));
        assertFalse(hasCssClass(grid.getCell(2, 0), "cellstyle"));

        refresh(1);
        assertEquals("Lisa", grid.getCell(0, 1).getText());
        assertEquals("!Joshua", grid.getCell(1, 1).getText());
        assertEquals("Marge", grid.getCell(2, 1).getText());

        assertFalse(hasCssClass(grid.getRow(0), "rowstyle"));
        assertTrue(hasCssClass(grid.getRow(1), "rowstyle"));
        assertFalse(hasCssClass(grid.getRow(2), "rowstyle"));
        assertFalse(hasCssClass(grid.getCell(0, 0), "cellstyle"));
        assertTrue(hasCssClass(grid.getCell(1, 0), "cellstyle"));
        assertFalse(hasCssClass(grid.getCell(2, 0), "cellstyle"));

        // Assert refreshing works many times and for many rows at the same time
        update(0);
        update(1);
        update(2);
        refresh10First();
        assertEquals("!!Lisa", grid.getCell(0, 1).getText());
        assertEquals("!!Joshua", grid.getCell(1, 1).getText());
        assertEquals("!!Marge", grid.getCell(2, 1).getText());
    }

    private void refresh10First() {
        $(ButtonElement.class).id("refresh10").click();
    }

    private void update(int i) {
        $(ButtonElement.class).id("update" + i).click();
    }

    private void style(int i) {
        $(CheckBoxElement.class).id("style" + i).click();
    }

    private void refresh(int i) {
        $(ButtonElement.class).id("refresh" + i).click();
    }
}
