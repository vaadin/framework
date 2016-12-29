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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridCustomSelectionModelTest extends MultiBrowserTest {

    @Test
    public void testCustomSelectionModel() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        GridCellElement cell = grid.getCell(0, 0);
        assertTrue("First column of Grid should not have an input element",
                cell.findElements(By.tagName("input")).isEmpty());

        assertFalse("Row should not be selected initially",
                grid.getRow(0).isSelected());

        cell.click(5, 5);
        assertTrue("Click should select row", grid.getRow(0).isSelected());
        cell.click(5, 5);
        assertFalse("Click should deselect row", grid.getRow(0).isSelected());

        grid.sendKeys(Keys.SPACE);
        assertTrue("Space should select row", grid.getRow(0).isSelected());
        grid.sendKeys(Keys.SPACE);
        assertFalse("Space should deselect row", grid.getRow(0).isSelected());

        assertNoErrorNotifications();
    }
}
