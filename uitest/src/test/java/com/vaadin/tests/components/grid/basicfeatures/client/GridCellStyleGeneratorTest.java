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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import com.vaadin.tests.widgetset.client.grid.GridBasicClientFeaturesWidget;

public class GridCellStyleGeneratorTest extends GridBasicClientFeaturesTest {

    @Test
    public void testStyleNameGeneratorScrolling() throws Exception {
        openTestURL();

        selectCellStyleNameGenerator(GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_ROW_INDEX);

        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);

        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));

        // Scroll down and verify that the old elements don't have the
        // stylename any more

        getGridElement().getRow(350);

        Assert.assertFalse(hasCssClass(row2, "2"));
        Assert.assertFalse(hasCssClass(cell4_2, "4_2"));
    }

    @Test
    public void testDisableStyleNameGenerator() throws Exception {
        openTestURL();

        selectCellStyleNameGenerator(GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_ROW_INDEX);

        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);

        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));

        // Disable the generator and check again
        selectCellStyleNameGenerator(GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_NONE);
        selectRowStyleNameGenerator(GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_NONE);

        Assert.assertFalse(hasCssClass(row2, "2"));
        Assert.assertFalse(hasCssClass(cell4_2, "4_2"));
    }

    @Test
    public void testChangeStyleNameGenerator() throws Exception {
        openTestURL();

        selectCellStyleNameGenerator(GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_ROW_INDEX);

        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);

        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));

        // Change the generator and check again
        selectRowStyleNameGenerator(GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_NONE);
        selectCellStyleNameGenerator(GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_SIMPLE);

        // Old styles removed?
        Assert.assertFalse(hasCssClass(row2, "2"));
        Assert.assertFalse(hasCssClass(cell4_2, "4_2"));

        // New style present?
        Assert.assertTrue(hasCssClass(cell4_2, "two"));
    }

    @Test
    public void testStyleNameGeneratorChangePrimary() throws Exception {
        openTestURL();

        selectCellStyleNameGenerator(GridBasicClientFeaturesWidget.CELL_STYLE_GENERATOR_COL_INDEX);
        selectRowStyleNameGenerator(GridBasicClientFeaturesWidget.ROW_STYLE_GENERATOR_ROW_INDEX);

        // Just verify that change was effective
        GridRowElement row2 = getGridElement().getRow(2);
        GridCellElement cell4_2 = getGridElement().getCell(4, 2);

        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));

        // Change primary stylename
        selectMenuPath("Component", "State", "Primary Stylename", "v-escalator");

        // Styles still present
        Assert.assertTrue(hasCssClass(row2, "2"));
        Assert.assertTrue(hasCssClass(cell4_2, "4_2"));

        // New styles present?
        Assert.assertFalse(hasCssClass(row2, "v-escalator-row-2"));
        Assert.assertFalse(hasCssClass(cell4_2, "v-escalator-cell-content-4_2"));
    }

    private void selectCellStyleNameGenerator(String name) {
        selectMenuPath("Component", "State", "Cell style generator", name);
    }

    private void selectRowStyleNameGenerator(String name) {
        selectMenuPath("Component", "State", "Row style generator", name);
    }
}
