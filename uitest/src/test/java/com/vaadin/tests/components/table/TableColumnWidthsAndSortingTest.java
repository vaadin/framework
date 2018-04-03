package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableColumnWidthsAndSortingTest extends MultiBrowserTest {

    @Test
    public void testHeaderHeight() {
        openTestURL();
        TableElement t = $(TableElement.class).first();

        assertHeaderCellHeight(t);

        // Sort according to age
        t.getHeaderCell(2).click();
        assertHeaderCellHeight(t);

        // Sort again according to age
        t.getHeaderCell(2).click();
        assertHeaderCellHeight(t);

    }

    private void assertHeaderCellHeight(TableElement t) {
        // Assert all headers are correct height (37px according to default
        // Valo)
        for (int i = 0; i < 5; i++) {
            assertEquals("Height of header cell " + i + " is wrong", 37,
                    t.getHeaderCell(0).getSize().getHeight());
        }

    }
}
