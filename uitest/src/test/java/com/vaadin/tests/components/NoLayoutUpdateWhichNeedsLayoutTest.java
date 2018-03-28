package com.vaadin.tests.components;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NoLayoutUpdateWhichNeedsLayoutTest extends SingleBrowserTest {

    @Test
    public void layoutRunForNoLayoutUpdate() {
        openTestURL("debug");
        ButtonElement open = $(ButtonElement.class).id("openWindow");
        open.click();
        final ProgressBarElement progress = $(ProgressBarElement.class).first();
        waitUntil(driver -> {
            double p = progress.getValue();
            return Math.abs(p - 0.5) < 0.01;
        });

        ButtonElement close = $(ButtonElement.class).id("closeWindow");
        close.click();

        assertNoErrorNotifications();
    }
}
