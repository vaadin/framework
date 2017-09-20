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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridRemoveColumnAndDetachTest extends SingleBrowserTest {

    @Test
    public void gridDetachesWithoutErrors() {
        openTestURL("debug");
        $(ButtonElement.class).id("detach").click();

        assertElementNotPresent(By.className("v-grid"));
        assertNoErrorNotifications();
    }

    @Test
    public void frozenColumnCountAfterRemovingHiddenColumn() {
        openTestURL("debug");
        assertVisibleFrozenColumns(2);
        $(ButtonElement.class).id("remove1").click();
        assertVisibleFrozenColumns(2);

    }

    @Test
    public void frozenColumnCountAfterWhenRemovingFrozenColumn() {
        openTestURL("debug");
        assertVisibleFrozenColumns(2);
        $(ButtonElement.class).id("remove0").click();
        assertVisibleFrozenColumns(1);

    }

    private void assertVisibleFrozenColumns(int nrFrozenColumns) {
        GridElement grid = $(GridElement.class).first();
        for (int i = 0; i < nrFrozenColumns; i++) {
            TestBenchElement cell = grid.getCell(0, i);
            assertTrue("Column " + i + " should be frozen",
                    cell.hasClassName("frozen"));
        }
        assertTrue("Only " + nrFrozenColumns + " should be frozen", grid
                .getCell(0, nrFrozenColumns - 1).hasClassName("last-frozen"));
    }

    @Test
    public void frozenColumnCountAfterWhenRemovingNonFrozenColumn() {
        openTestURL("debug");
        assertVisibleFrozenColumns(2);
        $(ButtonElement.class).id("remove3").click();
        assertVisibleFrozenColumns(2);
    }
}
