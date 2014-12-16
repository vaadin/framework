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
package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridClientSelectionTest extends GridBasicClientFeaturesTest {

    @Test
    public void testChangeSelectionMode() {
        openTestURL();

        setSelectionModelNone();
        assertTrue("First column was selection column", getGridElement()
                .getCell(0, 0).getText().equals("(0, 0)"));
        setSelectionModelMulti();
        assertTrue("First column was not selection column", getGridElement()
                .getCell(0, 1).getText().equals("(0, 0)"));
    }

    @Test
    public void testSelectAllCheckbox() {
        openTestURL();

        setSelectionModelMulti();
        selectMenuPath("Component", "DataSource", "Reset with 100 rows of Data");
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();

        for (int i = 0; i < 100; i += 10) {
            assertTrue("Row " + i + " was not selected.", getGridElement()
                    .getRow(i).isSelected());
        }

        header.findElement(By.tagName("input")).click();
        assertFalse("Row 52 was still selected", getGridElement().getRow(52)
                .isSelected());
    }

    @Test
    public void testSelectAllCheckboxWhenChangingModels() {
        openTestURL();

        GridCellElement header;
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(0, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelNone();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));

    }

    private void setSelectionModelMulti() {
        selectMenuPath("Component", "State", "Selection mode", "multi");
    }

    private void setSelectionModelSingle() {
        selectMenuPath("Component", "State", "Selection mode", "single");
    }

    private void setSelectionModelNone() {
        selectMenuPath("Component", "State", "Selection mode", "none");
    }
}
