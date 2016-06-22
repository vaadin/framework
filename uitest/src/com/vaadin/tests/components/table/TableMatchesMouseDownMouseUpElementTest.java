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

import static com.vaadin.tests.components.table.TableMatchesMouseDownMouseUpElement.CLEAR_BUTTON_ID;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Regular click cases already covered by @LabelEmbeddedClickThroughForTableTest
 * Testing cases when mouse down and mouse up positions are different
 * 
 * @author Vaadin Ltd
 */
public class TableMatchesMouseDownMouseUpElementTest extends MultiBrowserTest {

    TableElement table;

    @Test
    public void testClick() {
        openTestURL();
        table = $(TableElement.class).first();

        testMoveOut(getBoldTag(0, 2));
        testMoveIn(getBoldTag(0, 2));

        testMoveOut(getLabel(0, 1));
        testMoveIn(getLabel(0, 1));

        testClickOnDifferentRows();
    }

    /**
     * MouseDown on element and mouseUp outside element but on same cell
     */
    private void testMoveOut(WebElement element) {
        clearSelection();
        clickAndMove(element, 5, 5, 0, 50);
        checkSelectedRowCount(1);
        checkRowSelected(0);
    }

    /**
     * MouseDown outside element but on same cell and mouseUp on element
     */
    private void testMoveIn(WebElement element) {
        clearSelection();
        clickAndMove(element, 5, 55, 0, -50);
        checkSelectedRowCount(1);
        checkRowSelected(0);
    }

    /**
     * Mouse down in cell of row1 holds and mouse up in cell of row 2
     */
    public void testClickOnDifferentRows() {
        clearSelection();
        WebElement elementFrom = getCell(0, 1);
        WebElement elementTo = getCell(0, 2);
        clickAndMove(elementFrom, elementTo);
        checkSelectedRowCount(0);
    }

    private WebElement getBoldTag(int row, int column) {
        return table.getCell(row, column).findElement(By.className("v-label"))
                .findElement(By.tagName("b"));
    }

    private WebElement getLabel(int row, int column) {
        return table.getCell(row, column).findElement(By.className("v-label"));
    }

    private WebElement getCell(int row, int column) {
        return table.getCell(row, column);
    }

    private void clearSelection() {
        WebElement clearButton = vaadinElementById(CLEAR_BUTTON_ID);
        clearButton.click();
    }

    /**
     * Mouse down on element + initial offset -> Moves the "move offset" ->
     * Mouse up
     */
    private void clickAndMove(WebElement element, int initialX, int initialY,
            int moveX, int moveY) {
        new Actions(driver).moveToElement(element, initialX, initialY)
                .clickAndHold().perform();
        new Actions(driver).moveByOffset(moveX, moveY).perform();
        new Actions(driver).release().perform();
    }

    /**
     * Mouse down on elementFrom -> Moves to elementTo -> Mouse up
     */
    private void clickAndMove(WebElement elementFrom, WebElement elementTo) {
        new Actions(driver).moveToElement(elementFrom, 5, 5).clickAndHold()
                .perform();
        new Actions(driver).moveToElement(elementTo, 5, 5).perform();
        new Actions(driver).release().perform();
    }

    private void checkRowSelected(int rowIndex) {
        assertEquals(
                "contents of the selected row don't match contents of the row #"
                        + rowIndex,
                table.getCell(rowIndex, 0).getText(),
                getSelectedRows().get(0)
                        .findElement(By.className("v-table-cell-wrapper"))
                        .getText());
    }

    private void checkSelectedRowCount(int expected) {
        assertEquals("unexpected table selection size", expected,
                getSelectedRows().size());
    }

    private List<WebElement> getSelectedRows() {
        return table.findElement(By.className("v-table-body")).findElements(
                By.className("v-selected"));
    }

}
