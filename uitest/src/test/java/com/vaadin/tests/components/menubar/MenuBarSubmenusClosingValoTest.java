package com.vaadin.tests.components.menubar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class MenuBarSubmenusClosingValoTest extends MultiBrowserTest {

    @Test
    public void testEnableParentLayoutControlByKeyboard() {
        openTestURL();

        MenuBarElement menu = $(MenuBarElement.class).get(0);
        menu.focus();
        menu.sendKeys(Keys.SPACE);
        menu.sendKeys(Keys.DOWN);

        waitForElementPresent(By.className("v-menubar-popup"));

        menu.sendKeys(Keys.ARROW_RIGHT);
        menu.sendKeys(Keys.ARROW_RIGHT);

        int count = driver.findElements(By.className("v-menubar-popup")).size();
        assertTrue("The count of open popups should be one", count == 1);
    }

    @Test
    public void testEnableParentLayoutControlByMouse()
            throws InterruptedException {
        openTestURL();

        List<WebElement> menuItemList = driver
                .findElements(By.className("v-menubar-menuitem"));

        new Actions(getDriver()).moveToElement(menuItemList.get(1)).click()
                .perform();
        waitForElementPresent(By.className("v-menubar-popup"));

        new Actions(getDriver()).moveToElement(menuItemList.get(1)).perform();
        new Actions(getDriver()).moveToElement(menuItemList.get(2)).perform();
        waitForElementPresent(By.className("v-menubar-popup"));

        int count = driver.findElements(By.className("v-menubar-popup")).size();
        assertEquals("The count of open popups should be one", 1, count);
    }
}
