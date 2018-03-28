package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTestPhantomJS2;

public class TimingInfoReportedTest extends SingleBrowserTestPhantomJS2 {

    @Test
    public void ensureTimingsAvailable() {
        openTestURL();
        assertEquals("2. Timings ok", getLogRow(0));
        $(ButtonElement.class).first().click();
        assertEquals("4. Timings ok", getLogRow(0));
    }
}
