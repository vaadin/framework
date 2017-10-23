package com.vaadin.tests.components.grid;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@TestCategory("grid")
public class GridComponentRendererClickTest extends MultiBrowserTest {

    @Test
    public void testComponentRendererClickIsForwardedToGrid() throws Exception {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        clickGridElement(grid, 0, 0, By.id("label_to_click"));

        assertTrue(grid.getRow(0).isSelected());
    }

    @Test
    public void testButtonRendererClickIsForwardedToGrid() throws Exception {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        clickGridElement(grid, 1, 1, org.openqa.selenium.By.tagName("button"));

        assertTrue(grid.getRow(1).isSelected());
    }

    @Test
    public void testSelectionChangesCorrectly() throws Exception {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        // ComponentRenderer click first in first row
        clickGridElement(grid, 0, 0, By.id("label_to_click"));
        assertTrue(grid.getRow(0).isSelected());
        // ButtonRenderer click in next row
        clickGridElement(grid, 1, 1, org.openqa.selenium.By.tagName("button"));
        assertTrue(grid.getRow(1).isSelected());
        assertFalse(grid.getRow(0).isSelected());
    }

    private void clickGridElement(GridElement grid, int row, int column, org.openqa.selenium.By cellBy) {
        GridElement.GridCellElement buttonCell = grid.getCell(row, column);
        buttonCell.findElement(cellBy).click();
    }

}
