package com.vaadin.tests.components.window;

import java.awt.AWTException;
import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowShadowTest extends MultiBrowserTest {

    @Test
    public void dragBackgroundWindow()
            throws AWTException, IOException, InterruptedException {
        openTestURL();
        WebElement wnd = getDriver().findElement(By.id("topwindow"));
        // There is some bug in Selenium. Can't move window using header
        // need use footer instead.
        WebElement wnd1Footer = wnd
                .findElement(By.className("v-window-footer"));
        Point startLoc = wnd.getLocation();

        new Actions(getDriver()).moveToElement(wnd1Footer).clickAndHold()
                .moveByOffset(200, 200).release().perform();

        Point endLoc = wnd.getLocation();
        // don't compare to specific coordinate, because in IE9 and IE11
        // the window position is random.
        // So, checkt that the window was moved
        org.junit.Assert.assertNotEquals(startLoc, endLoc);
    }

}
