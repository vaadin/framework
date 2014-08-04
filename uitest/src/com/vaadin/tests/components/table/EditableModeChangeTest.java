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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that editing and selecting work correctly.
 * 
 * @author Vaadin Ltd
 */
public class EditableModeChangeTest extends MultiBrowserTest {

    @Test
    public void testNotification() throws IOException, InterruptedException {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        // check original value
        TestBenchElement cell_1_0 = table.getCell(1, 0);
        assertEquals(
                "original value not found, wrong cell or contents (1st column of the 2nd row expected)",
                "Teppo", cell_1_0.getText());

        // double-click to edit cell contents
        cell_1_0.click();
        new Actions(getDriver()).doubleClick(cell_1_0).build().perform();
        sleep(100);

        // fetch the updated cell
        WebElement textField = table.getCell(1, 0).findElement(
                By.className("v-textfield"));
        assertEquals(
                "original value not found, wrong cell or contents (1st column of the 2nd row expected)",
                "Teppo", textField.getAttribute("value"));

        // update value
        textField.clear();
        textField.sendKeys("baa");

        // click on another row
        table.getCell(0, 1).click();

        // check the value got updated correctly
        assertEquals(
                "updated value not found, wrong cell or contents (1st column of the 2nd row expected)",
                "baa", table.getCell(1, 0).getText());

        // check that selection got updated correctly
        List<WebElement> selected = table.findElement(
                By.className("v-table-body")).findElements(
                By.className("v-selected"));
        assertEquals(1, selected.size());

        WebElement content = selected.get(0).findElement(
                By.className("v-table-cell-wrapper"));
        assertEquals(
                "expected value not found, wrong cell or contents (1st column of the 1st row expected)",
                "Teemu", content.getText());

        compareScreen("selection");
    }

}
