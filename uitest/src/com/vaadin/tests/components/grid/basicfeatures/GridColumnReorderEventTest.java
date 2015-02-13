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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.annotations.TestCategory;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridColumnReorderEventTest extends GridBasicClientFeaturesTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void columnReorderEventTriggered() {
        final int firstIndex = 3;
        final int secondIndex = 4;
        final String firstHeaderText = getGridElement().getHeaderCell(0,
                firstIndex).getText();
        final String secondHeaderText = getGridElement().getHeaderCell(0,
                secondIndex).getText();
        selectMenuPath("Component", "Internals", "Listeners",
                "Add ColumnReorder listener");
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        // columns 3 and 4 should have swapped to 4 and 3
        GridCellElement headerCell = getGridElement().getHeaderCell(0,
                firstIndex);
        assertEquals(secondHeaderText, headerCell.getText());
        headerCell = getGridElement().getHeaderCell(0, secondIndex);
        assertEquals(firstHeaderText, headerCell.getText());

        // the reorder event should have typed the order to this label
        WebElement columnReorderElement = findElement(By.id("columnreorder"));
        int eventIndex = Integer.parseInt(columnReorderElement
                .getAttribute("columns"));
        assertEquals(1, eventIndex);

        // trigger another event
        selectMenuPath("Component", "Columns", "Column " + secondIndex,
                "Move column left");
        columnReorderElement = findElement(By.id("columnreorder"));
        eventIndex = Integer.parseInt(columnReorderElement
                .getAttribute("columns"));
        assertEquals(2, eventIndex);
    }
}
