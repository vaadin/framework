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
package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.customelements.GridElement;

@TestCategory("grid")
public class GridMultiSelectionOnInitTest extends MultiBrowserTest {

    @Test
    public void testSelectAllCheckBoxExists() {
        openTestURL();
        assertTrue("The select all checkbox was missing.",
                $(GridElement.class).first().getHeaderCell(0, 0)
                        .isElementPresent(By.tagName("input")));
    }

    @Test
    public void testSetSelectedUpdatesClient() {
        openTestURL();
        assertFalse("Rows should not be selected initially.",
                $(GridElement.class).first().getRow(0).isSelected());
        $(ButtonElement.class).first().click();
        assertTrue("Rows should be selected after button click.",
                $(GridElement.class).first().getRow(0).isSelected());
    }
}
