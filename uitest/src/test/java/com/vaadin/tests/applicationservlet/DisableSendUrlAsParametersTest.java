package com.vaadin.tests.applicationservlet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class DisableSendUrlAsParametersTest extends SingleBrowserTest {

    @Test
    public void testInitLocation() {
        openTestURL();

        String logRow = getLogRow(0);

        Assert.assertEquals(
                "1. Init location exception: Location is not available as the sendUrlsAsParameters parameter is configured as false",
                logRow);
    }
}
