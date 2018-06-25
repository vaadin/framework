package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridColumnAutoExpandTest extends MultiBrowserTest {

    @Test
    public void testSecondColumnHasExpanded() {
        openTestURL();

        GridCellElement headerCell = $(GridElement.class).first()
                .getHeaderCell(0, 1);

        assertTrue("Column did not expand as expected",
                headerCell.getSize().getWidth() > 400);
    }

}
