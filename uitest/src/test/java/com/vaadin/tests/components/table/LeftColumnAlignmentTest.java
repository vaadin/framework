package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test class for issue #13399 : Left alignment should not be set explicitly
 * instead of relying on default behavior
 *
 * @author Vaadin Ltd
 */
public class LeftColumnAlignmentTest extends MultiBrowserTest {

    @Test
    public void testLeftColumnAlignment() throws Exception {
        openTestURL();

        // Do align columns to the left
        WebElement webElement = driver.findElement(By.className("v-button"));
        webElement.click();

        assertTrue("Table caption is not aligned to the left", isElementPresent(
                By.className("v-table-caption-container-align-left")));

        WebElement footer = driver
                .findElement(By.className("v-table-footer-container"));

        assertEquals("Table footer is not aligned to the left", "left",
                footer.getCssValue("text-align"));

        WebElement cell = driver
                .findElement(By.className("v-table-cell-wrapper"));

        assertEquals("Table cell is not aligned to the left", "left",
                cell.getCssValue("text-align"));
    }

}
