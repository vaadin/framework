package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CompatibilityGridToggleMultiSelectSortTest
        extends SingleBrowserTest {

    @Test
    public void sortFirstColumnAfterToggle() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        ButtonElement button = $(ButtonElement.class).first();

        button.click();

        assertEquals("Unexpected initial sorting.", "0",
                grid.getCell(0, 0).getText());

        GridCellElement headerCell = grid.getHeaderCell(0, 0);

        // sort ascending
        headerCell.click();
        assertEquals("Unexpected first sorting.", "0",
                grid.getCell(0, 0).getText());

        // sort descending
        headerCell.click();
        assertEquals("Unexpected second sorting.", "99.9",
                grid.getCell(0, 0).getText());
    }

}
