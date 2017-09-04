package com.vaadin.tests.components.listselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ListSelectJumpTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getIEBrowsersOnly();
    }

    @Test
    public void list_select_does_not_change_scroll_position_when_receiving_uidl() {
        openTestURL();
        WebElement listSelect = findElements(By.className("v-select-select"))
                .get(0);
        List<WebElement> options = listSelect
                .findElements(By.tagName("option"));
        options.get(0).click();
        getTestBenchCommandExecutor()
                .executeScript("arguments[0].scrollTop = " + 100, listSelect);
        new Actions(getDriver()).keyDown(Keys.META).perform();
        options.get(9).click();
        $(ButtonElement.class).first().click();
        Assert.assertEquals(new Long(100), (Long) getTestBenchCommandExecutor()
                .executeScript("return arguments[0].scrollTop", listSelect));
    }
}
