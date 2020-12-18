package com.vaadin.tests.requesthandlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AppResource404Test extends MultiBrowserTest {
    @Test
    public void testOpenExistingResource() throws Exception {
        openTestURL();
        $(LinkElement.class).first().click(5, 5);
        disableWaitingAndWait();
        assertFalse("Page contains the given text",
                driver.getPageSource().contains("404"));
    }

    @Test
    public void testOpenNonExistingResource() {
        openTestURL();
        $(LinkElement.class).get(1).click(5, 5);
        disableWaitingAndWait();
        assertTrue("Page does not contain the given text",
                driver.getPageSource().contains(
                        "/APP/connector/0/4/asdfasdf can not be found"));
    }

    @Test
    public void testOpenResourceWith404() {
        openTestURL();
        $(LinkElement.class).get(2).click(5, 5);
        disableWaitingAndWait();
        assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("HTTP ERROR 404"));
        assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("/run/APP/"));
        assertTrue("Page does not contain the given text",
                driver.getPageSource().contains(
                        "Request was not handled by any registered handler."));
    }

    @Test
    public void testOpenResourceToUIProvider() {
        openTestURL();
        $(LinkElement.class).get(3).click(5, 5);
        disableWaitingAndWait();
        assertFalse("Page contains the given text",
                driver.getPageSource().contains("can not be found"));
    }

    protected void disableWaitingAndWait() {
        testBench().disableWaitForVaadin();
        sleep(500);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE11 does not show the details on the 404 page
        return super.getBrowsersExcludingIE();
    }
}
