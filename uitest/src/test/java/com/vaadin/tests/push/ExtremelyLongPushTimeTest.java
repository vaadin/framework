package com.vaadin.tests.push;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.tests.tb3.ExcludeFromSuite;
import com.vaadin.tests.tb3.MultiBrowserTest;

@ExcludeFromSuite
public abstract class ExtremelyLongPushTimeTest extends MultiBrowserTest {

    private static final int ONE_HOUR_IN_MS = 20 * 1000;

    @Test
    public void test24HourPush() throws Exception {
        openTestURL();

        // Without this there is a large chance that we will wait for all pushes
        // to complete before moving on
        testBench(driver).disableWaitForVaadin();

        // Wait for startButton to be present
        waitForElementVisible(vaadinLocatorById("startButton"));

        String logRow0Id = "Log_row_0";
        By logRow0 = vaadinLocatorById(logRow0Id);

        // Start the test
        vaadinElementById("startButton").click();

        // Wait for push to start. Should take 60s
        waitUntil(ExpectedConditions.textToBePresentInElement(logRow0,
                "Package "), 120);

        // Check every hour that push is still going on
        for (int i = 0; i < 24; i++) {
            sleep(ONE_HOUR_IN_MS);
            ensureStillPushing(logRow0);
        }

    }

    private void ensureStillPushing(By logRow0) {
        String logValue = getDriver().findElement(logRow0).getText();
        // Wait for the log value to change. Should take max 60s
        waitUntilNot(
                ExpectedConditions.textToBePresentInElement(logRow0, logValue),
                120);
    }

}
