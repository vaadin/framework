/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for a Table with a customised BeanItemContainer.
 * 
 * @author Vaadin Ltd
 */
public class TableWithContainerRequiringEqualsForItemIdTest extends
        MultiBrowserTest {

    @Test
    public void testSorting() {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        List<WebElement> rows = table.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpect amount of rows", 46, rows.size());

        // click the button on the first visible row
        clickButton(table, 0, 3, "1. Button Button999 clicked");

        // click the button on the last visible row
        clickButton(table, 14, 3, "2. Button Button985 clicked");

        clickTableHeaderToSort(table);

        // check the first cell of the new first visible row
        checkFirstCell(table, "0");

        // click the button on the first visible row
        clickButton(table, 0, 3, "3. Button Button0 clicked");

        // sort by the first column (descending)
        clickTableHeaderToSort(table);

        // check the first cell of the new first visible row
        checkFirstCell(table, "999");

        // click the button on the first visible row
        clickButton(table, 0, 3, "4. Button Button999 clicked");
    }

    private void checkFirstCell(TableElement table, String expected) {
        assertEquals("unexpected contents", expected, table.getCell(0, 0)
                .getText());
    }

    private void clickTableHeaderToSort(TableElement table) {
        table.findElement(By.className("v-table-header"))
                .findElement(By.tagName("tr"))
                .findElement(By.className("v-table-caption-container")).click();
    }

    private void clickButton(TableElement table, int row, int column,
            String expectedLog) {
        table.getCell(row, column).findElement(By.className("v-button"))
                .click();

        // check the new log row
        assertEquals("unexpected log row", expectedLog,
                findElement(By.id("Log_row_0")).getText());
    }

}
