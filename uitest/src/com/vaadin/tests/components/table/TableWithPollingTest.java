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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableWithPollingTest extends MultiBrowserTest {

    @Test
    public void testColumnResizing() throws Exception {
        openTestURL();

        int offset = -20;
        int headerCellWidth = getHeaderCell(0).getSize().width;
        int bodyCellWidth = getBodyCell(0).getSize().width;

        resizeColumn(0, offset);

        assertHeaderCellWidth(0, headerCellWidth + offset);
        assertBodyCellWidth(0, bodyCellWidth + offset);

        offset = 50;
        headerCellWidth = getHeaderCell(1).getSize().width;
        bodyCellWidth = getBodyCell(1).getSize().width;

        resizeColumn(1, offset);

        assertHeaderCellWidth(1, headerCellWidth + offset);
        assertBodyCellWidth(1, bodyCellWidth + offset);

    }

    private WebElement getHeaderCell(int column) {
        return $(TableElement.class).get(0).getHeaderCell(column);
    }

    private WebElement getBodyCell(int column) {
        return $(TableElement.class).get(0).getCell(0, column);
    }

    private WebElement getColumnResizer(int column) {
        return getHeaderCell(column).findElement(
                By.className("v-table-resizer"));
    }

    private void resizeColumn(int column, int by) throws InterruptedException {
        new Actions(driver).clickAndHold(getColumnResizer(column))
                .moveByOffset(by, 0).perform();
        sleep(2000);
        new Actions(driver).release().perform();
    }

    private void assertHeaderCellWidth(int column, int width)
            throws AssertionError {
        Assert.assertEquals(width, getHeaderCell(column).getSize().width);
    }

    private void assertBodyCellWidth(int column, int width)
            throws AssertionError {
        Assert.assertEquals(width, getBodyCell(column).getSize().width);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Selenium has issues with drag-and-drop on IE8 making it impossible to
        // drag a target as small as the table resizer. So we'll just have to
        // ignore IE8 completely.
        List<DesiredCapabilities> browsers = super.getBrowsersToTest();
        browsers.remove(Browser.IE8.getDesiredCapabilities());
        return browsers;
    }
}
