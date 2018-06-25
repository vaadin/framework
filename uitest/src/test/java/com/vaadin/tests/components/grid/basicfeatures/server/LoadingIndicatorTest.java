package com.vaadin.tests.components.grid.basicfeatures.server;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class LoadingIndicatorTest extends GridBasicFeaturesTest {
    @Test
    public void testLoadingIndicator() throws InterruptedException {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "State", "Container delay", "2000");

        GridElement gridElement = $(GridElement.class).first();

        Assert.assertFalse(
                "Loading indicator should not be visible before disabling waitForVaadin",
                isLoadingIndicatorVisible());

        testBench().disableWaitForVaadin();

        // Scroll to a completely new location
        gridElement.getCell(200, 1);

        // Wait for loading indicator delay
        waitUntil(ExpectedConditions.visibilityOfElementLocated(
                By.className("v-loading-indicator")));

        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(
                By.className("v-loading-indicator")));

        // Scroll so much that more data gets fetched, but not so much that
        // missing rows are shown
        gridElement.getCell(230, 1);

        // Wait for potentially triggered loading indicator to become visible
        Thread.sleep(500);

        Assert.assertFalse(
                "Loading indicator should not be visible when fetching rows that are not visible",
                isLoadingIndicatorVisible());

        // Finally verify that there was actually a request going on
        waitUntilLogContains("Requested items");
    }

    private void waitUntilLogContains(final String value) {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return getLogRow(0).contains(value);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "first log row to contain '" + value + "' (was: '"
                        + getLogRow(0) + "')";
            }
        });
    }

}
