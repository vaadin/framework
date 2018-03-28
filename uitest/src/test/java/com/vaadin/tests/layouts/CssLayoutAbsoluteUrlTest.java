package com.vaadin.tests.layouts;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class CssLayoutAbsoluteUrlTest extends SingleBrowserTest {
    @Test
    public void testAboutBlankStyle() {
        openTestURL();

        WebElement myLabel = findElement(By.id("myLabel"));

        String backgroundImage = myLabel.getCssValue("background-image");

        // Not testing string equality since some browsers return the style with
        // quotes around the url argument and some without quotes.
        assertTrue(backgroundImage + " does not contain 'about:blank'",
                backgroundImage.contains("about:blank"));
    }
}
