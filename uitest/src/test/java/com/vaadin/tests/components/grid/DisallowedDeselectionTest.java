package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DisallowedDeselectionTest extends MultiBrowserTest {

    @Test
    public void checkDeselection() {
        openTestURL();

        GridRowElement row = $(GridElement.class).first().getRow(0);
        assertFalse(row.isSelected());

        select(row);
        assertTrue(row.isSelected());

        // deselection is disallowed
        select(row);
        assertTrue(row.isSelected());

        // select another row
        GridRowElement oldRow = row;
        row = $(GridElement.class).first().getRow(1);
        select(row);
        assertTrue(row.isSelected());
        assertFalse(oldRow.isSelected());

        $(ButtonElement.class).first().click();

        select(row);
        assertFalse(row.isSelected());
    }

    private void select(GridRowElement row) {
        row.getCell(0).click();
    }
}
