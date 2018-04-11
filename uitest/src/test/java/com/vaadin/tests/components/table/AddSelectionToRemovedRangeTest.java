package com.vaadin.tests.components.table;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class AddSelectionToRemovedRangeTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.CHROME);
    }

    @Test
    public void addAndRemoveItemToRemovedRange() throws IOException {
        openTestURL();
        List<WebElement> rows = driver
                .findElements(By.className("v-table-cell-wrapper"));
        WebElement rangeStart = rows.get(0);
        WebElement rangeEnd = rows.get(1);
        rangeStart.click();
        new Actions(driver).keyDown(Keys.SHIFT).perform();
        rangeEnd.click();
        new Actions(driver).keyUp(Keys.SHIFT).perform();
        driver.findElement(By.className("v-button")).click();
        WebElement extraRow = driver
                .findElements(By.className("v-table-cell-wrapper")).get(1);
        new Actions(driver).keyDown(Keys.CONTROL).click(extraRow)
                .click(extraRow).keyUp(Keys.CONTROL).perform();
        driver.findElement(By.className("v-button")).click();
        try {
            driver.findElement(By.vaadin("Root/VNotification[0]"));
            fail("Notification is shown");
        } catch (NoSuchElementException e) {
            // All is well.
        }
    }
}
