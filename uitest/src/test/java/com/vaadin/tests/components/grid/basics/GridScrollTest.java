package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;

public class GridScrollTest extends GridBasicsTest {

    @Test
    public void workPendingWhileScrolling() {
        openTestURL("theme=valo");
        String script = "var c = window.vaadin.clients.runcomvaadintestscomponentsgridbasicsGridBasics;\n"
                // Scroll down and cause lazy loading
                + "c.getElementByPath(\"//Grid[0]#cell[21]\"); \n"
                + "return c.isActive();";

        Boolean active = (Boolean) executeScript(script);
        assertTrue("Grid should be marked to have workPending while scrolling",
                active);
    }

    @Test
    public void scrollIntoViewThroughSubPart() {
        openTestURL("theme=valo");
        GridElement grid = $(GridElement.class).first();
        assertEquals("(10, 0)", grid.getCell(10, 0).getText());
    }
}
