package com.vaadin.tests.components.table;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests if table focus is moved correctly to the selected item
 *
 * @author Vaadin Ltd
 */
public class TableMoveFocusWithSelectionTest extends MultiBrowserTest {

    @Test
    public void selectUnfocusedTableAndAssumeSelectionGetsFocus() {

        openTestURL();

        // Click on row 5
        getDriver().findElement(By.id("row-5")).click();

        // Ensure row 5 gets focused
        WebElement row5TableRow = getDriver()
                .findElement(By.xpath("//div[@id='row-5']/../../.."));
        String row5StyleName = row5TableRow.getAttribute("class");
        assertTrue(row5StyleName.contains("v-table-focus"));
    }

    @Test
    public void focusShouldStayOnUserSelectedRowIfSelectionChangesServerSide() {

        openTestURL();

        // Select multiselect
        getDriver().findElement(By.id("toggle-mode")).click();

        // Click on row 7
        getDriver().findElement(By.id("row-7")).click();

        // Clicking a row should get the row focus
        WebElement row7TableRow = getDriver()
                .findElement(By.xpath("//div[@id='row-7']/../../.."));
        String row7StyleName = row7TableRow.getAttribute("class");
        assertTrue(row7StyleName.contains("v-table-focus"));

        // Select row 5-10 server side
        getDriver().findElement(By.id("select-510")).click();

        /*
         * Focus the table again (some browsers steal focus when performing
         * button click, other don't)
         */
        getDriver().findElement(By.id("test-table")).click();

        // Ensure row 7 is still focused
        row7TableRow = getDriver()
                .findElement(By.xpath("//div[@id='row-7']/../../.."));
        row7StyleName = row7TableRow.getAttribute("class");
        assertTrue(row7StyleName.contains("v-table-focus"));
    }
}
