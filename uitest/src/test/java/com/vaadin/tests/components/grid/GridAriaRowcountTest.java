/*
 * Copyright 2000-2017 Vaadin Ltd.
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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Vaadin Ltd
 */
public class GridAriaRowcountTest extends SingleBrowserTest {

    private GridElement grid;

    @Test
    public void checkGridAriaRowcount() {
        openTestURL();

        grid = $(GridElement.class).first();

        // default grid should contain at least one of each role
        String gridHtml = grid.getHTML();
        System.err.println(">Debug Grid html: "+ gridHtml);
        assertTrue("Grid should contains a role=\"rowheader\"", gridHtml.contains("role=\"rowheader\""));
        assertTrue("Grid should contains a role=\"columnheader\"", gridHtml.contains("role=\"columnheader\""));
        assertTrue("Grid should contains a role=\"row\"", gridHtml.contains("role=\"row\""));
        assertTrue("Grid should contains a role=\"gridcell\"", gridHtml.contains("role=\"gridcell\""));
        assertTrue("Grid should contains a role=\"rowgroup\"", gridHtml.contains("role=\"rowgroup\""));

        // default with 1 header row and 2 body rows.
        assertTrue("Grid should have 3 rows", containsRows(3));

        $(ButtonElement.class).caption("addFooter").first().click();
        // 1 header row, 2 body rows and 1 footer row.
        assertTrue("Grid should have 4 rows", containsRows(4));

        $(ButtonElement.class).caption("removeFooter").first().click();
        // 1 header row and 2 body rows.
        assertTrue("Grid should have 3 rows", containsRows(3));

        $(ButtonElement.class).caption("addHeader").first().click();
        // 2 header row and 2 body rows.
        assertTrue("Grid should have 4 rows", containsRows(4));

        $(ButtonElement.class).caption("removeHeader").first().click();
        // 1 header row and 2 body rows.
        assertTrue("Grid should have 3 rows", containsRows(3));

        $(ButtonElement.class).caption("setItemsTo3").first().click();
        // 1 header row and 3 body rows.
        assertTrue("Grid should have 4 rows", containsRows(4));

        $(ButtonElement.class).caption("setItemsTo6").first().click();
        // 1 header row and 6 body rows.
        assertTrue("Grid should have 7 rows", containsRows(7));

        $(ButtonElement.class).caption("updateAll").first().click();
        // 2 header rows, 4 body rows and 1 footer row.
        assertTrue("Grid should have 7 rows", containsRows(7));
    }

    private boolean containsRows(int rowcount) {
        return grid.getHTML().contains("aria-rowcount=\"" + String.valueOf(rowcount) + "\"");
    }
}
