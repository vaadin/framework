package com.vaadin.tests.widgetset.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class NoneLoadStyleTest extends SingleBrowserTest {
    @Test
    public void connectorNotLoaded() {
        openTestURL();

        String componentText = findElement(By.id("component")).getText();

        assertTrue(componentText.contains("does not contain"));
    }
}
