package com.vaadin.tests.components.grid;

import org.junit.Assert;
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

        Assert.assertEquals("Lisa", grid.getCell(0, 1).getText());
        Assert.assertEquals("Joshua", grid.getCell(1, 1).getText());
        Assert.assertEquals("Marge", grid.getCell(2, 1).getText());

        Assert.assertFalse(hasCssClass(grid.getRow(0), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getRow(1), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getRow(2), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(0, 0), "cellstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(1, 0), "cellstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(2, 0), "cellstyle"));

        refresh(1);
        Assert.assertEquals("Lisa", grid.getCell(0, 1).getText());
        Assert.assertEquals("!Joshua", grid.getCell(1, 1).getText());
        Assert.assertEquals("Marge", grid.getCell(2, 1).getText());

        Assert.assertFalse(hasCssClass(grid.getRow(0), "rowstyle"));
        Assert.assertTrue(hasCssClass(grid.getRow(1), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getRow(2), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(0, 0), "cellstyle"));
        Assert.assertTrue(hasCssClass(grid.getCell(1, 0), "cellstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(2, 0), "cellstyle"));

        // Assert refreshing works many times and for many rows at the same time
        update(0);
        update(1);
        update(2);
        refresh10First();
        Assert.assertEquals("!!Lisa", grid.getCell(0, 1).getText());
        Assert.assertEquals("!!Joshua", grid.getCell(1, 1).getText());
        Assert.assertEquals("!!Marge", grid.getCell(2, 1).getText());
    }

    @Test
    public void refreshAllRows() {
        openTestURL();
        grid = $(GridElement.class).first();
        update(0);
        update(1);
        update(2);
        style(1);
        style(2);

        Assert.assertEquals("Lisa", grid.getCell(0, 1).getText());
        Assert.assertEquals("Joshua", grid.getCell(1, 1).getText());
        Assert.assertEquals("Marge", grid.getCell(2, 1).getText());

        Assert.assertFalse(hasCssClass(grid.getRow(0), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getRow(1), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getRow(2), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(0, 0), "cellstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(1, 0), "cellstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(2, 0), "cellstyle"));

        refreshAll();
        Assert.assertEquals("!Lisa", grid.getCell(0, 1).getText());
        Assert.assertEquals("!Joshua", grid.getCell(1, 1).getText());
        Assert.assertEquals("!Marge", grid.getCell(2, 1).getText());

        Assert.assertFalse(hasCssClass(grid.getRow(0), "rowstyle"));
        Assert.assertTrue(hasCssClass(grid.getRow(1), "rowstyle"));
        Assert.assertTrue(hasCssClass(grid.getRow(2), "rowstyle"));
        Assert.assertFalse(hasCssClass(grid.getCell(0, 0), "cellstyle"));
        Assert.assertTrue(hasCssClass(grid.getCell(1, 0), "cellstyle"));
        Assert.assertTrue(hasCssClass(grid.getCell(2, 0), "cellstyle"));
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

    private void refreshAll() {
        $(ButtonElement.class).id("refreshAll").click();
    }
}
