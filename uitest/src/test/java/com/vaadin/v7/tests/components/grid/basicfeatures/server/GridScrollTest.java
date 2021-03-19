package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridScrollTest extends GridBasicFeaturesTest {

    @Test
    public void testCorrectItemRequestsOnScroll() {
        openTestURL();

        assertTrue("Initial push from server not found",
                getLogRow(1).equals("0. Requested items 0 - 40"));
        // Client response varies a bit between browsers as different amount of
        // rows is cached.
        assertTrue("First row request from client not found",
                getLogRow(0).startsWith("1. Requested items 0 - "));

        selectMenuPath("Component", "Size", "HeightMode Row");

        selectMenuPath("Settings", "Clear log");
        $(GridElement.class).first().scrollToRow(40);
        assertEquals("Log row did not contain expected item request",
                "0. Requested items 0 - 91", getLogRow(0));
        assertEquals("There should be only one log row", " ", getLogRow(1));
        selectMenuPath("Settings", "Clear log");
        $(GridElement.class).first().scrollToRow(100);
        assertEquals("Log row did not contain expected item request",
                "0. Requested items 43 - 151", getLogRow(0));
        assertEquals("There should be only one log row", " ", getLogRow(1));
        selectMenuPath("Settings", "Clear log");
        $(GridElement.class).first().scrollToRow(300);
        assertEquals("Log row did not contain expected item request",
                "0. Requested items 243 - 351", getLogRow(0));
        assertEquals("There should be only one log row", " ", getLogRow(1));
    }

    @Test
    public void workPendingWhileScrolling() {
        openTestURL("theme=valo");
        String script = "var c = window.vaadin.clients.runcomvaadinv7testscomponentsgridbasicfeaturesGridBasicFeatures;\n"
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
