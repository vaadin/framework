package com.vaadin.tests.components.browserframe;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class BrowserFrameDoubleScrollbarsTest extends MultiBrowserTest {

    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();

        compareScreen("BrowserFrameDoubleScrollbars");
    }
}
