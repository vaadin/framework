/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import static com.vaadin.tests.tb3.TB3Runner.localWebDriverIsUsed;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UIProvider;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.tb3.MultiBrowserTest.Browser;
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

    /**
     * Timeout used by the TB grid
     */
    private static final int BROWSER_TIMEOUT_IN_MS = 30 * 1000;

    private static final int BROWSER_INIT_ATTEMPTS = 5;

    private DesiredCapabilities desiredCapabilities;

    private boolean debug = false;

    private boolean push = false;
    {
        // Default browser to run on unless setDesiredCapabilities is called
        desiredCapabilities = Browser.FIREFOX.getDesiredCapabilities();
    }

    static {
        com.vaadin.testbench.Parameters
                .setScreenshotComparisonCursorDetection(true);
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
        DesiredCapabilities capabilities;

        Browser runLocallyBrowser = getRunLocallyBrowser();
        if (runLocallyBrowser != null) {
            if (System.getenv().containsKey("TEAMCITY_VERSION")) {
                throw new RuntimeException(
                        "@RunLocally is not supported for tests run on the build server");
            }
            capabilities = runLocallyBrowser.getDesiredCapabilities();
            setupLocalDriver(capabilities);
        } else {
            capabilities = getDesiredCapabilities();

            if (localWebDriverIsUsed()) {
                setupLocalDriver(capabilities);
            } else {
                setupRemoteDriver(capabilities);
            }
        }

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

    protected Browser getRunLocallyBrowser() {
        RunLocally runLocally = getClass().getAnnotation(RunLocally.class);
        if (runLocally != null) {
            return runLocally.value();
        } else {
            return null;
        }
    }

    protected WebElement getTooltipElement() {
        return getDriver().findElement(
                com.vaadin.testbench.By.className("v-tooltip-text"));
    }

    protected Coordinates getCoordinates(TestBenchElement element) {
        return ((Locatable) element.getWrappedElement()).getCoordinates();
    }

    private boolean hasDebugMessage(String message) {
        return getDebugMessage(message) != null;
    }

    private WebElement getDebugMessage(String message) {
        return driver.findElement(By.xpath(String.format(
                "//span[@class='v-debugwindow-message' and text()='%s']",
                message)));
    }

    protected void waitForDebugMessage(final String expectedMessage) {
        waitForDebugMessage(expectedMessage, 30);
    }

    protected void waitForDebugMessage(final String expectedMessage, int timeout) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return hasDebugMessage(expectedMessage);
            }
        }, timeout);
    }

    protected void clearDebugMessages() {
        driver.findElement(
                By.xpath("//button[@class='v-debugwindow-button' and @title='Clear log']"))
                .click();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface RunLocally {
        public Browser value() default Browser.FIREFOX;
    }

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes. Used only when {@link #runLocally()} is overridden to
     * return true;
     */
    protected abstract void setupLocalDriver(
            DesiredCapabilities desiredCapabilities);

    /**
     * Creates a {@link WebDriver} instance used for running the test remotely.
     * 
     * @since
     * @param capabilities
     *            the type of browser needed
     * @throws Exception
     */
    private void setupRemoteDriver(DesiredCapabilities capabilities)
            throws Exception {
        if (BrowserUtil.isIE(capabilities)) {
            if (requireWindowFocusForIE()) {
                capabilities.setCapability(
                        InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
            }
            if (!usePersistentHoverForIE()) {
                capabilities.setCapability(
                        InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,
                        false);
            }
        }

        for (int i = 1; i <= BROWSER_INIT_ATTEMPTS; i++) {
            try {
                WebDriver dr = TestBench.createDriver(new RemoteWebDriver(
                        new URL(getHubURL()), capabilities));
                setDriver(dr);
                return;
            } catch (Exception e) {
                System.err.println("Browser startup for " + capabilities
                        + " failed on attempt " + i + ": " + e.getMessage());
                if (i == BROWSER_INIT_ATTEMPTS) {
                    throw e;
                }
            }
        }

    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    protected void openTestURL() {
        openTestURL("");
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    protected void openTestURL(String extraParameters) {
        String url = getTestUrl();
        if (url.contains("?")) {
            url = url + "&" + extraParameters;
        } else {
            url = url + "?" + extraParameters;
        }
        driver.get(url);
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
     * Used to determine what port the test is running on
     * 
     * @return The port teh test is running on, by default 8888
     */
    protected abstract int getDeploymentPort();

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
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections.singletonList(Browser.FIREFOX
                .getDesiredCapabilities());
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
        // Make a copy as the desired capabilities can come from a shared,
        // static resource. This will cause all kinds of problems if some test
        // modifies the capabilities
        this.desiredCapabilities = new DesiredCapabilities(desiredCapabilities);
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
     * Uses JavaScript to determine the currently focused element.
     * 
     * @return Focused element or null
     */
    protected WebElement getFocusedElement() {
        Object focusedElement = executeScript("return document.activeElement");
        if (null != focusedElement) {
            return (WebElement) focusedElement;
        } else {
            return null;
        }
    }

    /**
     * Executes the given Javascript
     * 
     * @param script
     *            the script to execute
     * @return whatever
     *         {@link org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)}
     *         returns
     */
    protected Object executeScript(String script) {
        return ((JavascriptExecutor) getDriver()).executeScript(script);
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
     * Waits up to 10s for the given condition to become true. Use e.g. as
     * {@link #waitUntil(ExpectedConditions.textToBePresentInElement(by, text))}
     * 
     * @param condition
     *            the condition to wait for to become true
     */
    protected <T> void waitUntil(ExpectedCondition<T> condition) {
        waitUntil(condition, 10);
    }

    /**
     * Waits the given number of seconds for the given condition to become true.
     * Use e.g. as {@link
     * #waitUntil(ExpectedConditions.textToBePresentInElement(by, text))}
     * 
     * @param condition
     *            the condition to wait for to become true
     */
    protected <T> void waitUntil(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        new WebDriverWait(driver, timeoutInSeconds).until(condition);
    }

    /**
     * Waits up to 10s for the given condition to become false. Use e.g. as
     * {@link #waitUntilNot(ExpectedConditions.textToBePresentInElement(by,
     * text))}
     * 
     * @param condition
     *            the condition to wait for to become false
     */
    protected <T> void waitUntilNot(ExpectedCondition<T> condition) {
        waitUntilNot(condition, 10);
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * false. Use e.g. as {@link
     * #waitUntilNot(ExpectedConditions.textToBePresentInElement(by, text))}
     * 
     * @param condition
     *            the condition to wait for to become false
     */
    protected <T> void waitUntilNot(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        waitUntil(ExpectedConditions.not(condition), timeoutInSeconds);
    }

    protected void waitForElementPresent(final By by) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void waitForElementVisible(final By by) {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Checks if the given element has the given class name.
     * 
     * Matches only full class names, i.e. has ("foo") does not match
     * class="foobar"
     * 
     * @param element
     * @param className
     * @return
     */
    protected boolean hasCssClass(WebElement element, String className) {
        String classes = element.getAttribute("class");
        if (classes == null || classes.isEmpty()) {
            return (className == null || className.isEmpty());
        }

        for (String cls : classes.split(" ")) {
            if (className.equals(cls)) {
                return true;
            }
        }

        return false;
    }

    /**
     * For tests extending {@link AbstractTestUIWithLog}, returns the element
     * for the Nth log row
     * 
     * @param rowNr
     *            The log row to retrieve
     * @return the Nth log row
     */
    protected WebElement getLogRowElement(int rowNr) {
        return vaadinElementById("Log_row_" + rowNr);
    }

    /**
     * For tests extending {@link AbstractTestUIWithLog}, returns the text in
     * the Nth log row
     * 
     * @param rowNr
     *            The log row to retrieve text for
     * @return the text in the log row
     */
    protected String getLogRow(int rowNr) {
        return getLogRowElement(rowNr).getText();
    }

    /**
     * Asserts that {@literal a} is &gt;= {@literal b}
     * 
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertGreaterOrEqual(String message,
            Comparable<T> a, T b) throws AssertionError {
        if (a.compareTo(b) >= 0) {
            return;
        }

        throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &gt; {@literal b}
     * 
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertGreater(String message, Comparable<T> a,
            T b) throws AssertionError {
        if (a.compareTo(b) > 0) {
            return;
        }
        throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &lt;= {@literal b}
     * 
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertLessThanOrEqual(String message,
            Comparable<T> a, T b) throws AssertionError {
        if (a.compareTo(b) <= 0) {
            return;
        }

        throw new AssertionError(decorate(message, a, b));
    }

    /**
     * Asserts that {@literal a} is &lt; {@literal b}
     * 
     * @param message
     *            The message to include in the {@link AssertionError}
     * @param a
     * @param b
     * @throws AssertionError
     *             If comparison fails
     */
    public static final <T> void assertLessThan(String message,
            Comparable<T> a, T b) throws AssertionError {
        if (a.compareTo(b) < 0) {
            return;
        }
        throw new AssertionError(decorate(message, a, b));
    }

    private static <T> String decorate(String message, Comparable<T> a, T b) {
        message = message.replace("{0}", a.toString());
        message = message.replace("{1}", b.toString());
        return message;
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
     * Returns the UI class the current test is connected to (or in special
     * cases UIProvider or LegacyApplication). Uses the enclosing class if the
     * test class is a static inner class to a UI class.
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
        try {
            // Convention: SomeUITest uses the SomeUI UI class
            String uiClassName = getClass().getName().replaceFirst("Test$", "");
            Class<?> cls = Class.forName(uiClassName);
            if (isSupportedRunnerClass(cls)) {
                return cls;
            }
        } catch (Exception e) {
        }
        throw new RuntimeException(
                "Could not determine UI class. Ensure the test is named UIClassTest and is in the same package as the UIClass");
    }

    /**
     * @return true if the given class is supported by ApplicationServletRunner
     */
    @SuppressWarnings("deprecation")
    private boolean isSupportedRunnerClass(Class<?> cls) {
        if (UI.class.isAssignableFrom(cls)) {
            return true;
        }
        if (UIProvider.class.isAssignableFrom(cls)) {
            return true;
        }
        if (LegacyApplication.class.isAssignableFrom(cls)) {
            return true;
        }

        return false;
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
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
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
     * Sleeps for the given number of ms but ensures that the browser connection
     * does not time out.
     * 
     * @param timeoutMillis
     *            Number of ms to wait
     * @throws InterruptedException
     */
    protected void sleep(int timeoutMillis) throws InterruptedException {
        while (timeoutMillis > 0) {
            int d = Math.min(BROWSER_TIMEOUT_IN_MS, timeoutMillis);
            Thread.sleep(d);
            timeoutMillis -= d;

            // Do something to keep the connection alive
            getDriver().getTitle();
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
            c.setPlatform(Platform.MAC);
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
            c.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,
                    true);
            return c;
        }

        /**
         * Gets the capabilities for PhantomJS of the given version
         * 
         * @param version
         *            the major version
         * @return an object describing the capabilities required for running a
         *         test on the given PhantomJS version
         */
        public static DesiredCapabilities phantomJS(int version) {
            DesiredCapabilities c = DesiredCapabilities.phantomjs();
            c.setPlatform(Platform.LINUX);
            c.setVersion("" + version);
            return c;
        }

        /**
         * Checks if the given capabilities refer to Internet Explorer 8
         * 
         * @param capabilities
         * @param version
         * @return true if the capabilities refer to IE8, false otherwise
         */
        public static boolean isIE8(DesiredCapabilities capabilities) {
            return isIE(8, capabilities);
        }

        /**
         * Checks if the given capabilities refer to Internet Explorer of the
         * given version
         * 
         * @param capabilities
         * @param version
         * @return true if the capabilities refer to IE of the given version,
         *         false otherwise
         */
        public static boolean isIE(int version, DesiredCapabilities capabilities) {
            return isIE(capabilities)
                    && ("" + version).equals(capabilities.getVersion());
        }

        /**
         * @param capabilities
         *            The capabilities to check
         * @return true if the capabilities refer to Internet Explorer, false
         *         otherwise
         */
        public static boolean isIE(DesiredCapabilities capabilities) {
            return BrowserType.IE.equals(capabilities.getBrowserName());
        }

        /**
         * @param capabilities
         *            The capabilities to check
         * @return true if the capabilities refer to Chrome, false otherwise
         */
        public static boolean isChrome(DesiredCapabilities capabilities) {
            return BrowserType.CHROME.equals(capabilities.getBrowserName());
        }

        /**
         * @param capabilities
         *            The capabilities to check
         * @return true if the capabilities refer to Opera, false otherwise
         */
        public static boolean isOpera(DesiredCapabilities capabilities) {
            return BrowserType.OPERA.equals(capabilities.getBrowserName());
        }

        /**
         * @param capabilities
         *            The capabilities to check
         * @return true if the capabilities refer to Safari, false otherwise
         */
        public static boolean isSafari(DesiredCapabilities capabilities) {
            return BrowserType.SAFARI.equals(capabilities.getBrowserName());
        }

        /**
         * @param capabilities
         *            The capabilities to check
         * @return true if the capabilities refer to Firefox, false otherwise
         */
        public static boolean isFirefox(DesiredCapabilities capabilities) {
            return BrowserType.FIREFOX.equals(capabilities.getBrowserName());
        }

        /**
         * @param capabilities
         *            The capabilities to check
         * @return true if the capabilities refer to PhantomJS, false otherwise
         */
        public static boolean isPhantomJS(DesiredCapabilities capabilities) {
            return BrowserType.PHANTOMJS.equals(capabilities.getBrowserName());
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
            if (isIE(capabilities)) {
                return "InternetExplorer";
            } else if (isFirefox(capabilities)) {
                return "Firefox";
            } else if (isChrome(capabilities)) {
                return "Chrome";
            } else if (isSafari(capabilities)) {
                return "Safari";
            } else if (isOpera(capabilities)) {
                return "Opera";
            } else if (isPhantomJS(capabilities)) {
                return "PhantomJS";
            }

            return capabilities.getBrowserName();
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

    /**
     * Returns the mouse object for doing mouse commands
     * 
     * @return Returns the mouse
     */
    public Mouse getMouse() {
        return ((HasInputDevices) getDriver()).getMouse();
    }

    /**
     * Returns the keyboard object for controlling keyboard events
     * 
     * @return Return the keyboard
     */
    public Keyboard getKeyboard() {
        return ((HasInputDevices) getDriver()).getKeyboard();
    }

    public void hitButton(String id) {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            driver.findElement(By.id(id)).click();
        } else {
            WebDriverBackedSelenium selenium = new WebDriverBackedSelenium(
                    driver, driver.getCurrentUrl());

            selenium.keyPress("id=" + id, "\\13");
        }
    }

    protected void openDebugLogTab() {

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                WebElement element = getDebugLogButton();
                return element != null;
            }
        }, 15);
        getDebugLogButton().click();
    }

    private WebElement getDebugLogButton() {
        return findElement(By.xpath("//button[@title='Debug message log']"));
    }

    /**
     * Should the "require window focus" be enabled for Internet Explorer.
     * RequireWindowFocus makes tests more stable but seems to be broken with
     * certain commands such as sendKeys. Therefore it is not enabled by default
     * for all tests
     * 
     * @return true, to use the "require window focus" feature, false otherwise
     */
    protected boolean requireWindowFocusForIE() {
        return false;
    }

    /**
     * Should the "enable persistent hover" be enabled for Internet Explorer.
     * 
     * Persistent hovering causes continuous firing of mouse over events at the
     * last location the mouse cursor has been moved to. This is to avoid
     * problems where the real mouse cursor is inside the browser window and
     * Internet Explorer uses that location for some undefined operation
     * (http://
     * jimevansmusic.blogspot.fi/2012/06/whats-wrong-with-internet-explorer
     * .html)
     * 
     * @return true, to use the "persistent hover" feature, false otherwise
     */
    protected boolean usePersistentHoverForIE() {
        return true;
    }

    // FIXME: Remove this once TB4 getRemoteControlName works properly
    private RemoteWebDriver getRemoteDriver() {
        WebDriver d = getDriver();
        if (d instanceof TestBenchDriverProxy) {
            try {
                Field f = TestBenchDriverProxy.class
                        .getDeclaredField("actualDriver");
                f.setAccessible(true);
                return (RemoteWebDriver) f.get(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (d instanceof RemoteWebDriver) {
            return (RemoteWebDriver) d;
        }

        return null;

    }

    // FIXME: Remove this once TB4 getRemoteControlName works properly
    protected String getRemoteControlName() {
        try {
            RemoteWebDriver d = getRemoteDriver();
            if (d == null) {
                return null;
            }
            HttpCommandExecutor ce = (HttpCommandExecutor) d
                    .getCommandExecutor();
            String hostName = ce.getAddressOfRemoteServer().getHost();
            int port = ce.getAddressOfRemoteServer().getPort();
            HttpHost host = new HttpHost(hostName, port);
            DefaultHttpClient client = new DefaultHttpClient();
            URL sessionURL = new URL("http://" + hostName + ":" + port
                    + "/grid/api/testsession?session=" + d.getSessionId());
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest(
                    "POST", sessionURL.toExternalForm());
            HttpResponse response = client.execute(host, r);
            JSONObject object = extractObject(response);
            URL myURL = new URL(object.getString("proxyId"));
            if ((myURL.getHost() != null) && (myURL.getPort() != -1)) {
                return myURL.getHost();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected boolean logContainsText(String string) {
        List<String> logs = getLogs();

        for (String text : logs) {
            if (text.contains(string)) {
                return true;
            }
        }

        return false;
    }

    protected List<String> getLogs() {
        VerticalLayoutElement log = $(VerticalLayoutElement.class).id("Log");
        List<LabelElement> logLabels = log.$(LabelElement.class).all();
        List<String> logTexts = new ArrayList<String>();

        for (LabelElement label : logLabels) {
            logTexts.add(label.getText());
        }

        return logTexts;
    }

    private static JSONObject extractObject(HttpResponse resp)
            throws IOException, JSONException {
        InputStream contents = resp.getEntity().getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(contents, writer, "UTF8");
        JSONObject objToReturn = new JSONObject(writer.toString());
        return objToReturn;
    }

}
