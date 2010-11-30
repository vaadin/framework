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
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeNotifier;

/**
 * An abstract base class for in-memory containers for JavaBeans.
 * 
 * <p>
 * The properties of the container are determined automatically by introspecting
 * the used JavaBean class. Only beans of the same type can be added to the
 * container.
 * </p>
 * 
 * <p>
 * Subclasses should implement adding items to the container, typically calling
 * the protected methods {@link #addItem(Object, Object)},
 * {@link #addItemAfter(Object, Object, Object)} and
 * {@link #addItemAt(int, Object, Object)}, and if necessary,
 * {@link #internalAddAt(int, Object, Object)} and
 * {@link #internalIndexOf(Object)}.
 * </p>
 * 
 * <p>
 * It is not possible to add additional properties to the container and nested
 * bean properties are not supported.
 * </p>
 * 
 * @param <IDTYPE>
 *            The type of the item identifier
 * @param <BT>
 *            The type of the Bean
 * 
 * @since 6.5
 */
public abstract class AbstractBeanContainer<IDTYPE, BT> implements Indexed,
        Filterable, Sortable, ValueChangeListener, ItemSetChangeNotifier {

    /**
     * The item sorter which is used for sorting the container.
     */
    private ItemSorter itemSorter = new DefaultItemSorter();

    /**
     * Filters currently applied to the container.
     */
    private Set<Filter> filters = new HashSet<Filter>();

    /**
     * The filteredItems variable contains the ids for items that are visible
     * outside the container. If filters are enabled this contains a subset of
     * allItems, if no filters are set this contains the same items as allItems.
     */
    private ListSet<IDTYPE> filteredItemIds = new ListSet<IDTYPE>();

    /**
     * The allItems variable always contains the ids for all the items in the
     * container. Some or all of these are also in the filteredItems list.
     */
    private ListSet<IDTYPE> allItemIds = new ListSet<IDTYPE>();

    /**
     * Maps all item ids in the container (including filtered) to their
     * corresponding BeanItem.
     */
    private final Map<IDTYPE, BeanItem<BT>> itemIdToItem = new HashMap<IDTYPE, BeanItem<BT>>();

    /**
     * The type of the beans in the container.
     */
    private final Class<? super BT> type;

    /**
     * A description of the properties found in beans of type {@link #type}.
     * Determines the property ids that are present in the container.
     */
    private transient LinkedHashMap<String, PropertyDescriptor> model;

    /**
     * Collection of listeners interested in
     * {@link Container.ItemSetChangeEvent ItemSetChangeEvent} events.
     */
    private List<ItemSetChangeListener> itemSetChangeListeners;

    /**
     * Constructs a {@code AbstractBeanContainer} for beans of the given type.
     * 
     * @param type
     *            the type of the beans that will be added to the container.
     * @throws IllegalArgumentException
     *             If {@code type} is null
     */
    protected AbstractBeanContainer(Class<? super BT> type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "The bean type passed to AbstractBeanContainer must not be null");
        }
        this.type = type;
        model = BeanItem.getPropertyDescriptors(type);
    }

    /**
     * A special deserialization method that resolves {@link #model} is needed
     * as PropertyDescriptor is not {@link Serializable}.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        model = BeanItem.getPropertyDescriptors(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    public Class<?> getType(Object propertyId) {
        return model.get(propertyId).getPropertyType();
    }

    /**
     * Create a BeanItem for a bean using pre-parsed bean metadata (based on
     * {@link #getBeanType()}).
     * 
     * @param bean
     * @return
     */
    protected BeanItem<BT> createBeanItem(BT bean) {
        return new BeanItem<BT>(bean, model);
    }

    /**
     * Returns the type of beans this Container can contain.
     * 
     * This comes from the bean type constructor parameter, and bean metadata
     * (including container properties) is based on this.
     * 
     * @return
     */
    public Class<? super BT> getBeanType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */
    public Collection<String> getContainerPropertyIds() {
        return model.keySet();
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
    protected void fireItemSetChange() {
        if (itemSetChangeListeners != null) {
            final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
                public Container getContainer() {
                    return AbstractBeanContainer.this;
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
     * @see com.vaadin.data.Container#size()
     */
    public int size() {
        return filteredItemIds.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#removeAllItems()
     */
    public boolean removeAllItems() {
        allItemIds.clear();
        filteredItemIds.clear();
        // detach listeners from all BeanItems
        for (BeanItem<BT> item : itemIdToItem.values()) {
            removeAllValueChangeListeners(item);
        }
        itemIdToItem.clear();
        fireItemSetChange();
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */
    public boolean containsId(Object itemId) {
        // only look at visible items after filtering
        return filteredItemIds.contains(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    public BeanItem<BT> getItem(Object itemId) {
        return itemIdToItem.get(itemId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getItemIds()
     */
    @SuppressWarnings("unchecked")
    public Collection<IDTYPE> getItemIds() {
        return (Collection<IDTYPE>) filteredItemIds.clone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     * java.lang.Object)
     */
    public Property getContainerProperty(Object itemId, Object propertyId) {
        BeanItem<BT> item = getItem(itemId);
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
    public boolean removeItem(Object itemId) {
        if (!allItemIds.remove(itemId)) {
            return false;
        }
        // detach listeners from Item
        removeAllValueChangeListeners(getItem(itemId));
        // remove item
        itemIdToItem.remove(itemId);
        filteredItemIds.remove(itemId);
        fireItemSetChange();
        return true;
    }

    /**
     * Re-filter the container when one of the monitored properties changes.
     */
    public void valueChange(ValueChangeEvent event) {
        // if a property that is used in a filter is changed, refresh filtering
        filterAll();
    }

    /**
     * Filter the view to recreate the visible item list from the unfiltered
     * items, and send a notification if the set of visible items changed in any
     * way.
     */
    protected void filterAll() {
        // avoid notification if the filtering had no effect
        List<IDTYPE> originalItems = filteredItemIds;
        // it is somewhat inefficient to do a (shallow) clone() every time
        filteredItemIds = (ListSet<IDTYPE>) allItemIds.clone();
        for (Filter f : filters) {
            filter(f);
        }
        // check if exactly the same items are there after filtering to avoid
        // unnecessary notifications
        // this may be slow in some cases as it uses BT.equals()
        if (!originalItems.equals(filteredItemIds)) {
            fireItemSetChange();
        }
    }

    /**
     * Returns an unmodifiable collection of filters in use by the container.
     * 
     * @return
     */
    protected Set<Filter> getFilters() {
        return Collections.unmodifiableSet(filters);
    }

    /**
     * Remove (from the filtered list) any items that do not match the given
     * filter.
     * 
     * @param f
     *            The filter used to determine if items should be removed
     */
    protected void filter(Filter f) {
        Iterator<IDTYPE> iterator = filteredItemIds.iterator();
        while (iterator.hasNext()) {
            IDTYPE itemId = iterator.next();
            if (!f.passesFilter(getItem(itemId))) {
                iterator.remove();
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
            filteredItemIds = (ListSet<IDTYPE>) allItemIds.clone();
        }
        // listen to change events to be able to update filtering
        for (BeanItem<BT> item : itemIdToItem.values()) {
            addValueChangeListener(item, propertyId);
        }
        Filter f = new Filter(propertyId, filterString, ignoreCase,
                onlyMatchPrefix);
        filter(f);
        filters.add(f);
        fireItemSetChange();
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
            for (BeanItem<BT> item : itemIdToItem.values()) {
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
            boolean filteringChanged = false;
            for (Iterator<Filter> iterator = filters.iterator(); iterator
                    .hasNext();) {
                Filter f = iterator.next();
                if (f.propertyId.equals(propertyId)) {
                    iterator.remove();
                    filteringChanged = true;
                }
            }
            if (filteringChanged) {
                // stop listening to change events for the property
                for (BeanItem<BT> item : itemIdToItem.values()) {
                    removeValueChangeListener(item, propertyId);
                }
                filterAll();
            }
        }
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
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    public IDTYPE nextItemId(Object itemId) {
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
    public IDTYPE prevItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index > 0) {
            return getIdByIndex(index - 1);
        } else {
            // out of bounds
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */
    public IDTYPE firstItemId() {
        if (size() > 0) {
            return getIdByIndex(0);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#lastItemId()
     */
    public IDTYPE lastItemId() {
        if (size() > 0) {
            return getIdByIndex(size() - 1);
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
     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
     */
    public IDTYPE getIdByIndex(int index) {
        return filteredItemIds.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
     */
    public int indexOfId(Object itemId) {
        return filteredItemIds.indexOf(itemId);
    }

    /**
     * Unsupported operation. Use other methods to add items.
     */
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation. Beans should be added through
     * {@code addItemAt(int, ...)}.
     */
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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
        Collections.sort(allItemIds, getItemSorter());
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

    /**
     * Unsupported operation. See subclasses of {@link AbstractBeanContainer}
     * for the correct way to add items.
     */
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
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
     * For internal use by subclasses only.
     * 
     * @param position
     *            The position at which the bean should be inserted in the
     *            unfiltered collection of items
     * @param itemId
     *            The item identifier for the bean to insert
     * @param bean
     *            The bean to insert
     * 
     * @return true if the bean was added successfully, false otherwise
     */
    protected BeanItem<BT> internalAddAt(int position, IDTYPE itemId, BT bean) {
        // Make sure that the item has not been added previously
        if (allItemIds.contains(bean)) {
            return null;
        }

        if (!getBeanType().isAssignableFrom(bean.getClass())) {
            return null;
        }

        // "filteredList" will be updated in filterAll() which should be invoked
        // by the caller after calling this method.
        allItemIds.add(position, itemId);
        BeanItem<BT> beanItem = createBeanItem(bean);
        itemIdToItem.put(itemId, beanItem);

        // add listeners to be able to update filtering on property
        // changes
        for (Filter filter : getFilters()) {
            // addValueChangeListener avoids adding duplicates
            addValueChangeListener(beanItem, filter.propertyId);
        }

        return beanItem;
    }

    /**
     * Returns the index of an item within the unfiltered collection of items.
     * 
     * For internal use in subclasses only.
     * 
     * @param itemId
     * @return
     */
    protected int internalIndexOf(IDTYPE itemId) {
        return allItemIds.indexOf(itemId);
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
     * @param bean
     *            bean to be added
     * @return Returns new item or null if the operation fails.
     */
    protected BeanItem<BT> addItemAtInternalIndex(int index, IDTYPE newItemId,
            BT bean) {
        BeanItem<BT> beanItem = internalAddAt(index, newItemId, bean);
        if (beanItem != null) {
            filterAll();
        }

        return beanItem;
    }

    /**
     * Adds the bean to the Container.
     * 
     * @see com.vaadin.data.Container#addItem(Object)
     */
    protected BeanItem<BT> addItem(IDTYPE itemId, BT bean) {
        if (size() > 0) {
            // add immediately after last visible item
            int lastIndex = internalIndexOf(lastItemId());
            return addItemAtInternalIndex(lastIndex + 1, itemId, bean);
        } else {
            return addItemAtInternalIndex(0, itemId, bean);
        }
    }

    /**
     * Adds the bean after the given bean.
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(Object, Object)
     */
    protected BeanItem<BT> addItemAfter(IDTYPE previousItemId,
            IDTYPE newItemId, BT bean) {
        // only add if the previous item is visible
        if (previousItemId == null) {
            return addItemAtInternalIndex(0, newItemId, bean);
        } else if (containsId(previousItemId)) {
            return addItemAtInternalIndex(internalIndexOf(previousItemId) + 1,
                    newItemId, bean);
        } else {
            return null;
        }
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
    protected BeanItem<BT> addItemAt(int index, IDTYPE newItemId, BT bean) {
        if (index < 0 || index > size()) {
            return null;
        } else if (index == 0) {
            // add before any item, visible or not
            return addItemAtInternalIndex(0, newItemId, bean);
        } else {
            // if index==size(), adds immediately after last visible item
            return addItemAfter(getIdByIndex(index - 1), newItemId, bean);
        }
    }

}
