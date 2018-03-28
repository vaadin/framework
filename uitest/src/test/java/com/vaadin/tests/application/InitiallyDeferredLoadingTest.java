package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class InitiallyDeferredLoadingTest extends SingleBrowserTest {
    @Test
    public void testInitiallyDeferredComponent() {
        openTestURL();

        WebElement deferredComponent = findElement(By.id("deferred"));

        assertEquals("DeferredConnector", deferredComponent.getText());
    }
}
