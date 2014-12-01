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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Grid;

public class GridColumnBuildTest {

    Grid grid = new Grid();
    Container.Indexed container;

    @Before
    public void setUp() {
        container = grid.getContainerDatasource();
        container.addItem();
    }

    @Test
    public void testAddColumn() {
        grid.addColumn("foo");

        Property<?> property = container.getContainerProperty(
                container.firstItemId(), "foo");
        assertEquals(property.getType(), String.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddColumnTwice() {
        grid.addColumn("foo");
        grid.addColumn("foo");
    }

    @Test
    public void testAddRemoveAndAddAgainColumn() {
        grid.addColumn("foo");
        grid.removeColumn("foo");

        // Removing a column, doesn't remove the property
        Property<?> property = container.getContainerProperty(
                container.firstItemId(), "foo");
        assertEquals(property.getType(), String.class);
        grid.addColumn("foo");
    }

    public void testAddNumberColumns() {
        grid.addColumn("bar", Integer.class);
        grid.addColumn("baz", Double.class);

        Property<?> property = container.getContainerProperty(
                container.firstItemId(), "bar");
        assertEquals(property.getType(), Integer.class);
        property = container.getContainerProperty(container.firstItemId(),
                "baz");
        assertEquals(property.getType(), Double.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDifferentTypeColumn() {
        grid.addColumn("foo");
        grid.removeColumn("foo");
        grid.addColumn("foo", Integer.class);
    }
}
