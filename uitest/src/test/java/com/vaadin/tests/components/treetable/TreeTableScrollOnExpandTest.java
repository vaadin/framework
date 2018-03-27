package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeTableScrollOnExpandTest extends MultiBrowserTest {

    @Test
    public void testScrollOnExpand() throws InterruptedException, IOException {
        openTestURL();
        TreeTableElement tt = $(TreeTableElement.class).first();
        tt.getRow(0).click();
        tt.scroll(300);
        sleep(1000);
        tt.getRow(20).toggleExpanded();
        // Need to wait a bit to avoid accepting the case where the TreeTable is
        // in the desired state only for a short while.
        sleep(1000);
        WebElement focusedRow = getDriver()
                .findElement(By.className("v-table-focus"));
        assertEquals("Item 21", focusedRow.getText());
    }
}