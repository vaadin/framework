package com.vaadin.tests.components.uitest;

import java.io.IOException;

public class ChameleonThemeTest extends ThemeTest {
    @Override
    protected String getTheme() {
        return "chameleon";
    }

    @Override
    protected void testWindows() throws IOException {
        super.testWindows();

        // chameleon theme only
        testWindow(4, "subwindow-opaque");
    }
}
