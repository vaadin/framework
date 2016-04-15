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
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test whether adding the first item to a table calculates the table width
 * correctly
 * 
 * @author Vaadin Ltd
 */
public class TableWidthItemRemoveTest extends MultiBrowserTest {
    @Test
    public void testWidthResizeOnItemAdd() {
        openTestURL();

        WebElement populateButton = driver.findElement(By
                .vaadin("//Button[caption=\"Populate\"]"));
        WebElement table = driver.findElement(By
                .vaadin("//Table[caption=\"My table\"]"));
        int original_width = table.getSize().getWidth();
        populateButton.click();
        Assert.assertTrue("Width changed on item add.", original_width == table
                .getSize().getWidth());
    }

}
