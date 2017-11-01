/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that column keeps its header, icon, alignment after toggling visibility
 * (#6245, #12303).
 *
 * @author Vaadin Ltd
 */
public class TableToggleColumnVisibilityTest extends MultiBrowserTest {

    @Test
    public void testColumnWidthRestoredAfterTogglingVisibility() {
        openTestURL();

        ButtonElement toggleVisibilityButton = $(ButtonElement.class)
                .id("visib-toggler");
        ButtonElement changeOrderButton = $(ButtonElement.class)
                .id("order-toggler");

        checkHeaderAttributes(1);

        toggleVisibilityButton.click(); // hide column #1
        assertEquals("One column should be visible",
                findElements(By.className("v-table-header-cell")).size(), 1);

        toggleVisibilityButton.click(); // restore column #1
        assertEquals("Two columns should be visible",
                findElements(By.className("v-table-header-cell")).size(), 2);
        checkHeaderAttributes(1);

        // change column order, column #1 now becomes column #0
        changeOrderButton.click();
        checkHeaderAttributes(0);
    }

    /*
     * Checks column header with number columnNumber.
     */
    private void checkHeaderAttributes(int columnNumber) {
        TableHeaderElement headerCell = $(TableElement.class).first()
                .getHeaderCell(columnNumber);

        assertTrue("Column header text should be custom",
                headerCell.getText().equalsIgnoreCase("Hello World"));

        assertFalse("Column should have an icon",
                headerCell.findElements(By.className("v-icon")).isEmpty());

        assertFalse("Column should have alignment to the right", headerCell
                .findElements(
                        By.className("v-table-caption-container-align-right"))
                .isEmpty());
    }
}
