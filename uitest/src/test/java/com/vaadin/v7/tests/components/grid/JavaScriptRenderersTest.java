package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class JavaScriptRenderersTest extends MultiBrowserTest {

    @Test
    public void testJavaScriptRenderer() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement cell_1_1 = grid.getCell(1, 1);

        GridCellElement cell_2_2 = grid.getCell(2, 2);

        // Verify render functionality
        assertEquals("Bean(2, 0)", cell_1_1.getText());

        assertEquals("string2", cell_2_2.getText());

        // Verify init functionality
        assertEquals("1", cell_1_1.getAttribute("column"));

        // Verify onbrowserevent
        cell_1_1.click();
        assertTrue(cell_1_1.getText().startsWith("Clicked 1 with key 2 at"));
    }
}
