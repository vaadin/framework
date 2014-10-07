package com.vaadin.tests.components.window;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowWithIconTest extends MultiBrowserTest {

    @Test
    public void testWindowWithIcon() throws Exception {
        openTestURL();

        compareScreen("icon-rendered-properly");
    }

}
