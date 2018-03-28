package com.vaadin.tests.components.table;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that rows are completely visible and clicking buttons doesn't change
 * anything.
 *
 * @author Vaadin Ltd
 */
public class TableRowHeight2Test extends MultiBrowserTest {

    @Test
    public void testRowHeights() throws IOException {
        openTestURL();

        compareScreen("initial");

        TableElement table = $(TableElement.class).first();
        List<WebElement> rows = table.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));

        rows.get(0).findElements(By.className("v-button")).get(1).click();
        rows.get(1).findElements(By.className("v-button")).get(1).click();

        compareScreen("after");
    }

}
