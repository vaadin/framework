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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Tests that clicking on active fields doesn't change Table selection, nor does
 * dragging rows.
 * 
 * @author Vaadin Ltd
 */
public class TableClickAndDragOnIconAndComponentsTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void clickOnTextFieldDoesNotSelectRow() {
        selectRow(1);

        clickOnTextField(2);
        assertThatFocusTextFieldHasText("foo 2foo");

        assertThat(getSelectedRowTextValue(), is(1));
    }

    @Test
    public void clickOnReadOnlyTextFieldSelectsRow() {
        selectRow(1);

        clickOnReadOnlyTextField(2);

        assertThat(getSelectedRowTextValue(), is(2));
    }

    @Test
    public void clickOnLabelSelectsRow() {
        selectRow(1);

        clickOnLabel(2);

        assertThat(getSelectedRowTextValue(), is(2));
    }

    @Test
    public void clickOnEmbeddedIconSelectsRow() {
        selectRow(1);

        clickOnEmbeddedIcon(2);

        assertThat(getSelectedRowTextValue(), is(2));
    }

    @Test
    public void dragAndDroppingRowDoesNotSelectRow() {
        selectRow(1);

        moveRow(0, 3);

        assertThat(getSelectedRowTextValue(), is(1));
        assertThat(getSelectedRowIndex(), is(0));
    }

    @Test
    public void dragAndDroppingSelectedRowStaysSelected() {
        selectRow(1);

        moveRow(1, 4);

        assertThat(getSelectedRowTextValue(), is(1));
        assertThat(getSelectedRowIndex(), is(4));
    }

    private void assertThatFocusTextFieldHasText(String text) {
        List<WebElement> focused = getTable().findElements(
                By.className("v-textfield-focus"));

        assertThat(focused.get(0).getAttribute("value"), is(text));
    }

    private int getSelectedRowTextValue() {
        WebElement selectedRow = getSelectedRow();

        // i.e. 'red 1foo'
        String text = getText(selectedRow, 2);

        return Integer.parseInt(text.substring(4, 5));
    }

    private String getText(WebElement row, int column) {
        List<WebElement> cellContents = getCellContents(row);

        return cellContents.get(column).getText();
    }

    private List<WebElement> getCellContents(WebElement row) {
        return row.findElements(By.className("v-table-cell-content"));
    }

    private WebElement getSelectedRow() {
        return getTable().findElement(By.className("v-selected"));
    }

    private void clickOnTextField(int row) {
        WebElement textField = getTextField(row, 0);

        textField.click();
    }

    private void clickOnReadOnlyTextField(int row) {
        WebElement textField = getTextField(row, 1);

        textField.click();
    }

    private WebElement getTextField(int row, int index) {
        return getElement(row, index, "v-textfield");
    }

    private WebElement getElement(int row, String className) {
        return getElement(row, 0, className);
    }

    private WebElement getElement(int row, int index, String className) {
        return getRows().get(row).findElements(By.className(className))
                .get(index);
    }

    private List<WebElement> getRows() {
        return getTable().findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));
    }

    private void selectRow(int row) {
        TableElement table = getTable();

        table.getCell(row, 2).click();
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void clickOnLabel(int row) {
        WebElement label = getElement(row, "v-label");
        label.click();
    }

    private void clickOnEmbeddedIcon(int row) {
        WebElement embeddedIcon = getElement(row, "v-embedded");
        embeddedIcon.click();
    }

    private void moveRow(int from, int to) {
        List<WebElement> rows = getRows();
        List<WebElement> cellContents = getCellContents(rows.get(from));

        new Actions(getDriver()).moveToElement(cellContents.get(2))
                .clickAndHold().moveToElement(rows.get(to)).release().perform();
    }

    private int getSelectedRowIndex() {
        List<WebElement> rows = getRows();

        // Unfortunately rows.getIndexOf(getSelectedRow()) doesn't work.
        for (WebElement r : rows) {
            if (r.getAttribute("class").contains("v-selected")) {
                return rows.indexOf(r);
            }
        }

        return -1;
    }
}
