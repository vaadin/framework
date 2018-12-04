package com.vaadin.tests.debug;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

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
    public void testDisabledPush() throws InterruptedException {
        setDebug(true);
        openTestURL();

        selectInfoTab();
        Thread.sleep(500);
        assertNull("Found push info server string for disabled Push",
                getPushRowValue("Push server version"));
        assertNull("Found push info client string for disabled Push",
                getPushRowValue("Push client version"));
    }

    @Test
    public void testEnabledPush() throws InterruptedException {
        setDebug(true);
        openTestURL("enablePush=true");

        selectInfoTab();
        Thread.sleep(500);
        WebElement pushRow = getPushRowValue("Push server version");
        String atmVersion = findElement(By.className("atmosphere-version"))
                .getText();
        assertTrue("Push row doesn't contain Atmosphere version",
                pushRow.getText().contains(atmVersion));
        String jsString = getPushRowValue("Push client version").getText();
        assertTrue("Push client version doesn't contain 'vaadin' string",
                jsString.contains("vaadin"));
        assertTrue("Push client version doesn't contain 'javascript' string",
                jsString.contains("javascript"));
    }

    private void selectInfoTab() {
        findElements(By.className("v-debugwindow-tab")).get(0).click();
        findElements(By.className("v-debugwindow-tab")).get(1).click();
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
