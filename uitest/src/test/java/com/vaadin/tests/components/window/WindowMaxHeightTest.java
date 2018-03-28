package com.vaadin.tests.components.window;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class WindowMaxHeightTest extends SingleBrowserTest {

    @Test
    public void ensureWindowNotFullHeight() {
        openTestURL();
        WindowElement window = $(WindowElement.class).first();
        Dimension size = window.getSize();
        assertTrue("Window should be 200-250px high, was " + size.getHeight(),
                size.getHeight() < 250);
    }
}
