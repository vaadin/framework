package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridFrozenColumnResetTest extends MultiBrowserTest {

    @Test
    public void testFrozenColumnReset() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        assertTrue(grid.getCell(0, 1).isFrozen());

        ButtonElement button = $(ButtonElement.class).first();
        button.click();

        assertTrue(grid.getCell(0, 1).isFrozen());
    }

}
