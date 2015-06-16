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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDragSelectionWhileScrolledTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Test
    public void testDragSelect() throws IOException {
        openTestURL();

        // Scroll grid to view
        GridElement grid = $(GridElement.class).first();
        ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].scrollIntoView(true);", grid);

        // Drag select 2 rows
        new Actions(getDriver()).moveToElement(grid.getCell(3, 0), 5, 5)
                .clickAndHold().moveToElement(grid.getCell(2, 0), 5, 5)
                .release().perform();

        // Assert only those are selected.
        assertTrue("Row 3 should be selected", grid.getRow(3).isSelected());
        assertTrue("Row 2 should be selected", grid.getRow(2).isSelected());
        assertFalse("Row 4 should not be selected", grid.getRow(4).isSelected());
        assertFalse("Row 1 should not be selected", grid.getRow(1).isSelected());
    }
}
