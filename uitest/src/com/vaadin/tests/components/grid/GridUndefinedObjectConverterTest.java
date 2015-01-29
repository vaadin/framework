package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridUndefinedObjectConverterTest extends MultiBrowserTest {
    @Test
    public void testDefaultToStringRendering() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("pojo", "Pojo:foo", grid.getCell(0, 0).getText());
        assertEquals("pojo object", "Pojo:bar", grid.getCell(0, 1).getText());
        assertEquals("int", "1", grid.getCell(0, 2).getText());
        assertEquals("int object", "2", grid.getCell(0, 3).getText());
        assertEquals("string", "foo", grid.getCell(0, 4).getText());
        assertEquals("string object", "bar", grid.getCell(0, 5).getText());
    }
}
