package com.vaadin.tests.smoke;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSmokeTest extends MultiBrowserTest {
    @Test
    public void testAddRow() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertEquals("Lorem", grid.getCell(0, 1).getText());
        assertEquals("2", grid.getCell(1, 2).getText());

        addRow();

        assertEquals("Dolor", grid.getCell(2, 1).getText());

        addRow();

        assertEquals("Dolor", grid.getCell(3, 1).getText());
    }

    private void addRow() {
        $(ButtonElement.class).caption("Add new row").first().click();
    }

}
