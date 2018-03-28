package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class PushToggleComponentVisibilityTest extends SingleBrowserTest {

    private static final String HIDE = "hide";

    @Test
    public void ensureComponentVisible() {
        openTestURL();

        $(ButtonElement.class).id(HIDE).click();
        assertEquals("Please wait", $(LabelElement.class).first().getText());

        waitUntil(driver -> isElementPresent(ButtonElement.class));
        $(ButtonElement.class).id(HIDE).click();
        assertEquals("Please wait", $(LabelElement.class).first().getText());
    }
}
