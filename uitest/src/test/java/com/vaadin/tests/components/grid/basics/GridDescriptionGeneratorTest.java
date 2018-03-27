/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.components.grid.basics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

public class GridDescriptionGeneratorTest extends GridBasicsTest {

    @Test
    public void testPreformattedCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Cell description generator",
                "Preformatted");

        getGridElement().getCell(1, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row <b>1</b>, Column 0",
                tooltipText);

        getGridElement().getCell(1, 1).showTooltip();
        assertTrue("Tooltip should not be present in cell (1, 1) ",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }

    @Test
    public void testHTMLCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Cell description generator",
                "HTML");

        getGridElement().getCell(1, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 1, Column 0",
                tooltipText);

        getGridElement().getCell(1, 1).showTooltip();
        assertTrue("Tooltip should not be present in cell (1, 1) ",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }

    @Test
    public void testHTMLCellDescriptionOnEvenRows() {
        openTestURL();
        selectMenuPath("Component", "State", "Cell description generator",
                "Even rows HTML");

        getGridElement().getCell(0, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 0, Column 0",
                tooltipText);

        getGridElement().getCell(1, 0).showTooltip();
        assertTrue("Tooltip should not be present in cell (1, 0) ",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }

    @Test
    public void testPreformattedRowDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator",
                "Preformatted");

        getGridElement().getCell(5, 3).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Row tooltip for row <b>5</b>",
                tooltipText);

        getGridElement().getCell(15, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row <b>15</b>",
                tooltipText);
    }

    @Test
    public void testHTMLRowDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator",
                "HTML");

        getGridElement().getCell(5, 3).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);

        getGridElement().getCell(15, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row 15", tooltipText);
    }

    @Test
    public void testHTMLRowDescriptionOnEvenRows() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator",
                "Even rows HTML");

        getGridElement().getCell(4, 3).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Row tooltip for row 4", tooltipText);

        getGridElement().getCell(5, 3).showTooltip();
        assertTrue("Tooltip should not be present on row 5",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }

    @Test
    public void testRowAndCellDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator",
                "HTML");
        selectMenuPath("Component", "State", "Cell description generator",
                "HTML");

        getGridElement().getCell(5, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 5, Column 0",
                tooltipText);

        getGridElement().getCell(5, 3).showTooltip();
        tooltipText = findElement(By.className("v-tooltip-text")).getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);
    }

    @Test
    public void testRemoveCellDescription() {
        selectMenuPath("Component", "State", "Cell description generator",
                "HTML");

        getGridElement().getCell(1, 0).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Cell tooltip for row 1, Column 0",
                tooltipText);

        selectMenuPath("Component", "State", "Cell description generator",
                "Remove descriptions");

        getGridElement().getCell(1, 0).showTooltip();
        assertTrue("Tooltip should not be present in cell (1, 0) ",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }

    @Test
    public void testRemoveRowDescription() {
        openTestURL();
        selectMenuPath("Component", "State", "Row description generator",
                "HTML");

        getGridElement().getCell(5, 3).showTooltip();
        String tooltipText = findElement(By.className("v-tooltip-text"))
                .getText();
        assertEquals("Tooltip text", "Row tooltip for row 5", tooltipText);

        selectMenuPath("Component", "State", "Row description generator",
                "Remove descriptions");

        getGridElement().getCell(5, 3).showTooltip();
        assertTrue("Tooltip should not be present on row 5",
                findElement(By.className("v-tooltip-text")).getText()
                        .isEmpty());
    }
}
