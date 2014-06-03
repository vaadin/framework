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

import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableWithPollingTest extends MultiBrowserTest {

    @Test
    public void testColumnResizing() throws Exception {
        openTestURL();

        WebElement headerCell = driver.findElement(By
                .xpath("(//td[contains(@class, 'v-table-header-cell')])[1]"));
        WebElement bodyCell = driver.findElement(By
                .xpath("(//td[contains(@class, 'v-table-cell-content')])[1]"));
        WebElement resizer = driver.findElement(By
                .xpath("(//div[contains(@class, 'v-table-resizer')])[1]"));

        final int offset = 50;
        final int headerCellWidth = headerCell.getSize().width;
        final int bodyCellWidth = bodyCell.getSize().width;

        new Actions(driver).clickAndHold(resizer).moveByOffset(offset, 0)
                .perform();
        sleep(2000);
        new Actions(driver).release().perform();

        Assert.assertEquals(headerCellWidth + offset,
                headerCell.getSize().width);
        Assert.assertEquals(bodyCellWidth + offset, bodyCell.getSize().width);
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
