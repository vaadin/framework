package com.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.Browser;

/**
 * @author Vaadin Ltd
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VaadinSpringBootSmokeIT extends TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotRule = new ScreenshotOnFailureRule(
            this, true);

    @LocalServerPort
    Integer port;

    @Before
    public void setUp() {
        setDriver(TestBench.createDriver(new PhantomJSDriver(
                Browser.PHANTOMJS.getDesiredCapabilities())));
    }

    @Test
    public void testPageLoadsAndButtonWorks() {
        getDriver().navigate().to("http://localhost:" + port + "");
        $(ButtonElement.class).first().click();
        Assert.assertTrue($(NotificationElement.class).exists());
        Assert.assertEquals(ThankYouService.THANK_YOU_TEXT,
                $(NotificationElement.class).first().getText());
    }
}
