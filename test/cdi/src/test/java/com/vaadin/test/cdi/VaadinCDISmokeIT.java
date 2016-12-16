package com.vaadin.test.cdi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;

public class VaadinCDISmokeIT extends TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule rule = new ScreenshotOnFailureRule(this,
            true);

    @Before
    public void setup() {
        // Screenshot rule tears down the driver
        setDriver(new PhantomJSDriver());
    }

    @Test
    public void testPageLoadsAndCanBeInterractedWith() {
        getDriver().navigate().to("http://localhost:8080/");

        $(ButtonElement.class).first().click();

        Assert.assertTrue($(NotificationElement.class).exists());
        Assert.assertEquals(ThankYouServiceImpl.THANK_YOU_TEXT,
                $(NotificationElement.class).first().getText());
    }
}
