package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ColumnReorderingWithManyColumnsTest extends MultiBrowserTest {
    @Test
    public void testReordering() throws IOException {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        TableHeaderElement sourceCell = table.getHeaderCell(0);
        TableHeaderElement targetCell = table.getHeaderCell(10);
        drag(sourceCell, targetCell);
        WebElement markedElement = table
                .findElement(By.className("v-table-focus-slot-right"));
        String markedColumnName = markedElement.findElement(By.xpath(".."))
                .getText();
        assertEquals("col-9", markedColumnName.toLowerCase(Locale.ROOT));
    }

    private void drag(WebElement source, WebElement target) {
        Actions actions = new Actions(getDriver());
        actions.moveToElement(source, getXOffset(source, 10),
                getYOffset(source, 10));
        actions.clickAndHold(source);
        actions.moveToElement(target, getXOffset(target, 10),
                getYOffset(target, 10));
        actions.perform();
    }
}
