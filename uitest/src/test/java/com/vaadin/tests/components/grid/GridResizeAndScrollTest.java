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

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridResizeAndScrollTest extends MultiBrowserTest {

    @Test
    public void scrollAndClick() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(49);
        // select a row (click on checkbox)
        grid.getCell(49, 0).click();

        // verify rows are what they should be
        GridCellElement cell = grid.getCell(33, 1);
        String textBefore = cell.getText();
        cell.click();

        Assert.assertEquals("String contents changed on click", textBefore,
                cell.getText());

    }

}
