package com.vaadin.tests.components.customfield;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class CustomFieldSizeTest extends MultiBrowserTest {

    @Test
    public void checkScreenshot() throws IOException {
        openTestURL();
        compareScreen("size");
    }
}
