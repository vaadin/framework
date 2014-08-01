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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that clicking on active fields doesn't change Table selection, nor does
 * dragging rows.
 * 
 * @author Vaadin Ltd
 */
public class TableClickAndDragOnIconAndComponentsTest extends MultiBrowserTest {
    @Test
    public void testClickingAndDragging() {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        // ensure there's no initial selection
        List<WebElement> selected = table.findElements(By
                .className("v-selected"));
        assertTrue("selection found when there should be none",
                selected.isEmpty());

        // click a cell
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 2nd row expected)",
                "red 1foo", table.getCell(1, 2).getText());
        table.getCell(1, 2).click();

        // ensure the correct row and nothing but that got selected
        selected = table.findElements(By.className("v-selected"));
        assertFalse("no selection found when there should be some",
                selected.isEmpty());
        // find cell contents (row header included)
        List<WebElement> cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 2nd row expected)",
                "red 1foo", cellContents.get(2).getText());
        assertEquals("unexpected table selection size", 1, selected.size());

        List<WebElement> rows = table.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
        assertEquals("unexpected table row count", 5, rows.size());

        // find a row that isn't the one selected
        cellContents = rows.get(2).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 3rd row expected)",
                "red 2foo", cellContents.get(2).getText());

        // click on a TextField on that row
        WebElement textField = rows.get(2)
                .findElements(By.className("v-textfield")).get(0);
        assertEquals(
                "expected value not found, wrong cell or contents (6th column of the 3rd row expected)",
                "foo 2foo", textField.getAttribute("value"));
        textField.click();

        // ensure the focus shifted correctly
        List<WebElement> focused = table.findElements(By
                .className("v-textfield-focus"));
        assertEquals("unexpected amount of focused textfields", 1,
                focused.size());
        assertEquals(
                "expected value not found, wrong cell or contents (6th column of the 3rd row expected)",
                "foo 2foo", focused.get(0).getAttribute("value"));

        // ensure the selection didn't change
        selected = table.findElements(By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selected.size());
        cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 2nd row expected)",
                "red 1foo", cellContents.get(2).getText());

        // click on a Label on that row
        WebElement label = rows.get(2).findElements(By.className("v-label"))
                .get(0);
        assertEquals(
                "expected value not found, wrong cell or contents (5th column of the 3rd row expected)",
                "foo 2foo", label.getText());
        label.click();

        // ensure the focus shifted correctly
        focused = table.findElements(By.className("v-textfield-focus"));
        assertTrue("focused textfields found when there should be none",
                focused.isEmpty());

        // ensure the selection changed
        selected = table.findElements(By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selected.size());
        cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 3rd row expected)",
                "red 2foo", cellContents.get(2).getText());

        // click on the selected row's textfield (same as earlier)
        textField.click();

        // ensure the focus shifted correctly
        focused = table.findElements(By.className("v-textfield-focus"));
        assertEquals("unexpected amount of focused textfields", 1,
                focused.size());
        assertEquals(
                "expected value not found, wrong cell or contents (6th column of the 3rd row expected)",
                "foo 2foo", focused.get(0).getAttribute("value"));

        // ensure the selection didn't change
        selected = table.findElements(By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selected.size());
        cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 3rd row expected)",
                "red 2foo", cellContents.get(2).getText());

        // find the readOnly TextField of the previously selected row
        textField = rows.get(1).findElements(By.className("v-textfield"))
                .get(1);
        assertEquals(
                "expected value not found, wrong cell or contents (7th column of the 2nd row expected)",
                "foo 1foo", textField.getAttribute("value"));
        assertEquals(
                "expected readonly status not found, wrong cell or contents (7th column of the 2nd row expected)",
                "true", textField.getAttribute("readonly"));

        // click on that TextField
        textField.click();

        // ensure the focus shifted correctly
        focused = table.findElements(By.className("v-textfield-focus"));
        assertTrue("focused textfields found when there should be none",
                focused.isEmpty());

        // ensure the selection changed
        selected = table.findElements(By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selected.size());
        cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 2nd row expected)",
                "red 1foo", cellContents.get(2).getText());

        // click the embedded icon of the other row
        WebElement embedded = rows.get(2).findElement(
                By.className("v-embedded"));
        embedded.click();

        // ensure the selection changed
        selected = table.findElements(By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selected.size());
        cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 3rd row expected)",
                "red 2foo", cellContents.get(2).getText());

        // check row you are about to drag
        cellContents = rows.get(4).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 5th row expected)",
                "red 4foo", cellContents.get(2).getText());

        // check the row above it
        cellContents = rows.get(3).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 4th row expected)",
                "red 3foo", cellContents.get(2).getText());

        // drag the row to the row that's two places above it (gets dropped
        // below that)
        cellContents = rows.get(4).findElements(
                By.className("v-table-cell-content"));
        new Actions(getDriver()).moveToElement(cellContents.get(2))
                .clickAndHold().moveToElement(rows.get(2)).release().perform();

        // find the current order of the rows
        rows = table.findElement(By.className("v-table-body")).findElements(
                By.tagName("tr"));
        assertEquals("unexpected table row count", 5, rows.size());

        // ensure the row got dragged
        cellContents = rows.get(3).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the dragged row expected, should be on 4th row now)",
                "red 4foo", cellContents.get(2).getText());

        cellContents = rows.get(4).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the previous 4th row expected, should be on 5th row now)",
                "red 3foo", cellContents.get(2).getText());

        // ensure the selection didn't change
        selected = table.findElements(By.className("v-selected"));
        assertEquals("unexpected table selection size", 1, selected.size());
        cellContents = selected.get(0).findElements(
                By.className("v-table-cell-content"));
        assertEquals(
                "expected value not found, wrong cell or contents (3rd column of the 3rd row expected)",
                "red 2foo", cellContents.get(2).getText());
    }

}
