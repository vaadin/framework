package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UriFragmentTest extends MultiBrowserTest {

    @Test
    public void testUriFragment() throws Exception {
        driver.get(getTestUrl() + "#urifragment");
        assertFragment("urifragment");
        navigateToTest();
        assertFragment("test");
        ((JavascriptExecutor) driver).executeScript("history.back()");

        assertFragment("urifragment");
        ((JavascriptExecutor) driver).executeScript("history.forward()");
        assertFragment("test");

        // Open other URL in between to ensure the page is loaded again
        // (testbench doesn't like opening a URI that only changes the fragment)
        driver.get(getBaseURL() + "/statictestfiles/");
        driver.get(getTestUrl());

        // Empty initial fragment
        assertEquals("No URI fragment set", getFragmentLabelValue());

        navigateToNull();
        // Still no # after setting to null
        assertEquals("No URI fragment set", getFragmentLabelValue());
        navigateToEmptyFragment();
        // Empty # is added when setting to ""
        assertEquals("Current URI fragment:", getFragmentLabelValue());
        navigateToTest();
        assertFragment("test");
        navigateToNull(); // Setting to null when there is a fragment actually
                          // sets it to #
        assertEquals("Current URI fragment:", getFragmentLabelValue());

        // ensure IE works with new popstate based implementation, see
        // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/3740423/
        driver.findElement(By.xpath("//*[@id = 'link']/a")).click();
        assertFragment("linktest");

    }

    private void assertFragment(String fragment) {
        final String expectedText = "Current URI fragment: " + fragment;
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return expectedText.equals(getFragmentLabelValue());
            }
        });

    }

    private void navigateToEmptyFragment() {
        hitButton("empty");
    }

    private void navigateToNull() {
        hitButton("null");
    }

    private void navigateToTest() {
        hitButton("test");
    }

    private String getFragmentLabelValue() {
        return vaadinElementById("fragmentLabel").getText();
    }

}
