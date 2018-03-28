package com.vaadin.tests.components.uitest;

import java.io.IOException;

public class ReindeerThemeTest extends ThemeTest {
    @Override
    protected String getTheme() {
        return "reindeer";
    }

    @Override
    protected void testWindows() throws IOException {
        super.testWindows();

        // reindeer theme only
        testWindow(1, "subwindow-light");
        testWindow(2, "subwindow-black");
    }
}
