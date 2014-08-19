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

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests sorting labels in table.
 * 
 * @author Vaadin Ltd
 */
public class SortLabelsInTableTest extends MultiBrowserTest {

    @Test
    public void testSorting() {
        openTestURL();

        TableElement table = $(TableElement.class).first();

        // check unsorted
        assertEquals("Text 0", table.getCell(0, 0).getText());
        assertEquals("Label 0", table.getCell(0, 1).getText());
        assertEquals("Text 14", table.getCell(14, 0).getText());
        assertEquals("Label 14", table.getCell(14, 1).getText());

        // sort by first column (ascending order)
        table.getHeaderCell(0).click();

        // check sorted
        assertEquals("Text 0", table.getCell(0, 0).getText());
        assertEquals("Label 0", table.getCell(0, 1).getText());
        assertEquals("Text 10", table.getCell(2, 0).getText());
        assertEquals("Label 10", table.getCell(2, 1).getText());
        assertEquals("Text 19", table.getCell(11, 0).getText());
        assertEquals("Label 19", table.getCell(11, 1).getText());
        assertEquals("Text 4", table.getCell(14, 0).getText());
        assertEquals("Label 4", table.getCell(14, 1).getText());

        // sort by first column (descending order)
        table.getHeaderCell(0).click();

        // check sorted
        assertEquals("Text 9", table.getCell(0, 0).getText());
        assertEquals("Label 9", table.getCell(0, 1).getText());
        assertEquals("Text 19", table.getCell(8, 0).getText());
        assertEquals("Label 19", table.getCell(8, 1).getText());
        assertEquals("Text 13", table.getCell(14, 0).getText());
        assertEquals("Label 13", table.getCell(14, 1).getText());

        // sort by second column (descending order)
        table.getHeaderCell(1).click();

        // check no change
        assertEquals("Text 9", table.getCell(0, 0).getText());
        assertEquals("Label 9", table.getCell(0, 1).getText());
        assertEquals("Text 19", table.getCell(8, 0).getText());
        assertEquals("Label 19", table.getCell(8, 1).getText());
        assertEquals("Text 13", table.getCell(14, 0).getText());
        assertEquals("Label 13", table.getCell(14, 1).getText());

        // sort by second column (ascending order)
        table.getHeaderCell(1).click();

        // check back to first sorting results
        assertEquals("Text 0", table.getCell(0, 0).getText());
        assertEquals("Label 0", table.getCell(0, 1).getText());
        assertEquals("Text 10", table.getCell(2, 0).getText());
        assertEquals("Label 10", table.getCell(2, 1).getText());
        assertEquals("Text 19", table.getCell(11, 0).getText());
        assertEquals("Label 19", table.getCell(11, 1).getText());
        assertEquals("Text 4", table.getCell(14, 0).getText());
        assertEquals("Label 4", table.getCell(14, 1).getText());
    }

}
