/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that column keeps its header, icon, alignment after toggling visibility
 * (#6245).
 * 
 * @author Vaadin Ltd
 */
public class TableToggleColumnVisibilityTest extends MultiBrowserTest {

    @Test
    public void testColumnWidthRestoredAfterTogglingVisibility() {
        openTestURL();

        WebElement toggleVisibilityButton = findElement(By.id("visib-toggler"));
        WebElement changeOrderButton = findElement(By.id("order-toggler"));

        checkHeaderAttributes(1);

        toggleVisibilityButton.click(); // hide column #1
        Assert.assertEquals("One column should be visible",
                findElements(By.className("v-table-header-cell")).size(), 1);

        toggleVisibilityButton.click(); // restore column #1
        Assert.assertEquals("Two columns should be visible",
                findElements(By.className("v-table-header-cell")).size(), 2);
        checkHeaderAttributes(1);

        changeOrderButton.click(); // change column order, column #1 now becomes
                                   // column #0
        checkHeaderAttributes(0);

    }

    /*
     * Checks column header with number columnNumber.
     */
    private void checkHeaderAttributes(int columnNumber) {
        WebElement headerCell = findElements(
                By.className("v-table-header-cell")).get(columnNumber);

        Assert.assertTrue("Column header text should be custom", headerCell
                .getText().equalsIgnoreCase("Hello World"));

        Assert.assertTrue("Column should have an icon", headerCell
                .findElements(By.className("v-icon")).size() > 0);

        Assert.assertTrue(
                "Column should have alignment to the right",
                headerCell.findElements(
                        By.className("v-table-caption-container-align-right"))
                        .size() > 0);
    }

}