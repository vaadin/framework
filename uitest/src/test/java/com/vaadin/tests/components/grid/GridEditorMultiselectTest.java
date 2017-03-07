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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridEditorMultiselectTest extends MultiBrowserTest {

    @Test
    public void testSelectCheckboxesDisabled() {
        openTestURL();
        GridElement grid = openEditor();
        assertCheckboxesEnabled(grid, false);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnSave() {
        openTestURL();
        GridElement grid = openEditor();
        grid.getEditor().save();
        waitForElementNotPresent(By.className("v-grid-editor-cells"));
        assertCheckboxesEnabled(grid, true);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnCancel() {
        openTestURL();
        GridElement grid = openEditor();
        grid.getEditor().cancel();
        assertCheckboxesEnabled(grid, true);
    }

    private GridElement openEditor() {
        GridElement grid = $(GridElement.class).first();
        grid.getRow(0).doubleClick();
        Assert.assertTrue("Grid editor should be displayed.",
                grid.getEditor().isDisplayed());
        return grid;
    }

    private void assertCheckboxesEnabled(GridElement grid, boolean isEnabled) {
        List<WebElement> checkboxes = grid
                .findElements(By.xpath("//input[@type='checkbox']"));
        for (WebElement checkbox : checkboxes) {
            Assert.assertEquals(
                    "Select checkboxes should be "
                            + (isEnabled ? "enabled" : "disabled"),
                    isEnabled, checkbox.isEnabled());
        }
    }
}
