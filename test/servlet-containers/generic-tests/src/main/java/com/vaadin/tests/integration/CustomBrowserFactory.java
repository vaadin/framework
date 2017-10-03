package com.vaadin.tests.integration;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

public class CustomBrowserFactory extends DefaultBrowserFactory {

    @Override
    public DesiredCapabilities create(Browser browser) {
        DesiredCapabilities capabilities = super.create(browser);
        if (BrowserUtil.isPhantomJS(capabilities)) {
            capabilities.setVersion("2");
            capabilities.setCapability("phantomjs.binary.path",
                    "/usr/bin/phantomjs2");
        }
        return capabilities;
    }
}
