package com.vaadin.tests.tb3;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

public class XvfbHubBrowserFactory extends DefaultBrowserFactory {

    public DesiredCapabilities create(Browser browser) {
        switch (browser) {
        case IE11:
            return super.create(browser);
        case PHANTOMJS:
            return create(browser, "2", Platform.LINUX);
        case CHROME:
            return create(browser, "", Platform.ANY);
        case FIREFOX:
        default:
            return create(Browser.FIREFOX, "", Platform.ANY);
        }
    }
}
