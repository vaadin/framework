package com.vaadin.tests.components;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class FocusShortcutsTest extends SingleBrowserTest {

    @Test
    public void triggerShortCuts() {
        openTestURL();

        WebElement body = findElement(By.xpath("//body"));
        Actions actions = new Actions(getDriver());
        actions.keyDown(body, Keys.LEFT_ALT).sendKeys("a").keyUp(Keys.LEFT_ALT)
                .build().perform();

        Assert.assertEquals("1. Alt+A", getLogRow(0));

        body.click();

        actions = new Actions(getDriver());
        actions.keyDown(body, Keys.LEFT_ALT).sendKeys("n").keyUp(Keys.LEFT_ALT)
                .build().perform();

        Assert.assertEquals("2. Alt+N", getLogRow(0));

        body.click();

        actions = new Actions(getDriver());
        actions.keyDown(body, Keys.LEFT_CONTROL).keyDown(body, Keys.LEFT_SHIFT)
                .sendKeys("d").keyUp(Keys.LEFT_ALT).build().perform();

        Assert.assertEquals("3. Ctrl+Shift+D", getLogRow(0));
    }

}
