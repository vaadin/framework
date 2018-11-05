package com.vaadin.tests.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridRefreshWithGetIdTest extends SingleBrowserTest {

    @Test
    public void testDataIdentifiedAndUpdated() {
        openTestURL();
        // Select item
        GridElement grid = $(GridElement.class).first();
        grid.getCell(2, 0).click();
        assertTrue("Row should be selected", grid.getRow(2).isSelected());
        assertEquals("green", grid.getCell(2, 0).getText());
        $(ButtonElement.class).first().click();
        assertTrue("Row was no longer selected", grid.getRow(2).isSelected());
        assertEquals("black", grid.getCell(2, 0).getText());
    }

}
