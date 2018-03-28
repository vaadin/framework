package com.vaadin.tests.components.treetable;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeTableRowIconsTest extends MultiBrowserTest {

    public final String SCREENSHOT_NAME = "TreeTableRowIcons";

    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen(SCREENSHOT_NAME);
    }

}
