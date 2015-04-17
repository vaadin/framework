package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIAccessExceptionHandlingTest extends MultiBrowserTest {

    @Test
    public void testExceptionHandlingOnUIAccess() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ConnectorErrorEvent : java.util.concurrent.ExecutionException");

        $(ButtonElement.class).get(1).click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ErrorEvent : java.util.concurrent.ExecutionException");

        $(ButtonElement.class).get(2).click();
        assertLogTexts(
                "1. Exception caught on get: java.util.concurrent.ExecutionException",
                "0. Exception caught on execution with ConnectorErrorEvent : java.util.concurrent.ExecutionException");
    }

    private void assertLogTexts(String first, String second) {
        assertLogText(0, first);
        assertLogText(1, second);
    }

    private void assertLogText(int index, String expected) {
        Assert.assertEquals("Unexpected log contents,", expected,
                getLogRow(index));
    }
}
