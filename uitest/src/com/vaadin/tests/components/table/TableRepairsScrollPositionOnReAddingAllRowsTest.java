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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableRepairsScrollPositionOnReAddingAllRowsTest extends
        MultiBrowserTest {

    @Test
    public void testScrollRepairsAfterReAddingAllRows()
            throws InterruptedException {
        openTestURL();

        WebElement buttonReAddRows = findElement(By.id("button1"));

        scrollUp();

        waitUntilNot(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return $(TableElement.class).first().getCell(49, 0) == null;
            }
        }, 10);

        WebElement row = $(TableElement.class).first().getCell(49, 0);
        int rowLocation = row.getLocation().getY();

        buttonReAddRows.click();

        row = $(TableElement.class).first().getCell(49, 0);
        int newRowLocation = row.getLocation().getY();

        assertThat(
                "Scroll position should be the same as before Re-Adding all rows",
                rowLocation == newRowLocation, is(true));
    }

    private void scrollUp() {
        WebElement actualElement = getDriver().findElement(
                By.className("v-table-body-wrapper"));
        JavascriptExecutor js = new TestBenchCommandExecutor(getDriver(),
                new ImageComparison(), new ReferenceNameGenerator());
        js.executeScript("arguments[0].scrollTop = " + 1200, actualElement);
    }
}
