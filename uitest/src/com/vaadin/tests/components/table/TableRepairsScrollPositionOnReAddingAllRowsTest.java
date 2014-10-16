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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

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

/**
 * Scroll position should be restored when removing and re-adding all rows in
 * Table.
 * 
 * @author Vaadin Ltd
 */
public class TableRepairsScrollPositionOnReAddingAllRowsTest extends
        MultiBrowserTest {

    @Test
    public void testScrollRepairsAfterReAddingAllRows()
            throws InterruptedException {
        openTestURL();

        WebElement row0 = $(TableElement.class).first().getCell(0, 0);
        int rowLocation0 = row0.getLocation().getY();

        // case 1
        scrollUp();

        waitUntilNot(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return $(TableElement.class).first().getCell(48, 0) == null;
            }
        }, 10);

        WebElement row = $(TableElement.class).first().getCell(48, 0);
        int rowLocation = row.getLocation().getY();

        // This button is for re-adding all rows (original itemIds) at once
        // (removeAll() + addAll())
        hitButton("buttonReAddAllViaAddAll");

        row = $(TableElement.class).first().getCell(48, 0);
        int newRowLocation = row.getLocation().getY();

        // ranged check because IE9 consistently misses the mark by 1 pixel
        assertThat(
                "Scroll position should be the same as before Re-Adding rows via addAll()",
                (double) newRowLocation,
                closeTo(rowLocation, row.getSize().height + 1));

        // case 2
        scrollUp();

        waitUntilNot(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return $(TableElement.class).first().getCell(48, 0) == null;
            }
        }, 10);

        row = $(TableElement.class).first().getCell(48, 0);
        rowLocation = row.getLocation().getY();

        // This button is for replacing all rows at once (removeAll() +
        // addAll())
        hitButton("buttonReplaceByAnotherCollectionViaAddAll");

        row = $(TableElement.class).first().getCell(48, 0);
        newRowLocation = row.getLocation().getY();

        // ranged check because IE9 consistently misses the mark by 1 pixel
        assertThat(
                "Scroll position should be the same as before Replacing rows via addAll()",
                (double) newRowLocation,
                closeTo(rowLocation, row.getSize().height + 1));

        // case 3
        // This button is for replacing all rows one by one (removeAll() + add()
        // + add()..)
        hitButton("buttonReplaceByAnotherCollectionViaAdd");

        row = $(TableElement.class).first().getCell(0, 0);
        newRowLocation = row.getLocation().getY();

        // ranged check because IE9 consistently misses the mark by 1 pixel
        assertThat("Scroll position should be 0", (double) newRowLocation,
                closeTo(rowLocation0, 1));

        // case 4
        // This button is for restoring initial list and for scrolling to 0
        // position
        hitButton("buttonRestore");
        scrollUp();

        waitUntilNot(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return $(TableElement.class).first().getCell(48, 0) == null;
            }
        }, 10);

        // This button is for replacing all rows at once but the count of rows
        // is less then first index to scroll
        hitButton("buttonReplaceBySubsetOfSmallerSize");

        row = $(TableElement.class).first().getCell(5, 0);

        newRowLocation = row.getLocation().getY();

        // ranged check because IE9 consistently misses the mark by 1 pixel
        assertThat("Scroll position should be 0", (double) newRowLocation,
                closeTo(rowLocation0, 1));

        // case 5
        // This button is for restoring initial list and for scrolling to 0
        // position
        hitButton("buttonRestore");
        scrollUp();

        waitUntilNot(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return $(TableElement.class).first().getCell(48, 0) == null;
            }
        }, 10);

        row = $(TableElement.class).first().getCell(48, 0);
        rowLocation = row.getLocation().getY();

        // This button is for replacing by whole original sub-set of items plus
        // one new
        hitButton("buttonReplaceByWholeSubsetPlusOnNew");

        row = $(TableElement.class).first().getCell(48, 0);
        newRowLocation = row.getLocation().getY();

        // ranged check because IE9 consistently misses the mark by 1 pixel
        assertThat("Scroll position should be the same as before Replacing",
                (double) newRowLocation,
                closeTo(rowLocation, row.getSize().height + 1));

    }

    private void scrollUp() {
        WebElement actualElement = getDriver().findElement(
                By.className("v-table-body-wrapper"));
        JavascriptExecutor js = new TestBenchCommandExecutor(getDriver(),
                new ImageComparison(), new ReferenceNameGenerator());
        js.executeScript("arguments[0].scrollTop = " + 1205, actualElement);
    }
}
