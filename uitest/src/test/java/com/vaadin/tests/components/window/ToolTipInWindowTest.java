package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ToolTipInWindowTest extends MultiBrowserTest {

    @Test
    public void testToolTipInHeader() throws Exception {

        openTestURL();

        WebElement header = driver
                .findElement(By.className("v-window-outerheader"));
        new Actions(driver)
                .moveToElement(driver.findElement(By.className("v-ui")), 0, 0)
                .perform();
        sleep(500);
        new Actions(driver).moveToElement(header).perform();
        sleep(1100);

        WebElement ttip = findElement(By.className("v-tooltip"));
        assertNotNull(ttip);
        assertEquals("Tooltip", ttip.getText());

    }

    @Test
    public void testToolTipInContent() throws Exception {

        openTestURL();

        WebElement header = driver
                .findElement(By.className("v-window-contents"));
        new Actions(driver)
                .moveToElement(driver.findElement(By.className("v-ui")), 0, 300)
                .perform();
        sleep(500);
        new Actions(driver).moveToElement(header).perform();
        sleep(1000);

        WebElement ttip = findElement(By.className("v-tooltip"));
        assertNotNull(ttip);
        assertEquals("Tooltip", ttip.getText());

    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Test with the same browsers as in the other tooltip tests
        return getBrowsersSupportingTooltip();
    }

}
