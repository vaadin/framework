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
package com.vaadin.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.components.grid.basics.GridBasicsTest;

@TestCategory("grid")
public class GridColumnResizeModeTest extends GridBasicsTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testSimpleResizeModeToggle() throws Exception {

        GridElement grid = getGridElement();

        List<WebElement> handles = grid
                .findElements(By.className("v-grid-column-resize-handle"));
        WebElement handle = handles.get(1);

        Actions drag1 = new Actions(getDriver()).moveToElement(handle)
                .clickAndHold();
        Actions drag2 = new Actions(getDriver()).moveByOffset(-50, 0);
        Actions drag3 = new Actions(getDriver()).moveByOffset(100, 0);
        Actions dragEndAction = new Actions(getDriver()).release()
                .moveToElement(grid);

        selectMenuPath("Component", "Columns", "Simple resize mode");
        sleep(250);

        drag1.perform();
        sleep(500);
        drag2.perform();
        sleep(500);
        drag3.perform();
        sleep(500);

        // Make sure we find at least one simple resize mode splitter
        assertElementPresent(
                By.className("v-grid-column-resize-simple-indicator"));

        dragEndAction.perform();

        // Make sure it went away
        assertElementNotPresent(
                By.className("v-grid-column-resize-simple-indicator"));

        // See that we got a resize event
        sleep(500);
        assertTrue("Log shows resize event", getLogRow(0)
                .contains("ColumnResizeEvent: isUserOriginated? true"));

    }

    @Test
    public void testSimpleResizeModeMultipleDrag() {
        GridElement grid = getGridElement();

        List<WebElement> handles = grid
                .findElements(By.className("v-grid-column-resize-handle"));
        WebElement handle = handles.get(1);

        GridCellElement cell = grid.getHeaderCell(0, 1);

        int initialWidth = cell.getSize().getWidth();

        selectMenuPath("Component", "Columns", "Simple resize mode");
        sleep(250);

        drag(handle, 100);
        Assert.assertEquals(initialWidth + 100, cell.getSize().getWidth());

        drag(handle, -100);
        Assert.assertEquals(initialWidth, cell.getSize().getWidth());
    }

    @Test
    public void testResizeReportedWidth() {
        GridElement grid = getGridElement();

        selectMenuPath("Component", "Columns", "Add resize listener");

        List<WebElement> handles = grid
                .findElements(By.className("v-grid-column-resize-handle"));
        WebElement handle = handles.get(1);

        GridCellElement cell = grid.getHeaderCell(0, 1);

        // ANIMATED resize mode
        drag(handle, 100);
        assertTrue(getLogRow(0).contains(
                "Column resized: caption=Column 1, width=" + cell.getSize()
                        .getWidth()));

        drag(handle, -100);
        assertTrue(getLogRow(0).contains(
                "Column resized: caption=Column 1, width=" + cell.getSize()
                        .getWidth()));

        // SIMPLE resize mode
        selectMenuPath("Component", "Columns", "Simple resize mode");
        sleep(250);

        drag(handle, 100);
        assertTrue(getLogRow(0).contains(
                "Column resized: caption=Column 1, width=" + cell.getSize()
                        .getWidth()));

        drag(handle, -100);
        assertTrue(getLogRow(0).contains(
                "Column resized: caption=Column 1, width=" + cell.getSize()
                        .getWidth()));
    }

    private void drag(WebElement handle, int xOffset) {
        new Actions(getDriver()).moveToElement(handle).clickAndHold()
                .moveByOffset(20, 0).moveByOffset(xOffset - 20, 0).release()
                .perform();
        sleep(250);
    }
}
