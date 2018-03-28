package com.vaadin.tests.push;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class RefreshCloseConnectionTest extends MultiBrowserTest {

    @Test
    public void testSessionRefresh() {
        openTestURL("restartApplication");

        assertEquals("1. Init", getLogRow(0));

        openTestURL();

        assertEquals("2. Refresh", getLogRow(1));
        assertEquals("3. Push", getLogRow(0));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }
}
