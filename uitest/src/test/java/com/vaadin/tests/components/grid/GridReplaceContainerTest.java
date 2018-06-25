package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridReplaceContainerTest extends SingleBrowserTest {

    @Test
    public void selectAfterContainerChange() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 0).click();
        Assert.assertTrue(grid.getRow(0).isSelected());

        $(ButtonElement.class).first().click();
        Assert.assertFalse(grid.getRow(0).isSelected());
        grid.getCell(0, 0).click();
        Assert.assertTrue(grid.getRow(0).isSelected());
    }
}
