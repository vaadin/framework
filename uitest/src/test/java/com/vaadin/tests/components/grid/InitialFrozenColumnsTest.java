package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
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

        Assert.assertFalse("Notification was present",
                isElementPresent(NotificationElement.class));

        WebElement cell = $(GridElement.class).first().getCell(0, 0);
        assertTrue(cell.getAttribute("class").contains("frozen"));
    }
}
