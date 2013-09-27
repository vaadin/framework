/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.server.LegacyApplication;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.ui.UI;

/**
 * Base class for TestBench 3+ tests. All TB3+ tests in the project should
 * extend this class.
 * 
 * Provides:
 * <ul>
 * <li>Helpers for browser selection</li>
 * <li>Hub connection setup and teardown</li>
 * <li>Automatic generation of URL for a given test on the development server
 * using {@link #getUIClass()} or by automatically finding an enclosing UI class
 * and based on requested features, e.g. {@link #isDebug()}, {@link #isPush()}</li>
 * <li>Generic helpers for creating TB3+ tests</li>
 * </ul>
 * 
 * @author Vaadin Ltd
 */
@RunWith(value = TB3Runner.class)
public abstract class AbstractTB3Test extends TestBenchTestCase {
    /**
     * Height of the screenshots we want to capture
     */
    private static final int SCREENSHOT_HEIGHT = 850;

    /**
     * Width of the screenshots we want to capture
     */
    private static final int SCREENSHOT_WIDTH = 1500;

    private DesiredCapabilities desiredCapabilities;

    private boolean debug = false;

    private boolean push = false;
    {
        // Default browser to run on unless setDesiredCapabilities is called
        desiredCapabilities = BrowserUtil.firefox(24);
    }

    /**
     * Connect to the hub using a remote web driver, set the canvas size and
     * opens the initial URL as specified by {@link #getTestUrl()}
     * 
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        setupDriver();
    }

    /**
     * Creates and configure the web driver to be used for the test. By default
     * creates a remote web driver which connects to {@link #getHubURL()} and
     * selects a browser based on {@link #getDesiredCapabilities()}.
     * 
     * This method MUST call {@link #setDriver(WebDriver)} with the newly
     * generated driver.
     * 
     * @throws Exception
     *             If something goes wrong
     */
    protected void setupDriver() throws Exception {
        DesiredCapabilities capabilities = getDesiredCapabilities();

        WebDriver dr = TestBench.createDriver(new RemoteWebDriver(new URL(
                getHubURL()), capabilities));
        setDriver(dr);

        int w = SCREENSHOT_WIDTH;
        int h = SCREENSHOT_HEIGHT;

        if (BrowserUtil.isIE8(capabilities)) {
            // IE8 gets size wrong, who would have guessed...
            w += 4;
            h += 4;
        }
        try {
            testBench().resizeViewPortTo(w, h);
        } catch (UnsupportedOperationException e) {
            // Opera does not support this...
        }

    }

    /**
     * Opens the given test (defined by {@link #getTestUrl(boolean, boolean)},
     * optionally with debug window and/or push
     * 
     * @param debug
     * @param push
     */
    protected void openTestURL() {
        driver.get(getTestUrl());
    }

    /**
     * Returns the full URL to be used for the test
     * 
     * @return the full URL for the test
     */
    protected String getTestUrl() {
        String baseUrl = getBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        return baseUrl + getDeploymentPath();
    }

    /**
     * 
     * @return the location (URL) of the TB hub
     */
    protected String getHubURL() {
        return "http://" + getHubHostname() + ":4444/wd/hub";
    }

    /**
     * Used for building the hub URL to use for the test
     * 
     * @return the host name of the TestBench hub
     */
    protected abstract String getHubHostname();

    /**
     * Used to determine what URL to initially open for the test
     * 
     * @return the host name of development server
     */
    protected abstract String getDeploymentHostname();

    /**
     * Produces a collection of browsers to run the test on. This method is
     * executed by the test runner when determining how many test methods to
     * invoke and with what parameters. For each returned value a test method is
     * ran and before running that,
     * {@link #setDesiredCapabilities(DesiredCapabilities)} is invoked with the
     * value returned by this method.
     * 
     * This method is not static to allow overriding it in sub classes. By
     * default runs the test only on Firefox
     * 
     * @return The browsers to run the test on
     */
    public Collection<DesiredCapabilities> getBrowsersToTest() {
        return Collections.singleton(BrowserUtil.firefox(17));

    }

    /**
     * Used to determine which capabilities should be used when setting up a
     * {@link WebDriver} for this test. Typically set by a test runner or left
     * at its default (Firefox 24). If you want to run a test on a single
     * browser other than Firefox 24 you can override this method.
     * 
     * @return the requested browser capabilities
     */
    protected DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    /**
     * Sets the requested browser capabilities (typically browser name and
     * version)
     * 
     * @param desiredCapabilities
     */
    public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }

    /**
     * Shuts down the driver after the test has been completed
     * 
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
    }

    /**
     * Finds an element based on the part of a TB2 style locator following the
     * :: (e.g. vaadin=runLabelModes::PID_Scheckboxaction-Enabled/domChild[0] ->
     * PID_Scheckboxaction-Enabled/domChild[0]).
     * 
     * @param vaadinLocator
     *            The part following :: of the vaadin locator string
     * @return
     */
    protected WebElement vaadinElement(String vaadinLocator) {
        return driver.findElement(vaadinLocator(vaadinLocator));
    }

    /**
     * Find a Vaadin element based on its id given using Component.setId
     * 
     * @param id
     *            The id to locate
     * @return
     */
    public WebElement vaadinElementById(String id) {
        return driver.findElement(vaadinLocatorById(id));
    }

    /**
     * Finds a {@link By} locator based on the part of a TB2 style locator
     * following the :: (e.g.
     * vaadin=runLabelModes::PID_Scheckboxaction-Enabled/domChild[0] ->
     * PID_Scheckboxaction-Enabled/domChild[0]).
     * 
     * @param vaadinLocator
     *            The part following :: of the vaadin locator string
     * @return
     */
    public org.openqa.selenium.By vaadinLocator(String vaadinLocator) {
        String base = getApplicationId(getDeploymentPath());

        base += "::";
        return com.vaadin.testbench.By.vaadin(base + vaadinLocator);
    }

    /**
     * Constructs a {@link By} locator for the id given using Component.setId
     * 
     * @param id
     *            The id to locate
     * @return a locator for the given id
     */
    public By vaadinLocatorById(String id) {
        return vaadinLocator("PID_S" + id);
    }

    /**
     * Waits a short while for the given condition to become true. Use e.g. as
     * {@link #waitUntil(ExpectedConditions.textToBePresentInElement(by, text))}
     * 
     * @param condition
     *            the condition to wait for to become true
     */
    protected void waitUntil(ExpectedCondition<Boolean> condition) {
        new WebDriverWait(driver, 10).until(condition);
    }

    /**
     * Waits a short while for the given condition to become false. Use e.g. as
     * {@link #waitUntilNot(ExpectedConditions.textToBePresentInElement(by,
     * text))}
     * 
     * @param condition
     *            the condition to wait for to become false
     */
    protected void waitUntilNot(ExpectedCondition<Boolean> condition) {
        new WebDriverWait(driver, 10).until(ExpectedConditions.not(condition));
    }

    /**
     * Returns the path that should be used for the test. The path contains the
     * full path (appended to hostname+port) and must start with a slash.
     * 
     * @param push
     *            true if "?debug" should be added
     * @param debug
     *            true if /run-push should be used instead of /run
     * 
     * @return The URL path to the UI class to test
     */
    protected String getDeploymentPath() {
        Class<?> uiClass = getUIClass();
        if (uiClass != null) {
            return getDeploymentPath(uiClass);
        }
        throw new IllegalArgumentException("Unable to determine path for "
                + getClass().getCanonicalName());

    }

    /**
     * Returns the UI class the current test is connected to. Uses the enclosing
     * class if the test class is a static inner class to a UI class.
     * 
     * Test which are not enclosed by a UI class must implement this method and
     * return the UI class they want to test.
     * 
     * Note that this method will update the test name to the enclosing class to
     * be compatible with TB2 screenshot naming
     * 
     * @return the UI class the current test is connected to
     */
    protected Class<?> getUIClass() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            return enclosingClass;
        }
        return null;
    }

    /**
     * Returns whether to run the test in debug mode (with the debug console
     * open) or not
     * 
     * @return true to run with the debug window open, false by default
     */
    protected final boolean isDebug() {
        return debug;
    }

    /**
     * Sets whether to run the test in debug mode (with the debug console open)
     * or not.
     * 
     * @param debug
     *            true to open debug window, false otherwise
     */
    protected final void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Returns whether to run the test with push enabled (using /run-push) or
     * not. Note that push tests can and should typically be created using @Push
     * on the UI instead of overriding this method
     * 
     * @return true if /run-push is used, false otherwise
     */
    protected final boolean isPush() {
        return push;
    }

    /**
     * Sets whether to run the test with push enabled (using /run-push) or not.
     * Note that push tests can and should typically be created using @Push on
     * the UI instead of overriding this method
     * 
     * @param push
     *            true to use /run-push in the test, false otherwise
     */
    protected final void setPush(boolean push) {
        this.push = push;
    }

    /**
     * Returns the path for the given UI class when deployed on the test server.
     * The path contains the full path (appended to hostname+port) and must
     * start with a slash.
     * 
     * This method takes into account {@link #isPush()} and {@link #isDebug()}
     * when the path is generated.
     * 
     * @param uiClass
     * @param push
     *            true if "?debug" should be added
     * @param debug
     *            true if /run-push should be used instead of /run
     * @return The path to the given UI class
     */
    private String getDeploymentPath(Class<?> uiClass) {
        String runPath = "/run";
        if (isPush()) {
            runPath = "/run-push";
        }

        if (UI.class.isAssignableFrom(uiClass)) {
            return runPath + "/" + uiClass.getCanonicalName()
                    + (isDebug() ? "?debug" : "");
        } else if (LegacyApplication.class.isAssignableFrom(uiClass)) {
            return runPath + "/" + uiClass.getCanonicalName()
                    + "?restartApplication" + (isDebug() ? "&debug" : "");
        } else {
            throw new IllegalArgumentException(
                    "Unable to determine path for enclosing class "
                            + uiClass.getCanonicalName());
        }
    }

    /**
     * Used to determine what URL to initially open for the test
     * 
     * @return The base URL for the test. Does not include a trailing slash.
     */
    protected String getBaseURL() {
        return "http://" + getDeploymentHostname() + ":8888";
    }

    /**
     * Generates the application id based on the URL in a way compatible with
     * VaadinServletService.
     * 
     * @param pathWithQueryParameters
     *            The path part of the URL, possibly still containing query
     *            parameters
     * @return The application ID string used in Vaadin locators
     */
    private String getApplicationId(String pathWithQueryParameters) {
        // Remove any possible URL parameters
        String pathWithoutQueryParameters = pathWithQueryParameters.replaceAll(
                "\\?.*", "");
        if ("".equals(pathWithoutQueryParameters)) {
            return "ROOT";
        }

        // Retain only a-z and numbers
        return pathWithoutQueryParameters.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Helper method for sleeping X ms in a test. Catches and ignores
     * InterruptedExceptions
     * 
     * @param timeoutMillis
     *            Number of ms to wait
     */
    protected void sleep(int timeoutMillis) {
        try {
            Thread.sleep(timeoutMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Provides helper method for selecting the browser to run on
     * 
     * @author Vaadin Ltd
     */
    public static class BrowserUtil {
        /**
         * Gets the capabilities for Safari of the given version
         * 
         * @param version
         *            the major version
         * @return an object describing the capabilities required for running a
         *         test on the given Safari version
         */
        public static DesiredCapabilities safari(int version) {
            DesiredCapabilities c = DesiredCapabilities.safari();
            c.setVersion("" + version);
            return c;
        }

        /**
         * Gets the capabilities for Chrome of the given version
         * 
         * @param version
         *            the major version
         * @return an object describing the capabilities required for running a
         *         test on the given Chrome version
         */
        public static DesiredCapabilities chrome(int version) {
            DesiredCapabilities c = DesiredCapabilities.chrome();
            c.setVersion("" + version);
            c.setPlatform(Platform.XP);
            return c;
        }

        /**
         * Gets the capabilities for Opera of the given version
         * 
         * @param version
         *            the major version
         * @return an object describing the capabilities required for running a
         *         test on the given Opera version
         */
        public static DesiredCapabilities opera(int version) {
            DesiredCapabilities c = DesiredCapabilities.opera();
            c.setVersion("" + version);
            c.setPlatform(Platform.XP);
            return c;
        }

        /**
         * Gets the capabilities for Firefox of the given version
         * 
         * @param version
         *            the major version
         * @return an object describing the capabilities required for running a
         *         test on the given Firefox version
         */
        public static DesiredCapabilities firefox(int version) {
            DesiredCapabilities c = DesiredCapabilities.firefox();
            c.setVersion("" + version);
            c.setPlatform(Platform.XP);
            return c;
        }

        /**
         * Gets the capabilities for Internet Explorer of the given version
         * 
         * @param version
         *            the major version
         * @return an object describing the capabilities required for running a
         *         test on the given Internet Explorer version
         */
        public static DesiredCapabilities ie(int version) {
            DesiredCapabilities c = DesiredCapabilities.internetExplorer();
            c.setVersion("" + version);
            return c;
        }

        /**
         * Checks if the given capabilities refer to Internet Explorer 8
         * 
         * @param capabilities
         * @return true if the capabilities refer to IE8, false otherwise
         */
        public static boolean isIE8(DesiredCapabilities capabilities) {
            return BrowserType.IE.equals(capabilities.getBrowserName())
                    && "8".equals(capabilities.getVersion());
        }

        /**
         * Returns a human readable identifier of the given browser. Used for
         * test naming and screenshots
         * 
         * @param capabilities
         * @return a human readable string describing the capabilities
         */
        public static String getBrowserIdentifier(
                DesiredCapabilities capabilities) {
            String browserName = capabilities.getBrowserName();

            if (BrowserType.IE.equals(browserName)) {
                return "InternetExplorer";
            } else if (BrowserType.FIREFOX.equals(browserName)) {
                return "Firefox";
            } else if (BrowserType.CHROME.equals(browserName)) {
                return "Chrome";
            } else if (BrowserType.SAFARI.equals(browserName)) {
                return "Safari";
            } else if (BrowserType.OPERA.equals(browserName)) {
                return "Opera";
            }

            return browserName;
        }

        /**
         * Returns a human readable identifier of the platform described by the
         * given capabilities. Used mainly for screenshots
         * 
         * @param capabilities
         * @return a human readable string describing the platform
         */
        public static String getPlatform(DesiredCapabilities capabilities) {
            if (capabilities.getPlatform() == Platform.WIN8
                    || capabilities.getPlatform() == Platform.WINDOWS
                    || capabilities.getPlatform() == Platform.VISTA
                    || capabilities.getPlatform() == Platform.XP) {
                return "Windows";
            } else if (capabilities.getPlatform() == Platform.MAC) {
                return "Mac";
            }
            return capabilities.getPlatform().toString();
        }

        /**
         * Returns a string which uniquely (enough) identifies this browser.
         * Used mainly in screenshot names.
         * 
         * @param capabilities
         * 
         * @return a unique string for each browser
         */
        public static String getUniqueIdentifier(
                DesiredCapabilities capabilities) {
            return getUniqueIdentifier(getPlatform(capabilities),
                    getBrowserIdentifier(capabilities),
                    capabilities.getVersion());
        }

        /**
         * Returns a string which uniquely (enough) identifies this browser.
         * Used mainly in screenshot names.
         * 
         * @param capabilities
         * 
         * @return a unique string for each browser
         */
        public static String getUniqueIdentifier(
                DesiredCapabilities capabilities, String versionOverride) {
            return getUniqueIdentifier(getPlatform(capabilities),
                    getBrowserIdentifier(capabilities), versionOverride);
        }

        private static String getUniqueIdentifier(String platform,
                String browser, String version) {
            return platform + "_" + browser + "_" + version;
        }

    }

    /**
     * Called by the test runner whenever there is an exception in the test that
     * will cause termination of the test
     * 
     * @param t
     *            the throwable which caused the termination
     */
    public void onUncaughtException(Throwable t) {
        // Do nothing by default

    }
}
