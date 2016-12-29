package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PushStateAndReplaceStateTest extends MultiBrowserTest {
	
    @Test
    @Ignore("Don't know how to work with test setup and instructions are not clear...")
    public void testUriFragment() throws Exception {
    	driver.get(getTestUrl());
    	assertUri(getTestUrl());
        navigateToTest();
        
        URI base = new URI(getTestUrl());
        
        URI current = base.resolve("/test");
        assertUri(current.toString());
        
        ((JavascriptExecutor) driver).executeScript("history.back()");

    	assertUri(getTestUrl());
    	
    	// TODO automatic test for replaceState as well
        
    }

    private void assertUri(String uri) {
        final String expectedText = "Current URI : " + uri;
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return expectedText.equals(getLocationLabelValue());
            }
        });
        
        assertEquals(uri, driver.getCurrentUrl());
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

    private String getLocationLabelValue() {
        return vaadinElementById("locationLabel").getText();
    }

}
