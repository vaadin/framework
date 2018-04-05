package com.vaadin.tests.layouts.layouttester;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class BaseCaptionTest extends MultiBrowserTest {
    @Test
    public void LayoutCaption() throws IOException, InterruptedException {
        openTestURL();
        compareScreen("caption");
    }
}
