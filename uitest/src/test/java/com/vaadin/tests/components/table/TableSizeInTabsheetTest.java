package com.vaadin.tests.components.table;

import static com.vaadin.tests.components.table.TableSizeInTabsheet.TABLE;
import static com.vaadin.tests.components.table.TableSizeInTabsheet.TABSHEET;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableSizeInTabsheetTest extends MultiBrowserTest {

    private static final String TABSHEET_CONTENT_STYLENAME = "v-tabsheet-content";

    @Test
    public void testTabsheetContentHasTheSameHeightAsTable() {
        openTestURL();
        int tableHeight = getTableHeigth();
        int tabSheetContentHeight = getTableSheetContentHeight();

        assertEquals(tableHeight, tabSheetContentHeight);
    }

    private int getTableHeigth() {
        return vaadinElementById(TABLE).getSize().getHeight();
    }

    private int getTableSheetContentHeight() {
        WebElement tabsheetContent = vaadinElementById(TABSHEET)
                .findElement(By.className(TABSHEET_CONTENT_STYLENAME));
        return tabsheetContent.getSize().getHeight();
    }
}
