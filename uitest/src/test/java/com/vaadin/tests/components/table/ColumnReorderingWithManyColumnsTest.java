/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ColumnReorderingWithManyColumnsTest extends MultiBrowserTest {
    @Test
    public void testReordering() throws IOException {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        TableHeaderElement sourceCell = table.getHeaderCell(0);
        TableHeaderElement targetCell = table.getHeaderCell(10);
        drag(sourceCell, targetCell);
        WebElement markedElement = table
                .findElement(By.className("v-table-focus-slot-right"));
        String markedColumnName = markedElement.findElement(By.xpath(".."))
                .getText();
        assertEquals("col-9", markedColumnName.toLowerCase());
    }

    private void drag(WebElement source, WebElement target) {
        Actions actions = new Actions(getDriver());
        actions.moveToElement(source, 10, 10);
        actions.clickAndHold(source);
        actions.moveToElement(target, 10, 10);
        actions.perform();
    }
}
