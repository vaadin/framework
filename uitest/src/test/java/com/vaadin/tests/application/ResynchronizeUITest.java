package com.vaadin.tests.application;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ResynchronizeUITest extends SingleBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // PhantomJS does not send onload events for styles
        return Collections
                .singletonList(Browser.FIREFOX.getDesiredCapabilities());
    }

    @Test
    public void ensureResynchronizeRecreatesDOM() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        // Click causes repaint, after this the old button element should no
        // longer be available
        // Ensure that the theme has changed
        waitForThemeToChange("runo");
        try {
            button.click();
            Assert.fail(
                    "The old button element should have been removed by the click and replaced by a new one.");
        } catch (StaleElementReferenceException e) {
            // This is what should happen
        }
    }
}
