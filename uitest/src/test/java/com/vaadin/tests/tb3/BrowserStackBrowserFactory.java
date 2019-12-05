package com.vaadin.tests.tb3;

import java.util.logging.Logger;

import org.openqa.selenium.Platform;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

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
        /* Ignored browsers */
        case CHROME:
            caps = DesiredCapabilities.chrome();
            break;
        case PHANTOMJS:
            caps = DesiredCapabilities.phantomjs();
            break;
        case SAFARI:
            caps = DesiredCapabilities.safari();
            break;
        case FIREFOX:
            caps = DesiredCapabilities.firefox();
            break;
        /* Actual browsers */
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
            // Workaround for an IE11 bug in BrowserStack sendKeys handling, see
            // https://www.browserstack.com/automate/using-sendkeys-on-remote-IE11
            caps.setCapability("browserstack.sendKeys", true);
            break;
        default:
            caps = DesiredCapabilities.firefox();
        }

        // BrowserStack specific parts

        // for now, run all tests on Windows 7
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "7");
        caps.setPlatform(Platform.WINDOWS);

        // enable logging on BrowserStack
        caps.setCapability("browserstack.debug", "true");

        // tunnel
        caps.setCapability("browserstack.local", "true");
        String localIdentifier = System.getProperty("browserstack.identifier",
                "");
        if (!localIdentifier.isEmpty()) {
            caps.setCapability("browserstack.localIdentifier", localIdentifier);
        }

        // build name for easy identification in BrowserStack UI
        caps.setCapability("build",
                "BrowserStack Tests" + (localIdentifier.isEmpty() ? ""
                        : " [" + localIdentifier + "]"));

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
