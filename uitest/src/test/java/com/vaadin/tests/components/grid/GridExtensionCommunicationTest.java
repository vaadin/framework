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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridExtensionCommunicationTest extends SingleBrowserTest {

    @Test
    public void testMouseClickIsSentToExtension() {
        openTestURL();

        GridCellElement cell = $(GridElement.class).first().getCell(0, 4);
        cell.click(5, 5);

        int expectedX = cell.getLocation().getX() + 5;
        int expectedY = cell.getLocation().getY() + 5;

        assertEquals(
                "1. Click on Person Nina Brown on column Column[propertyId:gender]",
                getLogRow(1));
        assertEquals("2. MouseEventDetails: left (" + expectedX + ", "
                + expectedY + ")", getLogRow(0));

    }
}
