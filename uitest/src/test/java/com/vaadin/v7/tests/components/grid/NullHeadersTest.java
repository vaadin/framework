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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NullHeadersTest extends SingleBrowserTest {

    @Test
    public void gridWithNullHeadersShouldBeRendered() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        Assert.assertEquals(1, grid.getHeaderCount());
        Assert.assertEquals(3, grid.getHeaderCells(0).size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("", grid.getHeaderCell(0, 0).getText());
        }
        assertRow(grid, 0, "Finland", "foo", "1");
        assertRow(grid, 1, "Swaziland", "bar", "2");
        assertRow(grid, 2, "Japan", "baz", "3");
    }

    private void assertRow(GridElement grid, int row, String... contents) {
        for (int col = 0; col < contents.length; col++) {
            Assert.assertEquals(contents[col],
                    grid.getCell(row, col).getText());
        }

    }
}
