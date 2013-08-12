package com.vaadin.tests.tb3;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.server.LegacyApplication;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.ui.UI;

public abstract class AbstractTB3Test extends TestBenchTestCase {
    private String testName = getClass().getSimpleName();

    private DesiredCapabilities desiredCapabilities;
    {
        // Default browser to run on unless setDesiredCapabilities is called
        desiredCapabilities = firefox(17);
    }

    @Before
    public void setup() throws MalformedURLException {
        driver = TestBench.createDriver(new RemoteWebDriver(
                new URL(getHubURL()), getDesiredCapabilities()));
        try {
            testBench().resizeViewPortTo(1500, 850);
        } catch (UnsupportedOperationException e) {
            // Opera does not support this...
        }
        String baseUrl = getBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        driver.get(baseUrl + getPath());
    }

    protected String getHubURL() {
        return "http://" + getHubHostname() + ":4444/wd/hub";
    }

    protected abstract String getHubHostname();

    protected abstract String getDeploymentHostname();

    protected DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }
        driver = null;
    }

    /**
     * @since
     * @param string
     * @return
     */
    protected WebElement vaadinElement(String vaadinLocator) {
        String base = getLocatorBase(getPath());

        base += "::";

        return driver.findElement(By.vaadin(base + vaadinLocator));
    }

    /**
     * @since
     * @return
     */
    protected String getPath() {
        Class<?> uiClass = getUIClass();
        if (uiClass != null) {
            return getPath(uiClass);
        }
        throw new IllegalArgumentException("Unable to determine path for "
                + getClass().getCanonicalName());

    }

    /**
     * @since
     * @return
     */
    protected Class<?> getUIClass() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            // For compatibility in screenshot naming
            setTestName(enclosingClass.getSimpleName());
            return enclosingClass;
        }
        return null;
    }

    protected boolean isDebug() {
        return false;
    }

    protected boolean isPushEnabled() {
        return false;
    }

    /**
     * @since
     * @param enclosingClass
     * @return
     */
    private String getPath(Class<?> uiClass) {
        String runPath = "/run";
        if (isPushEnabled()) {
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

    protected String getBaseURL() {
        return "http://" + getDeploymentHostname() + ":8888";
    }

    /**
     * @since
     * @param path
     * @return
     */
    private String getLocatorBase(String path) {
        String base = path.replaceAll("\\?.*", "");
        if ("".equals(base)) {
            return "ROOT";
        }

        int sep = base.lastIndexOf('/');
        String pre = base.substring(0, sep + 1);
        String post = base.substring(sep + 1);
        // pre = pre.replace("/", "").replace(".", "").toLowerCase();
        // post = post.replace("/", "").replace(".", "");
        pre = pre.replaceAll("[^a-zA-Z0-9]", "");
        post = post.replaceAll("[^a-zA-Z0-9]", "");

        return pre + post;
    }

    protected void setTestName(String testName) {
        this.testName = testName;
    }

    protected String getTestName() {
        return testName;
    }

    protected void sleep(int timeoutMillis) {
        try {
            Thread.sleep(timeoutMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected static DesiredCapabilities safari(int version) {
        DesiredCapabilities c = DesiredCapabilities.safari();
        c.setVersion("" + version);
        return c;
    }

    protected static DesiredCapabilities chrome(int version) {
        DesiredCapabilities c = DesiredCapabilities.chrome();
        c.setVersion("" + version);
        c.setPlatform(Platform.XP);
        return c;
    }

    protected static DesiredCapabilities opera(int version) {
        DesiredCapabilities c = DesiredCapabilities.opera();
        c.setVersion("" + version);
        c.setPlatform(Platform.XP);
        return c;
    }

    protected static DesiredCapabilities firefox(int version) {
        DesiredCapabilities c = DesiredCapabilities.firefox();
        c.setVersion("" + version);
        c.setPlatform(Platform.XP);
        return c;
    }

    protected static DesiredCapabilities ie(int version) {
        DesiredCapabilities c = DesiredCapabilities.internetExplorer();
        c.setVersion("" + version);
        return c;
    }

    public void onUncaughtException(Throwable t) {
        // Do nothing by default

    }
}
