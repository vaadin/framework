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
        // No Flash support in Chrome, FF, PhantomJS
        List<DesiredCapabilities> capabilities = getBrowsersSupportingShiftClick();
        capabilities.removeAll(getBrowserCapabilities(Browser.CHROME));
        return capabilities;
    }

    private final By locator = By.tagName("embed");

    @Test
    public void testFlashIsExpanded() throws Exception {
        openTestURL();
        /* Allow the flash plugin to load */
        sleep(5000);
        WebElement embed = $(FlashElement.class).first().findElement(locator);
        String width = embed.getAttribute("width");
        Assert.assertTrue("Width is not 400.0px initially",
                "400.0px".equals(width));
        $(ButtonElement.class).first().click();
        String widthAfterExpansion = embed.getAttribute("width");
        Assert.assertFalse("Width is still 400.0px after expansion",
                "400.0px".equals(widthAfterExpansion));
    }

}
