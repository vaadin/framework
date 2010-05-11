/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;

/**
 * An in-memory container for JavaBeans.
 * 
 * <p>
 * The properties of the container are determined automatically by introspecting
 * the used JavaBean class. Only beans of the same type can be added to the
 * container.
 * </p>
 * 
 * <p>
 * BeanItemContainer uses the beans themselves as identifiers. The
 * {@link Object#hashCode()} of a bean is used when storing and looking up beans
 * so it must not change during the lifetime of the bean. Typically this
 * restricts the implementation of {@link Object#equals(Object)} as well so it
 * does not depend on the contents of the bean. This is not strictly needed but
 * the contract between {@code equals()} and {@code hashCode()} must be
 * fulfilled.
 * </p>
 * 
 * <p>
 * It is not possible to add additional properties to the container and nested
 * bean properties are not supported.
 * </p>
 * 
 * @param <BT>
 *            The type of the Bean
 * 
 * @since 5.4
 */
@SuppressWarnings("serial")
public class BeanItemContainer<BT> implements Indexed, Sortable, Filterable,
        ItemSetChangeNotifier, ValueChangeListener {
    /**
     * The filteredItems variable contains the items that are visible outside
     * the container. If filters are enabled this contains a subset of allItems,
     * if no filters are set this contains the same items as allItems.
     */
    private ListSet<BT> filteredItems = new ListSet<BT>();

    /**
     * The allItems variable always contains all the items in the container.
     * Some or all of these are also in the filteredItems list.
     */
    private ListSet<BT> allItems = new ListSet<BT>();

    /**
     * Maps all beans (item ids) in the container (including filtered) to their
     * corresponding BeanItem. This requires the beans to implement
     * {@link Object#equals(Object)} and {@link Object#hashCode()} so it is not
     * affected by the contents of the bean.
     */
    private final Map<BT, BeanItem<BT>> beanToItem = new HashMap<BT, BeanItem<BT>>();

    /**
     * The type of the beans in the container.
     */
    private final Class<? extends BT> type;

    /**
     * A description of the properties found in beans of type {@link #type}.
     * Determines the property ids that are present in the container.
     */
    private transient LinkedHashMap<String, PropertyDescriptor> model;

    /**
     * Collection of listeners interested in {@link ItemSetChangeEvent} events.
     */
    private List<ItemSetChangeListener> itemSetChangeListeners;

    /**
     * Filters currently applied to the container.
     */
    private Set<Filter> filters = new HashSet<Filter>();

    /**
     * The item sorter which is used for sorting the container.
     */
    private ItemSorter itemSorter = new DefaultItemSorter();

    /**
     * A special deserialization method that resolves {@link #model} is needed
     * as PropertyDescriptor is not {@link Serializable}.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        model = BeanItem.getPropertyDescriptors(type);
    }

    /**
     * Constructs a {@code BeanItemContainer} for beans of the given type.
     * 
     * @param type
     *            the type of the beans that will be added to the container.
     * @throws IllegalArgumentException
     *             If {@code type} is null
     */
    public BeanItemContainer(Class<? extends BT> type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "The type passed to BeanItemContainer must not be null");
        }
        this.type = type;
        model = BeanItem.getPropertyDescriptors(type);
    }

    /**
     * Constructs a {@code BeanItemContainer} and adds the given beans to it.
     * The collection must not be empty.
     * {@link BeanItemContainer#BeanItemContainer(Class)} can be used for
     * creating an initially empty {@code BeanItemContainer}.
     * 
     * @param collection
     *            a non empty {@link Collection} of beans.
     * @throws IllegalArgumentException
     *             If the collection is null or empty.
     */
    @SuppressWarnings("unchecked")
    public BeanItemContainer(Collection<BT> collection)
            throws IllegalArgumentException {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(
                    "The collection passed to BeanItemContainer must not be null or empty");
        }

        type = (Class<? extends BT>) collection.iterator().next().getClass();
        model = BeanItem.getPropertyDescriptors(type);
        addAll(collection);
    }

    /**
     * Adds all the beans in {@code collection} in one go. More efficient than
     * adding them one by one.
     * 
     * @param collection
     *            The collection of beans to add. Must not be null.
     */
    private void addAll(Collection<BT> collection) {
        // Pre-allocate space for the collection
        allItems.ensureCapacity(allItems.size() + collection.size());

        int idx = size();
        for (BT bean : collection) {
            if (internalAddAt(idx, bean) != null) {
                idx++;
            }
        }

        // Filter the contents when all items have been added
        filterAll();
    }

    /**
     * Unsupported operation. Beans should be added through
     * {@link #addItemAt(int, Object)}.
     */
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a new bean at the given index.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @param index
     *            Index at which the bean should be added.
     * @param newItemId
     *            The bean to add to the container.
     * @return Returns the new BeanItem or null if the operation fails.
     */
    public BeanItem<BT> addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        if (index < 0 || index > size()) {
            return null;
        } else if (index == 0) {
            // add before any item, visible or not
            return addItemAtInternalIndex(0, newItemId);
        } else {
            // if index==size(), adds immediately after last visible item
            return addItemAfter(getIdByIndex(index - 1), newItemId);
        }
    }

    /**
     * Adds a bean at the given index of the internal (unfiltered) list.
     * <p>
     * The item is also added in the visible part of the list if it passes the
     * filters.
     * </p>
     * 
     * @param index
     *            Internal index to add the new item.
     * @param newItemId
     *            Id of the new item to be added.
     * @return Returns new item or null if the operation fails.
     */
    private BeanItem<BT> addItemAtInternalIndex(int index, Object newItemId) {
        BeanItem<BT> beanItem = internalAddAt(index, (BT) newItemId);
        if (beanItem != null) {
            filterAll();
        }

        return beanItem;
    }

    /**
     * Adds the bean to all internal data structures at the given position.
     * Fails if the bean is already in the container or is not assignable to the
     * correct type. Returns a new BeanItem if the bean was added successfully.
     * 
     * <p>
     * Caller should call {@link #filterAll()} after calling this method to
     * ensure the filtered list is updated.
     * </p>
     * 
     * @param position
     *            The position at which the bean should be inserted
     * @param bean
     *            The bean to insert
     * 
     * @return true if the bean was added successfully, false otherwise
     */
    private BeanItem<BT> internalAddAt(int position, BT bean) {
        // Make sure that the item has not been added previously
        if (allItems.contains(bean)) {
            return null;
        }

        if (!type.isAssignableFrom(bean.getClass())) {
            return null;
        }

        // "filteredList" will be updated in filterAll() which should be invoked
        // by the caller after calling this method.
        allItems.add(position, bean);
        BeanItem<BT> beanItem = new BeanItem<BT>(bean, model);
        beanToItem.put(bean, beanItem);

        // add listeners to be able to update filtering on property
        // changes
        for (Filter filter : filters) {
            // addValueChangeListener avoids adding duplicates
            addValueChangeListener(beanItem, filter.propertyId);
        }

        return beanItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
     */
    public BT getIdByIndex(int index) {
        return filteredItems.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
     */
    public int indexOfId(Object itemId) {
        return filteredItems.indexOf(itemId);
    }

    /**
     * Unsupported operation. Use {@link #addItemAfter(Object, Object)}.
     */
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the bean after the given bean.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    public BeanItem<BT> addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        // only add if the previous item is visible
        if (previousItemId == null) {
            return addItemAtInternalIndex(0, newItemId);
        } else if (containsId(previousItemId)) {
            return addItemAtInternalIndex(allItems.indexOf(previousItemId) + 1,
                    newItemId);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */
    public BT firstItemId() {
        if (size() > 0) {
            return getIdByIndex(0);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
     */
    public boolean isFirstId(Object itemId) {
        return firstItemId() == itemId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */
    public boolean isLastId(Object itemId) {
        return lastItemId() == itemId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#lastItemId()
     */
    public BT lastItemId() {
        if (size() > 0) {
            return getIdByIndex(size() - 1);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    public BT nextItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index >= 0 && index < size() - 1) {
            return getIdByIndex(index + 1);
        } else {
            // out of bounds
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
     */
    public BT prevItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index > 0) {
            return getIdByIndex(index - 1);
        } else {
            // out of bounds
            return null;
        }
    }

    /**
     * Unsupported operation. Properties are determined by the introspecting the
     * bean class.
     */
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation. Use {@link #addBean(Object)}.
     * 
     * @see com.vaadin.data.Container#addItem()
     */
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds the bean to the Container.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    public BeanItem<BT> addBean(BT bean) {
        return addItem(bean);
    }

    /**
     * Adds the bean to the Container.
     * 
     * The bean is used both as the item contents and as the item identifier.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    public BeanItem<BT> addItem(Object itemId)
            throws UnsupportedOperationException {
        if (size() > 0) {
            // add immediately after last visible item
            int lastIndex = allItems.indexOf(lastItemId());
            return addItemAtInternalIndex(lastIndex + 1, itemId);
        } else {
            return addItemAtInternalIndex(0, itemId);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */
    public boolean containsId(Object itemId) {
        // only look at visible items after filtering
        return filteredItems.contains(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     * java.lang.Object)
     */
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return getItem(itemId).getItemProperty(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */
    public Collection<String> getContainerPropertyIds() {
        return model.keySet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    public BeanItem<BT> getItem(Object itemId) {
        return beanToItem.get(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItemIds()
     */
    @SuppressWarnings("unchecked")
    public Collection<BT> getItemIds() {
        return (Collection<BT>) filteredItems.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    public Class<?> getType(Object propertyId) {
        return model.get(propertyId).getPropertyType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */
    public boolean removeAllItems() throws UnsupportedOperationException {
        allItems.clear();
        filteredItems.clear();
        // detach listeners from all BeanItems
        for (BeanItem<BT> item : beanToItem.values()) {
            removeAllValueChangeListeners(item);
        }
        beanToItem.clear();
        fireItemSetChange();
        return true;
    }

    /**
     * Unsupported operation. Properties are determined by the introspecting the
     * bean class.
     */
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        if (!allItems.remove(itemId)) {
            return false;
        }
        // detach listeners from Item
        removeAllValueChangeListeners(getItem(itemId));
        // remove item
        beanToItem.remove(itemId);
        filteredItems.remove(itemId);
        fireItemSetChange();
        return true;
    }

    /**
     * Make this container listen to the given property provided it notifies
     * when its value changes.
     * 
     * @param beanItem
     *            The BeanItem that contains the property
     * @param propertyId
     *            The id of the property
     */
    private void addValueChangeListener(BeanItem<BT> beanItem, Object propertyId) {
        Property property = beanItem.getItemProperty(propertyId);
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
     *            The BeanItem that contains the property
     * @param propertyId
     *            The id of the property
     */
    private void removeValueChangeListener(BeanItem<BT> item, Object propertyId) {
        Property property = item.getItemProperty(propertyId);
        if (property instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) property).removeListener(this);
        }
    }

    /**
     * Remove this contains as a listener for all the properties in the given
     * BeanItem.
     * 
     * @param item
     *            The BeanItem that contains the properties
     */
    private void removeAllValueChangeListeners(BeanItem<BT> item) {
        for (Object propertyId : item.getItemPropertyIds()) {
            removeValueChangeListener(item, propertyId);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#size()
     */
    public int size() {
        return filteredItems.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
     */
    public Collection<Object> getSortableContainerPropertyIds() {
        LinkedList<Object> sortables = new LinkedList<Object>();
        for (Object propertyId : getContainerPropertyIds()) {
            Class<?> propertyType = getType(propertyId);
            if (Comparable.class.isAssignableFrom(propertyType)
                    || propertyType.isPrimitive()) {
                sortables.add(propertyId);
            }
        }
        return sortables;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     * boolean[])
     */
    public void sort(Object[] propertyId, boolean[] ascending) {
        itemSorter.setSortProperties(this, propertyId, ascending);

        doSort();

        // notifies if anything changes in the filtered list, including order
        filterAll();
    }

    /**
     * Perform the sorting of the data structures in the container. This is
     * invoked when the <code>itemSorter</code> has been prepared for the sort
     * operation. Typically this method calls
     * <code>Collections.sort(aCollection, getItemSorter())</code> on all arrays
     * (containing item ids) that need to be sorted.
     * 
     */
    protected void doSort() {
        Collections.sort(allItems, getItemSorter());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com.vaadin
     * .data.Container.ItemSetChangeListener)
     */
    public void addListener(ItemSetChangeListener listener) {
        if (itemSetChangeListeners == null) {
            itemSetChangeListeners = new LinkedList<ItemSetChangeListener>();
        }
        itemSetChangeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin
     * .data.Container.ItemSetChangeListener)
     */
    public void removeListener(ItemSetChangeListener listener) {
        if (itemSetChangeListeners != null) {
            itemSetChangeListeners.remove(listener);
        }
    }

    /**
     * Send an ItemSetChange event to all listeners.
     */
    private void fireItemSetChange() {
        if (itemSetChangeListeners != null) {
            final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
                public Container getContainer() {
                    return BeanItemContainer.this;
                }
            };
            for (ItemSetChangeListener listener : itemSetChangeListeners) {
                listener.containerItemSetChange(event);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.Filterable#addContainerFilter(java.lang.Object,
     * java.lang.String, boolean, boolean)
     */
    public void addContainerFilter(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        if (filters.isEmpty()) {
            filteredItems = (ListSet<BT>) allItems.clone();
        }
        // listen to change events to be able to update filtering
        for (BeanItem<BT> item : beanToItem.values()) {
            addValueChangeListener(item, propertyId);
        }
        Filter f = new Filter(propertyId, filterString, ignoreCase,
                onlyMatchPrefix);
        filter(f);
        filters.add(f);
        fireItemSetChange();
    }

    /**
     * Filter the view to recreate the visible item list from the unfiltered
     * items, and send a notification if the set of visible items changed in any
     * way.
     */
    protected void filterAll() {
        // avoid notification if the filtering had no effect
        List<BT> originalItems = filteredItems;
        // it is somewhat inefficient to do a (shallow) clone() every time
        filteredItems = (ListSet<BT>) allItems.clone();
        for (Filter f : filters) {
            filter(f);
        }
        // check if exactly the same items are there after filtering to avoid
        // unnecessary notifications
        // this may be slow in some cases as it uses BT.equals()
        if (!originalItems.equals(filteredItems)) {
            fireItemSetChange();
        }
    }

    /**
     * Remove (from the filtered list) any items that do not match the given
     * filter.
     * 
     * @param f
     *            The filter used to determine if items should be removed
     */
    protected void filter(Filter f) {
        Iterator<BT> iterator = filteredItems.iterator();
        while (iterator.hasNext()) {
            BT bean = iterator.next();
            if (!f.passesFilter(getItem(bean))) {
                iterator.remove();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Filterable#removeAllContainerFilters()
     */
    public void removeAllContainerFilters() {
        if (!filters.isEmpty()) {
            filters = new HashSet<Filter>();
            // stop listening to change events for any property
            for (BeanItem<BT> item : beanToItem.values()) {
                removeAllValueChangeListeners(item);
            }
            filterAll();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.Filterable#removeContainerFilters(java.lang
     * .Object)
     */
    public void removeContainerFilters(Object propertyId) {
        if (!filters.isEmpty()) {
            for (Iterator<Filter> iterator = filters.iterator(); iterator
                    .hasNext();) {
                Filter f = iterator.next();
                if (f.propertyId.equals(propertyId)) {
                    iterator.remove();
                }
            }
            // stop listening to change events for the property
            for (BeanItem<BT> item : beanToItem.values()) {
                removeValueChangeListener(item, propertyId);
            }
            filterAll();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data
     * .Property.ValueChangeEvent)
     */
    public void valueChange(ValueChangeEvent event) {
        // if a property that is used in a filter is changed, refresh filtering
        filterAll();
    }

    /**
     * Returns the ItemSorter that is used for sorting the container.
     * 
     * @see #setItemSorter(ItemSorter)
     * 
     * @return The ItemSorter that is used for sorting the container
     */
    public ItemSorter getItemSorter() {
        return itemSorter;
    }

    /**
     * Sets the ItemSorter that is used for sorting the container. The
     * {@link ItemSorter#compare(Object, Object)} method is called to compare
     * two beans (item ids).
     * 
     * @param itemSorter
     *            The ItemSorter to use when sorting the container
     */
    public void setItemSorter(ItemSorter itemSorter) {
        this.itemSorter = itemSorter;
    }

}
