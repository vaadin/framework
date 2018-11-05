package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ServiceInitListenersTest extends SingleBrowserTest {

    @Test
    public void testServiceInitListenerTriggered() {
        openTestURL();

        assertNotEquals(getLogRow(0), 0, extractCount(getLogRow(0)));
        assertNotEquals(getLogRow(1), 0, extractCount(getLogRow(1)));
        assertNotEquals(getLogRow(2), 0, extractCount(getLogRow(2)));
    }

    private int extractCount(String logRow) {
        // Assuming row pattern is "label: 1"
        String substring = logRow.replaceAll("[^:]*:\\s*", "");
        return Integer.parseInt(substring);
    }

}
