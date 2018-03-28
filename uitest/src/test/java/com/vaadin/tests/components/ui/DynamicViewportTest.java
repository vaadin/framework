package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class DynamicViewportTest extends SingleBrowserTest {

    @Test
    public void testGeneratedViewport() {
        openTestURL();

        WebElement viewportElement = findElement(
                By.cssSelector("meta[name=viewport]"));

        assertTrue(
                viewportElement.getAttribute("content").contains("PhantomJS"));
    }
}
