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

import static com.vaadin.tests.components.table.SelectAllRows.TOTAL_NUMBER_OF_ROWS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if all items of the table can be selected by selecting first row,
 * press shift then select last (#13008)
 * 
 * @author Vaadin Ltd
 */
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

        clickFirstRow();
        scrollTableToBottom();
        clickLastRow();

        assertEquals(TOTAL_NUMBER_OF_ROWS, countSelectedItems());
    }

    protected void clickFirstRow() {
        getVisibleTableRows().get(0).click();
    }

    private void clickLastRow() {
        List<WebElement> rows = getVisibleTableRows();
        shiftClickElement(rows.get(rows.size() - 1));
    }

    protected void shiftClickElement(WebElement element) {
        new Actions(getDriver()).keyDown(Keys.SHIFT).click(element)
                .keyUp(Keys.SHIFT).perform();
    }

    private int countSelectedItems() {
        $(ButtonElement.class).first().click();
        String count = $(LabelElement.class).get(1).getText();
        return Integer.parseInt(count);
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void scrollTableToBottom() {
        testBenchElement(getTable().findElement(By.className("v-scrollable")))
                .scroll(TOTAL_NUMBER_OF_ROWS * 30);
        waitUntilRowIsVisible(TOTAL_NUMBER_OF_ROWS - 1);
    }

    private void waitUntilRowIsVisible(final int row) {
        waitUntil(new ExpectedCondition<Object>() {
            @Override
            public Object apply(WebDriver input) {
                try {
                    return getTable().getCell(row, 0) != null;
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        });
    }

    protected List<WebElement> getVisibleTableRows() {
        return getTable().findElements(By.cssSelector(".v-table-table tr"));
    }

}
