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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridDefaultSelectionModeTest extends MultiBrowserTest {

    @Test
    public void testSelectionFromServer() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).caption("Select on server").first().click();

        assertTrue("Row should be selected.",
                $(GridElement.class).first().getRow(0).isSelected());

        $(ButtonElement.class).caption("Deselect on server").first().click();

        assertFalse("Row should not be selected.",
                $(GridElement.class).first().getRow(0).isSelected());

        assertNoErrorNotifications();
    }

    @Test
    public void testSelectionWithSort() {
        setDebug(true);
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 0).click();

        GridCellElement header = grid.getHeaderCell(0, 1);
        header.click();
        header.click();

        assertTrue("Row should be selected.", grid.getRow(1).isSelected());

        assertNoErrorNotifications();
    }

    @Test
    public void testReselectDeselectedRow() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).caption("Select on server").first().click();

        GridElement grid = $(GridElement.class).first();
        assertTrue("Row should be selected.", grid.getRow(0).isSelected());

        $(ButtonElement.class).caption("Deselect on server").first().click();

        assertFalse("Row should not be selected.", grid.getRow(0).isSelected());

        grid.getCell(0, 0).click();
        assertTrue("Row should be selected.", grid.getRow(0).isSelected());

        assertNoErrorNotifications();
    }
}
