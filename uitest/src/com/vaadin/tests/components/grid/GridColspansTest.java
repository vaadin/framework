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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridColspansTest extends MultiBrowserTest {

    @Test
    public void testHeaderColSpans() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("5", grid.getHeaderCell(0, 1).getAttribute("colspan"));
        assertEquals("2", grid.getHeaderCell(1, 1).getAttribute("colspan"));
        assertEquals("3", grid.getHeaderCell(1, 3).getAttribute("colspan"));
    }

    @Test
    public void testFooterColSpans() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertEquals("5", grid.getFooterCell(1, 1).getAttribute("colspan"));
        assertEquals("2", grid.getFooterCell(0, 1).getAttribute("colspan"));
        assertEquals("3", grid.getFooterCell(0, 3).getAttribute("colspan"));
    }

    @Test
    public void testActiveHeaderColumnsWithNavigation() throws IOException {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 1).click();

        compareScreen("beforeNavigation");

        for (int i = 1; i <= 6; ++i) {
            assertEquals(true, grid.getFooterCell(1, 1).isActiveHeader());
            assertEquals(i < 3, grid.getFooterCell(0, 1).isActiveHeader());
            assertEquals(i >= 3, grid.getFooterCell(0, 3).isActiveHeader());
            assertEquals(true, grid.getHeaderCell(0, 1).isActiveHeader());
            assertEquals(i < 3, grid.getHeaderCell(1, 1).isActiveHeader());
            assertEquals(i >= 3, grid.getHeaderCell(1, 3).isActiveHeader());
            new Actions(getDriver()).sendKeys(Keys.ARROW_RIGHT).perform();
        }

        compareScreen("afterNavigation");
    }
}
