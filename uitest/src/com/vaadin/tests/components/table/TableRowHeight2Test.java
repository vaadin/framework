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

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that rows are completely visible and clicking buttons doesn't change
 * anything.
 * 
 * @author Vaadin Ltd
 */
public class TableRowHeight2Test extends MultiBrowserTest {

    @Test
    public void testRowHeights() throws IOException {
        openTestURL();

        compareScreen("initial");

        TableElement table = $(TableElement.class).first();
        List<WebElement> rows = table.findElement(By.className("v-table-body"))
                .findElements(By.tagName("tr"));

        rows.get(0).findElements(By.className("v-button")).get(1).click();
        rows.get(1).findElements(By.className("v-button")).get(1).click();

        compareScreen("after");
    }

}
