package com.vaadin.tests.application;

import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ResynchronizeUITest extends SingleBrowserTest {

    @Test
    public void ensureResynchronizeRecreatesDOM() {
        Assume.assumeFalse("PhantomJS does not send onload events for styles",
                BrowserUtil.isPhantomJS(getDesiredCapabilities()));

        openTestURL();
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        // Click causes repaint, after this the old button element should no
        // longer be available
        // Ensure that the theme has changed
        waitForThemeToChange("runo");
        try {
            button.click();
            fail("The old button element should have been removed by the click and replaced by a new one.");
        } catch (StaleElementReferenceException e) {
            // This is what should happen
        }
    }
}
