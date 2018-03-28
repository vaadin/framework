package com.vaadin.tests.layouts;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class IE8MeasuredSizeMemoryLeakTest extends MultiBrowserTest {

    @Test
    public void testMeasuredSizesMapCleaned() {
        openTestURL();
        Assert.assertEquals("No extra measured sizes in the beginning", 3,
                getMeasuredSizesMapSize());
        vaadinElementById("toggle").click();
        Assert.assertEquals("Measured sizes after single toggle", 204,
                getMeasuredSizesMapSize());
        vaadinElementById("toggle").click();
        Assert.assertEquals("Measured sizes cleaned on toggle", 204,
                getMeasuredSizesMapSize());
    }

    private int getMeasuredSizesMapSize() {
        JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
        Number result = (Number) jsExec
                .executeScript("return window.vaadin.getMeasuredSizesCount();");
        return result.intValue();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE8);
    }
}
