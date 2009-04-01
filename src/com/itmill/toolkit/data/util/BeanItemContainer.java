package com.itmill.toolkit.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Container.Filterable;
import com.itmill.toolkit.data.Container.Indexed;
import com.itmill.toolkit.data.Container.ItemSetChangeNotifier;
import com.itmill.toolkit.data.Container.Sortable;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.data.Property.ValueChangeNotifier;

/**
 * An {@link ArrayList} backed container for {@link BeanItem}s.
 * <p>
 * Bean objects act as identifiers. For this reason, they should implement
 * Object.equals(Object) and Object.hashCode() .
 * 
 * @param <BT>
 * 
 * @since 5.4
 */
public class BeanItemContainer<BT> implements Indexed, Sortable, Filterable,
        ItemSetChangeNotifier, ValueChangeListener {
    // filtered and unfiltered item IDs
    private ArrayList<BT> list = new ArrayList<BT>();
    private ArrayList<BT> allItems = new ArrayList<BT>();
    private final Map<BT, BeanItem> beanToItem = new HashMap<BT, BeanItem>();

    private final Class<BT> type;
    private final BeanItem model;

    private List<ItemSetChangeListener> itemSetChangeListeners;

    private Set<Filter> filters = new HashSet<Filter>();

    public BeanItemContainer(Class<BT> type) throws InstantiationException,
            IllegalAccessException {
        this.type = type;
        BT pojomodel = type.newInstance();
        model = new BeanItem(pojomodel);
    }

    /**
     * Constructs BeanItemContainer with given collection of beans in it.
     * 
     * @param list
     *            non empty {@link Collection} of beans.
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public BeanItemContainer(Collection<BT> list)
            throws InstantiationException, IllegalAccessException {
        type = (Class<BT>) list.iterator().next().getClass();
        BT pojomodel = type.newInstance();
        model = new BeanItem(pojomodel);
        int i = 0;
        for (BT bt : list) {
            addItemAt(i++, bt);
        }
    }

    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Item addItemAt(int index, Object newItemId)
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
     * Adds new item at given index of the internal (unfiltered) list.
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
    @SuppressWarnings("unchecked")
    private Item addItemAtInternalIndex(int index, Object newItemId) {
        // Make sure that the Item has not been created yet
        if (allItems.contains(newItemId)) {
            return null;
        }
        if (newItemId.getClass().isAssignableFrom(type)) {
            BT pojo = (BT) newItemId;
            // "list" will be updated in filterAll()
            allItems.add(index, pojo);
            BeanItem beanItem = new BeanItem(pojo);
            beanToItem.put(pojo, beanItem);
            // add listeners to be able to update filtering on property changes
            for (Filter filter : filters) {
                // addValueChangeListener avoids adding duplicates
                addValueChangeListener(beanItem, filter.propertyId);
            }

            // it is somewhat suboptimal to filter all items
            filterAll();
            return beanItem;
        } else {
            return null;
        }
    }

    public Object getIdByIndex(int index) {
        return list.get(index);
    }

    public int indexOfId(Object itemId) {
        return list.indexOf(itemId);
    }

    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        // only add if the previous item is visible
        if (list.contains(previousItemId)) {
            return addItemAtInternalIndex(allItems.indexOf(previousItemId) + 1,
                    newItemId);
        } else {
            return null;
        }
    }

    public Object firstItemId() {
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public boolean isFirstId(Object itemId) {
        return firstItemId() == itemId;
    }

    public boolean isLastId(Object itemId) {
        return lastItemId() == itemId;
    }

    public Object lastItemId() {
        if (list.size() > 0) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    public Object nextItemId(Object itemId) {
        int index = list.indexOf(itemId);
        if (index >= 0 && index < list.size() - 1) {
            return list.get(index + 1);
        } else {
            // out of bounds
            return null;
        }
    }

    public Object prevItemId(Object itemId) {
        int index = list.indexOf(itemId);
        if (index > 0) {
            return list.get(index - 1);
        } else {
            // out of bounds
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean addContainerProperty(Object propertyId, Class type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public Item addItem(Object itemId) throws UnsupportedOperationException {
        if (list.size() > 0) {
            // add immediately after last visible item
            int lastIndex = allItems.indexOf(lastItemId());
            return addItemAtInternalIndex(lastIndex + 1, itemId);
        } else {
            return addItemAtInternalIndex(0, itemId);
        }
    }

    public boolean containsId(Object itemId) {
        // only look at visible items after filtering
        return list.contains(itemId);
    }

    public Property getContainerProperty(Object itemId, Object propertyId) {
        return beanToItem.get(itemId).getItemProperty(propertyId);
    }

    @SuppressWarnings("unchecked")
    public Collection getContainerPropertyIds() {
        return model.getItemPropertyIds();
    }

    public Item getItem(Object itemId) {
        return beanToItem.get(itemId);
    }

    @SuppressWarnings("unchecked")
    public Collection getItemIds() {
        return (Collection) list.clone();
    }

    public Class<?> getType(Object propertyId) {
        return model.getItemProperty(propertyId).getType();
    }

    public boolean removeAllItems() throws UnsupportedOperationException {
        allItems.clear();
        list.clear();
        // detach listeners from all BeanItems
        for (BeanItem item : beanToItem.values()) {
            removeAllValueChangeListeners(item);
        }
        beanToItem.clear();
        fireItemSetChange();
        return true;
    }

    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        if (!allItems.remove(itemId)) {
            return false;
        }
        // detach listeners from Item
        removeAllValueChangeListeners(beanToItem.get(itemId));
        // remove item
        beanToItem.remove(itemId);
        list.remove(itemId);
        fireItemSetChange();
        return true;
    }

    private void addValueChangeListener(BeanItem beanItem, Object propertyId) {
        Property property = beanItem.getItemProperty(propertyId);
        if (property instanceof ValueChangeNotifier) {
            // avoid multiple notifications for the same property if
            // multiple filters are in use
            ValueChangeNotifier notifier = (ValueChangeNotifier) property;
            notifier.removeListener(this);
            notifier.addListener(this);
        }
    }

    private void removeValueChangeListener(BeanItem item, Object propertyId) {
        Property property = item.getItemProperty(propertyId);
        if (property instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) property).removeListener(this);
        }
    }

    private void removeAllValueChangeListeners(BeanItem item) {
        for (Object propertyId : item.getItemPropertyIds()) {
            removeValueChangeListener(item, propertyId);
        }
    }

    public int size() {
        return list.size();
    }

    public Collection<Object> getSortableContainerPropertyIds() {
        LinkedList<Object> sortables = new LinkedList<Object>();
        for (Object propertyId : getContainerPropertyIds()) {
            Class<?> propertyType = getType(propertyId);
            if (Comparable.class.isAssignableFrom(propertyType)) {
                sortables.add(propertyId);
            }
        }
        return sortables;
    }

    public void sort(Object[] propertyId, boolean[] ascending) {
        for (int i = 0; i < ascending.length; i++) {
            final boolean asc = ascending[i];
            final Object property = propertyId[i];
            // sort allItems, then filter and notify
            Collections.sort(allItems, new Comparator<BT>() {
                @SuppressWarnings("unchecked")
                public int compare(BT a, BT b) {
                    Comparable va = (Comparable) beanToItem.get(a)
                            .getItemProperty(property).getValue();
                    Comparable vb = (Comparable) beanToItem.get(b)
                            .getItemProperty(property).getValue();

                    return asc ? va.compareTo(vb) : vb.compareTo(va);
                }
            });
        }
        // notifies if anything changes in the filtered list, including order
        filterAll();
    }

    public void addListener(ItemSetChangeListener listener) {
        if (itemSetChangeListeners == null) {
            itemSetChangeListeners = new LinkedList<ItemSetChangeListener>();
        }
        itemSetChangeListeners.add(listener);
    }

    public void removeListener(ItemSetChangeListener listener) {
        if (itemSetChangeListeners != null) {
            itemSetChangeListeners.remove(listener);
        }
    }

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

    class Filter {

        private final Object propertyId;
        private final String filterString;
        private final boolean onlyMatchPrefix;
        private final boolean ignoreCase;

        public Filter(Object propertyId, String filterString,
                boolean ignoreCase, boolean onlyMatchPrefix) {
            this.propertyId = propertyId;
            this.ignoreCase = ignoreCase;
            this.filterString = ignoreCase ? filterString.toLowerCase()
                    : filterString;
            this.onlyMatchPrefix = onlyMatchPrefix;
        }

    }

    @SuppressWarnings("unchecked")
    public void addContainerFilter(Object propertyId, String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {
        if (filters.isEmpty()) {
            list = (ArrayList<BT>) allItems.clone();
        }
        // listen to change events to be able to update filtering
        for (BeanItem item : beanToItem.values()) {
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
    @SuppressWarnings("unchecked")
    protected void filterAll() {
        // avoid notification if the filtering had no effect
        List<BT> originalItems = list;
        // it is somewhat inefficient to do a (shallow) clone() every time
        list = (ArrayList<BT>) allItems.clone();
        for (Filter f : filters) {
            filter(f);
        }
        // check if exactly the same items are there after filtering to avoid
        // unnecessary notifications
        // this may be slow in some cases as it uses BT.equals()
        if (!originalItems.equals(list)) {
            fireItemSetChange();
        }
    }

    protected void filter(Filter f) {
        Iterator<BT> iterator = list.iterator();
        while (iterator.hasNext()) {
            BT bean = iterator.next();
            // TODO #2517: should not swallow exceptions - requires several
            // checks
            try {
                String value = getContainerProperty(bean, f.propertyId)
                        .getValue().toString();
                if (f.ignoreCase) {
                    value = value.toLowerCase();
                }
                if (f.onlyMatchPrefix) {
                    if (!value.startsWith(f.filterString)) {
                        iterator.remove();
                    }
                } else {
                    if (!value.contains(f.filterString)) {
                        iterator.remove();
                    }
                }
            } catch (Exception e) {
                iterator.remove();
            }
        }
    }

    public void removeAllContainerFilters() {
        if (!filters.isEmpty()) {
            filters = new HashSet<Filter>();
            // stop listening to change events for any property
            for (BeanItem item : beanToItem.values()) {
                removeAllValueChangeListeners(item);
            }
            filterAll();
        }
    }

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
            for (BeanItem item : beanToItem.values()) {
                removeValueChangeListener(item, propertyId);
            }
            filterAll();
        }
    }

    public void valueChange(ValueChangeEvent event) {
        // if a property that is used in a filter is changed, refresh filtering
        filterAll();
    }

}
