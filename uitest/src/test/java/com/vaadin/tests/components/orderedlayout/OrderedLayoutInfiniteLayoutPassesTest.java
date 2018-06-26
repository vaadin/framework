package com.vaadin.tests.components.orderedlayout;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class OrderedLayoutInfiniteLayoutPassesTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
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
