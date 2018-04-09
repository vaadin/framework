package com.vaadin.tests.components.progressindicator;

import org.junit.Test;

import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ProgressBarStaticReindeerTest extends MultiBrowserTest {
    @Test
    public void compareScreenshot() throws Exception {
        openTestURL();
        compareScreen($(ProgressBarElement.class).first(), "screen");
    }
}
