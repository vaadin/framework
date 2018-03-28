package com.vaadin.tests.components.table;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test whether adding the first item to a table calculates the table width
 * correctly
 *
 * @author Vaadin Ltd
 */
public class TableWidthItemRemoveTest extends MultiBrowserTest {
    @Test
    public void testWidthResizeOnItemAdd() {
        openTestURL();

        WebElement populateButton = driver
                .findElement(By.vaadin("//Button[caption=\"Populate\"]"));
        WebElement table = driver
                .findElement(By.vaadin("//Table[caption=\"My table\"]"));
        int original_width = table.getSize().getWidth();
        populateButton.click();
        assertTrue("Width changed on item add.",
                original_width == table.getSize().getWidth());
    }

}
