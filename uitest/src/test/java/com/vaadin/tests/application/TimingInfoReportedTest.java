package com.vaadin.tests.application;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class TimingInfoReportedTest extends SingleBrowserTestPhantomJS2 {

    @Test
    public void ensureTimingsAvailable() {
        openTestURL();
        Assert.assertEquals("2. Timings ok", getLogRow(0));
        $(ButtonElement.class).first().click();
        Assert.assertEquals("4. Timings ok", getLogRow(0));
    }
}
