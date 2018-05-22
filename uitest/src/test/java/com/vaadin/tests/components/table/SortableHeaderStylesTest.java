package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.OptionGroupElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SortableHeaderStylesTest extends SingleBrowserTest {
    @Test
    public void testSortableHeaderStyles() {
        openTestURL();

        Assert.assertFalse(hasSortableStyle(0));
        for (int i = 1; i < 8; i++) {
            Assert.assertTrue(hasSortableStyle(i));
        }

        OptionGroupElement sortableSelector = $(OptionGroupElement.class)
                .first();

        // Toggle sortability
        sortableSelector.selectByText("lastName");
        Assert.assertFalse(hasSortableStyle(3));

        // Toggle back
        sortableSelector.selectByText("lastName");
        Assert.assertTrue(hasSortableStyle(3));
    }

    private boolean hasSortableStyle(int column) {
        return $(TableElement.class).first().getHeaderCell(column)
                .getAttribute("class").contains("sortable");
    }
}
