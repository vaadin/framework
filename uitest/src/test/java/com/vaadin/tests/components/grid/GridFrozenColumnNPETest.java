package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridFrozenColumnNPETest extends MultiBrowserTest {

    @Test
    public void testFrozenColumnNPE() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        assertTrue(grid.getCell(0, 0).isFrozen());

        ButtonElement button = $(ButtonElement.class).first();
        button.click();

        assertTrue(grid.getCell(0, 1).isFrozen());
    }

}
