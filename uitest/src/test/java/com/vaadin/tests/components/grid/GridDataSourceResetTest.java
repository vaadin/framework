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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridDataSourceResetTest extends SingleBrowserTest {

    @Test
    public void testRemoveWithSelectUpdatesRowsCorrectly() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertTrue("First row was not selected", grid.getRow(0).isSelected());
        for (int i = 1; i < 10; ++i) {
            assertFalse("Only first row should be selected", grid.getRow(i)
                    .isSelected());
        }

        $(ButtonElement.class).first().click();

        assertTrue("First row was not selected after remove", grid.getRow(0)
                .isSelected());
        for (int i = 1; i < 9; ++i) {
            assertFalse("Only first row should be selected after remove", grid
                    .getRow(i).isSelected());
        }
    }
}
