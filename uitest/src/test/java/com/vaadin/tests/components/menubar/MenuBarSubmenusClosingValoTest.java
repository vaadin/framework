package com.vaadin.tests.components.menubar;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;

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
        Assert.assertTrue("The count of open popups should be one", count == 1);
    }

    @Test
    public void testEnableParentLayoutControlByMouse() {
        openTestURL();

        Mouse mouse = ((HasInputDevices) getDriver()).getMouse();

        List<WebElement> menuItemList = driver
                .findElements(By.className("v-menubar-menuitem"));

        mouse.click(((Locatable) menuItemList.get(0)).getCoordinates());
        waitForElementPresent(By.className("v-menubar-popup"));

        mouse.mouseMove(((Locatable) menuItemList.get(1)).getCoordinates());
        mouse.mouseMove(((Locatable) menuItemList.get(2)).getCoordinates());

        waitForElementPresent(By.className("v-menubar-popup"));

        int count = driver.findElements(By.className("v-menubar-popup")).size();
        Assert.assertTrue("The count of open popups should be one", count == 1);
    }
}
