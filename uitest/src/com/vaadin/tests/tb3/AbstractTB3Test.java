package com.vaadin.tests.tb3;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.server.LegacyApplication;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.ui.UI;

public abstract class AbstractTB3Test extends TestBenchTestCase {
    protected WebDriver driver;
    private DesiredCapabilities desiredCapabilities;
    {
        // Default browser to run on unless setDesiredCapabilities is called
        desiredCapabilities = DesiredCapabilities.firefox();
        desiredCapabilities.setVersion("17");
        desiredCapabilities.setPlatform(Platform.WIN8);
    }

    @Before
    public void setup() throws MalformedURLException {
        driver = TestBench.createDriver(new RemoteWebDriver(
                new URL(getHubURL()), getDesiredCapabilities()));
        try {
            testBench(driver).resizeViewPortTo(1500, 850);
        } catch (UnsupportedOperationException e) {
            // Opera does not support this...
        }
        driver.get(getBaseURL() + getPath());
    }

    protected abstract String getHubURL();

    protected DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     * @since
     * @param string
     * @return
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
        Class<?> enclosingClass = getClass().getEnclosingClass();
        if (enclosingClass != null) {
            return getPath(enclosingClass);
        }
        throw new IllegalArgumentException("Unable to determine path for "
                + getClass().getCanonicalName());

    }

    /**
     * @since
     * @param enclosingClass
     * @return
     */
    private String getPath(Class<?> enclosingClass) {
        if (UI.class.isAssignableFrom(enclosingClass)) {
            return "/run/" + enclosingClass.getCanonicalName();
        } else if (LegacyApplication.class.isAssignableFrom(enclosingClass)) {
            return "/run/" + enclosingClass.getCanonicalName()
                    + "?restartApplication";
        } else {
            throw new IllegalArgumentException(
                    "Unable to determin path for enclosing class "
                            + enclosingClass.getCanonicalName());
        }
    }

    protected String getBaseURL() {
        return System.getProperty("com.vaadin.testbench.deployment.url");
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

    protected String getTestName() {
        return getClass().getSimpleName();
    }

    protected void sleep(int timeoutMillis) throws InterruptedException {
        Thread.sleep(timeoutMillis);
    }
}
