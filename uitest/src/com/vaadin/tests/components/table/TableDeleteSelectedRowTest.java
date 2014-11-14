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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check selected rows in multiselect table after deletion.
 * 
 * @author Vaadin Ltd
 */
public class TableDeleteSelectedRowTest extends MultiBrowserTest {

    @Test
    public void deleteSelectedRows() {
        openTestURL();

        // Select row in the table
        findElement(By.className("v-table-row-odd")).click();

        // Delete selected row
        findElement(By.className("delete")).click();

        WebElement selectedSize = findElement(By.className("selected-rows"));
        int size = Integer.parseInt(selectedSize.getText());

        Assert.assertEquals(
                "Non empty collection of selected rows after remove via container",
                0, size);

        // Reset table and set multiselect mode
        findElement(By.className("multiselect")).click();

        // Select row in the table
        findElement(By.className("v-table-row-odd")).click();

        // Delete selected row
        findElement(By.className("delete")).click();

        selectedSize = findElement(By.className("selected-rows"));
        size = Integer.parseInt(selectedSize.getText());

        Assert.assertEquals(
                "Non empty collection of selected rows after remove via container",
                0, size);
    }
}
