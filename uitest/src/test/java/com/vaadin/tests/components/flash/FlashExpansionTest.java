package com.vaadin.tests.components.flash;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.FlashElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FlashExpansionTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingFlash();
    }

    private final By locator = By.tagName("embed");

    @Test
    public void testFlashIsExpanded() throws Exception {
        openTestURL();
        /* Allow the flash plugin to load */
        waitForElementPresent(locator);
        WebElement embed = $(FlashElement.class).first().findElement(locator);
        String width = embed.getAttribute("width");
        Assert.assertEquals("Width is not 400.0px initially", "400", width);
        $(ButtonElement.class).first().click();
        embed = $(FlashElement.class).first().findElement(locator);
        String widthAfterExpansion = embed.getAttribute("width");
        Assert.assertNotEquals("Width is still 400.0px after expansion", "400",
                widthAfterExpansion);
    }

    private List<DesiredCapabilities> getBrowsersSupportingFlash() {
        // No Flash support in Chrome, FF, PhantomJS
        return getBrowserCapabilities(Browser.IE11);
    }
}
