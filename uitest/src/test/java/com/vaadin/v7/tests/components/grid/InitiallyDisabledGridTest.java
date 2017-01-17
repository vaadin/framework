package com.vaadin.v7.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class InitiallyDisabledGridTest extends SingleBrowserTest {

    @Test
    public void columnsExpanded() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement col0 = grid.getCell(0, 0);
        GridCellElement col1 = grid.getCell(0, 1);
        Assert.assertTrue(col0.getSize().getWidth() > 250);
        Assert.assertTrue(col1.getSize().getWidth() > 250);
    }

    @Test
    public void worksWhenEnabled() {
        openTestURL();
        $(ButtonElement.class).first().click();

        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(80);
        GridCellElement col0 = grid.getCell(80, 0);
        Assert.assertEquals("First 80", col0.getText());
    }
}
