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

        GridCellElement cell = $(GridElement.class).first().getCell(0, 4);
        cell.click(getXOffset(cell, 5), getYOffset(cell, 5));

        assertEquals(
                "1. Click on Person Nina Brown on column Column[propertyId:gender]",
                getLogRow(1));
     }
}
