package com.vaadin.tests.components.table;

import static com.vaadin.tests.components.table.ColumnWidthsAfterChangeTableColumnsCountOrOrder.BUTTON_CHANGE_COLUMN_COUNT_AND_WIDTH;
import static com.vaadin.tests.components.table.ColumnWidthsAfterChangeTableColumnsCountOrOrder.BUTTON_CHANGE_ORDER_AND_WIDTH_ID;
import static com.vaadin.tests.components.table.ColumnWidthsAfterChangeTableColumnsCountOrOrder.NEW_COLUMN_WIDTH;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ColumnWidthsAfterChangeTableColumnsCountOrOrderTest
        extends MultiBrowserTest {

    @Test
    public void testColumnWidthAfterChangeTableColumnsOrder() {
        openTestURL();

        getButtonChangeOrderAndWidth().click();

        waitForElementPresent(By.className("v-table"));

        assertEquals("The width of descr column should be " + NEW_COLUMN_WIDTH,
                NEW_COLUMN_WIDTH, getDescriptionColumnWidth());
    }

    @Test
    public void testColumnWidthAfterChangeTableColumnsCount() {
        openTestURL();

        getButtonChangeColumnCountAndWidth().click();

        waitForElementPresent(By.className("v-table"));

        assertEquals("The width of descr column should be " + NEW_COLUMN_WIDTH,
                NEW_COLUMN_WIDTH, getDescriptionColumnWidth());
    }

    private WebElement getButtonChangeOrderAndWidth() {
        return vaadinElementById(BUTTON_CHANGE_ORDER_AND_WIDTH_ID);
    }

    private WebElement getButtonChangeColumnCountAndWidth() {
        return vaadinElementById(BUTTON_CHANGE_COLUMN_COUNT_AND_WIDTH);
    }

    private int getDescriptionColumnWidth() {
        return driver.findElement(By.xpath("//div[@class='v-table-cell-wrapper'"
                + " and text() = 'descr1']")).getSize().getWidth();
    }

}
