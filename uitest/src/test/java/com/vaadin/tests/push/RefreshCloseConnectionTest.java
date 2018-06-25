package com.vaadin.tests.push;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("push")
public class RefreshCloseConnectionTest extends MultiBrowserTest {

    @Test
    public void testSessionRefresh() {
        openTestURL("restartApplication");

        Assert.assertEquals("1. Init", getLogRow(0));

        openTestURL();

        Assert.assertEquals("2. Refresh", getLogRow(1));
        Assert.assertEquals("3. Push", getLogRow(0));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingWebSocket();
    }
}
