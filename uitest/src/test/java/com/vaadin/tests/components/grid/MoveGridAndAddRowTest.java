package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

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
        assertEquals("1", grid.getCell(0, 0).getText());
        assertEquals("2", grid.getCell(1, 0).getText());
    }
}
