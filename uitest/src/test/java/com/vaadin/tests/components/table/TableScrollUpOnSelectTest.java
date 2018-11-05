package com.vaadin.tests.components.table;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if Table appears to scroll up under an obscure set of conditions
 * (Scrolled down, set to expand, selecting updates a TextField that precedes
 * the Table in a VerticalLayout.) (#10106)
 *
 * @author Vaadin Ltd
 */
public class TableScrollUpOnSelectTest extends MultiBrowserTest {

    @Test
    public void TestThatSelectingDoesntScroll() {
        openTestURL();

        // WebElement table = driver.findElement(By.vaadin("//Table"));
        WebElement row = $(TableElement.class).first().getCell(49, 0);
        final WebElement scrollPositionDisplay = getDriver()
                .findElement(By.className("v-table-scrollposition"));
        waitUntilNot(input -> scrollPositionDisplay.isDisplayed(), 10);

        int rowLocation = row.getLocation().getY();
        row.click();
        int newRowLocation = row.getLocation().getY();

        assertTrue("Table has scrolled.", rowLocation == newRowLocation);
    }
}
