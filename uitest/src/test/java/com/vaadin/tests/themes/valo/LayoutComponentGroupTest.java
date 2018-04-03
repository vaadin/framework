package com.vaadin.tests.themes.valo;

import org.junit.Test;

import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class LayoutComponentGroupTest extends SingleBrowserTestPhantomJS2 {

    @Test
    public void renderedWithoutRoundedBordersInTheMiddle() throws Exception {
        openTestURL();
        compareScreen($(VerticalLayoutElement.class).id("container"),
                "buttongroups");
    }
}
