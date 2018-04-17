package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.TimeoutException;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RefreshFragmentChangeTest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return com.vaadin.tests.application.RefreshStatePreserve.class;
    }

    @Test
    public void testFragmentChange() throws Exception {
        getDriver().manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        openTestURL();
        assertLogText("1. Initial fragment: null");

        try {
            getDriver().get(getTestUrl() + "#asdf");
        } catch (TimeoutException e) {
            // Chrome throws timeout exception even when loading is successful
            if (!BrowserUtil.isChrome(getDesiredCapabilities())) {
                throw e;
            }
        }

        assertLogText("2. Fragment changed to asdf");
        openTestURL();
        assertLogText("3. Fragment changed to null");
    }

    private void assertLogText(String expected) {
        waitForElementPresent(By.className("v-label"));
        assertEquals("Incorrect log text,", expected, getLogRow(0));
    }
}
