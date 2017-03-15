package com.vaadin.tests.integration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.runner.RunWith;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelRunner;
import com.vaadin.testbench.parallel.ParallelTest;
import com.vaadin.testbench.parallel.TestNameSuffix;
import com.vaadin.testbench.screenshot.ImageFileUtil;

@RunOnHub("tb3-hub.intra.itmill.com")
@RunWith(ParallelRunner.class)
@BrowserFactory(CustomBrowserFactory.class)
@TestNameSuffix(property = "server-name")
public abstract class AbstractIntegrationTest extends ParallelTest {

    /**
     * Height of the screenshots we want to capture
     */
    private static final int SCREENSHOT_HEIGHT = 850;

    /**
     * Width of the screenshots we want to capture
     */
    private static final int SCREENSHOT_WIDTH = 1500;

    private boolean screenshotErrors;

    @BrowserConfiguration
    public final List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsers().map(BrowserUtil.getBrowserFactory()::create)
                .collect(Collectors.toList());
    }

    protected Stream<Browser> getBrowsers() {
        return Stream.of(Browser.PHANTOMJS);
    }

    @Override
    public void setup() throws Exception {
        super.setup();

        testBench().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);

        openTestURL();
    }

    private void openTestURL() {
        String url = getDeploymentURL() + getContextPath() + getTestPath() + "?"
                + getParameters().collect(Collectors.joining("&"));
        driver.get(url);

        if (!isElementPresent(UIElement.class)) {
            waitUntil(e -> isElementPresent(UIElement.class), 10);
        }
    }

    protected Stream<String> getParameters() {
        return Stream.of("restartApplication");
    }

    /**
     * Returns a path where the test UI is found.
     *
     * @return path for test
     */
    protected abstract String getTestPath();

    private String getDeploymentURL() {
        String deploymentUrl = System.getProperty("deployment.url");
        if (deploymentUrl == null || deploymentUrl.equals("")) {
            throw new RuntimeException(
                    "Deployment url must be given as deployment.url");
        }
        return deploymentUrl;
    }

    protected void compareScreen(String identifier) throws IOException {
        String refFileName = identifier + "-"
                + getDesiredCapabilities().getBrowserName().toLowerCase()
                + ".png";
        String errorFileName = identifier + "-"
                + getDesiredCapabilities().getBrowserName().toLowerCase() + "-"
                + System.getProperty("server-name") + ".png";
        File referenceFile = ImageFileUtil
                .getReferenceScreenshotFile(refFileName);
        try {
            BufferedImage reference = ImageIO.read(referenceFile);
            if (testBench().compareScreen(reference, errorFileName)) {
                return;
            }
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).warning(
                    "Missing screenshot reference: " + referenceFile.getPath());
        }
        screenshotErrors = true;
    }

    @After
    public void teardown() {
        if (screenshotErrors) {
            throw new RuntimeException("Screenshots failed.");
        }
    }

    /**
     * Waits the given number of seconds for the given condition to become true.
     * Use e.g. as
     * {@link #waitUntil(ExpectedConditions.textToBePresentInElement(by, text))}
     *
     * @param condition
     *            the condition to wait for to become true
     */
    protected <T> void waitUntil(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        new WebDriverWait(driver, timeoutInSeconds).until(condition);
    }

    protected String getContextPath() {
        return "/demo";
    }
}
