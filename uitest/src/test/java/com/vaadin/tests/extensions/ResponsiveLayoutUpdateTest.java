package com.vaadin.tests.extensions;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ResponsiveLayoutUpdateTest extends MultiBrowserTest {

    @Test
    public void testWidthAndHeightRanges() throws Exception {
        openTestURL();

        final PanelElement panelElement = $(PanelElement.class).first();
        // I currently have no idea why PhantomJS wants a click here to work
        // properly
        panelElement.click();
        waitForElementVisible(By.cssSelector(".layout-update"));

        compareScreen("large");

        // Resize below 600px width breakpoint
        testBench().resizeViewPortTo(550, 768);

        waitUntil(input -> panelElement.getSize().getWidth() < 500);
        compareScreen("small");
    }
}
