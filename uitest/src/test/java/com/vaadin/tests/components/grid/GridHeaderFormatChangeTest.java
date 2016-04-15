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
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridHeaderFormatChangeTest extends MultiBrowserTest {

    @Test
    public void testHeaderRetainsSelectAllForColumnRemoval() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Assert that we do not have the select all checkbox
        Assert.assertTrue(
                "Found input in header even though none should exist.", grid
                        .getHeader().findElements(By.tagName("input"))
                        .isEmpty());

        // Set grid into multiselection mode
        toggleSelectionMode();

        // Assert that we now have a select all checkbox in the header
        Assert.assertFalse("Expected one input field in header", grid
                .getHeader().findElements(By.tagName("input")).isEmpty());

        // Hide the firstName column from the grid.
        toggleFirstName();

        // Assert that we still have the select all checkbox in the header.
        Assert.assertFalse("Header was missing checkbox after hiding column",
                grid.getHeader().findElements(By.tagName("input")).isEmpty());

        // Show the firstName column.
        toggleFirstName();

        // Assert that we still have the select all checkbox in the header.
        Assert.assertFalse(
                "Header was missing checkbox after bringing back column", grid
                        .getHeader().findElements(By.tagName("input"))
                        .isEmpty());
    }

    @Test
    public void testHeaderRetainsSelectAllForJoinColumnAdd() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Assert that we do not have the select all checkbox
        Assert.assertTrue(
                "Found input in header even though none should exist.", grid
                        .getHeader().findElements(By.tagName("input"))
                        .isEmpty());

        // Set grid into multiselection mode
        toggleSelectionMode();

        // Assert that we now have a select all checkbox in the header
        Assert.assertFalse("Expected one input field in header", grid
                .getHeader().findElements(By.tagName("input")).isEmpty());

        // Add Join columns header
        toggleJoin();

        // Assert that we still have the select all checkbox in the header.
        Assert.assertFalse("Header was missing checkbox after hiding column",
                grid.getHeader().findElements(By.tagName("input")).isEmpty());

        // remove Join Columns header
        toggleJoin();

        // Assert that we still have the select all checkbox in the header.
        Assert.assertFalse(
                "Header was missing checkbox after bringing back column", grid
                        .getHeader().findElements(By.tagName("input"))
                        .isEmpty());
    }

    @Test
    public void selectAllShouldKeepState() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Assert that we do not have the select all checkbox
        Assert.assertTrue(
                "Found input in header even though none should exist.", grid
                        .getHeader().findElements(By.tagName("input"))
                        .isEmpty());

        // Set grid into multiselection mode
        toggleSelectionMode();

        // Assert that we now have a select all checkbox in the header
        Assert.assertFalse("Should not be selected after adding", grid
                .getHeader().findElement(By.tagName("input")).isSelected());

        grid.getHeader().findElement(By.tagName("input")).click();

        // Assert that checkbox is checked
        assertSelectAllChecked(
                "Not selected even though we just clicked selection", grid);

        // Hide the firstName column from the grid.
        toggleFirstName();

        // Assert that checkbox is still checked
        assertSelectAllChecked("Selection disappeared after removing column",
                grid);

        // Show the firstName column.
        toggleFirstName();

        // Assert that checkbox is still checked
        assertSelectAllChecked("Selection disappeared after adding column",
                grid);

    }

    private void assertSelectAllChecked(String message, GridElement grid) {
        Assert.assertTrue(message,
                grid.getHeader().findElement(By.tagName("input")).isSelected());
    }

    private void toggleSelectionMode() {
        $(ButtonElement.class).id("selection_mode").click();
    }

    private void toggleFirstName() {
        $(ButtonElement.class).id("show_hide").click();
    }

    private void toggleJoin() {
        $(ButtonElement.class).id("join").click();
    }
}
