package com.vaadin.tests.components.ui;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DynamicViewportEmptyTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return DynamicViewport.class;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.CHROME);
    }

    @Test
    public void testGeneratedEmptyViewport() {
        openTestURL();

        List<WebElement> viewportElements = findElements(
                By.cssSelector("meta[name=viewport]"));

        Assert.assertTrue("There should be no viewport tags",
                viewportElements.isEmpty());
    }

}
