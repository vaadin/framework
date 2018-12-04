package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UIAccessExceptionHandlingTest extends SingleBrowserTest {

    @Test
    public void testExceptionHandlingOnUIAccess() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ConnectorErrorEvent : java.lang.RuntimeException");

        $(ButtonElement.class).get(1).click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ErrorEvent : java.lang.RuntimeException");

        $(ButtonElement.class).get(2).click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ConnectorErrorEvent : java.lang.RuntimeException");

        $(ButtonElement.class).get(3).click();
        assertLogText(0,
                "0. Exception caught on execution with ConnectorErrorEvent : java.lang.NullPointerException");
    }

    private void assertLogTexts(String first, String second) {
        assertLogText(0, first);
        assertLogText(1, second);
    }

    private void assertLogText(int index, String expected) {
        assertEquals("Unexpected log contents,", expected, getLogRow(index));
    }
}
