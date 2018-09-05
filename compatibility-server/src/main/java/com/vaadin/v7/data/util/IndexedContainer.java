/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;

/**
 * An implementation of the <code>{@link Container.Indexed}</code> interface
 * with all important features.
 * <p>
 *
 * Features:
 * <ul>
 * <li>{@link Container.Indexed}
 * <li>{@link Container.Ordered}
 * <li>{@link Container.Sortable}
 * <li>{@link Container.Filterable}
 * <li>{@link Cloneable} (deprecated, might be removed in the future)
 * <li>Sends all needed events on content changes.
 * </ul>
 *
 * @see Container
 *
 * @author Vaadin Ltd.
 * @since 3.0
 *
 * @deprecated As of 8.0, replaced by {@link ListDataProvider}
 */

@Deprecated
@SuppressWarnings("serial")
// item type is really IndexedContainerItem, but using Item not to show it in
// public API
public class IndexedContainer
        extends AbstractInMemoryContainer<Object, Object, Item>
        implements Container.PropertySetChangeNotifier,
        Property.ValueChangeNotifier, Container.Sortable, Cloneable,
        Container.Filterable, Container.SimpleFilterable {

    /* Internal structure */

    /**
     * Linked list of ordered Property IDs.
     */
    private List<Object> propertyIds = new ArrayList<Object>();

    /**
     * Property ID to type mapping.
     */
    private Map<Object, Class<?>> types = new Hashtable<Object, Class<?>>();

    /**
     * Hash of Items, where each Item is implemented as a mapping from Property
     * ID to Property value.
     */
    private Map<Object, Map<Object, Object>> items = new Hashtable<Object, Map<Object, Object>>();

    /**
     * Set of properties that are read-only.
     */
    private Set<Property<?>> readOnlyProperties = new HashSet<Property<?>>();

    /**
     * List of all Property value change event listeners listening all the
     * properties.
     */
    private List<Property.ValueChangeListener> propertyValueChangeListeners = null;

    /**
     * Data structure containing all listeners interested in changes to single
     * Properties. The data structure is a hashtable mapping Property IDs to a
     * hashtable that maps Item IDs to a linked list of listeners listening
     * Property identified by given Property ID and Item ID.
     */
    private Map<Object, Map<Object, List<Property.ValueChangeListener>>> singlePropertyValueChangeListeners;

    private Map<Object, Object> defaultPropertyValues;

    private int nextGeneratedItemId = 1;

    /* Container constructors */

    public IndexedContainer() {
        super();
    }

    public IndexedContainer(Collection<?> itemIds) {
        this();
        if (items != null) {
            for (final Iterator<?> i = itemIds.iterator(); i.hasNext();) {
                Object itemId = i.next();
                internalAddItemAtEnd(itemId, new IndexedContainerItem(itemId),
                        false);
            }
            filterAll();
        }
    }

    /* Container methods */

    @Override
    protected Item getUnfilteredItem(Object itemId) {
        if (itemId != null && items.containsKey(itemId)) {
            return new IndexedContainerItem(itemId);
        }
        return null;
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * Gets the type of a Property stored in the list.
     *
     * @param propertyId
     *            the ID of the Property.
     * @return Type of the requested Property
     */
    @Override
    public Class<?> getType(Object propertyId) {
        return types.get(propertyId);
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        // map lookup more efficient than propertyIds if there are many
        // properties
        if (!containsId(itemId) || propertyId == null
                || !types.containsKey(propertyId)) {
            return null;
        }

        return new IndexedContainerProperty(itemId, propertyId);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) {

        // Fails, if nulls are given
        if (propertyId == null || type == null) {
            return false;
        }

        // Fails if the Property is already present
        if (propertyIds.contains(propertyId)) {
            return false;
        }

        // Adds the Property to Property list and types
        propertyIds.add(propertyId);
        types.put(propertyId, type);

        // If default value is given, set it
        if (defaultValue != null) {
            // for existing rows
            for (final Iterator<?> i = getAllItemIds().iterator(); i
                    .hasNext();) {
                getItem(i.next()).getItemProperty(propertyId)
                        .setValue(defaultValue);
            }
            // store for next rows
            if (defaultPropertyValues == null) {
                defaultPropertyValues = new HashMap<Object, Object>();
            }
            defaultPropertyValues.put(propertyId, defaultValue);
        }

        // Sends a change event
        fireContainerPropertySetChange();

        return true;
    }

    @Override
    public boolean removeAllItems() {
        int origSize = size();
        Object firstItem = getFirstVisibleItem();

        internalRemoveAllItems();

        items.clear();

        // fire event only if the visible view changed, regardless of whether
        // filtered out items were removed or not
        if (origSize != 0) {
            // Sends a change event
            fireItemsRemoved(0, firstItem, origSize);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The item ID is generated from a sequence of Integers. The id of the first
     * added item is 1.
     */
    @Override
    public Object addItem() {

        // Creates a new id
        final Object id = generateId();

        // Adds the Item into container
        addItem(id);

        return id;
    }

    @Override
    public Item addItem(Object itemId) {
        Item item = internalAddItemAtEnd(itemId,
                new IndexedContainerItem(itemId), false);
        if (item == null) {
            return null;
        } else if (!isFiltered()) {
            // always the last item
            fireItemAdded(size() - 1, itemId, item);
        } else if (passesFilters(itemId) && !containsId(itemId)) {
            getFilteredItemIds().add(itemId);
            // always the last item
            fireItemAdded(size() - 1, itemId, item);
        }
        return item;
    }

    /**
     * Helper method to add default values for items if available
     *
     * @param t
     *            data table of added item
     */
    private void addDefaultValues(Map<Object, Object> t) {
        if (defaultPropertyValues != null) {
            for (Object key : defaultPropertyValues.keySet()) {
                t.put(key, defaultPropertyValues.get(key));
            }
        }
    }

    @Override
    public boolean removeItem(Object itemId) {
        if (itemId == null || items.remove(itemId) == null) {
            return false;
        }
        int origSize = size();
        int position = indexOfId(itemId);
        if (internalRemoveItem(itemId)) {
            // fire event only if the visible view changed, regardless of
            // whether filtered out items were removed or not
            if (size() != origSize) {
                fireItemRemoved(position, itemId);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) {

        // Fails if the Property is not present
        if (!propertyIds.contains(propertyId)) {
            return false;
        }

        // Removes the Property to Property list and types
        propertyIds.remove(propertyId);
        types.remove(propertyId);
        if (defaultPropertyValues != null) {
            defaultPropertyValues.remove(propertyId);
        }

        // If remove the Property from all Items
        for (final Iterator<Object> i = getAllItemIds().iterator(); i
                .hasNext();) {
            items.get(i.next()).remove(propertyId);
        }

        // Sends a change event
        fireContainerPropertySetChange();

        return true;
    }

    /* Container.Ordered methods */

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) {
        return internalAddItemAfter(previousItemId, newItemId,
                new IndexedContainerItem(newItemId), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The item ID is generated from a sequence of Integers. The id of the first
     * added item is 1.
     */
    @Override
    public Object addItemAfter(Object previousItemId) {

        // Creates a new id
        final Object id = generateId();

        if (addItemAfter(previousItemId, id) != null) {
            return id;
        } else {
            return null;
        }
    }

    @Override
    public Item addItemAt(int index, Object newItemId) {
        return internalAddItemAt(index, newItemId,
                new IndexedContainerItem(newItemId), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The item ID is generated from a sequence of Integers. The id of the first
     * added item is 1.
     */
    @Override
    public Object addItemAt(int index) {

        // Creates a new id
        final Object id = generateId();

        // Adds the Item into container
        addItemAt(index, id);

        return id;
    }

    /**
     * Generates an unique identifier for use as an item id. Guarantees that the
     * generated id is not currently used as an id.
     *
     * @return
     */
    private Serializable generateId() {
        Serializable id;
        do {
            id = Integer.valueOf(nextGeneratedItemId++);
        } while (items.containsKey(id));

        return id;
    }

    @Override
    protected void registerNewItem(int index, Object newItemId, Item item) {
        Map<Object, Object> t = new Hashtable<Object, Object>();
        items.put(newItemId, t);
        addDefaultValues(t);
    }

    /* Event notifiers */

    /**
     * An <code>event</code> object specifying the list whose Item set has
     * changed.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @Deprecated
    public static class ItemSetChangeEvent extends BaseItemSetChangeEvent {

        private final int addedItemIndex;

        private ItemSetChangeEvent(IndexedContainer source,
                int addedItemIndex) {
            super(source);
            this.addedItemIndex = addedItemIndex;
        }

        /**
         * If and only if one item is added, gives its index.
         *
         * @return -1 if either multiple items are changed or some other change
         *         than add is done.
         */
        public int getAddedItemIndex() {
            return addedItemIndex;
        }

    }

    /**
     * An <code>event</code> object specifying the Property in a list whose
     * value has changed.
     *
     * @author Vaadin Ltd.
     * @since 3.0
     */
    private static class PropertyValueChangeEvent extends EventObject
            implements Property.ValueChangeEvent {

        private PropertyValueChangeEvent(Property source) {
            super(source);
        }

        @Override
        public Property getProperty() {
            return (Property) getSource();
        }

    }

    @Override
    public void addPropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        super.addPropertySetChangeListener(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addPropertySetChangeListener(Container.PropertySetChangeListener)}
     */
    @Deprecated
    @Override
    public void addListener(Container.PropertySetChangeListener listener) {
        addPropertySetChangeListener(listener);
    }

    @Override
    public void removePropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        super.removePropertySetChangeListener(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removePropertySetChangeListener(Container.PropertySetChangeListener)}
     */
    @Deprecated
    @Override
    public void removeListener(Container.PropertySetChangeListener listener) {
        removePropertySetChangeListener(listener);
    }

    @Override
    public void addValueChangeListener(Property.ValueChangeListener listener) {
        if (propertyValueChangeListeners == null) {
            propertyValueChangeListeners = new LinkedList<Property.ValueChangeListener>();
        }
        propertyValueChangeListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addValueChangeListener(Property.ValueChangeListener)}
     */
    @Override
    @Deprecated
    public void addListener(Property.ValueChangeListener listener) {
        addValueChangeListener(listener);
    }

    @Override
    public void removeValueChangeListener(
            Property.ValueChangeListener listener) {
        if (propertyValueChangeListeners != null) {
            propertyValueChangeListeners.remove(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeValueChangeListener(Property.ValueChangeListener)}
     */
    @Override
    @Deprecated
    public void removeListener(Property.ValueChangeListener listener) {
        removeValueChangeListener(listener);
    }

    /**
     * Sends a Property value change event to all interested listeners.
     *
     * @param source
     *            the IndexedContainerProperty object.
     */
    private void firePropertyValueChange(IndexedContainerProperty source) {

        // Sends event to listeners listening all value changes
        if (propertyValueChangeListeners != null) {
            final Property.ValueChangeEvent event = new IndexedContainer.PropertyValueChangeEvent(
                    source);
            for (Object l : propertyValueChangeListeners.toArray()) {
                ((Property.ValueChangeListener) l).valueChange(event);
            }
        }

        // Sends event to single property value change listeners
        if (singlePropertyValueChangeListeners != null) {
            final Map<Object, List<Property.ValueChangeListener>> propertySetToListenerListMap = singlePropertyValueChangeListeners
                    .get(source.propertyId);
            if (propertySetToListenerListMap != null) {
                final List<Property.ValueChangeListener> listenerList = propertySetToListenerListMap
                        .get(source.itemId);
                if (listenerList != null) {
                    final Property.ValueChangeEvent event = new IndexedContainer.PropertyValueChangeEvent(
                            source);
                    for (Object listener : listenerList.toArray()) {
                        ((Property.ValueChangeListener) listener)
                                .valueChange(event);
                    }
                }
            }
        }
    }

    @Override
    public Collection<?> getListeners(Class<?> eventType) {
        if (Property.ValueChangeEvent.class.isAssignableFrom(eventType)) {
            if (propertyValueChangeListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections
                        .unmodifiableCollection(propertyValueChangeListeners);
            }
        }
        return super.getListeners(eventType);
    }

    @Override
    protected void fireItemAdded(int position, Object itemId, Item item) {
        if (position >= 0) {
            super.fireItemAdded(position, itemId, item);
        }
    }

    @Override
    protected void fireItemSetChange() {
        fireItemSetChange(new IndexedContainer.ItemSetChangeEvent(this, -1));
    }

    /**
     * Adds new single Property change listener.
     *
     * @param propertyId
     *            the ID of the Property to add.
     * @param itemId
     *            the ID of the Item .
     * @param listener
     *            the listener to be added.
     */
    private void addSinglePropertyChangeListener(Object propertyId,
            Object itemId, Property.ValueChangeListener listener) {
        if (listener != null) {
            if (singlePropertyValueChangeListeners == null) {
                singlePropertyValueChangeListeners = new Hashtable<Object, Map<Object, List<Property.ValueChangeListener>>>();
            }
            Map<Object, List<Property.ValueChangeListener>> propertySetToListenerListMap = singlePropertyValueChangeListeners
                    .get(propertyId);
            if (propertySetToListenerListMap == null) {
                propertySetToListenerListMap = new Hashtable<Object, List<Property.ValueChangeListener>>();
                singlePropertyValueChangeListeners.put(propertyId,
                        propertySetToListenerListMap);
            }
            List<Property.ValueChangeListener> listenerList = propertySetToListenerListMap
                    .get(itemId);
            if (listenerList == null) {
                listenerList = new LinkedList<Property.ValueChangeListener>();
                propertySetToListenerListMap.put(itemId, listenerList);
            }
            listenerList.add(listener);
        }
    }

    /**
     * Removes a previously registered single Property change listener.
     *
     * @param propertyId
     *            the ID of the Property to remove.
     * @param itemId
     *            the ID of the Item.
     * @param listener
     *            the listener to be removed.
     */
    private void removeSinglePropertyChangeListener(Object propertyId,
            Object itemId, Property.ValueChangeListener listener) {
        if (listener != null && singlePropertyValueChangeListeners != null) {
            final Map<Object, List<Property.ValueChangeListener>> propertySetToListenerListMap = singlePropertyValueChangeListeners
                    .get(propertyId);
            if (propertySetToListenerListMap != null) {
                final List<Property.ValueChangeListener> listenerList = propertySetToListenerListMap
                        .get(itemId);
                if (listenerList != null) {
                    listenerList.remove(listener);
                    if (listenerList.isEmpty()) {
                        propertySetToListenerListMap.remove(itemId);
                    }
                }
                if (propertySetToListenerListMap.isEmpty()) {
                    singlePropertyValueChangeListeners.remove(propertyId);
                }
            }
            if (singlePropertyValueChangeListeners.isEmpty()) {
                singlePropertyValueChangeListeners = null;
            }
        }
    }

    /* Internal Item and Property implementations */

    /**
     * A class implementing the {@link Item} interface to be contained in the
     * list.
     *
     * @author Vaadin Ltd.
     *
     *
     * @since 3.0
     */
    @Deprecated
    class IndexedContainerItem implements Item {

        /**
         * Item ID in the host container for this Item.
         */
        private final Object itemId;

        /**
         * Constructs a new ListItem instance and connects it to a host
         * container.
         *
         * @param itemId
         *            the Item ID of the new Item.
         */
        private IndexedContainerItem(Object itemId) {
            this.itemId = itemId;
        }

        @Override
        public Property getItemProperty(Object id) {
            if (!propertyIds.contains(id)) {
                return null;
            }

            return new IndexedContainerProperty(itemId, id);
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            return Collections.unmodifiableCollection(propertyIds);
        }

        /**
         * Gets the <code>String</code> representation of the contents of the
         * Item. The format of the string is a space separated catenation of the
         * <code>String</code> representations of the values of the Properties
         * contained by the Item.
         *
         * @return <code>String</code> representation of the Item contents
         */
        @Override
        public String toString() {
            String retValue = "";

            for (final Iterator<?> i = propertyIds.iterator(); i.hasNext();) {
                final Object propertyId = i.next();
                retValue += getItemProperty(propertyId).getValue();
                if (i.hasNext()) {
                    retValue += " ";
                }
            }

            return retValue;
        }

        /**
         * Calculates a integer hash-code for the Item that's unique inside the
         * list. Two Items inside the same list have always different
         * hash-codes, though Items in different lists may have identical
         * hash-codes.
         *
         * @return A locally unique hash-code as integer
         */
        @Override
        public int hashCode() {
            return itemId.hashCode();
        }

        /**
         * Tests if the given object is the same as the this object. Two Items
         * got from a list container with the same ID are equal.
         *
         * @param obj
         *            an object to compare with this object
         * @return <code>true</code> if the given object is the same as this
         *         object, <code>false</code> if not
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null
                    || !obj.getClass().equals(IndexedContainerItem.class)) {
                return false;
            }
            final IndexedContainerItem li = (IndexedContainerItem) obj;
            return getHost() == li.getHost() && itemId.equals(li.itemId);
        }

        private IndexedContainer getHost() {
            return IndexedContainer.this;
        }

        /**
         * IndexedContainerItem does not support adding new properties. Add
         * properties at container level. See
         * {@link IndexedContainer#addContainerProperty(Object, Class, Object)}
         *
         * @see Item#addProperty(Object, Property)
         */
        @Override
        public boolean addItemProperty(Object id, Property property)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Indexed container item "
                    + "does not support adding new properties");
        }

        /**
         * Indexed container does not support removing properties. Remove
         * properties at container level. See
         * {@link IndexedContainer#removeContainerProperty(Object)}
         *
         * @see Item#removeProperty(Object)
         */
        @Override
        public boolean removeItemProperty(Object id)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException(
                    "Indexed container item does not support property removal");
        }

    }

    /**
     * A class implementing the {@link Property} interface to be contained in
     * the {@link IndexedContainerItem} contained in the
     * {@link IndexedContainer}.
     *
     * @author Vaadin Ltd.
     *
     * @since 3.0
     */
    private class IndexedContainerProperty<T>
            implements Property<T>, Property.ValueChangeNotifier {

        /**
         * ID of the Item, where this property resides.
         */
        private final Object itemId;

        /**
         * Id of the Property.
         */
        private final Object propertyId;

        /**
         * Constructs a new {@link IndexedContainerProperty} object.
         *
         * @param itemId
         *            the ID of the Item to connect the new Property to.
         * @param propertyId
         *            the Property ID of the new Property.
         * @param host
         *            the list that contains the Item to contain the new
         *            Property.
         */
        private IndexedContainerProperty(Object itemId, Object propertyId) {
            if (itemId == null || propertyId == null) {
                // Null ids are not accepted
                throw new NullPointerException(
                        "Container item or property ids can not be null");
            }
            this.propertyId = propertyId;
            this.itemId = itemId;
        }

        @Override
        public Class<T> getType() {
            return (Class<T>) types.get(propertyId);
        }

        @Override
        public T getValue() {
            return (T) items.get(itemId).get(propertyId);
        }

        @Override
        public boolean isReadOnly() {
            return readOnlyProperties.contains(this);
        }

        @Override
        public void setReadOnly(boolean newStatus) {
            if (newStatus) {
                readOnlyProperties.add(this);
            } else {
                readOnlyProperties.remove(this);
            }
        }

        @Override
        public void setValue(Object newValue)
                throws Property.ReadOnlyException {
            // Gets the Property set
            final Map<Object, Object> propertySet = items.get(itemId);

            // Support null values on all types
            if (newValue == null) {
                propertySet.remove(propertyId);
            } else if (getType().isAssignableFrom(newValue.getClass())) {
                propertySet.put(propertyId, newValue);
            } else {
                throw new IllegalArgumentException(
                        "Value is of invalid type, got "
                                + newValue.getClass().getName() + " but "
                                + getType().getName() + " was expected");
            }

            // update the container filtering if this property is being filtered
            if (isPropertyFiltered(propertyId)) {
                filterAll();
            }

            firePropertyValueChange(this);
        }

        // LegacyPropertyHelper has been removed in Vaadin 8

        /**
         * Calculates a integer hash-code for the Property that's unique inside
         * the Item containing the Property. Two different Properties inside the
         * same Item contained in the same list always have different
         * hash-codes, though Properties in different Items may have identical
         * hash-codes.
         *
         * @return A locally unique hash-code as integer
         */
        @Override
        public int hashCode() {
            return itemId.hashCode() ^ propertyId.hashCode();
        }

        /**
         * Tests if the given object is the same as the this object. Two
         * Properties got from an Item with the same ID are equal.
         *
         * @param obj
         *            an object to compare with this object
         * @return <code>true</code> if the given object is the same as this
         *         object, <code>false</code> if not
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null
                    || !obj.getClass().equals(IndexedContainerProperty.class)) {
                return false;
            }
            final IndexedContainerProperty lp = (IndexedContainerProperty) obj;
            return lp.getHost() == getHost() && lp.propertyId.equals(propertyId)
                    && lp.itemId.equals(itemId);
        }

        @Override
        public void addValueChangeListener(
                Property.ValueChangeListener listener) {
            addSinglePropertyChangeListener(propertyId, itemId, listener);
        }

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addValueChangeListener(Property.ValueChangeListener)}
         */
        @Override
        @Deprecated
        public void addListener(Property.ValueChangeListener listener) {
            addValueChangeListener(listener);
        }

        @Override
        public void removeValueChangeListener(
                Property.ValueChangeListener listener) {
            removeSinglePropertyChangeListener(propertyId, itemId, listener);
        }

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removeValueChangeListener(Property.ValueChangeListener)}
         */
        @Override
        @Deprecated
        public void removeListener(Property.ValueChangeListener listener) {
            removeValueChangeListener(listener);
        }

        private IndexedContainer getHost() {
            return IndexedContainer.this;
        }

    }

    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
        sortContainer(propertyId, ascending);
    }

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        return getSortablePropertyIds();
    }

    @Override
    public ItemSorter getItemSorter() {
        return super.getItemSorter();
    }

    @Override
    public void setItemSorter(ItemSorter itemSorter) {
        super.setItemSorter(itemSorter);
    }

    /**
     * Supports cloning of the IndexedContainer cleanly.
     *
     * @throws CloneNotSupportedException
     *             if an object cannot be cloned. .
     *
     * @deprecated As of 6.6. Cloning support might be removed from
     *             IndexedContainer in the future
     */
    @Deprecated
    @Override
    public Object clone() throws CloneNotSupportedException {

        // Creates the clone
        final IndexedContainer nc = new IndexedContainer();

        // Clone the shallow properties
        nc.setAllItemIds(
                getAllItemIds() != null ? new ListSet<Object>(getAllItemIds())
                        : null);
        nc.setItemSetChangeListeners(getItemSetChangeListeners() != null
                ? new LinkedList<Container.ItemSetChangeListener>(
                        getItemSetChangeListeners())
                : null);
        nc.propertyIds = propertyIds != null
                ? new ArrayList<Object>(propertyIds)
                : null;
        nc.setPropertySetChangeListeners(getPropertySetChangeListeners() != null
                ? new LinkedList<Container.PropertySetChangeListener>(
                        getPropertySetChangeListeners())
                : null);
        nc.propertyValueChangeListeners = propertyValueChangeListeners != null
                ? new LinkedList<Property.ValueChangeListener>(
                        propertyValueChangeListeners)
                : null;
        nc.readOnlyProperties = readOnlyProperties != null
                ? new HashSet<Property<?>>(readOnlyProperties)
                : null;
        nc.singlePropertyValueChangeListeners = singlePropertyValueChangeListeners != null
                ? new Hashtable<Object, Map<Object, List<Property.ValueChangeListener>>>(
                        singlePropertyValueChangeListeners)
                : null;

        nc.types = types != null ? new Hashtable<Object, Class<?>>(types)
                : null;

        nc.setFilters(new HashSet<Filter>(getFilters()));

        nc.setFilteredItemIds(getFilteredItemIds() == null ? null
                : new ListSet<Object>(getFilteredItemIds()));

        // Clone property-values
        if (items == null) {
            nc.items = null;
        } else {
            nc.items = new Hashtable<Object, Map<Object, Object>>();
            for (final Iterator<?> i = items.keySet().iterator(); i
                    .hasNext();) {
                final Object id = i.next();
                final Hashtable<Object, Object> it = (Hashtable<Object, Object>) items
                        .get(id);
                nc.items.put(id, (Map<Object, Object>) it.clone());
            }
        }

        return nc;
    }

    @Override
    public void addContainerFilter(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        try {
            addFilter(new SimpleStringFilter(propertyId, filterString,
                    ignoreCase, onlyMatchPrefix));
        } catch (UnsupportedFilterException e) {
            // the filter instance created here is always valid for in-memory
            // containers
        }
    }

    @Override
    public void removeAllContainerFilters() {
        removeAllFilters();
    }

    @Override
    public void removeContainerFilters(Object propertyId) {
        removeFilters(propertyId);
    }

    @Override
    public void addContainerFilter(Filter filter)
            throws UnsupportedFilterException {
        addFilter(filter);
    }

    @Override
    public void removeContainerFilter(Filter filter) {
        removeFilter(filter);
    }

    @Override
    public boolean hasContainerFilters() {
        return super.hasContainerFilters();
    }

    @Override
    public Collection<Filter> getContainerFilters() {
        return super.getContainerFilters();
    }

}
