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
package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TreeTableElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Tests TreeTable tooltips with various settings.
 * 
 * @author Vaadin Ltd
 */
public class TreeTableItemDescriptionGeneratorUITest extends TooltipTest {

    @Test
    public void testDescriptions() throws Exception {
        openTestURL();

        checkTooltipNotPresent();

        TreeTableElement treeTable = $(TreeTableElement.class).first();
        List<CheckBoxElement> checkboxes = $(CheckBoxElement.class).all();
        assertEquals(3, checkboxes.size());

        // check text description
        TestBenchElement cell_1_0 = treeTable.getCell(1, 0);
        checkTooltip(cell_1_0, "Cell description item 1, Text");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check button description
        TestBenchElement cell_1_1 = treeTable.getCell(1, 1);
        checkTooltip(cell_1_1, "Button 1 description");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check textfield's description
        TestBenchElement cell_1_2 = treeTable.getCell(1, 2);
        checkTooltip(cell_1_2, "Textfield's own description");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // uncheck component tooltips
        checkboxes.get(0).findElement(By.tagName("input")).click();

        // check text description
        cell_1_0 = treeTable.getCell(1, 0);
        checkTooltip(cell_1_0, "Cell description item 1, Text");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check button description
        cell_1_1 = treeTable.getCell(1, 1);
        checkTooltip(cell_1_1, "Cell description item 1, Component");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check textfield's description
        cell_1_2 = treeTable.getCell(1, 2);
        checkTooltip(cell_1_2, "Cell description item 1, Generated component");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check component tooltips
        checkboxes.get(0).findElement(By.tagName("input")).click();
        // uncheck cell tooltips
        checkboxes.get(1).findElement(By.tagName("input")).click();

        // check text description
        cell_1_0 = treeTable.getCell(1, 0);
        checkTooltip(cell_1_0, "Row description item 1");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check button description
        cell_1_1 = treeTable.getCell(1, 1);
        checkTooltip(cell_1_1, "Button 1 description");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check textfield's description
        cell_1_2 = treeTable.getCell(1, 2);
        checkTooltip(cell_1_2, "Textfield's own description");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // uncheck component tooltips
        checkboxes.get(0).findElement(By.tagName("input")).click();

        // check text description
        cell_1_0 = treeTable.getCell(1, 0);
        checkTooltip(cell_1_0, "Row description item 1");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check button description
        cell_1_1 = treeTable.getCell(1, 1);
        checkTooltip(cell_1_1, "Row description item 1");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);

        // check textfield's description
        cell_1_2 = treeTable.getCell(1, 2);
        checkTooltip(cell_1_2, "Row description item 1");

        // move somewhere without a description
        checkTooltip(checkboxes.get(2), null);
    }

}
