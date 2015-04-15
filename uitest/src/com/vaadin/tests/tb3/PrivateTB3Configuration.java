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
import java.util.Enumeration;
import java.util.Properties;

import org.junit.Assert;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;

/**
 * Provides values for parameters which depend on where the test is run.
 * Parameters should be configured in work/eclipse-run-selected-test.properties.
 * A template is available in uitest/.
 * 
 * @author Vaadin Ltd
 */
@RunOnHub("tb3-hub.intra.itmill.com")
@BrowserFactory(VaadinBrowserFactory.class)
public abstract class PrivateTB3Configuration extends ScreenshotTB3Test {
    /**
     * 
     */
    public static final String SCREENSHOT_DIRECTORY = "com.vaadin.testbench.screenshot.directory";
    private static final String HOSTNAME_PROPERTY = "com.vaadin.testbench.deployment.hostname";
    private static final String RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.runLocally";
    private static final String ALLOW_RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.allowRunLocally";
    private static final String PORT_PROPERTY = "com.vaadin.testbench.deployment.port";
    private static final String DEPLOYMENT_PROPERTY = "com.vaadin.testbench.deployment.url";
    private static final String HUB_URL = "com.vaadin.testbench.hub.url";
    private static final Properties properties = new Properties();
    private static final File propertiesFile = new File("work",
            "eclipse-run-selected-test.properties");
    private static final String FIREFOX_PATH = "firefox.path";

    static {
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
                if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
                    System.setProperty("useLocalWebDriver", "true");
                    DesiredCapabilities localBrowser = getRunLocallyCapabilities();
                    System.setProperty(
                            "browsers.include",
                            localBrowser.getBrowserName()
                                    + localBrowser.getVersion());
                }
                if (properties.containsKey(FIREFOX_PATH)) {
                    System.setProperty(FIREFOX_PATH,
                            properties.getProperty(FIREFOX_PATH));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void setup() throws Exception {
        String allowRunLocally = getProperty(ALLOW_RUN_LOCALLY_PROPERTY);
        if ((allowRunLocally == null || !allowRunLocally.equals("" + true))
                && getClass().getAnnotation(RunLocally.class) != null) {
            Assert.fail("@RunLocally annotation is not allowed by default in framework tests. "
                    + "See file uitest/eclipse-run-selected-test.properties for more information.");
        }

        super.setup();
    }

    private static DesiredCapabilities getRunLocallyCapabilities() {
        Browser localBrowser;
        try {
            localBrowser = Browser.valueOf(properties.getProperty(
                    RUN_LOCALLY_PROPERTY).toUpperCase());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Falling back to FireFox");
            localBrowser = Browser.FIREFOX;
        }
        return localBrowser.getDesiredCapabilities();
    }

    protected static String getProperty(String name) {
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
        String screenshotDirectory = getProperty(SCREENSHOT_DIRECTORY);
        if (screenshotDirectory == null) {
            throw new RuntimeException(
                    "No screenshot directory defined. Use -D"
                            + SCREENSHOT_DIRECTORY + "=<path>");
        }
        return screenshotDirectory;
    }

    @Override
    protected String getHubURL() {
        String hubUrl = getProperty(HUB_URL);
        if (hubUrl == null || hubUrl.trim().isEmpty()) {
            return super.getHubURL();
        }

        return hubUrl;
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
}
