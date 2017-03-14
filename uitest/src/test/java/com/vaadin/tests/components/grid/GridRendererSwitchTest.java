package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridRendererSwitchTest extends SingleBrowserTest {

    @Test
    public void testSwitchRenderer() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals("Unexpected content in first grid cell", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        Assert.assertFalse("No button in cell", grid.getCell(0, 0)
                .findElements(By.tagName("button")).isEmpty());
        grid.getCell(0, 0).findElement(By.tagName("button")).click();
        Assert.assertTrue("Notification not shown",
                isElementPresent(NotificationElement.class));
        button.click();
        Assert.assertEquals("Cell should be back to text content.", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));

        assertNoErrorNotifications();
    }

}
