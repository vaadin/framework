package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("xvfb-test")
public class GridRendererSwitchTest extends SingleBrowserTest {

    @Test
    public void testSwitchRenderer() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Unexpected content in first grid cell", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        assertFalse("No button in cell", grid.getCell(0, 0)
                .findElements(By.tagName("button")).isEmpty());
        grid.getCell(0, 0).findElement(By.tagName("button")).click();
        assertTrue("Notification not shown",
                isElementPresent(NotificationElement.class));
        button.click();
        assertEquals("Cell should be back to text content.", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));

        assertNoErrorNotifications();
    }

    @Test
    public void testSwitchRendererReorderColumns() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Unexpected content in first grid cell", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));
        ButtonElement button = $(ButtonElement.class).caption("Switch").first();
        button.click();
        ButtonElement reverse = $(ButtonElement.class).caption("Reverse")
                .first();
        reverse.click();
        assertEquals("Unexpected content in first grid cell after reorder",
                "Bar 0", grid.getCell(0, 0).getAttribute("innerHTML"));

        assertFalse("No button in cell after reversing order", grid
                .getCell(0, 1).findElements(By.tagName("button")).isEmpty());
        grid.getCell(0, 1).findElement(By.tagName("button")).click();
        assertTrue("Notification not shown",
                isElementPresent(NotificationElement.class));
        reverse.click();
        assertFalse("No button in cell after restoring original order", grid
                .getCell(0, 0).findElements(By.tagName("button")).isEmpty());

        assertNoErrorNotifications();
    }

    @Test
    public void testReorderColumnsSwitchRenderer() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Unexpected content in first grid cell", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));
        ButtonElement reverse = $(ButtonElement.class).caption("Reverse")
                .first();

        reverse.click();
        assertEquals("Unexpected content in first grid cell after reorder",
                "Bar 0", grid.getCell(0, 0).getAttribute("innerHTML"));

        ButtonElement button = $(ButtonElement.class).caption("Switch").first();
        button.click();

        assertFalse(
                "No button in cell after reversing order and changing renderer",
                grid.getCell(0, 1).findElements(By.tagName("button"))
                        .isEmpty());
        grid.getCell(0, 1).findElement(By.tagName("button")).click();
        assertTrue("Notification not shown",
                isElementPresent(NotificationElement.class));

        button.click();
        assertEquals("Cell should be back to text content.", "Foo 0",
                grid.getCell(0, 1).getAttribute("innerHTML"));

        assertNoErrorNotifications();
    }

    @Test
    public void testReorderColumnsHideAndSwitch() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Unexpected content in first grid cell", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));
        ButtonElement reverse = $(ButtonElement.class).caption("Reverse")
                .first();

        reverse.click();
        assertEquals("Unexpected content in first grid cell after reorder",
                "Bar 0", grid.getCell(0, 0).getAttribute("innerHTML"));

        grid.toggleColumnHidden("Bar");

        ButtonElement button = $(ButtonElement.class).caption("Switch").first();
        button.click();

        assertNoErrorNotifications();

        assertFalse(
                "No button in cell after reversing order and changing renderer",
                grid.getCell(0, 0).findElements(By.tagName("button"))
                        .isEmpty());
        grid.getCell(0, 0).findElement(By.tagName("button")).click();
        assertTrue("Notification not shown",
                isElementPresent(NotificationElement.class));
    }

    @Test
    public void testSwitchRendererOfHiddenColumn() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("Unexpected content in first grid cell", "Foo 0",
                grid.getCell(0, 0).getAttribute("innerHTML"));

        grid.toggleColumnHidden("Foo");

        assertEquals("Unexpected content in first grid cell after hide",
                "Bar 0", grid.getCell(0, 0).getAttribute("innerHTML"));

        ButtonElement button = $(ButtonElement.class).caption("Switch").first();
        button.click();

        assertNoErrorNotifications();

        assertEquals(
                "Unexpected content in first grid cell after hidden renderer change",
                "Bar 0", grid.getCell(0, 0).getAttribute("innerHTML"));
    }
}
