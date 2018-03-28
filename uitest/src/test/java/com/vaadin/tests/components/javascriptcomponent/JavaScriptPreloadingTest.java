package com.vaadin.tests.components.javascriptcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class JavaScriptPreloadingTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // the test works on Firefox under low load, but often fails under high
        // load - seems to be a Firefox bug
        return getBrowserCapabilities(Browser.IE11, Browser.CHROME);
    }

    @Test
    public void scriptsShouldPreloadAndExecuteInCorrectOrder()
            throws InterruptedException {
        openTestURL();

        try {
            waitUntil(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            assertEquals("First", alert.getText());
            alert.accept();

            waitUntil(ExpectedConditions.alertIsPresent());
            alert = driver.switchTo().alert();
            assertEquals("Second", alert.getText());
            alert.accept();

        } catch (TimeoutException te) {
            fail("@Javascript widget loading halted.");
        }

    }
}
