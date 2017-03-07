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
package com.vaadin.v7.data.util;

import java.util.Collection;

import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;

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
        public Item addItem(Object itemId)
                throws UnsupportedOperationException {
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

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(
                new ContainerOrderedWrapper(new NotOrderedContainer()));
    }

    @Test
    public void testOrdered() {
        testContainerOrdered(
                new ContainerOrderedWrapper(new NotOrderedContainer()));
    }

}
