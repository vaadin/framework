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
package com.vaadin.tests.server.component.grid;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Label;

public class GridChildrenTest {

    private Grid grid;

    @Before
    public void createGrid() {
        grid = new Grid();
        grid.addColumn("foo");
        grid.addColumn("bar");
        grid.addColumn("baz");

    }

    @Test
    public void iteratorFindsComponentsInMergedHeader() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void removeComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        merged.setText("foo");
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeHeaderWithComponentInMergedHeaderCell() {
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        grid.removeHeaderRow(0);
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        merged.setText("foo");
        Assert.assertNull(label.getParent());
    }

    @Test
    public void removeFooterWithComponentInMergedFooterCell() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Assert.assertEquals(grid, label.getParent());
        grid.removeFooterRow(0);
        Assert.assertNull(label.getParent());
    }

    @Test
    public void componentsInMergedFooter() {
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
        Assert.assertEquals(grid, label.getParent());
    }
}
