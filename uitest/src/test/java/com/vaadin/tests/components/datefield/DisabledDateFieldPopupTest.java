package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledDateFieldPopupTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getIEBrowsersOnly();
    }

    @Test
    public void testPopup() throws IOException {
        openTestURL();

        WebElement button = driver
                .findElement(By.className("v-datefield-button"));
        new Actions(driver).moveToElement(button).click()
                .sendKeys(Keys.ARROW_DOWN).perform();

        assertFalse(
                "Calendar popup should not be opened for disabled date field",
                isElementPresent(By.className("v-datefield-popup")));
    }
}
