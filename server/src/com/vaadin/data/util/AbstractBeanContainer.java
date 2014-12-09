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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.PropertySetChangeNotifier;
import com.vaadin.data.Container.SimpleFilterable;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.util.MethodProperty.MethodException;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;

/**
 * An abstract base class for in-memory containers for JavaBeans.
 * 
 * <p>
 * The properties of the container are determined automatically by introspecting
 * the used JavaBean class and explicitly adding or removing properties is not
 * supported. Only beans of the same type can be added to the container.
 * </p>
 * 
 * <p>
 * Subclasses should implement any public methods adding items to the container,
 * typically calling the protected methods {@link #addItem(Object, Object)},
 * {@link #addItemAfter(Object, Object, Object)} and
 * {@link #addItemAt(int, Object, Object)}.
 * </p>
 * 
 * @param <IDTYPE>
 *            The type of the item identifier
 * @param <BEANTYPE>
 *            The type of the Bean
 * 
 * @since 6.5
 */
public abstract class AbstractBeanContainer<IDTYPE, BEANTYPE> extends
        AbstractInMemoryContainer<IDTYPE, String, BeanItem<BEANTYPE>> implements
        Filterable, SimpleFilterable, Sortable, ValueChangeListener,
        PropertySetChangeNotifier {

    /**
     * Resolver that maps beans to their (item) identifiers, removing the need
     * to explicitly specify item identifiers when there is no need to customize
     * this.
     * 
     * Note that beans can also be added with an explicit id even if a resolver
     * has been set.
     * 
     * @param <IDTYPE>
     * @param <BEANTYPE>
     * 
     * @since 6.5
     */
    public static interface BeanIdResolver<IDTYPE, BEANTYPE> extends
            Serializable {
        /**
         * Return the item identifier for a bean.
         * 
         * @param bean
         * @return
         */
        public IDTYPE getIdForBean(BEANTYPE bean);
    }

    /**
     * A item identifier resolver that returns the value of a bean property.
     * 
     * The bean must have a getter for the property, and the getter must return
     * an object of type IDTYPE.
     */
    protected class PropertyBasedBeanIdResolver implements
            BeanIdResolver<IDTYPE, BEANTYPE> {

        private final Object propertyId;

        public PropertyBasedBeanIdResolver(Object propertyId) {
            if (propertyId == null) {
                throw new IllegalArgumentException(
                        "Property identifier must not be null");
            }
            this.propertyId = propertyId;
        }

        @Override
        @SuppressWarnings("unchecked")
        public IDTYPE getIdForBean(BEANTYPE bean)
                throws IllegalArgumentException {
            VaadinPropertyDescriptor<BEANTYPE> pd = model.get(propertyId);
            if (null == pd) {
                throw new IllegalStateException("Property " + propertyId
                        + " not found");
            }
            try {
                Property<IDTYPE> property = (Property<IDTYPE>) pd
                        .createProperty(bean);
                return property.getValue();
            } catch (MethodException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

    /**
     * The resolver that finds the item ID for a bean, or null not to use
     * automatic resolving.
     * 
     * Methods that add a bean without specifying an ID must not be called if no
     * resolver has been set.
     */
    private BeanIdResolver<IDTYPE, BEANTYPE> beanIdResolver = null;

    /**
     * Maps all item ids in the container (including filtered) to their
     * corresponding BeanItem.
     */
    private final Map<IDTYPE, BeanItem<BEANTYPE>> itemIdToItem = new HashMap<IDTYPE, BeanItem<BEANTYPE>>();

    /**
     * The type of the beans in the container.
     */
    private final Class<? super BEANTYPE> type;

    /**
     * A description of the properties found in beans of type {@link #type}.
     * Determines the property ids that are present in the container.
     */
    private final LinkedHashMap<String, VaadinPropertyDescriptor<BEANTYPE>> model;

    /**
     * Constructs a {@code AbstractBeanContainer} for beans of the given type.
     * 
     * @param type
     *            the type of the beans that will be added to the container.
     * @throws IllegalArgumentException
     *             If {@code type} is null
     */
    protected AbstractBeanContainer(Class<? super BEANTYPE> type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "The bean type passed to AbstractBeanContainer must not be null");
        }
        this.type = type;
        model = BeanItem.getPropertyDescriptors((Class<BEANTYPE>) type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
        VaadinPropertyDescriptor<BEANTYPE> descriptor = model.get(propertyId);
        if (descriptor == null) {
            return null;
        }
        return descriptor.getPropertyType();
    }

    /**
     * Create a BeanItem for a bean using pre-parsed bean metadata (based on
     * {@link #getBeanType()}).
     * 
     * @param bean
     * @return created {@link BeanItem} or null if bean is null
     */
    protected BeanItem<BEANTYPE> createBeanItem(BEANTYPE bean) {
        return bean == null ? null : new BeanItem<BEANTYPE>(bean, model);
    }

    /**
     * Returns the type of beans this Container can contain.
     * 
     * This comes from the bean type constructor parameter, and bean metadata
     * (including container properties) is based on this.
     * 
     * @return
     */
    public Class<? super BEANTYPE> getBeanType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */
    @Override
    public Collection<String> getContainerPropertyIds() {
        return model.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() {
        int origSize = size();

        internalRemoveAllItems();

        // detach listeners from all Items
        for (Item item : itemIdToItem.values()) {
            removeAllValueChangeListeners(item);
        }
        itemIdToItem.clear();

        // fire event only if the visible view changed, regardless of whether
        // filtered out items were removed or not
        if (origSize != 0) {
            fireItemSetChange();
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    @Override
    public BeanItem<BEANTYPE> getItem(Object itemId) {
        // TODO return only if visible?
        return getUnfilteredItem(itemId);
    }

    @Override
    protected BeanItem<BEANTYPE> getUnfilteredItem(Object itemId) {
        return itemIdToItem.get(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItemIds()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IDTYPE> getItemIds() {
        return (List<IDTYPE>) super.getItemIds();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        Item item = getItem(itemId);
        if (item == null) {
            return null;
        }
        return item.getItemProperty(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
    @Override
    public boolean removeItem(Object itemId) {
        // TODO should also remove items that are filtered out
        int origSize = size();
        Item item = getItem(itemId);
        int position = indexOfId(itemId);

        if (internalRemoveItem(itemId)) {
            // detach listeners from Item
            removeAllValueChangeListeners(item);

            // remove item
            itemIdToItem.remove(itemId);

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

    /**
     * Re-filter the container when one of the monitored properties changes.
     */
    @Override
    public void valueChange(ValueChangeEvent event) {
        // if a property that is used in a filter is changed, refresh filtering
        filterAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.Filterable#addContainerFilter(java.lang.Object,
     * java.lang.String, boolean, boolean)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Filterable#removeAllContainerFilters()
     */
    @Override
    public void removeAllContainerFilters() {
        if (!getFilters().isEmpty()) {
            for (Item item : itemIdToItem.values()) {
                removeAllValueChangeListeners(item);
            }
            removeAllFilters();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.Filterable#removeContainerFilters(java.lang
     * .Object)
     */
    @Override
    public void removeContainerFilters(Object propertyId) {
        Collection<Filter> removedFilters = super.removeFilters(propertyId);
        if (!removedFilters.isEmpty()) {
            // stop listening to change events for the property
            for (Item item : itemIdToItem.values()) {
                removeValueChangeListener(item, propertyId);
            }
        }
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.AbstractInMemoryContainer#hasContainerFilters()
     */
    @Override
    public boolean hasContainerFilters() {
        return super.hasContainerFilters();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.AbstractInMemoryContainer#getContainerFilters()
     */
    @Override
    public Collection<Filter> getContainerFilters() {
        return super.getContainerFilters();
    }

    /**
     * Make this container listen to the given property provided it notifies
     * when its value changes.
     * 
     * @param item
     *            The {@link Item} that contains the property
     * @param propertyId
     *            The id of the property
     */
    private void addValueChangeListener(Item item, Object propertyId) {
        Property<?> property = item.getItemProperty(propertyId);
        if (property instanceof ValueChangeNotifier) {
            // avoid multiple notifications for the same property if
            // multiple filters are in use
            ValueChangeNotifier notifier = (ValueChangeNotifier) property;
            notifier.removeListener(this);
            notifier.addListener(this);
        }
    }

    /**
     * Remove this container as a listener for the given property.
     * 
     * @param item
     *            The {@link Item} that contains the property
     * @param propertyId
     *            The id of the property
     */
    private void removeValueChangeListener(Item item, Object propertyId) {
        Property<?> property = item.getItemProperty(propertyId);
        if (property instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) property).removeListener(this);
        }
    }

    /**
     * Remove this contains as a listener for all the properties in the given
     * {@link Item}.
     * 
     * @param item
     *            The {@link Item} that contains the properties
     */
    private void removeAllValueChangeListeners(Item item) {
        for (Object propertyId : item.getItemPropertyIds()) {
            removeValueChangeListener(item, propertyId);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
     */
    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        return getSortablePropertyIds();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     * boolean[])
     */
    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
        sortContainer(propertyId, ascending);
    }

    @Override
    public ItemSorter getItemSorter() {
        return super.getItemSorter();
    }

    @Override
    public void setItemSorter(ItemSorter itemSorter) {
        super.setItemSorter(itemSorter);
    }

    @Override
    protected void registerNewItem(int position, IDTYPE itemId,
            BeanItem<BEANTYPE> item) {
        itemIdToItem.put(itemId, item);

        // add listeners to be able to update filtering on property
        // changes
        for (Filter filter : getFilters()) {
            for (String propertyId : getContainerPropertyIds()) {
                if (filter.appliesToProperty(propertyId)) {
                    // addValueChangeListener avoids adding duplicates
                    addValueChangeListener(item, propertyId);
                }
            }
        }
    }

    /**
     * Check that a bean can be added to the container (is of the correct type
     * for the container).
     * 
     * @param bean
     * @return
     */
    private boolean validateBean(BEANTYPE bean) {
        return bean != null && getBeanType().isAssignableFrom(bean.getClass());
    }

    /**
     * Adds the bean to the Container.
     * 
     * Note: the behavior of this method changed in Vaadin 6.6 - now items are
     * added at the very end of the unfiltered container and not after the last
     * visible item if filtering is used.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    protected BeanItem<BEANTYPE> addItem(IDTYPE itemId, BEANTYPE bean) {
        if (!validateBean(bean)) {
            return null;
        }
        return internalAddItemAtEnd(itemId, createBeanItem(bean), true);
    }

    /**
     * Adds the bean after the given bean.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    protected BeanItem<BEANTYPE> addItemAfter(IDTYPE previousItemId,
            IDTYPE newItemId, BEANTYPE bean) {
        if (!validateBean(bean)) {
            return null;
        }
        return internalAddItemAfter(previousItemId, newItemId,
                createBeanItem(bean), true);
    }

    /**
     * Adds a new bean at the given index.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @param index
     *            Index at which the bean should be added.
     * @param newItemId
     *            The item id for the bean to add to the container.
     * @param bean
     *            The bean to add to the container.
     * 
     * @return Returns the new BeanItem or null if the operation fails.
     */
    protected BeanItem<BEANTYPE> addItemAt(int index, IDTYPE newItemId,
            BEANTYPE bean) {
        if (!validateBean(bean)) {
            return null;
        }
        return internalAddItemAt(index, newItemId, createBeanItem(bean), true);
    }

    /**
     * Adds a bean to the container using the bean item id resolver to find its
     * identifier.
     * 
     * A bean id resolver must be set before calling this method.
     * 
     * @see #addItem(Object, Object)
     * 
     * @param bean
     *            the bean to add
     * @return BeanItem<BEANTYPE> item added or null
     * @throws IllegalStateException
     *             if no bean identifier resolver has been set
     * @throws IllegalArgumentException
     *             if an identifier cannot be resolved for the bean
     */
    protected BeanItem<BEANTYPE> addBean(BEANTYPE bean)
            throws IllegalStateException, IllegalArgumentException {
        if (bean == null) {
            return null;
        }
        IDTYPE itemId = resolveBeanId(bean);
        if (itemId == null) {
            throw new IllegalArgumentException(
                    "Resolved identifier for a bean must not be null");
        }
        return addItem(itemId, bean);
    }

    /**
     * Adds a bean to the container after a specified item identifier, using the
     * bean item id resolver to find its identifier.
     * 
     * A bean id resolver must be set before calling this method.
     * 
     * @see #addItemAfter(Object, Object, Object)
     * 
     * @param previousItemId
     *            the identifier of the bean after which this bean should be
     *            added, null to add to the beginning
     * @param bean
     *            the bean to add
     * @return BeanItem<BEANTYPE> item added or null
     * @throws IllegalStateException
     *             if no bean identifier resolver has been set
     * @throws IllegalArgumentException
     *             if an identifier cannot be resolved for the bean
     */
    protected BeanItem<BEANTYPE> addBeanAfter(IDTYPE previousItemId,
            BEANTYPE bean) throws IllegalStateException,
            IllegalArgumentException {
        if (bean == null) {
            return null;
        }
        IDTYPE itemId = resolveBeanId(bean);
        if (itemId == null) {
            throw new IllegalArgumentException(
                    "Resolved identifier for a bean must not be null");
        }
        return addItemAfter(previousItemId, itemId, bean);
    }

    /**
     * Adds a bean at a specified (filtered view) position in the container
     * using the bean item id resolver to find its identifier.
     * 
     * A bean id resolver must be set before calling this method.
     * 
     * @see #addItemAfter(Object, Object, Object)
     * 
     * @param index
     *            the index (in the filtered view) at which to add the item
     * @param bean
     *            the bean to add
     * @return BeanItem<BEANTYPE> item added or null
     * @throws IllegalStateException
     *             if no bean identifier resolver has been set
     * @throws IllegalArgumentException
     *             if an identifier cannot be resolved for the bean
     */
    protected BeanItem<BEANTYPE> addBeanAt(int index, BEANTYPE bean)
            throws IllegalStateException, IllegalArgumentException {
        if (bean == null) {
            return null;
        }
        IDTYPE itemId = resolveBeanId(bean);
        if (itemId == null) {
            throw new IllegalArgumentException(
                    "Resolved identifier for a bean must not be null");
        }
        return addItemAt(index, itemId, bean);
    }

    /**
     * Adds all the beans from a {@link Collection} in one operation using the
     * bean item identifier resolver. More efficient than adding them one by
     * one.
     * 
     * A bean id resolver must be set before calling this method.
     * 
     * Note: the behavior of this method changed in Vaadin 6.6 - now items are
     * added at the very end of the unfiltered container and not after the last
     * visible item if filtering is used.
     * 
     * @param collection
     *            The collection of beans to add. Must not be null.
     * @throws IllegalStateException
     *             if no bean identifier resolver has been set
     * @throws IllegalArgumentException
     *             if the resolver returns a null itemId for one of the beans in
     *             the collection
     */
    protected void addAll(Collection<? extends BEANTYPE> collection)
            throws IllegalStateException, IllegalArgumentException {
        boolean modified = false;
        for (BEANTYPE bean : collection) {
            // TODO skipping invalid beans - should not allow them in javadoc?
            if (bean == null
                    || !getBeanType().isAssignableFrom(bean.getClass())) {
                continue;
            }
            IDTYPE itemId = resolveBeanId(bean);
            if (itemId == null) {
                throw new IllegalArgumentException(
                        "Resolved identifier for a bean must not be null");
            }

            if (internalAddItemAtEnd(itemId, createBeanItem(bean), false) != null) {
                modified = true;
            }
        }

        if (modified) {
            // Filter the contents when all items have been added
            if (isFiltered()) {
                filterAll();
            } else {
                fireItemSetChange();
            }
        }
    }

    /**
     * Use the bean resolver to get the identifier for a bean.
     * 
     * @param bean
     * @return resolved bean identifier, null if could not be resolved
     * @throws IllegalStateException
     *             if no bean resolver is set
     */
    protected IDTYPE resolveBeanId(BEANTYPE bean) {
        if (beanIdResolver == null) {
            throw new IllegalStateException(
                    "Bean item identifier resolver is required.");
        }
        return beanIdResolver.getIdForBean(bean);
    }

    /**
     * Sets the resolver that finds the item id for a bean, or null not to use
     * automatic resolving.
     * 
     * Methods that add a bean without specifying an id must not be called if no
     * resolver has been set.
     * 
     * Note that methods taking an explicit id can be used whether a resolver
     * has been defined or not.
     * 
     * @param beanIdResolver
     *            to use or null to disable automatic id resolution
     */
    protected void setBeanIdResolver(
            BeanIdResolver<IDTYPE, BEANTYPE> beanIdResolver) {
        this.beanIdResolver = beanIdResolver;
    }

    /**
     * Returns the resolver that finds the item ID for a bean.
     * 
     * @return resolver used or null if automatic item id resolving is disabled
     */
    public BeanIdResolver<IDTYPE, BEANTYPE> getBeanIdResolver() {
        return beanIdResolver;
    }

    /**
     * Create an item identifier resolver using a named bean property.
     * 
     * @param propertyId
     *            property identifier, which must map to a getter in BEANTYPE
     * @return created resolver
     */
    protected BeanIdResolver<IDTYPE, BEANTYPE> createBeanPropertyResolver(
            Object propertyId) {
        return new PropertyBasedBeanIdResolver(propertyId);
    }

    /**
     * @deprecated As of 7.0, replaced by {@link #addPropertySetChangeListener}
     **/
    @Deprecated
    @Override
    public void addListener(Container.PropertySetChangeListener listener) {
        addPropertySetChangeListener(listener);
    }

    @Override
    public void addPropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        super.addPropertySetChangeListener(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removePropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Deprecated
    @Override
    public void removeListener(Container.PropertySetChangeListener listener) {
        removePropertySetChangeListener(listener);
    }

    @Override
    public void removePropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        super.removePropertySetChangeListener(listener);
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Use addNestedContainerProperty(String) to add container properties to a "
                        + getClass().getSimpleName());
    }

    /**
     * Adds a property for the container and all its items.
     * 
     * Primarily for internal use, may change in future versions.
     * 
     * @param propertyId
     * @param propertyDescriptor
     * @return true if the property was added
     */
    protected final boolean addContainerProperty(String propertyId,
            VaadinPropertyDescriptor<BEANTYPE> propertyDescriptor) {
        if (null == propertyId || null == propertyDescriptor) {
            return false;
        }

        // Fails if the Property is already present
        if (model.containsKey(propertyId)) {
            return false;
        }

        model.put(propertyId, propertyDescriptor);
        for (BeanItem<BEANTYPE> item : itemIdToItem.values()) {
            item.addItemProperty(propertyId,
                    propertyDescriptor.createProperty(item.getBean()));
        }

        // Sends a change event
        fireContainerPropertySetChange();

        return true;
    }

    /**
     * Adds a nested container property for the container, e.g.
     * "manager.address.street".
     * 
     * All intermediate getters must exist and should return non-null values
     * when the property value is accessed. If an intermediate getter returns
     * null, a null value will be returned.
     * 
     * @see NestedMethodProperty
     * 
     * @param propertyId
     * @return true if the property was added
     */
    public boolean addNestedContainerProperty(String propertyId) {
        return addContainerProperty(propertyId, new NestedPropertyDescriptor(
                propertyId, type));
    }

    /**
     * Adds a nested container properties for all sub-properties of a named
     * property to the container. The named property itself is removed from the
     * model as its subproperties are added.
     * 
     * All intermediate getters must exist and should return non-null values
     * when the property value is accessed. If an intermediate getter returns
     * null, a null value will be returned.
     * 
     * @see NestedMethodProperty
     * @see #addNestedContainerProperty(String)
     * 
     * @param propertyId
     */
    @SuppressWarnings("unchecked")
    public void addNestedContainerBean(String propertyId) {
        Class<?> propertyType = getType(propertyId);
        LinkedHashMap<String, VaadinPropertyDescriptor<Object>> pds = BeanItem
                .getPropertyDescriptors((Class<Object>) propertyType);
        for (String subPropertyId : pds.keySet()) {
            String qualifiedPropertyId = propertyId + "." + subPropertyId;
            NestedPropertyDescriptor<BEANTYPE> pd = new NestedPropertyDescriptor<BEANTYPE>(
                    qualifiedPropertyId, (Class<BEANTYPE>) type);
            model.put(qualifiedPropertyId, pd);
            model.remove(propertyId);
            for (BeanItem<BEANTYPE> item : itemIdToItem.values()) {
                item.addItemProperty(qualifiedPropertyId,
                        pd.createProperty(item.getBean()));
                item.removeItemProperty(propertyId);
            }
        }

        // Sends a change event
        fireContainerPropertySetChange();
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        // Fails if the Property is not present
        if (!model.containsKey(propertyId)) {
            return false;
        }

        // Removes the Property to Property list and types
        model.remove(propertyId);

        // If remove the Property from all Items
        for (final Iterator<IDTYPE> i = getAllItemIds().iterator(); i.hasNext();) {
            getUnfilteredItem(i.next()).removeItemProperty(propertyId);
        }

        // Sends a change event
        fireContainerPropertySetChange();

        return true;
    }

}
