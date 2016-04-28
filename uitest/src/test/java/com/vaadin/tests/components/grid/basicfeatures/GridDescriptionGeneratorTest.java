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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

public class GridDescriptionGeneratorTest extends GridBasicFeaturesTest {

    @Test
    public void testCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Cell description generator");

        getGridElement().getCell(1, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 1, column 0",
                tooltipText);

        getGridElement().getCell(1, 1).showTooltip();
        assertTrue("Tooltip should not be present in cell (1, 1) ",
                findElement(By.className("v-tooltip-text")).getText().isEmpty());
    }

    @Test
    public void testRowDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator");

        getGridElement().getCell(5, 3).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);

        getGridElement().getCell(15, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row 15", tooltipText);
    }

    @Test
    public void testRowAndCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator");
        selectMenuPath("Component", "State", "Cell description generator");

        getGridElement().getCell(5, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 5, column 0",
                tooltipText);

        getGridElement().getCell(5, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);
    }

}
