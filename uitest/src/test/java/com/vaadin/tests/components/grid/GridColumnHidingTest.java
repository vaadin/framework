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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridColumnHidingTest extends MultiBrowserTest {

    @Test
    public void serverHideColumns() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        ButtonElement toggleNameColumn = $(ButtonElement.class).get(0);
        ButtonElement toggleAgeColumn = $(ButtonElement.class).get(1);
        ButtonElement toggleEmailColumn = $(ButtonElement.class).get(2);

        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("Maya", grid.getCell(1, 0).getText());
        Assert.assertEquals("46", grid.getCell(0, 1).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 2).getText());

        toggleAgeColumn.click();
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("Maya", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());

        toggleNameColumn.click();
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 0).getText());

        toggleEmailColumn.click();
        Assert.assertFalse(isElementPresent(By.className("v-grid-cell")));

        toggleAgeColumn.click();
        toggleNameColumn.click();
        toggleEmailColumn.click();
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("46", grid.getCell(0, 1).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 2).getText());
    }

    @Test
    public void clientHideColumns() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "custom age column caption").click();
        Assert.assertEquals("Foo", grid.getCell(0, 0).getText());
        Assert.assertEquals("Maya", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());
        Assert.assertEquals("maya@foo.bar", grid.getCell(1, 1).getText());

        getColumnHidingToggle(grid, "Name").click();
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 0).getText());

        getColumnHidingToggle(grid, "custom age column caption").click();
        Assert.assertEquals("46", grid.getCell(0, 0).getText());
        Assert.assertEquals("18", grid.getCell(1, 0).getText());
        Assert.assertEquals("yeah@cool.com", grid.getCell(0, 1).getText());
        Assert.assertEquals("maya@foo.bar", grid.getCell(1, 1).getText());
    }

    @Test
    public void columnVisibilityChangeListener() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        LabelElement isHiddenLabel = $(LabelElement.class).get(1);
        ButtonElement toggleNameColumn = $(ButtonElement.class).get(0);
        ButtonElement toggleAgeColumn = $(ButtonElement.class).get(1);

        Assert.assertEquals("visibility change label", isHiddenLabel.getText());
        toggleNameColumn.click();
        Assert.assertEquals("true", isHiddenLabel.getText());
        toggleAgeColumn.click();
        Assert.assertEquals("true", isHiddenLabel.getText());
        toggleAgeColumn.click();
        Assert.assertEquals("false", isHiddenLabel.getText());

        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "Name").click();
        Assert.assertEquals("false", isHiddenLabel.getText());
        getColumnHidingToggle(grid, "custom age column caption").click();
        Assert.assertEquals("true", isHiddenLabel.getText());
        getSidebarOpenButton(grid).click();
    }

    @Test
    public void columnTogglesVisibility() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        List<WebElement> elements = getColumnHidingToggles(grid);
        Assert.assertEquals(2, elements.size());
        Assert.assertTrue("Name".equals(elements.get(0).getText()));
        Assert.assertTrue(
                "custom age column caption".equals(elements.get(1).getText()));
    }

    @Test
    public void testShrinkColumnToZeroWithHiddenColumn() {
        openTestURL();

        // hide all
        $(ButtonElement.class).get(3).click();

        ButtonElement toggleNameColumn = $(ButtonElement.class).get(0);
        ButtonElement toggleEmailColumn = $(ButtonElement.class).get(2);

        // Show
        toggleNameColumn.click();
        toggleEmailColumn.click();

        GridElement gridElement = $(GridElement.class).first();

        GridCellElement cell = gridElement.getCell(0, 1);
        dragResizeColumn(1, 0, -cell.getSize().getWidth());
        assertGreaterOrEqual("Cell got too small.", cell.getSize().getWidth(),
                10);
        assertEquals(gridElement.getCell(0, 0).getLocation().getY(),
                gridElement.getCell(0, 1).getLocation().getY());
    }

    protected WebElement getSidebarOpenButton(GridElement grid) {
        List<WebElement> elements = grid
                .findElements(By.className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    protected List<WebElement> getColumnHidingToggles(GridElement grid) {
        WebElement sidebar = getSidebar(grid);
        return sidebar.findElements(By.className("column-hiding-toggle"));
    }

    protected WebElement getColumnHidingToggle(GridElement grid,
            String caption) {
        List<WebElement> elements = getColumnHidingToggles(grid);
        for (WebElement e : elements) {
            if (caption.equalsIgnoreCase(e.getText())) {
                return e;
            }
        }
        return null;
    }

    protected WebElement getSidebar(GridElement grid) {
        List<WebElement> elements = findElements(
                By.className("v-grid-sidebar-popup"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    private void dragResizeColumn(int columnIndex, int posX, int offset) {
        GridElement gridElement = $(GridElement.class).first();

        GridCellElement headerCell = gridElement.getHeaderCell(0, columnIndex);
        Dimension size = headerCell.getSize();
        new Actions(getDriver())
                .moveByOffset(
                        headerCell.getLocation().getX() + size.getWidth()
                                + posX,
                        headerCell.getLocation().getY() + size.getHeight() / 2)
                .clickAndHold().moveByOffset(offset, 0).release().perform();
    }
}
