package com.vaadin.tests.components;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.customelements.CustomProgressBarElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NoLayoutUpdateWhichNeedsLayoutTest extends SingleBrowserTest {

    @Test
    public void layoutRunForNoLayoutUpdate() {
        openTestURL("debug");
        ButtonElement open = $(ButtonElement.class).id("openWindow");
        open.click();
        final CustomProgressBarElement progress = $(
                CustomProgressBarElement.class).first();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                double p = progress.getValue();
                return Math.abs(p - 0.5) < 0.01;
            }
        });

        ButtonElement close = $(ButtonElement.class).id("closeWindow");
        close.click();

        assertNoErrorNotifications();
    }
}
