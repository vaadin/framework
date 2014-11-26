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
package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.components.grid.GridElement.GridCellElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridHeaderStyleNamesTest extends SingleBrowserTest {

    private GridElement grid;

    @Before
    public void findGridCells() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    private GridCellElement getMergedHeaderCell() {
        return grid.getHeaderCell(0, 3);
    }

    private GridCellElement getAgeFooterCell() {
        return grid.getFooterCell(0, 2);
    }

    @Test
    public void styleNamesCanBeAddedAndRemoved() {
        ButtonElement toggleStyles = $(ButtonElement.class).caption(
                "Toggle styles").first();

        assertStylesSet(true);
        toggleStyles.click();
        assertStylesSet(false);
        toggleStyles.click();
        assertStylesSet(true);
    }

    private void assertStylesSet(boolean set) {
        if (set) {
            assertHasStyleName(
                    "Footer cell should have the assigned 'age-footer' class name",
                    getAgeFooterCell(), "age-footer");
            assertHasStyleName(
                    "Header cell should have the assigned 'age' class name",
                    getAgeHeaderCell(), "age");
            assertHasStyleName(
                    "The merged header cell should have the assigned 'city-country' class name",
                    getMergedHeaderCell(), "city-country");
        } else {
            assertHasNotStyleName(
                    "Footer cell should not have the removed 'age-footer' class name",
                    getAgeFooterCell(), "age-footer");
            assertHasNotStyleName(
                    "Header cell should not have the removed 'age' class name",
                    getAgeHeaderCell(), "age");
            assertHasNotStyleName(
                    "Ther merged header cell should not have the removed 'city-country' class name",
                    getMergedHeaderCell(), "city-country");
        }
        assertHasStyleName(
                "The default v-grid-cell style name should not be removed from the header cell",
                getAgeHeaderCell(), "v-grid-cell");
        assertHasStyleName(
                "The default v-grid-cell style name should not be removed from the footer cell",
                getAgeFooterCell(), "v-grid-cell");
        assertHasStyleName(
                "The default v-grid-cell style name should not be removed from the merged header cell",
                getMergedHeaderCell(), "v-grid-cell");

    }

    private WebElement getAgeHeaderCell() {
        return grid.getHeaderCell(1, 2);
    }

    private void assertHasStyleName(String message, WebElement element,
            String stylename) {
        if (!hasCssClass(element, stylename)) {
            Assert.fail(message);
        }
    }

    private void assertHasNotStyleName(String message, WebElement element,
            String stylename) {
        if (hasCssClass(element, stylename)) {
            Assert.fail(message);
        }
    }

}
