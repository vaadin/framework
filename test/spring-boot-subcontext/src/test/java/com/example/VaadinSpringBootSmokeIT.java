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

import com.example.ui.SubPathUI;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.parallel.Browser;

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
        getDriver().navigate()
                .to("http://localhost:" + port + DemoApplication.CONTEXT);
        runSmokeTest();
    }

    @Test
    public void testPageLoadsAndButtonWorksWithExtraSlash() {
        getDriver().navigate()
                .to("http://localhost:" + port + "/" + DemoApplication.CONTEXT);
        runSmokeTest();
    }

    @Test
    public void testSubPathPageLoadsAndButtonWorks() {
        getDriver().navigate().to("http://localhost:" + port
                + DemoApplication.CONTEXT + "/" + SubPathUI.SUBPATH);
        runSmokeTest();
    }

    @Test
    public void testSubPathPageLoadsAndButtonWorksWithExtraSlash() {
        getDriver().navigate().to("http://localhost:" + port + "/"
                + DemoApplication.CONTEXT + "/" + SubPathUI.SUBPATH);
        runSmokeTest();
    }

    private void runSmokeTest() {
        $(ButtonElement.class).in($(PanelElement.class)).first().click();

        Assert.assertTrue($(NotificationElement.class).exists());
        Assert.assertEquals(ThankYouService.THANK_YOU_TEXT,
                $(NotificationElement.class).first().getText());
    }
}
