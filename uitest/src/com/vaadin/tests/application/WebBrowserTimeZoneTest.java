package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WebBrowserTimeZoneTest extends MultiBrowserTest {
    @Test
    public void testBrowserTimeZoneInfo() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLabelText("Browser raw offset", "7200000");
        assertLabelText("Browser to Europe/Helsinki offset difference", "0");
        assertLabelText("Browser could be in Helsinki", "Yes");
    }

    private void assertLabelText(String caption, String expected) {
        String actual = $(LabelElement.class).caption(caption).first()
                .getText();
        Assert.assertEquals(
                String.format("Unexpected text in label '%s',", caption),
                expected, actual);
    }
}
