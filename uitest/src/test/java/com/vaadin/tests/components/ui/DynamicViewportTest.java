package com.vaadin.tests.components.ui;

import org.junit.Assert;
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

        Assert.assertTrue(
                viewportElement.getAttribute("content").contains("PhantomJS"));
    }
}
