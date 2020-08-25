package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridColumnWidthInsideWindowCompositeContentTest
        extends SingleBrowserTest {

    @Test
    public void widthAfterExpansion() throws InterruptedException {
        openTestURL();
        $(ButtonElement.class).id("open-composite").click();
        GridElement grid = $(GridElement.class).first();
        int initialWidth = grid.getHeaderCell(0, 0).getSize().getWidth();

        WindowElement window = $(WindowElement.class).first();
        window.maximize();

        Thread.sleep(1000);

        int newWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        assertNotEquals(
                "Expected Grid cell to be resized after Window was expanded",
                initialWidth, newWidth);
    }

    @Test
    public void widthAfterExpansionWithoutComposite()
            throws InterruptedException {
        openTestURL();
        $(ButtonElement.class).id("open-non-composite").click();
        GridElement grid = $(GridElement.class).first();
        int initialWidth = grid.getHeaderCell(0, 0).getSize().getWidth();

        WindowElement window = $(WindowElement.class).first();
        window.maximize();

        Thread.sleep(1000);

        int newWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        assertNotEquals(
                "Expected Grid cell to be resized after Window was expanded",
                initialWidth, newWidth);
    }

}
