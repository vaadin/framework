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

import static com.vaadin.tests.components.table.SelectAllRows.COUNT_OF_SELECTED_ROWS_LABEL;
import static com.vaadin.tests.components.table.SelectAllRows.COUNT_SELECTED_BUTTON;
import static com.vaadin.tests.components.table.SelectAllRows.TABLE;
import static com.vaadin.tests.components.table.SelectAllRows.TOTAL_NUMBER_OF_ROWS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class SelectAllRowsTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Pressing Shift modifier key does not work with Firefox and PhantomJS
        ArrayList<DesiredCapabilities> browsers = new ArrayList<DesiredCapabilities>(
                super.getBrowsersToTest());
        browsers.remove(Browser.FIREFOX.getDesiredCapabilities());
        browsers.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return browsers;
    }

    @Test
    public void testAllRowsAreSelected() {
        openTestURL();

        selectAllRowsInTable();
        int selectedRows = countSelectedItems();

        assertEquals(TOTAL_NUMBER_OF_ROWS, selectedRows);
    }

    private int countSelectedItems() {
        WebElement countButton = vaadinElementById(COUNT_SELECTED_BUTTON);
        countButton.click();
        WebElement countOfSelectedRows = vaadinElementById(COUNT_OF_SELECTED_ROWS_LABEL);
        String count = countOfSelectedRows.getText();
        return Integer.parseInt(count);
    }

    private void selectAllRowsInTable() {
        clickFirstRow();
        scrollTableToBottom();
        new Actions(getDriver()).keyDown(Keys.SHIFT).click(getLastRow())
                .keyUp(Keys.SHIFT).perform();
    }

    private WebElement getLastRow() {
        List<WebElement> rows = allVisibleTableRows();
        WebElement lastRow = rows.get(rows.size() - 1);
        return lastRow;
    }

    private void clickFirstRow() {
        WebElement firstRow = allVisibleTableRows().get(0);
        firstRow.click();
    }

    private void scrollTableToBottom() {
        WebElement table = vaadinElementById(TABLE);
        testBenchElement(table.findElement(By.className("v-scrollable")))
                .scroll(TOTAL_NUMBER_OF_ROWS * 30);

        // Wait for scrolling to complete. Otherwise, clicking last row will
        // fail with Chrome
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<WebElement> allVisibleTableRows() {
        WebElement table = vaadinElementById(TABLE);
        List<WebElement> rows = table.findElements(By
                .cssSelector(".v-table-table tr"));
        return rows;
    }
}
