package com.vaadin.tests.components.table;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests whether right-click on a column header works after the column is
 * dragged.
 *
 * @author Vaadin Ltd
 */
public class HeaderRightClickAfterDragTest extends MultiBrowserTest {

    @Test
    public void dragAndRightClick() {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        TableElement table = $(TableElement.class).first();
        TestBenchElement header0 = table.getHeaderCell(0);
        Actions actions = new Actions(getDriver());
        actions.contextClick(header0).perform();

        // check that right-click opened a window
        waitForElementPresent(By.className("v-window"));

        closeWindow();

        actions.clickAndHold(header0).moveToElement(table.getHeaderCell(1))
                .release();

        actions.contextClick(header0).perform();

        // check that right-click still opened a window
        waitForElementPresent(By.className("v-window"));
    }

    private void closeWindow() {
        WindowElement window = $(WindowElement.class).first();
        window.findElement(By.className("v-window-closebox")).click();
        waitUntil(input -> findElements(By.className("v-window")).isEmpty());
    }
}
