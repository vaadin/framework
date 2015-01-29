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
package com.vaadin.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridItemClickTest extends GridBasicFeaturesTest {

    @Test
    public void testItemClick() {
        openTestURL();

        selectMenuPath("Component", "State", "ItemClickListener");

        GridCellElement cell = getGridElement().getCell(3, 2);
        new Actions(getDriver()).moveToElement(cell).click().perform();

        assertTrue("No click in log", logContainsText(itemClickOn(3, 2, false)));
    }

    @Test
    public void testItemDoubleClick() {
        openTestURL();

        selectMenuPath("Component", "State", "ItemClickListener");

        GridCellElement cell = getGridElement().getCell(3, 2);
        new Actions(getDriver()).moveToElement(cell).doubleClick().perform();

        assertTrue("No double click in log",
                logContainsText(itemClickOn(3, 2, true)));
    }

    private String itemClickOn(int row, int column, boolean dblClick) {
        return "Item " + (dblClick ? "double " : "") + "click on Column "
                + column + ", item " + row;
    }
}
