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
package com.vaadin.ui.components.grid;

import java.util.Arrays;
import java.util.HashSet;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class StaticSectionTest {
    private final Grid<String> grid = new Grid<>();
    private final Column<String, String> col1 = grid
            .addColumn(ValueProvider.identity()).setId("col1");
    private final Column<String, String> col2 = grid
            .addColumn(ValueProvider.identity()).setId("col2");
    private final Column<String, String> col3 = grid
            .addColumn(ValueProvider.identity()).setId("col3");

    private HeaderRow headerRow;
    private FooterRow footerRow;

    @Before
    public void setUp() {
        footerRow = grid.addFooterRowAt(0);
        headerRow = grid.addHeaderRowAt(0);
    }

    @Test
    public void joinFootersBySet() {
        footerRow.join(new HashSet<>(Arrays.asList(footerRow.getCell(col1),
                footerRow.getCell(col2))));

        assertFootersJoined();
    }

    @Test
    public void joinFootersByCells() {
        footerRow.join(footerRow.getCell(col1), footerRow.getCell(col2));

        assertFootersJoined();
    }

    @Test
    public void joinFootersByColumns() {
        footerRow.join(col1, col2);

        assertFootersJoined();
    }

    @Test
    public void joinFootersByIds() {
        footerRow.join("col1", "col2");

        assertFootersJoined();
    }

    @Test
    public void joinHeadersBySet() {
        headerRow.join(new HashSet<>(Arrays.asList(headerRow.getCell(col1),
                headerRow.getCell(col2))));

        assertHeadersJoined();
    }

    @Test
    public void joinHeadersByCells() {
        headerRow.join(headerRow.getCell(col1), headerRow.getCell(col2));

        assertHeadersJoined();
    }

    @Test
    public void joinHeadersByColumns() {
        headerRow.join(col1, col2);

        assertHeadersJoined();
    }

    @Test
    public void joinHeadersByIds() {
        headerRow.join("col1", "col2");

        assertHeadersJoined();
    }

    @Test(expected = IllegalStateException.class)
    public void joinHeadersByMissingIds() {
        headerRow.join("col1", "col4");
    }

    @Test(expected = IllegalStateException.class)
    public void joinFootersByMissingIds() {
        headerRow.join("col1", "col4");
    }

    private void assertFootersJoined() {
        assertJoined((StaticSection.StaticRow<?>) footerRow);
    }

    private void assertHeadersJoined() {
        assertJoined((StaticSection.StaticRow<?>) headerRow);
    }

    private static void assertJoined(StaticSection.StaticRow<?> staticRow) {
        // There doesn't seem to be any direct API for checking what's joined,
        // so verifying the merging by checking the declarative output
        Element container = new Element(Tag.valueOf("container"), "");

        staticRow.writeDesign(container, null);

        Assert.assertEquals(2, container.children().size());
        Assert.assertEquals("col1,col2", container.child(0).attr("column-ids"));
        Assert.assertEquals("col3", container.child(1).attr("column-ids"));
    }

}
