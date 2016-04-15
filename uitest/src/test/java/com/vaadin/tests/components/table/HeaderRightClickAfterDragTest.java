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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests whether right-click on a column header works after the column is
 * dragged.
 * 
 * @author Vaadin Ltd
 */
public class HeaderRightClickAfterDragTest extends MultiBrowserTest {

    @Test
    public void dragAndRightClick() {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        TableElement table = $(TableElement.class).first();
        TestBenchElement header0 = table.getHeaderCell(0);
        Actions actions = new Actions(getDriver());
        actions.contextClick(header0).perform();

        // check that right-click opened a window
        waitForElementPresent(By.className("v-window"));

        closeWindow();

        actions.clickAndHold(header0).moveToElement(table.getHeaderCell(1))
                .release();

        actions.contextClick(header0).perform();

        // check that right-click still opened a window
        waitForElementPresent(By.className("v-window"));
    }

    private void closeWindow() {
        WindowElement window = $(WindowElement.class).first();
        window.findElement(By.className("v-window-closebox")).click();
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return findElements(By.className("v-window")).isEmpty();
            }
        });
    }
}
