package com.vaadin.tests.components.grid;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;

@TestCategory("grid")
public class GridExtensionCommunicationTest extends SingleBrowserTest {

    @Test
    public void testMouseClickIsSentToExtension() {
        openTestURL();

        GridCellElement cell = $(GridElement.class).first().getCell(0, 1);
        cell.click(getXOffset(cell, 5), getYOffset(cell, 5));

        int expectedX = cell.getLocation().getX() + 5;
        int expectedY = cell.getLocation().getY() + 4;

        assertEquals(
                "1. Click on Person first name 1 last name 1 on column second",
                getLogRow(1));
        assertEquals("2. MouseEventDetails: left (" + expectedX + ", "
                + expectedY + ")", getLogRow(0));

    }
}
