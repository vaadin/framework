package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridAddRowTest extends MultiBrowserTest {
    @Test
    public void testAddRow() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        Assert.assertEquals("Lorem", grid.getCell(0, 1).getText());
        Assert.assertEquals("2", grid.getCell(1, 2).getText());

        addRow();

        Assert.assertEquals("Dolor", grid.getCell(2, 1).getText());

        addRow();

        Assert.assertEquals("Dolor", grid.getCell(3, 1).getText());
    }

    private void addRow() {
        $(ButtonElement.class).caption("Add new row").first().click();
    }

}
