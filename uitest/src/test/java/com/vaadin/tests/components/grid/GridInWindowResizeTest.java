package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridInWindowResizeTest extends SingleBrowserTest {

    @Test
    public void resizeWindow() {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();

        GridElement grid = $(GridElement.class).first();
        int col1WidthBefore = grid.getCell(0, 0).getSize().getWidth();
        $(ButtonElement.class).caption("resize").first().click();

        try {
            // Sleep for 1 second for animation.
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        int col1WidthAfter = grid.getCell(0, 0).getSize().getWidth();

        assertTrue(col1WidthAfter < col1WidthBefore);
    }
}
