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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests clicks on different types of Table contents.
 * 
 * @author Vaadin Ltd
 */
public class LabelEmbeddedClickThroughForTableTest extends MultiBrowserTest {

    @Test
    public void testNotification() {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        // click first cell of first row
        clickCell(table, 0, 0);

        // click first cell of second row
        clickCell(table, 1, 0);

        // click the ordinary label component on first row
        clickLabel(table, 0, 1);

        // click the ordinary label component on second row
        clickLabel(table, 1, 1);

        // click the html-content label component on first row
        clickBoldTag(table, 0, 2);

        // click the ordinary label component on second row (some browsers
        // navigate away from the page if you try to click the link in the
        // html-content label)
        clickLabel(table, 1, 1);

        // click the embedded image on first row
        clickImageTag(table, 0, 3);

        // click the embedded image on second row
        clickImageTag(table, 1, 3);
    }

    private void clickImageTag(TableElement table, int row, int column) {
        table.getCell(row, column).findElement(By.className("v-embedded"))
                .findElement(By.tagName("img")).click();
        checkRowSelected(table, row);
    }

    private void clickBoldTag(TableElement table, int row, int column) {
        table.getCell(row, column).findElement(By.className("v-label"))
                .findElement(By.tagName("b")).click();
        checkRowSelected(table, row);
    }

    private void clickLabel(TableElement table, int row, int column) {
        table.getCell(row, column).findElement(By.className("v-label")).click();
        checkRowSelected(table, row);
    }

    private void clickCell(TableElement table, int row, int column) {
        table.getCell(row, column).click();
        checkRowSelected(table, row);
    }

    private void checkRowSelected(TableElement table, int rowIndex) {
        List<WebElement> selectedRows = table.findElement(
                By.className("v-table-body")).findElements(
                By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selectedRows.size());
        assertEquals(
                "contents of the selected row don't match contents of the row #"
                        + rowIndex,
                table.getCell(rowIndex, 0).getText(),
                selectedRows.get(0)
                        .findElement(By.className("v-table-cell-wrapper"))
                        .getText());
    }

}
