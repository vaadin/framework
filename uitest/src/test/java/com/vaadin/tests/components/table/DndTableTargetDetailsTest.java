package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.DndActionsTest;

/**
 * Test for mouse details in AbstractSelectTargetDetails class when DnD target
 * is a table.
 *
 * @author Vaadin Ltd
 */
public class DndTableTargetDetailsTest extends DndActionsTest {

    @Test
    public void testMouseDetails() throws IOException, InterruptedException {
        openTestURL();

        WebElement row = findElement(By.className("v-table-cell-wrapper"));

        dragAndDrop(row, getTarget());

        WebElement label = findElement(By.className("dnd-button-name"));
        assertEquals("Button name=left", label.getText());
    }

    protected WebElement getTarget() {
        return findElement(By.className("target"))
                .findElement(By.className("v-table-cell-wrapper"));
    }

}
