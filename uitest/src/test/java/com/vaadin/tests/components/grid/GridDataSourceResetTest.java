package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridDataSourceResetTest extends SingleBrowserTest {

    @Test
    public void testRemoveWithSelectUpdatesRowsCorrectly() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertTrue("First row was not selected", grid.getRow(0).isSelected());
        for (int i = 1; i < 10; ++i) {
            assertFalse("Only first row should be selected",
                    grid.getRow(i).isSelected());
        }

        $(ButtonElement.class).first().click();

        assertTrue("First row was not selected after remove",
                grid.getRow(0).isSelected());
        for (int i = 1; i < 9; ++i) {
            assertFalse("Only first row should be selected after remove",
                    grid.getRow(i).isSelected());
        }
    }
}
