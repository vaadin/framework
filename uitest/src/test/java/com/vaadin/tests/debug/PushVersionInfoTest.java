package com.vaadin.tests.debug;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

/**
 * Test for PUSH version string in debug window.
 *
 * @author Vaadin Ltd
 */
@TestCategory("push")
public class PushVersionInfoTest extends SingleBrowserTest {

    @Test
    public void testDisabledPush() {
        setDebug(true);
        openTestURL();

        selectInfoTab();
        Assert.assertNull("Found push info server string for disabled Push",
                getPushRowValue("Push server version"));
        Assert.assertNull("Found push info client string for disabled Push",
                getPushRowValue("Push client version"));
    }

    @Test
    public void testEnabledPush() {
        setDebug(true);
        openTestURL("enablePush=true");

        selectInfoTab();
        WebElement pushRow = getPushRowValue("Push server version");
        String atmVersion = findElement(By.className("atmosphere-version"))
                .getText();
        Assert.assertTrue("Push row doesn't contain Atmosphere version",
                pushRow.getText().contains(atmVersion));
        String jsString = getPushRowValue("Push client version").getText();
        Assert.assertTrue("Push client version doesn't contain 'vaadin' string",
                jsString.contains("vaadin"));
        Assert.assertTrue(
                "Push client version doesn't contain 'javascript' string",
                jsString.contains("javascript"));
    }

    private void selectInfoTab() {
        if (isElementPresent(By.className("v-ie8"))) {

            int size = findElements(By.className("v-debugwindow-tab")).size();
            for (int i = 0; i < size; i++) {
                WebElement tab = findElement(
                        By.className("v-debugwindow-tab-selected"));
                String title = tab.getAttribute("title");
                if (title != null && title.startsWith("General information")) {
                    break;
                }
                Actions actions = new Actions(getDriver());
                actions.sendKeys(Keys.TAB);
                actions.sendKeys(Keys.SPACE);
                actions.build().perform();
            }
        } else {
            findElements(By.className("v-debugwindow-tab")).get(0).click();
            findElements(By.className("v-debugwindow-tab")).get(1).click();
        }
    }

    private WebElement getPushRowValue(String key) {
        List<WebElement> rows = findElements(By.className("v-debugwindow-row"));
        for (WebElement row : rows) {
            WebElement caption = row.findElement(By.className("caption"));
            if (caption.getText().startsWith(key)) {
                return row.findElement(By.className("value"));
            }
        }
        return null;
    }
}
