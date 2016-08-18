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
package com.vaadin.v7.tests.server.component.grid;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.FooterCell;
import com.vaadin.v7.ui.Grid.HeaderCell;

public class GridChildrenTest {

    @Test
    public void componentsInMergedHeader() {
        Grid grid = new Grid();
        grid.addColumn("foo");
        grid.addColumn("bar");
        grid.addColumn("baz");
        HeaderCell merged = grid.getDefaultHeaderRow().join("foo", "bar",
                "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
    }

    @Test
    public void componentsInMergedFooter() {
        Grid grid = new Grid();
        grid.addColumn("foo");
        grid.addColumn("bar");
        grid.addColumn("baz");
        FooterCell merged = grid.addFooterRowAt(0).join("foo", "bar", "baz");
        Label label = new Label();
        merged.setComponent(label);
        Iterator<Component> i = grid.iterator();
        Assert.assertEquals(label, i.next());
        Assert.assertFalse(i.hasNext());
    }
}
