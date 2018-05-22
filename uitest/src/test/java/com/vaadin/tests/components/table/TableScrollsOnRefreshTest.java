package com.vaadin.tests.components.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableScrollsOnRefreshTest extends MultiBrowserTest {

    @Test
    public void ensureNoScrolling() throws InterruptedException {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        table.scroll(10000);
        sleep(500); // wait for both data requests
        String firstCellText = getFirstVisibleCell(table).getText();

        ButtonElement refresh = $(ButtonElement.class).first();
        refresh.click();
        sleep(500); // wait for both data requests
        Assert.assertEquals(firstCellText,
                getFirstVisibleCell(table).getText());

        refresh.click();
        sleep(500); // wait for both data requests
        Assert.assertEquals(firstCellText,
                getFirstVisibleCell(table).getText());
    }

    private WebElement getFirstVisibleCell(TableElement table) {
        int tableBodyTop = table.findElement(By.className("v-table-body"))
                .getLocation().getY();
        List<WebElement> cells = table
                .findElements(By.className("v-table-cell-content"));
        for (WebElement cell : cells) {
            if (cell.getLocation().getY() > tableBodyTop) {
                return cell;
            }
        }
        return null;
    }
}
