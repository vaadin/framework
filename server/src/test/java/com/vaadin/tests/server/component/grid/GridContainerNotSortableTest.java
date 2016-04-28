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

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class GridContainerNotSortableTest {

    final AbstractInMemoryContainer<Object, Object, Item> notSortableDataSource = new AbstractInMemoryContainer<Object, Object, Item>() {

        private Map<Object, Property<?>> properties = new LinkedHashMap<Object, Property<?>>();

        {
            properties.put("Foo", new Property<String>() {

                @Override
                public String getValue() {
                    return "foo";
                }

                @Override
                public void setValue(String newValue) throws ReadOnlyException {
                    throw new ReadOnlyException();
                }

                @Override
                public Class<? extends String> getType() {
                    return String.class;
                }

                @Override
                public boolean isReadOnly() {
                    return true;
                }

                @Override
                public void setReadOnly(boolean newStatus) {
                    throw new UnsupportedOperationException();
                }
            });
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            return properties.keySet();
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            return properties.get(propertyId);
        }

        @Override
        public Class<?> getType(Object propertyId) {
            return properties.get(propertyId).getType();
        }

        @Override
        protected Item getUnfilteredItem(Object itemId) {
            return null;
        }
    };

    @Test
    public void testGridWithNotSortableContainer() {
        new Grid(notSortableDataSource);
    }

    @Test(expected = IllegalStateException.class)
    public void testNotSortableGridSetColumnSortable() {
        Grid grid = new Grid();
        grid.setContainerDataSource(notSortableDataSource);
        Column column = grid.getColumn("Foo");
        assertFalse("Column should not be sortable initially.",
                column.isSortable());
        column.setSortable(true);
    }
}
