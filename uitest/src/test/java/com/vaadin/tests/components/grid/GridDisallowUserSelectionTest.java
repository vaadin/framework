package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class GridDisallowUserSelectionTest extends MultiBrowserTest {

    @Test
    public void checkSelection() {
        openTestURL();

        assertNoSelection(0);
        assertNoSelection(1);

        // change model from single select to mutli
        $(ButtonElement.class).first().click();

        assertNoSelection(0);
        assertNoSelection(1);
    }

    private void assertNoSelection(int index) {
        GridElement grid = $(GridElement.class).first();
        grid.getCell(index, 0).click();

        assertFalse(grid.getRow(0).isSelected());
    }
}
