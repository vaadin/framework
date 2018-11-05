package com.vaadin.tests.actions;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class ActionsOnDetachedComponentsTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
        if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
            // focus the page to make shortcuts go to the right place
            getDriver().findElement(By.className("v-app")).click();
        }
    }

    @Test
    public void shortcutActionOnDetachedComponentShouldNotBeHandled()
            throws InterruptedException {

        Actions k = new Actions(driver);
        k.sendKeys("a").perform();
        k.sendKeys("a").perform();
        sleep(500);

        assertElementNotPresent(By.id("layer-A"));
        assertElementPresent(By.id("layer-B"));
        assertThat(getLogRow(0), endsWith("btn-A"));
        assertThat(getLogRow(1), not(endsWith("btn-B")));

    }

    @Test
    public void actionOnDetachedComponentShouldNotBeHandled()
            throws InterruptedException {
        TableElement table = $(TableElement.class).first();
        table.getRow(0).contextClick();
        // Find the opened menu
        WebElement menu = findElement(By.className("v-contextmenu"));
        WebElement menuitem = menu
                .findElement(By.xpath("//*[text() = 'Table action']"));

        Actions doubleClick = new Actions(getDriver());
        doubleClick.doubleClick(menuitem).build().perform();

        assertElementNotPresent(By.id("layer-A"));
        assertElementPresent(By.id("layer-B"));
        assertThat(getLogRow(0), endsWith("tableAction"));
        assertThat(getLogRow(1), not(endsWith("tableAction")));

    }

}
