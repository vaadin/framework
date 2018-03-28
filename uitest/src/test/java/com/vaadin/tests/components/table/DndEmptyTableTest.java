package com.vaadin.tests.components.table;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for empty table as a DnD target: it should not throws client side
 * exception.
 *
 * @since
 * @author Vaadin Ltd
 */
public class DndEmptyTableTest extends MultiBrowserTest {

    @Test
    public void testDndEmptyTable() {
        setDebug(true);
        openTestURL();

        WebElement source = driver.findElement(By.className("v-ddwrapper"));
        WebElement target = driver.findElement(By.className("v-table-body"));
        Actions actions = new Actions(driver);
        actions.clickAndHold(source).moveToElement(target).release();

        assertNoErrorNotifications();
    }

}
