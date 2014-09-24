/*
 * Copyright 2000-2013 Vaadind Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests.tb3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.tests.tb3.MultiBrowserTest.Browser;

/**
 * Provides values for parameters which depend on where the test is run.
 * Parameters should be configured in work/eclipse-run-selected-test.properties.
 * A template is available in uitest/.
 *
 * @author Vaadin Ltd
 */
public abstract class PrivateTB3Configuration extends ScreenshotTB3Test {
    private static final String RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.runLocally";
    private static final String HOSTNAME_PROPERTY = "com.vaadin.testbench.deployment.hostname";
    private static final String PORT_PROPERTY = "com.vaadin.testbench.deployment.port";
    private static final String DEPLOYMENT_PROPERTY = "com.vaadin.testbench.deployment.url";
    private static final Properties properties = new Properties();
    private static final File propertiesFile = new File("work",
            "eclipse-run-selected-test.properties");
    static {
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getProperty(String name) {
        String property = properties.getProperty(name);
        if (property == null) {
            property = System.getProperty(name);
        }

        return property;
    }

    private static String getSource(String propertyName) {
        if (properties.containsKey(propertyName)) {
            return propertiesFile.getAbsolutePath();
        } else if (System.getProperty(propertyName) != null) {
            return "System.getProperty()";
        } else {
            return null;
        }
    }

    @Override
    protected String getScreenshotDirectory() {
        String screenshotDirectory = getProperty("com.vaadin.testbench.screenshot.directory");
        if (screenshotDirectory == null) {
            throw new RuntimeException(
                    "No screenshot directory defined. Use -Dcom.vaadin.testbench.screenshot.directory=<path>");
        }
        return screenshotDirectory;
    }

    @Override
    protected String getHubHostname() {
        return "tb3-hub.intra.itmill.com";
    }

    @Override
    protected String getBaseURL() {
        String url = getProperty(DEPLOYMENT_PROPERTY);
        if (url == null || url.trim().isEmpty()) {
            return super.getBaseURL();
        }
        return url;
    }

    @Override
    protected String getDeploymentHostname() {
        if (getRunLocallyBrowser() != null) {
            return "localhost";
        }
        return getConfiguredDeploymentHostname();
    }

    /**
     * Gets the hostname that tests are configured to use.
     *
     * @return the host name configuration value
     */
    public static String getConfiguredDeploymentHostname() {
        String hostName = getProperty(HOSTNAME_PROPERTY);

        if (hostName == null || "".equals(hostName)) {
            hostName = findAutoHostname();
        }

        return hostName;
    }

    @Override
    protected int getDeploymentPort() {
        return getConfiguredDeploymentPort();
    }

    /**
     * Gets the port that tests are configured to use.
     *
     * @return the port configuration value
     */
    public static int getConfiguredDeploymentPort() {
        String portString = getProperty(PORT_PROPERTY);

        int port = 8888;
        if (portString != null && !"".equals(portString)) {
            port = Integer.parseInt(portString);
        }

        return port;
    }

    /**
     * Tries to automatically determine the IP address of the machine the test
     * is running on.
     *
     * @return An IP address of one of the network interfaces in the machine.
     * @throws RuntimeException
     *             if there was an error or no IP was found
     */
    private static String findAutoHostname() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nwInterface = interfaces.nextElement();
                if (!nwInterface.isUp() || nwInterface.isLoopback()
                        || nwInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nwInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        throw new RuntimeException(
                "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) ip address found.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.AbstractTB3Test#setupLocalDriver()
     */
    @Override
    protected void setupLocalDriver(DesiredCapabilities desiredCapabilities) {
        WebDriver driver;
        if (BrowserUtil.isFirefox(desiredCapabilities)) {
            String firefoxPath = getProperty("firefox.path");
            if (firefoxPath != null) {
                driver = new FirefoxDriver(new FirefoxBinary(new File(
                        firefoxPath)), null);
            } else {
                driver = new FirefoxDriver();
            }
        } else if (BrowserUtil.isChrome(desiredCapabilities)) {
            String propertyName = "chrome.driver.path";
            String chromeDriverPath = getProperty(propertyName);
            if (chromeDriverPath == null) {
                throw new RuntimeException(
                        "You need to install ChromeDriver to use @"
                                + RunLocally.class.getSimpleName()
                                + " with Chrome."
                                + "\nFirst install it from https://code.google.com/p/selenium/wiki/ChromeDriver."
                                + "\nThen update "
                                + propertiesFile.getAbsolutePath()
                                + " to define a property named "
                                + propertyName
                                + " containing the path of your local ChromeDriver installation.");
            }
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            // Tells chrome not to show warning
            // "You are using an unsupported command-line flag: --ignore-certifcate-errors".
            // #14319
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--test-type ");
            driver = new ChromeDriver(options);
        } else if (BrowserUtil.isSafari(desiredCapabilities)) {
            driver = new SafariDriver();
        } else if (BrowserUtil.isPhantomJS(desiredCapabilities)) {
            driver = new PhantomJSDriver();
        } else {
            throw new RuntimeException(
                    "Not implemented support for running locally on "
                            + BrowserUtil
                                    .getBrowserIdentifier(desiredCapabilities));
        }
        setDriver(TestBench.createDriver(driver));
        setDesiredCapabilities(desiredCapabilities);
    }

    @Override
    protected Browser getRunLocallyBrowser() {
        Browser runLocallyBrowser = super.getRunLocallyBrowser();
        if (runLocallyBrowser != null) {
            // Always use annotation value if present
            return runLocallyBrowser;
        }

        String runLocallyValue = getProperty(RUN_LOCALLY_PROPERTY);
        if (runLocallyValue == null || runLocallyValue.trim().isEmpty()) {
            return null;
        }

        String browserName = runLocallyValue.trim().toUpperCase();
        try {
            return Browser.valueOf(browserName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid " + RUN_LOCALLY_PROPERTY
                    + " property from " + getSource(RUN_LOCALLY_PROPERTY)
                    + ": " + runLocallyValue + ". Expected one of "
                    + Arrays.toString(Browser.values()));
        }
    }
}
