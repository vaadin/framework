package com.vaadin.tests.components.orderedlayout;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OrderedLayoutInfiniteLayoutPassesTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> b = super.getBrowsersToTest();
        // Chrome and PhantomJS do not support browser zoom changes
        b.remove(Browser.CHROME.getDesiredCapabilities());
        b.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return b;
    }

    @Test
    public void ensureFiniteLayoutPhase() throws Exception {
        openTestURL("debug");
        zoomBrowserIn();
        try {
            $(ButtonElement.class).first().click();
            assertNoErrorNotifications();
            resetZoom();
            assertNoErrorNotifications();
        } finally {
            // Reopen test to ensure that modal window does not prevent zoom
            // reset from taking place
            openTestURL();
            resetZoom();
        }
    }

    private void zoomBrowserIn() {
        WebElement html = driver.findElement(By.tagName("html"));
        html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
    }

    private void resetZoom() {
        WebElement html = driver.findElement(By.tagName("html"));
        html.sendKeys(Keys.chord(Keys.CONTROL, "0"));
    }
}
