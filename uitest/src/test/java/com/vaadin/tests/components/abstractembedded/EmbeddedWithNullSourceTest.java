package com.vaadin.tests.components.abstractembedded;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmbeddedWithNullSourceTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // No Flash on PhantomJS, IE 11 has a timeout issue, looks like a
        // IEDriver problem, not reproduced running locally.
        // Flash is disabled in Chrome.
        return getBrowserCapabilities(Browser.IE8, Browser.IE9, Browser.IE10,
                Browser.FIREFOX);
    }

    @Test
    public void testEmbeddedWithNullSource() throws IOException {
        openTestURL();

        waitForElementPresent(By.className("v-image"));

        compareScreen("nullSources");
    }
}
