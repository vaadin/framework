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

import static com.vaadin.tests.components.table.ExpandingContainerVisibleRowRaceCondition.TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ExpandingContainerVisibleRowRaceConditionTest extends
        MultiBrowserTest {

    private static final int ROW_HEIGHT = 20;

    @Test
    public void testScrollingWorksWithoutJumpingWhenItemSetChangeOccurs() {
        openTestURL();
        sleep(1000);

        WebElement table = vaadinElementById(TABLE);
        assertFirstRowIdIs("ROW #120");

        testBenchElement(table.findElement(By.className("v-scrollable")))
                .scroll(320 * ROW_HEIGHT);
        sleep(1000);

        assertRowIdIsInThePage("ROW #330");
        assertScrollPositionIsNotVisible();
    }

    @Override
    protected void sleep(int milliseconds) {
        try {
            super.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void assertFirstRowIdIs(String expected) {
        List<WebElement> cellsOfFirstColumn = getCellsOfFirstColumn();
        WebElement first = cellsOfFirstColumn.get(0);
        assertEquals(expected, first.getText());
    }

    private void assertRowIdIsInThePage(String expected) {
        List<WebElement> cellsOfFirstColumn = getCellsOfFirstColumn();
        for (WebElement rowId : cellsOfFirstColumn) {
            if (expected.equals(rowId.getText())) {
                return;
            }
        }
        fail("Expected row was not found");
    }

    private void assertScrollPositionIsNotVisible() {
        WebElement table = vaadinElementById(TABLE);
        WebElement scrollPosition = table.findElement(By
                .className("v-table-scrollposition"));
        assertFalse(scrollPosition.isDisplayed());
    }

    private List<WebElement> getCellsOfFirstColumn() {
        WebElement table = vaadinElementById(TABLE);
        List<WebElement> firstCellOfRows = table.findElements(By
                .cssSelector(".v-table-table tr > td"));
        return firstCellOfRows;
    }
}
