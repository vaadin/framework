package com.vaadin.tests.components.panel;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class UndefinedSizeScrollbarsTest extends MultiBrowserTest {

    @Test
    public void testNoScrollbars() throws IOException {
        openTestURL();
        compareScreen("noscrollbars");
    }
}
