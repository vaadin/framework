package com.vaadin.tests.components.abstractcomponent;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ResponseWritingErrorHandlingTest extends SingleBrowserTest {

    @Test
    public void testExceptionInBeforeClientResponse() {
        openTestURL();

        $(ButtonElement.class).first().click();

        Assert.assertEquals("Message should be logged by error handler",
                "1. Button.beforeClientResponse", getLogRow(0));
    }
}
