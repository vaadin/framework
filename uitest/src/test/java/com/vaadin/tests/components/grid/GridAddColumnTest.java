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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridAddColumnTest extends SingleBrowserTest {

    GridElement grid;

    @Before
    public void init() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void columns_rendered_correctly() {
        assertCellEquals(0, 0, "a");
        assertCellEquals(1, 0, "aa");
        assertCellEquals(2, 0, "aaa");

        assertCellEquals(0, 1, "1");
        assertCellEquals(1, 1, "2");
        assertCellEquals(2, 1, "3");

        assertCellEquals(0, 2, "1");
        assertCellEquals(1, 2, "2");
        assertCellEquals(2, 2, "3");

        assertCellEquals(0, 3, "-1");
        assertCellEquals(1, 3, "-2");
        assertCellEquals(2, 3, "-3");

        assertCellStartsWith(0, 4, "java.lang.Object@");
        assertCellStartsWith(1, 4, "java.lang.Object@");
        assertCellStartsWith(2, 4, "java.lang.Object@");
    }

    @Test
    @Ignore // TODO re-enable once #8128 is resolved
    public void sort_column_with_automatic_conversion() {
        grid.getHeaderCell(0, 2).click();
        assertCellEquals(0, 0, "a");
        assertCellEquals(1, 0, "aa");
        assertCellEquals(2, 0, "aaa");

        grid.getHeaderCell(0, 3).click();
        assertCellEquals(0, 0, "aaa");
        assertCellEquals(1, 0, "aa");
        assertCellEquals(2, 0, "a");
    }

    private void assertCellEquals(int rowIndex, int colIndex, String content) {
        Assert.assertEquals("Cell text should equal", content,
                grid.getCell(rowIndex, colIndex).getText());
    }

    private void assertCellStartsWith(int rowIndex, int colIndex,
            String startsWith) {
        Assert.assertTrue("Cell text should start with", grid
                .getCell(rowIndex, colIndex).getText().startsWith(startsWith));
    }
}
