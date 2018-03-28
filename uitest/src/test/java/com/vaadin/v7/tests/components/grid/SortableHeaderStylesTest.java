package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SortableHeaderStylesTest extends SingleBrowserTest {
    @Test
    public void testSortableHeaderStyles() {
        openTestURL();

        assertFalse(hasSortableStyle(0));
        for (int i = 1; i < 8; i++) {
            assertTrue(hasSortableStyle(i));
        }

        OptionGroupElement sortableSelector = $(OptionGroupElement.class)
                .first();

        // Toggle sortability
        sortableSelector.selectByText("lastName");
        assertFalse(hasSortableStyle(3));

        // Toggle back
        sortableSelector.selectByText("lastName");
        assertTrue(hasSortableStyle(3));
    }

    private boolean hasSortableStyle(int column) {
        return $(GridElement.class).first().getHeaderCell(0, column)
                .getAttribute("class").contains("sortable");
    }
}
