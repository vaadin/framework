package com.vaadin.tests.application;

import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;

public class TimingInfoReportedTest extends SingleBrowserTest {

    @Test
    public void ensureTimingsAvailable() {
        openTestURL();
        Assert.assertEquals("2. Timings ok", getLogRow(0));
        $(ButtonElement.class).first().click();
        Assert.assertEquals("4. Timings ok", getLogRow(0));
    }
}
