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
package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 * Tests Table tooltips with various settings.
 * 
 * @author Vaadin Ltd
 */
public class TableItemDescriptionGeneratorUITest extends TooltipTest {

    @Test
    public void testDescriptions() throws Exception {
        openTestURL();

        checkTooltipNotPresent();

        TableElement table = $(TableElement.class).first();
        List<CheckBoxElement> checkboxes = $(CheckBoxElement.class).all();
        assertEquals(3, checkboxes.size());

        // check text description
        TestBenchElement cell_1_0 = table.getCell(1, 0);
        cell_1_0.showTooltip();
        checkTooltip("Cell description item 1, Text");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check button description
        TestBenchElement cell_1_1 = table.getCell(1, 1);
        cell_1_1.showTooltip();
        checkTooltip("Button 1 description");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check textfield's description
        TestBenchElement cell_1_2 = table.getCell(1, 2);
        cell_1_2.showTooltip();
        checkTooltip("Textfield's own description");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // uncheck component tooltips
        checkboxes.get(0).findElement(By.tagName("input")).click();

        // check text description
        cell_1_0 = table.getCell(1, 0);
        cell_1_0.showTooltip();
        checkTooltip("Cell description item 1, Text");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check button description
        cell_1_1 = table.getCell(1, 1);
        cell_1_1.showTooltip();
        checkTooltip("Cell description item 1, Component");

        // move somewhere without a description
        new Actions(getDriver()).moveToElement(checkboxes.get(2)).perform();
        sleep(1000);
        checkTooltipNotPresent();

        // check textfield's description
        cell_1_2 = table.getCell(1, 2);
        cell_1_2.showTooltip();
        checkTooltip("Cell description item 1, Generated component");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check component tooltips
        checkboxes.get(0).findElement(By.tagName("input")).click();
        // uncheck cell tooltips
        checkboxes.get(1).findElement(By.tagName("input")).click();

        // check text description
        cell_1_0 = table.getCell(1, 0);
        cell_1_0.showTooltip();
        checkTooltip("Row description item 1");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check button description
        cell_1_1 = table.getCell(1, 1);
        cell_1_1.showTooltip();
        checkTooltip("Button 1 description");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check textfield's description
        cell_1_2 = table.getCell(1, 2);
        cell_1_2.showTooltip();
        checkTooltip("Textfield's own description");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // uncheck component tooltips
        checkboxes.get(0).findElement(By.tagName("input")).click();

        // check text description
        cell_1_0 = table.getCell(1, 0);
        cell_1_0.showTooltip();
        checkTooltip("Row description item 1");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check button description
        cell_1_1 = table.getCell(1, 1);
        cell_1_1.showTooltip();
        checkTooltip("Row description item 1");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();

        // check textfield's description
        cell_1_2 = table.getCell(1, 2);
        cell_1_2.showTooltip();
        checkTooltip("Row description item 1");

        // move somewhere without a description
        checkboxes.get(2).showTooltip();
        checkTooltipNotPresent();
    }

    @Test
    public void testPosition() throws Exception {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        List<CheckBoxElement> checkboxes = $(CheckBoxElement.class).all();
        assertEquals(3, checkboxes.size());

        TestBenchElement cell_3_0 = table.getCell(3, 0);

        // move to the center of the cell
        new Actions(getDriver()).moveToElement(cell_3_0).perform();
        sleep(1000);

        // ensure the tooltip is present
        checkTooltip("Cell description item 3, Text");
        clearTooltip();

        // move outside the cell
        new Actions(getDriver()).moveToElement(checkboxes.get(2)).perform();

        // move to the corner of the cell
        new Actions(getDriver()).moveToElement(cell_3_0, 0, 0).perform();
        sleep(1000);

        // ensure the tooltip is present
        checkTooltip("Cell description item 3, Text");
        clearTooltip();

        // uncheck cell tooltips
        checkboxes.get(1).findElement(By.tagName("input")).click();

        TestBenchElement cell_1_1 = table.getCell(1, 1);

        // move to the center of the cell
        new Actions(getDriver()).moveToElement(cell_1_1).perform();
        sleep(1000);

        // ensure the tooltip is present
        checkTooltip("Button 1 description");
        clearTooltip();

        // move to the corner of the element, outside of the button
        new Actions(getDriver()).moveToElement(cell_1_1, 0, 0).perform();
        sleep(1000);

        // ensure the tooltip is present
        checkTooltip("Row description item 1");
        clearTooltip();

        // check cell tooltips
        checkboxes.get(1).findElement(By.tagName("input")).click();

        TestBenchElement cell_4_2 = table.getCell(4, 2);

        // move to the center of the cell
        new Actions(getDriver()).moveToElement(cell_4_2).perform();
        sleep(1000);

        // ensure the tooltip is present
        checkTooltip("Textfield's own description");
        clearTooltip();

        // move to the corner of the element, outside of the textfield
        new Actions(getDriver()).moveToElement(cell_4_2, 0, 0).perform();
        sleep(1000);

        // ensure the tooltip is present
        checkTooltip("Cell description item 4, Generated component");
        clearTooltip();
    }

}
