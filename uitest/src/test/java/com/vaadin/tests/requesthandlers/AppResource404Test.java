package com.vaadin.tests.requesthandlers;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AppResource404Test extends MultiBrowserTest {
    @Test
    public void testOpenExistingResource() throws Exception {
        openTestURL();
        $(LinkElement.class).first().click(5, 5);
        disableWaitingAndWait();
        Assert.assertFalse("Page contains the given text",
                driver.getPageSource().contains("404"));
    }

    @Test
    public void testOpenNonExistingResource() {
        openTestURL();
        $(LinkElement.class).get(1).click(5, 5);
        disableWaitingAndWait();
        String errorText = BrowserUtil.isIE(getDesiredCapabilities()) ? "HTTP ERROR 404" : "/APP/connector/0/4/asdfasdf can not be found";
        Assert.assertTrue("Page does not contain the given text",
                driver.getPageSource().contains(errorText));
    }

    @Test
    public void testOpenResourceWith404() {
        openTestURL();
        $(LinkElement.class).get(2).click(5, 5);
        disableWaitingAndWait();
        String errorText = "Page does not contain the given text";
        Assert.assertTrue(errorText,
                driver.getPageSource().contains("HTTP ERROR 404"));
        Assert.assertTrue(errorText,
                driver.getPageSource().contains("/run/APP/"));
        Assert.assertTrue(errorText,
                driver.getPageSource().contains(
                        "Request was not handled by any registered handler"));
    }

    @Test
    public void testOpenResourceToUIProvider() {
        openTestURL();
        $(LinkElement.class).get(3).click(5, 5);
        disableWaitingAndWait();
        Assert.assertFalse("Page contains the given text",
                driver.getPageSource().contains("can not be found"));
    }

    protected void disableWaitingAndWait() {
        testBench().disableWaitForVaadin();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
