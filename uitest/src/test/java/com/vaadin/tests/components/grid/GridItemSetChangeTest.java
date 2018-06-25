package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridItemSetChangeTest extends SingleBrowserTest {

    @Test
    public void testValueChangeListenersWorkAfterItemSetChange() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Last name initially wrong", "Bar",
                grid.getCell(0, 1).getText());

        $(ButtonElement.class).caption("Modify").first().click();
        assertEquals("Last name was not updated", "Spam",
                grid.getCell(0, 1).getText());

        $(ButtonElement.class).caption("Reset").first().click();
        assertEquals("Last name was not updated on reset", "Baz",
                grid.getCell(0, 1).getText());

        $(ButtonElement.class).caption("Modify").first().click();
        assertEquals("Last name was not updated after reset modification",
                "Spam", grid.getCell(0, 1).getText());
    }
}
