package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class NullRenderersTest extends MultiBrowserTest {
    @Test
    public void testDefaults() throws Exception {
        openTestURL();

        GridElement grid = findGridWithDefaults();
        assertEquals("-- No Text --", grid.getCell(0, 0).getText());
        assertEquals("-- No Jokes --", grid.getCell(0, 1).getText());
        assertEquals("-- Never --", grid.getCell(0, 2).getText());
        assertEquals("-- Nothing --", grid.getCell(0, 3).getText());
        assertEquals("-- No Control --", grid.getCell(0, 5).getText());
    }

    @Test
    public void testNoDefaults() throws Exception {
        openTestURL();

        GridElement grid = findGridNoDefaults();
        assertEquals("", grid.getCell(0, 0).getText());
        assertEquals("", grid.getCell(0, 1).getText());
        assertEquals("", grid.getCell(0, 2).getText());
        assertEquals("", grid.getCell(0, 3).getText());
        assertEquals("", grid.getCell(0, 5).getText());
    }

    private GridElement findGridWithDefaults() {
        return $(GridElement.class).id("test-grid-defaults");
    }

    private GridElement findGridNoDefaults() {
        return $(GridElement.class).id("test-grid");
    }

}
