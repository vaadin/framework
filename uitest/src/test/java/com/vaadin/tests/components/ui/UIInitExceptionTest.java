package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class UIInitExceptionTest extends SingleBrowserTest {
    @Test
    public void testExceptionOnUIInit() throws Exception {
        openTestURL();
        assertTrue("Page does not contain the given text",
                driver.getPageSource().contains("Catch me if you can"));
    }
}
