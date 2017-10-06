package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridSubclassMouseEventTest extends SingleBrowserTest {

    @Test
    public void selectRowWithMouseClick() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        GridRowElement row = grid.getRow(0);
        Assert.assertTrue("Row should not be selected", !row.isSelected());
        grid.getCell(0, 0).click();
        Assert.assertTrue("Row should be selected", row.isSelected());
    }
}
