package com.vaadin.tests.components.gridlayout;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutFractionalSizeAndAlignmentTest extends MultiBrowserTest {

    @Test
    public void ensureNoScrollbarsWithAlignBottomRight() throws IOException {
        openTestURL();
        compareScreen("noscrollbars");
    }
}
