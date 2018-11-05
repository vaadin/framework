package com.vaadin.tests.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class MissingHierarchyDetectionTest extends SingleBrowserTest {
    @Test
    public void testMissingHierarchyDetection() {
        openTestURL();

        assertTrue(isElementPresent(By.id("label")));

        ButtonElement toggleProperly = $(ButtonElement.class)
                .caption("Toggle properly").first();

        toggleProperly.click();
        assertNoSystemNotifications();
        assertFalse(isElementPresent(By.id("label")));

        toggleProperly.click();
        assertNoSystemNotifications();
        assertTrue(isElementPresent(LabelElement.class));

        ButtonElement toggleImproperly = $(ButtonElement.class)
                .caption("Toggle improperly").first();
        toggleImproperly.click();
        assertSystemNotification();
    }
}
