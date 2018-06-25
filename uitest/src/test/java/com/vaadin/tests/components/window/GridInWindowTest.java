package com.vaadin.tests.components.window;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.tests.tb3.newelements.WindowElement;

public class GridInWindowTest extends SingleBrowserTest {

    @Test
    public void ensureAttachInHierachyChange() {
        openTestURL("debug");
        $(ButtonElement.class).first().click();
        assertNoErrorNotifications();
        $(WindowElement.class).first().close();
        assertNoErrorNotifications();
        $(ButtonElement.class).first().click();
        assertNoErrorNotifications();
    }
}
