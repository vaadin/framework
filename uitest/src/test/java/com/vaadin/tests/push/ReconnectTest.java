package com.vaadin.tests.push;

import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;

import java.io.IOException;

public abstract class ReconnectTest extends MultiBrowserTestWithProxy {

    @Override
    public void setup() throws Exception {
        super.setup();

        setDebug(true);
        openTestURL();
        openDebugLogTab();

        startTimer();
        waitUntilServerCounterChanges();

        testBench().disableWaitForVaadin();
    }

    @Test
    public void messageIsQueuedOnDisconnect() throws IOException {
        disconnectProxy();

        clickButtonAndWaitForTwoReconnectAttempts();

        connectAndVerifyConnectionEstablished();
        waitUntilClientCounterChanges(1);
    }

    @Test
    public void messageIsNotSentBeforeConnectionIsEstablished()
            throws IOException {
        disconnectProxy();

        waitForNextReconnectionAttempt();
        clickButtonAndWaitForTwoReconnectAttempts();

        connectAndVerifyConnectionEstablished();
        waitUntilClientCounterChanges(1);
    }

    private void clickButtonAndWaitForTwoReconnectAttempts() {
        clickClientButton();

        // Reconnection attempt is where pending messages can
        // falsely be sent to server.
        waitForNextReconnectionAttempt();

        // Waiting for the second reconnection attempt makes sure that the
        // first attempt has been completed or aborted.
        waitForNextReconnectionAttempt();
    }

    private void clickClientButton() {
        getIncrementClientCounterButton().click();
    }

    private void waitForNextReconnectionAttempt() {
        clearDebugMessages();
        waitForDebugMessage("Reopening push connection");
    }

    private void connectAndVerifyConnectionEstablished() throws IOException {
        connectProxy();
        waitUntilServerCounterChanges();
    }

    private WebElement getIncrementClientCounterButton() {
        return BasicPushTest.getIncrementButton(this);
    }

    private void waitUntilServerCounterChanges() {
        final int counter = BasicPushTest.getServerCounter(this);
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                try {
                    return BasicPushTest
                            .getServerCounter(ReconnectTest.this) > counter;
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        }, 30);
    }

    private void waitUntilClientCounterChanges(final int expectedValue) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                try {
                    return BasicPushTest
                            .getClientCounter(ReconnectTest.this) == expectedValue;
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        }, 5);
    }

    private void startTimer() {
        BasicPushTest.getServerCounterStartButton(this).click();
    }

}
