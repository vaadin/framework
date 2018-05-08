package com.vaadin.tests.tb3;

import org.openqa.selenium.Platform;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.shared.Version;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

public class SauceLabsBrowserFactory extends DefaultBrowserFactory {

    @Override
    public DesiredCapabilities create(Browser browser) {
        String version = "";
        // need to pick a version to request, but for these two auto-updating
        // browsers there is a special value "latest" (and "latest-1",
        // "latest-2")
        if (Browser.FIREFOX.equals(browser) || Browser.CHROME.equals(browser)) {
            version = "latest";
        }
        return create(browser, version, Platform.ANY);
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        DesiredCapabilities caps;

        switch (browser) {
        case CHROME:
            caps = DesiredCapabilities.chrome();
            caps.setVersion(version);
            break;
        case PHANTOMJS:
            // This will not work on SauceLabs - should be filtered with
            // browsers.exclude. However, we cannot throw an exception here as
            // filtering only takes place if there is no exception.
            caps = DesiredCapabilities.phantomjs();
            caps.setVersion("1");
            caps.setPlatform(Platform.LINUX);
            break;
        case SAFARI:
            caps = DesiredCapabilities.safari();
            caps.setVersion(version);
            break;
        case IE11:
            caps = DesiredCapabilities.internetExplorer();
            caps.setVersion("11.0");
            // There are 2 capabilities ie.ensureCleanSession and
            // ensureCleanSession in Selenium
            // IE 11 uses ie.ensureCleanSession
            caps.setCapability("ie.ensureCleanSession", true);
            caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,
                    true);
            break;
        case FIREFOX:
            caps = DesiredCapabilities.firefox();
            caps.setVersion(version);
            break;
        default:
            caps = DesiredCapabilities.firefox();
            caps.setVersion(version);
            caps.setPlatform(platform);
        }

        if (!Browser.PHANTOMJS.equals(browser)) {
            caps.setCapability("platform", "Windows 7");
        }

        // accept self-signed certificates
        caps.setCapability("acceptSslCerts", "true");

        // SauceLabs specific parts

        caps.setCapability("screenResolution", "1680x1050");

        // build and project for easy identification in SauceLabs UI
        caps.setCapability("build", Version.getFullVersion());

        return caps;
    }

}
