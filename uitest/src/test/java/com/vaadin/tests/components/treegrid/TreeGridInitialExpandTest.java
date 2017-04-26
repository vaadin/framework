package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class TreeGridInitialExpandTest extends SingleBrowserTest {

    @Test
    public void initial_expand_of_items() {
        openTestURL();
        TreeGridElement grid = $(TreeGridElement.class).first();

        assertEquals("parent1", grid.getCell(0, 0).getText());
        assertEquals("parent1-child1", grid.getCell(1, 0).getText());
        assertEquals("parent1-child2", grid.getCell(2, 0).getText());
        assertEquals("parent2", grid.getCell(3, 0).getText());
        assertEquals("parent2-child2", grid.getCell(4, 0).getText());
    }
}
