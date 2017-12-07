package com.vaadin.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class VerifyAssertionsEnabledTest extends SingleBrowserTest {
    @Test
    public void verifyServerAssertions() throws Exception {
        openTestURL();
        assertEquals("1. Assertions are enabled", getLogRow(0));
    }
}
