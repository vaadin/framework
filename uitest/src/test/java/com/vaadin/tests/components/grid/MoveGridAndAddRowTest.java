package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MoveGridAndAddRowTest extends SingleBrowserTest {

    @Test
    public void addRowAndChangeLayout() {
        openTestURL();
        $(ButtonElement.class).id("add").click();

        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals("1", grid.getCell(0, 0).getText());
        Assert.assertEquals("2", grid.getCell(1, 0).getText());
    }
}
