package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridSingleColumnTest extends MultiBrowserTest {

    @Test
    public void testHeaderIsVisible() {
        openTestURL();

        GridCellElement cell = $(GridElement.class).first().getHeaderCell(0, 0);
        assertTrue("No header available",
                cell.getText().equalsIgnoreCase("header"));
    }

    @Test
    public void testScrollDidNotThrow() {
        setDebug(true);
        openTestURL();

        assertFalse("Exception when scrolling on init",
                isElementPresent(NotificationElement.class));
    }
}
