package com.vaadin.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.server.WebBrowser;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.Label;

public class VerifyBrowserVersion extends TestBase {

    public static class BrowserVersionTest extends MultiBrowserTest {

        private Map<DesiredCapabilities, String> expectedUserAgent = new HashMap<DesiredCapabilities, String>();

        {
            expectedUserAgent
                    .put(BrowserUtil.firefox(24),
                            "Mozilla/5.0 (Windows NT 6.1; rv:24.0) Gecko/20100101 Firefox/24.0");
            expectedUserAgent
                    .put(BrowserUtil.ie(8),
                            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            expectedUserAgent
                    .put(BrowserUtil.ie(9),
                            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
            expectedUserAgent
                    .put(BrowserUtil.ie(10),
                            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
            expectedUserAgent
                    .put(BrowserUtil.ie(11),
                            "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko");
            expectedUserAgent
                    .put(BrowserUtil.chrome(29),
                            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.76 Safari/537.36");
            expectedUserAgent
                    .put(BrowserUtil.opera(12),
                            "Opera/9.80 (Windows NT 5.1) Presto/2.12.388 Version/12.15");

        }

        @Test
        public void verifyUserAgent() {
            openTestURL();
            Assert.assertEquals(
                    expectedUserAgent.get(getDesiredCapabilities()),
                    vaadinElementById("userAgent").getText());
            Assert.assertEquals("Touch device? No",
                    vaadinElementById("touchDevice").getText());
        }
    }

    @Override
    protected void setup() {
        WebBrowser browser = getBrowser();
        Label userAgent = new Label(browser.getBrowserApplication());
        userAgent.setId("userAgent");
        addComponent(userAgent);
        Label touchDevice = new Label("Touch device? "
                + (browser.isTouchDevice() ? "YES" : "No"));
        touchDevice.setId("touchDevice");
        addComponent(touchDevice);
    }

    @Override
    protected String getDescription() {
        return "Silly test just to get a screenshot of the browser's user agent string";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7655);
    }

}
