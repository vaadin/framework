package com.vaadin.tests.themes.valo;

import org.junit.Test;

import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class LayoutComponentGroupTest extends SingleBrowserTest {

    @Test
    public void renderedWithoutRoundedBordersInTheMiddle() throws Exception {
        openTestURL();
        sleep(500);
        compareScreen($(VerticalLayoutElement.class).id("container"),
                "buttongroups");
    }
}
