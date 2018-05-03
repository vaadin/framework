package com.vaadin.tests.components.progressindicator;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ProgressBarStaticRunoTest extends MultiBrowserTest {
    @Test
    public void compareScreenshot() throws Exception {
        openTestURL();
        compareScreen("screen");
    }
}
