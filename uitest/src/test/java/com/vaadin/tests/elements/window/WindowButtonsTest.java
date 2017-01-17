package com.vaadin.tests.elements.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowButtonsTest extends MultiBrowserTest {

    private WindowElement windowElement;

    @Override
    protected Class<?> getUIClass() {
        return WindowUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        windowElement = $(WindowElement.class).first();
    }

    @Test
    public void window_clickCloseButton_windowClosed() {
        windowElement.close();

        assertFalse($(WindowElement.class).exists());
    }

    @Test
    public void window_maximizeAndRestore_windowOriginalSize()
            throws IOException, InterruptedException {
        assertFalse(windowElement.isMaximized());
        final Dimension originalSize = windowElement.getSize();

        windowElement.maximize();

        assertTrue(windowElement.isMaximized());
        assertNotEquals(originalSize, windowElement.getSize());

        windowElement.restore();

        assertFalse(windowElement.isMaximized());
        assertEquals(originalSize, windowElement.getSize());
    }

}
