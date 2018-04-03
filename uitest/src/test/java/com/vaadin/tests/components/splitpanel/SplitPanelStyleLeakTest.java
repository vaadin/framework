package com.vaadin.tests.components.splitpanel;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SplitPanelStyleLeakTest extends MultiBrowserTest {

    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen("all");
    }
}
