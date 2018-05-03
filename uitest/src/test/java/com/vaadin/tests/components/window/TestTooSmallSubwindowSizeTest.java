package com.vaadin.tests.components.window;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that the styles work correctly in tiny subwindows that have more
 * content than can fit.
 *
 * @author Vaadin Ltd
 */
public class TestTooSmallSubwindowSizeTest extends MultiBrowserTest {

    @Test
    public void testSubwindowStyles() throws IOException {
        openTestURL();

        compareScreen("initial_state");
    }
}
