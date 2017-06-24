package com.vaadin.tests.components.grid;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertEquals;

/**
 * (c) elmot on 24.6.2017.
 */
@RunLocally
@TestCategory("grid")
public class GridColumnPresentationTest extends SingleBrowserTest {

    @Test
    public void presenterAndEditor() {
        openTestURL();
        GridElement grid = $(GridElement.class).get(0);
        assertEquals("Turku FINLAND", grid.getCell(0, 0).getText());
        assertEquals("Amsterdam NETHERLANDS", grid.getCell(1, 0).getText());
        //Activate editor
        GridElement.GridCellElement cell = grid.getCell(1, 0);
        cell.doubleClick();

        assertEquals("Address [streetAddress=Red street, postalCode=12, city=Amsterdam, country=Netherlands]",
                grid.getEditor().getField(0).getText());

    }
}
