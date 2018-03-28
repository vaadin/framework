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

import com.vaadin.tests.tb3.MultiBrowserTest;

public class JavaScriptPreloadingTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void scriptsShouldPreloadAndExecuteInCorrectOrder()
            throws InterruptedException {
        openTestURL();

        try {
            new WebDriverWait(driver, 10)
                    .until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            assertEquals("First", alert.getText());
            alert.accept();

            new WebDriverWait(driver, 10)
                    .until(ExpectedConditions.alertIsPresent());
            alert = driver.switchTo().alert();
            assertEquals("Second", alert.getText());
            alert.accept();

        } catch (TimeoutException te) {
            fail("@Javascript widget loading halted.");
        }

    }
}
