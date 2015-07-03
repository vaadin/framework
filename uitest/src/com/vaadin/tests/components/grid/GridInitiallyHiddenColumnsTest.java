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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridInitiallyHiddenColumnsTest extends SingleBrowserTest {

    @Test
    public void ensureCorrectlyRendered() {
        openTestURL("debug");
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals("Rowling", grid.getCell(0, 0).getText());
        Assert.assertEquals("Scott", grid.getCell(1, 0).getText());

        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "First Name").click();
        getColumnHidingToggle(grid, "Age").click();
        getSidebarOpenButton(grid).click();

        Assert.assertEquals("Umberto", grid.getCell(0, 0).getText());
        Assert.assertEquals("Rowling", grid.getCell(0, 1).getText());
        Assert.assertEquals("25", grid.getCell(0, 2).getText());
        Assert.assertEquals("Dan", grid.getCell(1, 0).getText());
        Assert.assertEquals("Scott", grid.getCell(1, 1).getText());
        Assert.assertEquals("54", grid.getCell(1, 2).getText());

    }

    protected WebElement getSidebarOpenButton(GridElement grid) {
        List<WebElement> elements = grid.findElements(By
                .className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    /**
     * Returns the toggle inside the sidebar for hiding the column at the given
     * index, or null if not found.
     */
    protected WebElement getColumnHidingToggle(GridElement grid, String caption) {
        WebElement sidebar = getSidebar(grid);
        List<WebElement> elements = sidebar.findElements(By
                .className("column-hiding-toggle"));
        for (WebElement e : elements) {
            if (caption.equalsIgnoreCase(e.getText())) {
                return e;
            }
        }
        return null;
    }

    protected WebElement getSidebar(GridElement grid) {
        List<WebElement> elements = findElements(By.className("v-grid-sidebar"));
        return elements.isEmpty() ? null : elements.get(0);
    }

}
