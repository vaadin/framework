package com.vaadin.tests.components.treegrid;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertTrue;

public class TreeGridDoubleClickTest extends SingleBrowserTest {

    @Test
    public void double_click_on_hierarchy_renderer() {
        openTestURL();

        TreeGridElement grid = $(TreeGridElement.class).first();
        WebElement hierarchyCell = grid
                .findElement(By.className("v-treegrid-node"));
        new Actions(getDriver()).doubleClick(hierarchyCell).perform();

        assertTrue("Double click is not handled",
                isElementPresent(By.className("v-Notification")));
    }
}
