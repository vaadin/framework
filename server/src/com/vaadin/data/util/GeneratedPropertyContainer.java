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
package com.vaadin.data.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.thirdparty.guava.common.collect.Sets;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Container supporting generated properties.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class GeneratedPropertyContainer implements Container.Indexed {

    private Container.Indexed wrappedContainer;
    private final Map<Object, PropertyValueGenerator<?>> propertyGenerators;

    /**
     * Property implementation for generated properties
     */
    protected static class GeneratedProperty<T> implements Property<T> {

        private Item item;
        private Object itemId;
        private Object propertyId;
        private PropertyValueGenerator<T> generator;

        public GeneratedProperty(Item item, Object propertyId, Object itemId,
                PropertyValueGenerator<T> generator) {
            this.item = item;
            this.itemId = itemId;
            this.propertyId = propertyId;
            this.generator = generator;
        }

        @Override
        public T getValue() {
            return generator.getValue(item, itemId, propertyId);
        }

        @Override
        public void setValue(T newValue) throws ReadOnlyException {
            throw new ReadOnlyException("Generated properties are read only");
        }

        @Override
        public Class<? extends T> getType() {
            return generator.getType();
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public void setReadOnly(boolean newStatus) {
            if (newStatus) {
                // No-op
                return;
            }
            throw new UnsupportedOperationException(
                    "Generated properties are read only");
        }
    }

    /**
     * Item implementation for generated properties.
     */
    protected static class GeneratedPropertyItem implements Item {

        private Map<Object, Property<?>> generatedProperties = new HashMap<Object, Property<?>>();
        Item wrappedItem;
        Object itemId;

        protected GeneratedPropertyItem(Object itemId, Item item) {
            this.itemId = itemId;
            wrappedItem = item;
        }

        @Override
        public Property getItemProperty(Object id) {
            if (generatedProperties.containsKey(id)) {
                return generatedProperties.get(id);
            }
            return wrappedItem.getItemProperty(id);
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            return Sets.union(asSet(wrappedItem.getItemPropertyIds()),
                    asSet(generatedProperties.keySet()));
        }

        @Override
        public boolean addItemProperty(Object id, Property property)
                throws UnsupportedOperationException {
            generatedProperties.put(id, property);
            return true;
        }

        @Override
        public boolean removeItemProperty(Object id)
                throws UnsupportedOperationException {
            return generatedProperties.remove(id) != null;
        }
    };

    /**
     * Constructor for GeneratedPropertyContainer.
     * 
     * @param container
     *            underlying indexed container
     */
    public GeneratedPropertyContainer(Container.Indexed container) {
        wrappedContainer = container;
        propertyGenerators = new HashMap<Object, PropertyValueGenerator<?>>();
    }

    /* Functions related to generated properties */

    /**
     * Add a new PropertyValueGenerator with given property id. This will
     * override any existing properties with the same property id. Fires a
     * PropertySetChangeEvent.
     * 
     * @param propertyId
     *            property id
     * @param generator
     *            a property value generator
     */
    public void addGeneratedProperty(Object propertyId,
            PropertyValueGenerator<?> generator) {
        propertyGenerators.put(propertyId, generator);
        // TODO: Fire event
    }

    /**
     * Removes any possible PropertyValueGenerator with given property id. Fires
     * a PropertySetChangeEvent.
     * 
     * @param propertyId
     *            property id
     */
    public void removeGeneratedProperty(Object propertyId) {
        if (propertyGenerators.containsKey(propertyId)) {
            propertyGenerators.remove(propertyId);
            // TODO: Fire event
        }
    }

    private Item createGeneratedPropertyItem(final Object itemId,
            final Item item) {
        Item generatedItem = new GeneratedPropertyItem(itemId, item);

        for (Object propertyId : propertyGenerators.keySet()) {
            generatedItem.addItemProperty(
                    propertyId,
                    createProperty(item, propertyId, itemId,
                            propertyGenerators.get(propertyId)));
        }
        return generatedItem;
    }

    private <T> Property<T> createProperty(final Item item,
            final Object propertyId, final Object itemId,
            final PropertyValueGenerator<T> generator) {
        return new GeneratedProperty<T>(item, propertyId, itemId, generator);
    }

    private static <T> Set<T> asSet(Collection<T> collection) {
        if (collection instanceof Set) {
            return (Set<T>) collection;
        } else {
            return new HashSet<T>(collection);
        }
    }

    /* Item related overrides */

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        Item item = wrappedContainer.addItemAfter(previousItemId, newItemId);
        return createGeneratedPropertyItem(newItemId, item);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        Item item = wrappedContainer.addItem(itemId);
        return createGeneratedPropertyItem(itemId, item);
    }

    @Override
    public Item addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        Item item = wrappedContainer.addItemAt(index, newItemId);
        return createGeneratedPropertyItem(newItemId, item);
    }

    @Override
    public Item getItem(Object itemId) {
        Item item = wrappedContainer.getItem(itemId);
        return createGeneratedPropertyItem(itemId, item);
    }

    /* Property related overrides */

    @SuppressWarnings("rawtypes")
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        if (propertyGenerators.keySet().contains(propertyId)) {
            return getItem(itemId).getItemProperty(propertyId);
        } else {
            return wrappedContainer.getContainerProperty(itemId, propertyId);
        }
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return Sets.union(asSet(wrappedContainer.getContainerPropertyIds()),
                asSet(propertyGenerators.keySet()));
    }

    /* Type related overrides */

    @Override
    public Class<?> getType(Object propertyId) {
        if (propertyGenerators.containsKey(propertyId)) {
            return propertyGenerators.get(propertyId).getType();
        } else {
            return wrappedContainer.getType(propertyId);
        }
    }

    /* Unmodified functions */

    @Override
    public Object nextItemId(Object itemId) {
        return wrappedContainer.nextItemId(itemId);
    }

    @Override
    public Object prevItemId(Object itemId) {
        return wrappedContainer.prevItemId(itemId);
    }

    @Override
    public Object firstItemId() {
        return wrappedContainer.firstItemId();
    }

    @Override
    public Object lastItemId() {
        return wrappedContainer.lastItemId();
    }

    @Override
    public boolean isFirstId(Object itemId) {
        return wrappedContainer.isFirstId(itemId);
    }

    @Override
    public boolean isLastId(Object itemId) {
        return wrappedContainer.isLastId(itemId);
    }

    @Override
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        return wrappedContainer.addItemAfter(previousItemId);
    }

    @Override
    public Collection<?> getItemIds() {
        return wrappedContainer.getItemIds();
    }

    @Override
    public int size() {
        return wrappedContainer.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        return wrappedContainer.containsId(itemId);
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        return wrappedContainer.addItem();
    }

    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        return wrappedContainer.removeItem(itemId);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        return wrappedContainer.addContainerProperty(propertyId, type,
                defaultValue);
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        return wrappedContainer.removeContainerProperty(propertyId);
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        return wrappedContainer.removeAllItems();
    }

    @Override
    public int indexOfId(Object itemId) {
        return wrappedContainer.indexOfId(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        return wrappedContainer.getIdByIndex(index);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        return wrappedContainer.getItemIds(startIndex, numberOfItems);
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        return wrappedContainer.addItemAt(index);
    }
}
