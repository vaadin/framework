package com.vaadin.tests.push;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.tests.tb3.WebsocketTest;

public class PushLargeDataWebsocketTest extends WebsocketTest {

    @Test
    public void testWebsocketLargeData() throws Exception {
        openTestURL();

        // Without this timing will be completly off as pushing "start" can
        // remain waiting for all pushes to complete
        testBench(driver).disableWaitForVaadin();

        push();
        // Push complete. Browser will reconnect now as > 10MB has been sent
        // Push again to ensure push still works
        push();

    }

    private void push() throws Exception {
        // Wait for startButton to be present
        waitForElementVisible(vaadinLocatorById("startButton"));

        String logRow0Id = "Log_row_0";
        By logRow0 = vaadinLocatorById(logRow0Id);

        vaadinElementById("startButton").click();
        // Wait for push to start
        waitUntil(ExpectedConditions.textToBePresentInElement(logRow0,
                "Package"));

        // Wait for until push should be done
        sleep(PushLargeData.DEFAULT_DURATION_MS);

        // Wait until push is actually done
        waitUntil(ExpectedConditions.textToBePresentInElement(logRow0,
                "Push complete"));
    }
}
