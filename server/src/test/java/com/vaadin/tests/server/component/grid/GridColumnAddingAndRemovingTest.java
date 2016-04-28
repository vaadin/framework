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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Grid;

public class GridColumnAddingAndRemovingTest {

    Grid grid = new Grid();
    Container.Indexed container;

    @Before
    public void setUp() {
        container = grid.getContainerDataSource();
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

    @Test
    public void testAddNumberColumns() {
        grid.addColumn("bar", Integer.class);
        grid.addColumn("baz", Double.class);

        Property<?> property = container.getContainerProperty(
                container.firstItemId(), "bar");
        assertEquals(property.getType(), Integer.class);
        assertEquals(null, property.getValue());
        property = container.getContainerProperty(container.firstItemId(),
                "baz");
        assertEquals(property.getType(), Double.class);
        assertEquals(null, property.getValue());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDifferentTypeColumn() {
        grid.addColumn("foo");
        grid.removeColumn("foo");
        grid.addColumn("foo", Integer.class);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddColumnToNonDefaultContainer() {
        grid.setContainerDataSource(new IndexedContainer());
        grid.addColumn("foo");
    }

    @Test
    public void testAddColumnForExistingProperty() {
        grid.addColumn("bar");
        IndexedContainer container2 = new IndexedContainer();
        container2.addContainerProperty("foo", Integer.class, 0);
        container2.addContainerProperty("bar", String.class, "");
        grid.setContainerDataSource(container2);
        assertNull("Grid should not have a column for property foo",
                grid.getColumn("foo"));
        assertNotNull("Grid did should have a column for property bar",
                grid.getColumn("bar"));
        for (Grid.Column column : grid.getColumns()) {
            assertNotNull("Grid getColumns returned a null value", column);
        }

        grid.removeAllColumns();
        grid.addColumn("foo");
        assertNotNull("Grid should now have a column for property foo",
                grid.getColumn("foo"));
        assertNull("Grid should not have a column for property bar anymore",
                grid.getColumn("bar"));
    }

    @Test(expected = IllegalStateException.class)
    public void testAddIncompatibleColumnProperty() {
        grid.addColumn("bar");
        grid.removeAllColumns();
        grid.addColumn("bar", Integer.class);
    }

    @Test
    public void testAddBooleanColumnProperty() {
        grid.addColumn("foo", Boolean.class);
        Property<?> property = container.getContainerProperty(
                container.firstItemId(), "foo");
        assertEquals(property.getType(), Boolean.class);
        assertEquals(property.getValue(), null);
    }
}
