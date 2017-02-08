package com.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VaadinSpringBootNavigatorIT extends TestBenchTestCase {

    private static final String VIEW_SEPARATOR = "#!";

    private String currentUIPath;

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
    public void testRootPathUINavigation() {
        currentUIPath = "http://localhost:" + port + "/"
                + DemoApplication.CONTEXT + "/";

        runNavigationTestPattern();
    }

    @Test
    public void testSubPathUINavigation() {
        currentUIPath = "http://localhost:" + port + "/"
                + DemoApplication.CONTEXT + "/" + SubPathUI.SUBPATH + "/";

        runNavigationTestPattern();
    }

    @Test
    public void testRootPathNotDefaultView() {
        currentUIPath = "http://localhost:" + port + "/"
                + DemoApplication.CONTEXT + "/";
        getDriver().navigate()
                .to(currentUIPath + VIEW_SEPARATOR + ViewScopedView.VIEW_NAME);

        verifyViewScopeViewOpen();
    }

    @Test
    public void testSubPathNotDefaultView() {
        currentUIPath = "http://localhost:" + port + "/"
                + DemoApplication.CONTEXT + "/" + SubPathUI.SUBPATH + "/";
        getDriver().navigate()
                .to(currentUIPath + VIEW_SEPARATOR + ViewScopedView.VIEW_NAME);

        verifyViewScopeViewOpen();
    }

    private void runNavigationTestPattern() {
        getDriver().navigate().to(currentUIPath);

        verifyDefaultViewOpen("");

        openView(UIScopedView.VIEW_NAME);
        verifyUIScopedViewOpen();

        openView(ViewScopedView.VIEW_NAME);
        verifyViewScopeViewOpen();

        openView(DefaultView.VIEW_NAME);
        verifyDefaultViewOpen(VIEW_SEPARATOR);

        getDriver().navigate().back();
        verifyViewScopeViewOpen();

        getDriver().navigate().back();
        verifyUIScopedViewOpen();

        getDriver().navigate().back();
        verifyDefaultViewOpen("");

        getDriver().navigate().forward();
        verifyUIScopedViewOpen();

        getDriver().navigate().forward();
        verifyViewScopeViewOpen();
    }

    private void verifyDefaultViewOpen(String viewIdentifier) {
        verifyViewOpen("default-view");
        verifyURL(currentUIPath + viewIdentifier);
    }

    private void verifyViewScopeViewOpen() {
        verifyViewOpen(ViewScopedView.VIEW_NAME);
        verifyURL(currentUIPath + VIEW_SEPARATOR + ViewScopedView.VIEW_NAME);
    }

    private void verifyUIScopedViewOpen() {
        verifyViewOpen(UIScopedView.VIEW_NAME);
        verifyURL(currentUIPath + VIEW_SEPARATOR + UIScopedView.VIEW_NAME);
    }

    private void verifyViewOpen(String viewName) {
        try {
            findElement(By.id(viewName));
        } catch (NoSuchElementException e) {
            Assert.fail(
                    "View <" + viewName + "> was not open, no element found");
        }
    }

    private void verifyURL(String url) {
        Assert.assertEquals("Invalid URL", url, getDriver().getCurrentUrl());
    }

    private void openView(String viewName) {
        $(ButtonElement.class).id(viewName + "-button").click();
    }

}
