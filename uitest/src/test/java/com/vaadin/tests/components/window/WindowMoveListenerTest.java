package com.vaadin.tests.components.window;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowMoveListenerTest extends MultiBrowserTest {

    @Test
    public void testWindowRepositioning() throws Exception {
        openTestURL();

        final WebElement window = getDriver().findElement(By.id("testwindow"));
        WebElement button = getDriver().findElement(By.id("testbutton"));

        // I'd loved to use the header, but that doesn't work. Footer works
        // fine, though :)
        WebElement windowFooter = getDriver()
                .findElement(By.className("v-window-footer"));

        final Point winPos = window.getLocation();

        // move window
        Action a = new Actions(driver).clickAndHold(windowFooter)
                .moveByOffset(100, 100).release().build();
        a.perform();
        assertNotEquals("Window was not dragged correctly.", winPos.x,
                window.getLocation().x);
        assertNotEquals("Window was not dragged correctly.", winPos.y,
                window.getLocation().y);

        // re-set window
        button.click();

        waitUntilWindowHasReseted(window, winPos);
    }

    private void waitUntilWindowHasReseted(final WebElement window,
            final Point winPos) {
        waitUntil(input -> winPos.x == window.getLocation().x
                && winPos.y == window.getLocation().y, 5);
    }
}
