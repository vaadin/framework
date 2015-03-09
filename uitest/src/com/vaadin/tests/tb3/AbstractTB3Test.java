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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gwt.thirdparty.guava.common.base.Joiner;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UIProvider;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.UI;

import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

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
@RunWith(TB3Runner.class)
public abstract class AbstractTB3Test extends ParallelTest {

    @Rule
    public RetryOnFail retry = new RetryOnFail();

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

    private boolean debug = false;

    private boolean push = false;

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
    @Override
    public void setup() throws Exception {
        super.setup();

        int w = SCREENSHOT_WIDTH;
        int h = SCREENSHOT_HEIGHT;

        if (BrowserUtil.isIE8(super.getDesiredCapabilities())) {
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
     * Method for closing the tested application.
     */
    protected void closeApplication() {
        if (driver != null) {
            try {
                openTestURL("closeApplication");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected WebElement getTooltipErrorElement() {
        WebElement tooltip = getDriver().findElement(
                com.vaadin.testbench.By.className("v-tooltip"));
        return tooltip.findElement(By.className("v-errormessage"));
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

    protected void waitUntilRowIsVisible(final TableElement table, final int row) {
        waitUntil(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver input) {
                try {
                    return table.getCell(row, 0) != null;
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        });
    }

    protected void scrollTable(TableElement table, int rows, int rowToWait) {
        testBenchElement(table.findElement(By.className("v-scrollable")))
                .scroll(rows * 30);

        waitUntilRowIsVisible(table, rowToWait);
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    protected void openTestURL(String... parameters) {
        openTestURL(getUIClass(), parameters);
    }

    /**
     * Opens the given test (defined by {@link #getTestUrl()}, optionally with
     * debug window and/or push (depending on {@link #isDebug()} and
     * {@link #isPush()}.
     */
    protected void openTestURL(Class<?> uiClass, String... parameters) {
        openTestURL(uiClass, new HashSet<String>(Arrays.asList(parameters)));
    }

    private void openTestURL(Class<?> uiClass, Set<String> parameters) {
        String url = getTestURL(uiClass);

        if (isDebug()) {
            parameters.add("debug");
        }

        if (LegacyApplication.class.isAssignableFrom(uiClass)) {
            parameters.add("restartApplication");
        }

        if (parameters.size() > 0) {
            url += "?" + Joiner.on("&").join(parameters);
        }

        driver.get(url);
    }

    /**
     * Returns the full URL to be used for the test
     * 
     * @return the full URL for the test
     */
    protected String getTestUrl() {
        return StringUtils.strip(getBaseURL(), "/") + getDeploymentPath();
    }

    /**
     * Returns the full URL to be used for the test for the provided UI class.
     * 
     * @return the full URL for the test
     */
    protected String getTestURL(Class<?> uiClass) {
        return StringUtils.strip(getBaseURL(), "/")
                + getDeploymentPath(uiClass);
    }

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
    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections.singletonList(Browser.FIREFOX
                .getDesiredCapabilities());
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
    protected String getDeploymentPath(Class<?> uiClass) {
        String runPath = "/run";
        if (isPush()) {
            runPath = "/run-push";
        }

        if (UI.class.isAssignableFrom(uiClass)
                || UIProvider.class.isAssignableFrom(uiClass)
                || LegacyApplication.class.isAssignableFrom(uiClass)) {
            return runPath + "/" + uiClass.getCanonicalName();
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
            JsonObject object = extractObject(response);
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

    private static JsonObject extractObject(HttpResponse resp)
            throws IOException {
        InputStream contents = resp.getEntity().getContent();
        StringWriter writer = new StringWriter();
        IOUtils.copy(contents, writer, "UTF8");
        return JsonUtil.parse(writer.toString());
    }

    protected void click(CheckBoxElement checkbox) {
        checkbox.findElement(By.xpath("input")).click();
    }
}
