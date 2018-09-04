package com.vaadin.tests.tb3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.UIProvider;
import com.vaadin.testbench.ScreenshotOnFailureRule;
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
 * and based on requested features, e.g. {@link #isDebug()},
 * {@link #isPush()}</li>
 * <li>Generic helpers for creating TB3+ tests</li>
 * </ul>
 *
 * @author Vaadin Ltd
 */
@RunWith(TB3Runner.class)
public abstract class AbstractTB3Test extends ParallelTest {

    @Rule
    public TestName testName = new TestName();

    {
        // Override default screenshotOnFailureRule to close application
        screenshotOnFailure = new ScreenshotOnFailureRule(this, true) {
            @Override
            protected void finished(Description description) {
                closeApplication();
                super.finished(description);
            }
        };
    }

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
        if (getDriver() != null) {
            try {
                openTestURL("closeApplication");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected WebElement getTooltipErrorElement() {
        WebElement tooltip = getDriver()
                .findElement(com.vaadin.testbench.By.className("v-tooltip"));
        return tooltip.findElement(By.className("v-errormessage"));
    }

    protected WebElement getTooltipElement() {
        return getDriver().findElement(
                com.vaadin.testbench.By.className("v-tooltip-text"));
    }

    private boolean hasDebugMessage(String message) {
        return getDebugMessage(message) != null;
    }

    private WebElement getDebugMessage(String message) {
        return driver.findElement(By.xpath(String.format(
                "//span[@class='v-debugwindow-message' and text()='%s']",
                message)));
    }

    protected void minimizeDebugWindow() {
        if (findElement(By.className("v-debugwindow-tabs")).isDisplayed()) {
            findElements(By.className("v-debugwindow-button")).stream()
                    .filter(e -> e.getAttribute("title").equals("Minimize"))
                    .findFirst().ifPresent(WebElement::click);
        }
    }

    protected void showDebugWindow() {
        if (!findElement(By.className("v-debugwindow-tabs")).isDisplayed()) {
            findElements(By.className("v-debugwindow-button")).stream()
                    .filter(e -> e.getAttribute("title").equals("Minimize"))
                    .findFirst().ifPresent(WebElement::click);
        }
    }

    protected void waitForDebugMessage(final String expectedMessage) {
        waitForDebugMessage(expectedMessage, 30);
    }

    protected void waitForDebugMessage(final String expectedMessage,
            int timeout) {
        waitUntil(input -> hasDebugMessage(expectedMessage), timeout);
    }

    protected void clearDebugMessages() {
        driver.findElement(By.xpath(
                "//button[@class='v-debugwindow-button' and @title='Clear log']"))
                .click();
    }

    protected void waitUntilRowIsVisible(final TableElement table,
            final int row) {
        waitUntil(input -> {
            try {
                return table.getCell(row, 0) != null;
            } catch (NoSuchElementException e) {
                return false;
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
    protected void openTestURL() {
        openTestURL(new String[0]);
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
        openTestURL(uiClass, new HashSet<>(Arrays.asList(parameters)));
    }

    private void openTestURL(Class<?> uiClass, Set<String> parameters) {
        String url = getTestURL(uiClass);

        if (isDebug()) {
            parameters.add("debug");
        }

        if (LegacyApplication.class.isAssignableFrom(uiClass)) {
            parameters.add("restartApplication");
        }

        if (!parameters.isEmpty()) {
            url += "?" + StringUtils.join(parameters, "&");
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
     * @return The port the test is running on, by default 8888
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
        return Collections
                .singletonList(Browser.FIREFOX.getDesiredCapabilities());
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
    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) getDriver()).executeScript(script, args);
    }

    /**
     * Find a Vaadin element based on its id given using Component.setId
     *
     * @param id
     *            The id to locate
     * @return
     */
    public WebElement vaadinElementById(String id) {
        return driver.findElement(By.id(id));
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
     * false. Use e.g. as
     * {@link #waitUntilNot(ExpectedConditions.textToBePresentInElement(by,
     * text))}
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

    protected void waitForElementNotPresent(final By by) {
        waitUntil(input -> input.findElements(by).isEmpty());
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
     * For tests extending AbstractTestUIWithLog, returns the element for the
     * Nth log row
     *
     * @param rowNr
     *            The log row to retrieve
     * @return the Nth log row
     */
    protected WebElement getLogRowElement(int rowNr) {
        return vaadinElementById("Log_row_" + rowNr);
    }

    /**
     * For tests extending AbstractTestUIWithLog, returns the text in the Nth
     * log row
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
    public static final <T> void assertLessThan(String message, Comparable<T> a,
            T b) throws AssertionError {
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
        String pathWithoutQueryParameters = pathWithQueryParameters
                .replaceAll("\\?.*", "");
        if (pathWithoutQueryParameters.isEmpty()) {
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
     */
    protected void sleep(int timeoutMillis) {
        while (timeoutMillis > 0) {
            int d = Math.min(BROWSER_TIMEOUT_IN_MS, timeoutMillis);
            try {
                Thread.sleep(d);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
        driver.findElement(By.id(id)).click();
    }

    protected void openDebugLogTab() {

        waitUntil(input -> {
            try {
                WebElement element = getDebugLogButton();
                return element != null;
            } catch (NoSuchElementException e) {
                return false;
            }
        }, 15);
        getDebugLogButton().click();
    }

    private WebElement getDebugLogButton() {
        return findElement(By.xpath("//button[@title='Debug message log']"));
    }

    protected void assertNoDebugMessage(Level level) {
        // class="v-debugwindow-row Level.getName()"
        List<WebElement> logElements = driver.findElements(By.xpath(String
                .format("//div[@class='v-debugwindow-row %s']/span[@class='v-debugwindow-message']",
                        level.getName())));
        if (!logElements.isEmpty()) {
            String logRows = "";
            for (WebElement e : logElements) {
                logRows += "\n" + e.getText();
            }
            fail("Found debug messages with level " + level.getName() + ": "
                    + logRows);
        }
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

    /**
     * Should the "native events" be enabled for Internet Explorer.
     * <p>
     * Native events sometimes cause failure in clicking on buttons/checkboxes
     * but are possibly needed for some operations.
     *
     * @return true, to use "native events", false to use generated Javascript
     *         events
     */
    protected boolean useNativeEventsForIE() {
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
            try (DefaultHttpClient client = new DefaultHttpClient()) {
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
        List<String> logTexts = new ArrayList<>();

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
        WebElement cb = checkbox.findElement(By.xpath("input"));
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            testBenchElement(cb).click(0, 0);
        } else if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // Firefox workaround
            getCommandExecutor().executeScript("arguments[0].click()", cb);
        } else {
            cb.click();
        }
    }

    protected void clickElement(WebElement element) {
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // Workaround for Selenium/TB and Firefox 45 issue
            ((TestBenchElement) (element)).clickHiddenElement();
        } else {
            element.click();
        }
    }

    protected void contextClickElement(WebElement element) {
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // Workaround for Selenium/TB and Firefox 45 issue
            getCommandExecutor().executeScript(
                    "var ev = document.createEvent('HTMLEvents'); ev.initEvent('contextmenu', true, false); arguments[0].dispatchEvent(ev);",
                    element);
        } else {
            new Actions(getDriver()).contextClick(element).perform();
        }
    }

    protected boolean isLoadingIndicatorVisible() {
        WebElement loadingIndicator = findElement(
                By.className("v-loading-indicator"));

        return loadingIndicator.isDisplayed();
    }

    protected void waitUntilLoadingIndicatorVisible() {
        waitUntil(input -> isLoadingIndicatorVisible());
    }

    protected void waitUntilLoadingIndicatorNotVisible() {
        waitUntil(input -> !isLoadingIndicatorVisible());
    }

    /**
     * Selects a menu item. By default, this will click on the menu item.
     *
     * @param menuCaption
     *            caption of the menu item
     */
    protected void selectMenu(String menuCaption) {
        selectMenu(menuCaption, true);
    }

    /**
     * Selects a menu item.
     *
     * @param menuCaption
     *            caption of the menu item
     * @param click
     *            <code>true</code> if should click the menu item;
     *            <code>false</code> if not
     */
    protected void selectMenu(String menuCaption, boolean click) {
        WebElement menuElement = getMenuElement(menuCaption);
        new Actions(getDriver()).moveToElement(menuElement).perform();
        if (click) {
            new Actions(getDriver()).click().perform();
        }
    }

    /**
     * Finds the menu item from the DOM based on menu item caption.
     *
     * @param menuCaption
     *            caption of the menu item
     * @return the found menu item
     * @throws NoSuchElementException
     *             if menu item is not found
     */
    protected WebElement getMenuElement(String menuCaption)
            throws NoSuchElementException {
        // Need the parent span to obtain the correct size
        return getDriver().findElement(
                By.xpath("//span[text() = '" + menuCaption + "']/.."));
    }

    /**
     * Selects a submenu described by a path of menus from the first MenuBar in
     * the UI.
     *
     * @param menuCaptions
     *            array of menu captions
     */
    protected void selectMenuPath(String... menuCaptions) {
        selectMenu(menuCaptions[0], true);

        // Make sure menu popup is opened.
        waitUntil(e -> isElementPresent(By.className("gwt-MenuBarPopup"))
                || isElementPresent(By.className("v-menubar-popup")));

        // Move to the menu item opened below the menu bar.
        new Actions(getDriver())
                .moveByOffset(0,
                        getMenuElement(menuCaptions[0]).getSize().getHeight())
                .perform();

        for (int i = 1; i < menuCaptions.length - 1; i++) {
            selectMenu(menuCaptions[i]);
            new Actions(getDriver()).moveByOffset(
                    getMenuElement(menuCaptions[i]).getSize().getWidth(), 0)
                    .build().perform();
        }
        selectMenu(menuCaptions[menuCaptions.length - 1], true);
    }

    /**
     * Asserts that an element is present
     *
     * @param by
     *            the locator for the element
     */
    protected void assertElementPresent(By by) {
        assertTrue("Element is not present", isElementPresent(by));
    }

    /**
     * Asserts that an element is not present
     *
     * @param by
     *            the locator for the element
     */
    protected void assertElementNotPresent(By by) {
        assertFalse("Element is present", isElementPresent(by));
    }

    /**
     * Asserts that no error notifications are shown. Requires the use of
     * "?debug" as exceptions are otherwise not shown as notifications.
     */
    protected void assertNoErrorNotifications() {
        assertFalse("Error notification with client side exception is shown",
                isNotificationPresent("error"));
    }

    /**
     * Asserts that no system notifications are shown.
     */
    protected void assertNoSystemNotifications() {
        assertFalse("Error notification with system error exception is shown",
                isNotificationPresent("system"));
    }

    /**
     * Asserts that a system notification is shown.
     */
    protected void assertSystemNotification() {
        assertTrue(
                "Error notification with system error exception is not shown",
                isNotificationPresent("system"));
    }

    private boolean isNotificationPresent(String type) {
        if ("error".equals(type)) {
            assertTrue(
                    "Debug window must be open to be able to see error notifications",
                    isDebugWindowOpen());
        }
        return isElementPresent(By.className("v-Notification-" + type));
    }

    private boolean isDebugWindowOpen() {
        return isElementPresent(By.className("v-debugwindow"));
    }

    protected void assertNoHorizontalScrollbar(WebElement element,
            String errorMessage) {
        assertHasHorizontalScrollbar(element, errorMessage, false);
    }

    protected void assertHorizontalScrollbar(WebElement element,
            String errorMessage) {
        assertHasHorizontalScrollbar(element, errorMessage, true);
    }

    private void assertHasHorizontalScrollbar(WebElement element,
            String errorMessage, boolean expected) {
        // IE rounds clientWidth/clientHeight down and scrollHeight/scrollWidth
        // up, so using clientWidth/clientHeight will fail if the element height
        // is not an integer
        int clientWidth = getClientWidth(element);
        int scrollWidth = getScrollWidth(element);
        boolean hasScrollbar = scrollWidth > clientWidth;
        String message = "The element should";
        if (!expected) {
            message += " not";
        }
        message += " have a horizontal scrollbar (scrollWidth: " + scrollWidth
                + ", clientWidth: " + clientWidth + "): " + errorMessage;
        assertEquals(message, expected, hasScrollbar);
    }

    protected void assertNoVerticalScrollbar(WebElement element,
            String errorMessage) {
        // IE rounds clientWidth/clientHeight down and scrollHeight/scrollWidth
        // up, so using clientWidth/clientHeight will fail if the element height
        // is not an integer
        int clientHeight = getClientHeight(element);
        int scrollHeight = getScrollHeight(element);
        boolean hasScrollbar = scrollHeight > clientHeight;

        assertFalse(
                "The element should not have a vertical scrollbar (scrollHeight: "
                        + scrollHeight + ", clientHeight: " + clientHeight
                        + "): " + errorMessage,
                hasScrollbar);
    }

    protected int getScrollHeight(WebElement element) {
        return ((Number) executeScript("return arguments[0].scrollHeight;",
                element)).intValue();
    }

    protected int getScrollWidth(WebElement element) {
        return ((Number) executeScript("return arguments[0].scrollWidth;",
                element)).intValue();
    }

    protected int getScrollTop(WebElement element) {
        return ((Number) executeScript("return arguments[0].scrollTop;",
                element)).intValue();
    }

    /**
     * Gets the X offset for
     * {@link Actions#moveToElement(WebElement, int, int)}. This method takes
     * into account the W3C specification in browsers that properly implement
     * it.
     *
     * @param element
     *            the element
     * @param targetX
     *            the X coordinate where the move is wanted to go to
     * @return the correct X offset
     */
    protected int getXOffset(WebElement element, int targetX) {
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // Firefox follow W3C spec and moveToElement is relative to center
            final int width = element.getSize().getWidth();
            return targetX - ((width + width % 2) / 2);
        }
        return targetX;
    }

    /**
     * Gets the Y offset for
     * {@link Actions#moveToElement(WebElement, int, int)}. This method takes
     * into account the W3C specification in browsers that properly implement
     * it.
     *
     * @param element
     *            the element
     * @param targetY
     *            the Y coordinate where the move is wanted to go to
     * @return the correct Y offset
     */
    protected int getYOffset(WebElement element, int targetY) {
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // Firefox follow W3C spec and moveToElement is relative to center
            final int height = element.getSize().getHeight();
            return targetY - ((height + height % 2) / 2);
        }
        return targetY;
    }

    /**
     * Returns client height rounded up instead of as double because of IE9
     * issues: https://dev.vaadin.com/ticket/18469
     */
    protected int getClientHeight(WebElement e) {
        String script = "var cs = window.getComputedStyle(arguments[0]);"
                + "return Math.ceil(parseFloat(cs.height)+parseFloat(cs.paddingTop)+parseFloat(cs.paddingBottom));";
        return ((Number) executeScript(script, e)).intValue();
    }

    /**
     * Returns client width rounded up instead of as double because of IE9
     * issues: https://dev.vaadin.com/ticket/18469
     */
    protected int getClientWidth(WebElement e) {
        String script = "var cs = window.getComputedStyle(arguments[0]);"
                + "var h = parseFloat(cs.width)+parseFloat(cs.paddingLeft)+parseFloat(cs.paddingRight);"
                + "return Math.ceil(h);";

        return ((Number) executeScript(script, e)).intValue();
    }

    protected TimeZone getBrowserTimeZone() {
        Assume.assumeFalse(
                "Internet Explorer 11 does not support resolvedOptions timeZone",
                BrowserUtil.isIE(getDesiredCapabilities(), 11));

        // Ask TimeZone from browser
        String browserTimeZone = ((JavascriptExecutor) getDriver())
                .executeScript(
                        "return Intl.DateTimeFormat().resolvedOptions().timeZone;")
                .toString();
        return TimeZone.getTimeZone(browserTimeZone);
    }

    protected void assertElementsEquals(WebElement expectedElement,
            WebElement actualElement) {
        while (expectedElement instanceof WrapsElement) {
            expectedElement = ((WrapsElement) expectedElement)
                    .getWrappedElement();
        }
        while (actualElement instanceof WrapsElement) {
            actualElement = ((WrapsElement) actualElement).getWrappedElement();
        }

        assertEquals(expectedElement, actualElement);
    }

    protected WebElement getActiveElement() {
        return (WebElement) executeScript("return document.activeElement;");

    }

    protected void waitForThemeToChange(final String theme) {

        final WebElement rootDiv = findElement(
                By.xpath("//div[contains(@class,'v-app')]"));
        waitUntil(input -> {
            String rootClass = rootDiv.getAttribute("class").trim();

            return rootClass.contains(theme);
        }, 30);
    }

}
