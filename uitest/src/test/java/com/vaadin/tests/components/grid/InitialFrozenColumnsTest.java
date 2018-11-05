package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class InitialFrozenColumnsTest extends MultiBrowserTest {
    @Test
    public void testInitialFrozenColumns() {
        setDebug(true);
        openTestURL();

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));

        WebElement cell = $(GridElement.class).first().getCell(0, 0);
        assertTrue(cell.getAttribute("class").contains("frozen"));
    }

    @Test
    public void testInitialAllColumnsFrozen() {
        setDebug(true);
        openTestURL("frozen=3");

        assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));
        assertNoDebugMessage(Level.SEVERE);
        WebElement cell = $(GridElement.class).first().getCell(0, 2);
        assertTrue(cell.getAttribute("class").contains("frozen"));
    }
}
