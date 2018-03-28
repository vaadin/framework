package com.vaadin.tests.components.uitest;

import java.io.IOException;

public class RunoThemeTest extends ThemeTest {
    @Override
    protected String getTheme() {
        return "runo";
    }

    @Override
    protected void testWindows() throws IOException {
        super.testWindows();

        // runo theme only
        testWindow(3, "subwindow-dialog");
    }
}
