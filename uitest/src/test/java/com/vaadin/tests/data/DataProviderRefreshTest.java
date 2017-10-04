package com.vaadin.tests.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DataProviderRefreshTest extends SingleBrowserTest {

    @Test
    public void select_and_replace() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertFalse("Row should not be initially selected",
                grid.getRow(0).isSelected());
        // Select item before replace
        $(ButtonElement.class).caption("Select old").first().click();
        assertTrue("Row should be selected", grid.getRow(0).isSelected());

        $(ButtonElement.class).caption("Replace item").first().click();
        assertTrue("Row should still be selected after item replace",
                grid.getRow(0).isSelected());
        assertEquals("Grid content was not updated.", "{ Bar, 10 }",
                grid.getCell(0, 0).getText());

        // Deselect row
        grid.getCell(0, 0).click();
        assertFalse("Row should be deselected after click",
                grid.getRow(0).isSelected());

        assertEquals("Second row was affected", "{ Baz, 11 }",
                grid.getCell(1, 0).getText());
    }

    @Test
    public void replace_and_select() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertFalse("Row should not be initially selected",
                grid.getRow(0).isSelected());

        // Replace item before select
        $(ButtonElement.class).caption("Replace item").first().click();
        assertFalse("Row should not be selected after item replace",
                grid.getRow(0).isSelected());
        assertEquals("Grid content was not updated.", "{ Bar, 10 }",
                grid.getCell(0, 0).getText());

        $(ButtonElement.class).caption("Select old").first().click();
        assertTrue("Row should be selected", grid.getRow(0).isSelected());
        assertEquals("Grid content should not update.", "{ Bar, 10 }",
                grid.getCell(0, 0).getText());

        // Deselect row
        grid.getCell(0, 0).click();
        assertFalse("Row should be deselected after click",
                grid.getRow(0).isSelected());

        assertEquals("Second row was affected", "{ Baz, 11 }",
                grid.getCell(1, 0).getText());
    }
}
