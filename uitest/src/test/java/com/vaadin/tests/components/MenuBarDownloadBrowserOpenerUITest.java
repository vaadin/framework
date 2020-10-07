package com.vaadin.tests.components;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarDownloadBrowserOpenerUITest extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // alerts do not work properly on PhantomJS
        return getBrowserCapabilities(Browser.CHROME);
    }

    @Test
    public void testTriggerExtension() {
        openTestURL();
        MenuBarElement menu = $(MenuBarElement.class).first();
        clickItem(menu, "TestExtension", "RunMe");
        checkAndCloseAlert();

        clickItem(menu, "TestExtension", "AddTrigger");
        clickItem(menu, "TestExtension", "RunMe");
        checkAndCloseAlert();
        checkAndCloseAlert();

        sleep(500);
        clickItem(menu, "TestExtension", "RemoveTrigger");
        clickItem(menu, "TestExtension", "RunMe");
        checkAndCloseAlert();
    }

    private void clickItem(MenuBarElement menu, String... captions) {
        // click each given menu item in turn
        for (String caption : captions) {
            // wait for the menu item to become available
            waitUntil(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver arg0) {
                    List<WebElement> captionElements = findElements(
                            By.className("v-menubar-menuitem-caption"));
                    for (WebElement captionElement : captionElements) {
                        try {
                            if (captionElement.getText().equals(caption)) {
                                return true;
                            }
                        } catch (WebDriverException e) {
                            // stale, detached element is not visible
                        }
                    }
                    return false;
                }

                @Override
                public String toString() {
                    // Expected condition failed: waiting for ...
                    return caption + " to be available";
                }
            });
            // menu item was found, click it
            menu.clickItem(caption);
        }
    }

    private void checkAndCloseAlert() {
        Alert alert = getDriver().switchTo().alert();
        Assert.assertEquals("Trigger", alert.getText());
        alert.dismiss();
    }

}
