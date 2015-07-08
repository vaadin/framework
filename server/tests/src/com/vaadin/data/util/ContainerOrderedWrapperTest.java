package com.vaadin.data.util;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class ContainerOrderedWrapperTest extends AbstractContainerTestBase {

    // This class is needed to get an implementation of container
    // which is not an implementation of Ordered interface.
    private class NotOrderedContainer implements Container {

        private IndexedContainer container;

        public NotOrderedContainer() {
            container = new IndexedContainer();
        }

        @Override
        public Item getItem(Object itemId) {
            return container.getItem(itemId);
        }

        @Override
        public Collection<?> getContainerPropertyIds() {
            return container.getContainerPropertyIds();
        }

        @Override
        public Collection<?> getItemIds() {
            return container.getItemIds();
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            return container.getContainerProperty(itemId, propertyId);
        }

        @Override
        public Class<?> getType(Object propertyId) {
            return container.getType(propertyId);
        }

        @Override
        public int size() {
            return container.size();
        }

        @Override
        public boolean containsId(Object itemId) {
            return container.containsId(itemId);
        }

        @Override
        public Item addItem(Object itemId) throws UnsupportedOperationException {
            return container.addItem(itemId);
        }

        @Override
        public Object addItem() throws UnsupportedOperationException {
            return container.addItem();
        }

        @Override
        public boolean removeItem(Object itemId)
                throws UnsupportedOperationException {
            return container.removeItem(itemId);
        }

        @Override
        public boolean addContainerProperty(Object propertyId, Class<?> type,
                Object defaultValue) throws UnsupportedOperationException {
            return container.addContainerProperty(propertyId, type,
                    defaultValue);
        }

        @Override
        public boolean removeContainerProperty(Object propertyId)
                throws UnsupportedOperationException {
            return container.removeContainerProperty(propertyId);
        }

        @Override
        public boolean removeAllItems() throws UnsupportedOperationException {
            return container.removeAllItems();
        }

    }

    public void testBasicOperations() {
        testBasicContainerOperations(new ContainerOrderedWrapper(
                new NotOrderedContainer()));
    }

    public void testOrdered() {
        testContainerOrdered(new ContainerOrderedWrapper(
                new NotOrderedContainer()));
    }

}
