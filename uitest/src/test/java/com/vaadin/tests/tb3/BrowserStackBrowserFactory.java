package com.vaadin.tests.tb3;

import java.util.logging.Logger;

import org.openqa.selenium.Platform;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.shared.Version;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

/**
 * Browser factory for the cloud test provider BrowserStack.
 */
public class BrowserStackBrowserFactory extends DefaultBrowserFactory {

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
            // This will not work on BrowserStack - should be filtered with
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
        case IE8:
            caps = DesiredCapabilities.internetExplorer();
            caps.setVersion("8");
            caps.setCapability("browser", "IE");
            caps.setCapability("browser_version", "8.0");
            caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,
                    true);
            break;
        case IE9:
            caps = DesiredCapabilities.internetExplorer();
            caps.setVersion("9");
            caps.setCapability("browser", "IE");
            caps.setCapability("browser_version", "9.0");
            caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,
                    true);
            break;
        case IE10:
            caps = DesiredCapabilities.internetExplorer();
            caps.setVersion("10");
            caps.setCapability("browser", "IE");
            caps.setCapability("browser_version", "10.0");
            caps.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,
                    true);
            break;
        case IE11:
            caps = DesiredCapabilities.internetExplorer();
            caps.setVersion("11");
            caps.setCapability("browser", "IE");
            caps.setCapability("browser_version", "11.0");
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

        // BrowserStack specific parts

        // for now, run all tests on Windows 7
        if (!Browser.PHANTOMJS.equals(browser)) {
            caps.setCapability("os", "Windows");
            caps.setCapability("os_version", "7");
            caps.setPlatform(Platform.WINDOWS);
        }

        // enable logging on BrowserStack
        caps.setCapability("browserstack.debug", "true");

        // tunnel
        caps.setCapability("browserstack.local", "true");
        // optionally, could also set browserstack.localIdentifier if we have a
        // tunnel name

        // build and project for easy identification in BrowserStack UI
        caps.setCapability("project", "vaadin");
        caps.setCapability("build", Version.getFullVersion());

        // accept self-signed certificates
        caps.setCapability("acceptSslCerts", "true");

        caps.setCapability("resolution", "1680x1050");

        getLogger().info("Using BrowserStack capabilities " + caps);

        return caps;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(BrowserStackBrowserFactory.class.getName());
    }
}
