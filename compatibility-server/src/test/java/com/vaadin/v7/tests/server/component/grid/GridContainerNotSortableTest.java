package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.AbstractInMemoryContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;

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
