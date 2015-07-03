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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridCellFocusOnResetSizeTest extends MultiBrowserTest {

    @ServerClass("com.vaadin.tests.widgetset.server.TestWidgetComponent")
    public static class MyGridElement extends GridElement {
    }

    @Test
    public void testCellFocusOnSizeReset() throws IOException {
        openTestURL();

        GridElement grid = $(MyGridElement.class).first();
        int rowIndex = 9;
        grid.getCell(rowIndex, 0).click();
        assertTrue("Row was not focused after click.", grid.getRow(rowIndex)
                .isFocused());

        // Clicking the button decreases size until it is down to 5 rows.
        while (rowIndex > 4) {
            findElement(By.tagName("button")).click();
            assertTrue("Row focus was not moved when size decreased", grid
                    .getRow(--rowIndex).isFocused());
        }

        // Next click increases size back to 10, this should not move focus.
        findElement(By.tagName("button")).click();
        assertTrue("Row focus should not have moved when size increased", grid
                .getRow(4).isFocused());
    }
}
