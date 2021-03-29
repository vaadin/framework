package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIAccessTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        setPush(false);
        openTestURL();
    }

    @Test
    public void testThreadLocals() {
        setPush(true);
        openTestURL();

        $(ButtonElement.class).get(7).click();

        waitForLogToContainText(
                "0. Current UI matches in beforeResponse? true");
        waitForLogToContainText(
                "1. Current session matches in beforeResponse? true");
    }

    @Test
    public void canBeAccessedFromUIThread() {
        $(ButtonElement.class).first().click();

        assertTrue(logContainsText(
                "0. Access from UI thread future is done? false"));
        assertTrue(logContainsText("1. Access from UI thread is run"));
        assertTrue(logContainsText(
                "2. beforeClientResponse future is done? true"));
    }

    @Test
    public void canBeAccessedFromBackgroundThread() {
        $(ButtonElement.class).get(1).click();

        assertTrue(logContainsText("0. Initial background message"));
        assertTrue(logContainsText("1. Thread has current response? false"));

        waitForLogToContainText("2. Thread got lock, inital future done? true");
    }

    private void waitForLogToContainText(final String text) {
        waitUntil(input -> logContainsText(text));
    }

    @Test
    public void exceptionCanBeThrown() {
        $(ButtonElement.class).get(2).click();

        assertTrue(logContainsText("0. Throwing exception in access"));
        assertTrue(logContainsText(
                "1. Exception occurred, java.lang.RuntimeException: Catch me if you can"));
        assertTrue(logContainsText("2. firstFuture is done? true"));
        assertTrue(logContainsText(
                "3. Got exception from firstFuture: java.lang.RuntimeException: Catch me if you can"));
    }

    @Test
    public void futureIsCancelledBeforeStarted() {
        $(ButtonElement.class).get(3).click();

        assertTrue(
                logContainsText("0. future was cancelled, should not start"));
    }

    @Test
    public void runningThreadIsCancelled() {
        $(ButtonElement.class).get(4).click();

        waitForLogToContainText("0. Waiting for thread to start");
        waitForLogToContainText("1. Thread started, waiting for interruption");
        waitForLogToContainText("2. I was interrupted");
    }

    @Test
    public void testAccessSynchronously() {
        $(ButtonElement.class).get(5).click();

        assertTrue(logContainsText("0. accessSynchronously has request? true"));
        assertTrue(logContainsText(
                "1. Test value in accessSynchronously: Set before accessSynchronosly"));
        assertTrue(logContainsText(
                "2. has request after accessSynchronously? true"));
        assertTrue(logContainsText(
                "3. Test value after accessSynchornously: Set in accessSynchronosly"));
    }

}
