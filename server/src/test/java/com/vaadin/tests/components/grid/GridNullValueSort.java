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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.SerializableComparator;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.AbstractRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridNullValueSort {

    private static AbstractRenderer<Integer, Boolean> booleanRenderer() {
        return new AbstractRenderer<Integer, Boolean>(Boolean.class) {
        };
    }

    private static class TestGrid extends Grid<Integer> {
        @Override
        public SerializableComparator<Integer> createSortingComparator() {
            return super.createSortingComparator();
        }
    }

    private TestGrid grid;

    @Before
    public void setup() {
        VaadinSession.setCurrent(null);
        grid = new TestGrid();
        grid.addColumn(i -> i, new NumberRenderer()).setId("int")
                .setSortable(true);
        grid.addColumn(i -> i == null ? null : String.valueOf(i))
                .setId("String").setSortable(true);
        grid.addColumn(i -> i == null ? null : i != 1, booleanRenderer())
                .setId("Boolean").setSortable(true);
    }

    @Test
    public void testNumbersNotNulls() {
        grid.sort(grid.getColumn("int"), SortDirection.ASCENDING);
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void testSortByColumnId() {
        grid.sort("int");
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void testSortByColumnIdAndDirection() {
        grid.sort("int", SortDirection.DESCENDING);
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(3, 2, 1));
    }

    @Test(expected = IllegalStateException.class)
    public void testSortByMissingColumnId() {
        grid.sort("notHere");
    }

    @Test(expected = IllegalStateException.class)
    public void testSortByMissingColumnIdAndDirection() {
        grid.sort("notHere", SortDirection.DESCENDING);
    }

    @Test
    public void testNumbers() {
        grid.sort(grid.getColumn("int"), SortDirection.ASCENDING);
        performSort(Arrays.asList(1, 2, null, 3, null, null),
                Arrays.asList(1, 2, 3, null, null, null));
    }

    @Test
    public void testNumbersNotNullsDescending() {
        grid.sort(grid.getColumn("int"), SortDirection.DESCENDING);
        performSort(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1));
    }

    @Test
    public void testNumbersDescending() {
        grid.sort(grid.getColumn("int"), SortDirection.DESCENDING);
        performSort(Arrays.asList(1, 3, null, null, null, 2),
                Arrays.asList(null, null, null, 3, 2, 1));
    }

    @Test
    public void testStringsNotNulls() {
        grid.sort(grid.getColumn("String"), SortDirection.ASCENDING);
        performSort(Arrays.asList(2, 1, 3), Arrays.asList(1, 2, 3));
    }

    @Test
    public void testStrings() {
        grid.sort(grid.getColumn("String"), SortDirection.ASCENDING);
        performSort(Arrays.asList(1, 2, null, 3, null, null),
                Arrays.asList(1, 2, 3, null, null, null));
    }

    @Test
    public void testStringsNotNullsDescending() {
        grid.sort(grid.getColumn("String"), SortDirection.DESCENDING);
        performSort(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1));
    }

    @Test
    public void testStringsDescending() {
        grid.sort(grid.getColumn("String"), SortDirection.DESCENDING);
        performSort(Arrays.asList(1, 3, null, null, null, 2),
                Arrays.asList(null, null, null, 3, 2, 1));
    }

    @Test
    public void testBooleansNotNulls() {
        grid.sort(grid.getColumn("Boolean"), SortDirection.ASCENDING);
        performSort(Arrays.asList(2, 1), Arrays.asList(1, 2));
    }

    @Test
    public void testBooleans() {
        grid.sort(grid.getColumn("Boolean"), SortDirection.ASCENDING);
        performSort(Arrays.asList(1, null, 2, null, null),
                Arrays.asList(1, 2, null, null, null));
    }

    @Test
    public void testBooleansNotNullsDescending() {
        grid.sort(grid.getColumn("Boolean"), SortDirection.DESCENDING);
        performSort(Arrays.asList(1, 2), Arrays.asList(2, 1));
    }

    @Test
    public void testBooleansDescending() {
        grid.sort(grid.getColumn("Boolean"), SortDirection.DESCENDING);
        performSort(Arrays.asList(1, null, null, null, 2),
                Arrays.asList(null, null, null, 2, 1));
    }

    private void performSort(List<Integer> source, List<Integer> expected) {
        SerializableComparator<Integer> sortingComparator = grid
                .createSortingComparator();
        List<Integer> data = new ArrayList<>(source);
        data.sort(sortingComparator);
        Assert.assertEquals(expected, data);
    }

}
