package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridComponentsTest extends MultiBrowserTest {

    @Test
    public void testRow5() {
        openTestURL();
        assertRowExists(5, "Row 5");
    }

    @Test
    public void testRow0() {
        openTestURL();
        assertRowExists(0, "Row 0");
    }

    @Test
    public void testRow999() {
        openTestURL();
        assertRowExists(999, "Row 999");
    }

    private void assertRowExists(int i, String string) {
        GridRowElement row = $(GridElement.class).first().getRow(i);
        Assert.assertEquals("Label text did not match", string,
                row.getCell(0).getText());
        row.findElement(By.id(string.replace(' ', '_').toLowerCase())).click();
        // IE 11 is slow, need to wait for the notification.
        waitForElementPresent(By.vaadin("//Notification"));
        Assert.assertTrue("Notification should contain given text",
                $(NotificationElement.class).first().getText()
                        .contains(string));
    }
}
