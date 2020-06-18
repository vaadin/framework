package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridFrozenColumnReplaceTest extends SingleBrowserTest {

    @Test
    public void testChangingColumns() {
        openTestURL("debug");

        GridElement grid = $(GridElement.class).first();
        String caption = grid.getHeaderCell(0, 1).getText();
        assertFalse("Unexpected column caption: " + caption,
                caption != null && caption.startsWith("New "));

        $(ButtonElement.class).first().click();

        assertEquals("Unexpected error notifications,", 0,
                findElements(By.className("v-Notification-error")).size());

        caption = grid.getHeaderCell(0, 1).getText();
        assertTrue("Unexpected column caption: " + caption,
                caption != null && caption.startsWith("New "));
    }
}
