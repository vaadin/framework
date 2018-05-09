package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;

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

        String viewportContent = viewportElement.getAttribute("content")
                .toLowerCase(Locale.ROOT);
        String browserName = getDesiredCapabilities().getBrowserName()
                .toLowerCase(Locale.ROOT);

        assertTrue(viewportContent.contains(browserName));
    }

    @Test
    public void testGeneratedEmptyViewport() {
        openTestURL(DynamicViewport.VIEWPORT_DISABLE_PARAMETER);

        List<WebElement> viewportElements = findElements(
                By.cssSelector("meta[name=viewport]"));

        assertTrue("There should be no viewport tags",
                viewportElements.isEmpty());
    }
}
