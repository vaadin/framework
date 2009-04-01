package com.itmill.toolkit.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Container.Filterable;
import com.itmill.toolkit.data.Container.Indexed;
import com.itmill.toolkit.data.Container.ItemSetChangeNotifier;
import com.itmill.toolkit.data.Container.Sortable;

/**
 * An {@link ArrayList} backed container for {@link BeanItem}s.
 * <p>
 * Bean objects act as identifiers.
 * 
 * @param <BT>
 */
public class BeanItemContainer<BT> implements Indexed, Sortable, Filterable,
        ItemSetChangeNotifier {
    private ArrayList<BT> list = new ArrayList<BT>();
    private final HashMap<BT, BeanItem> beanToItem = new HashMap<BT, BeanItem>();
    private final Class<BT> type;
    private final BeanItem model;
    private LinkedList<ItemSetChangeListener> itemSetChangeListeners;
    private ArrayList<BT> allItems;
    private HashSet<Filter> filters;

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

    @SuppressWarnings("unchecked")
    public Item addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        if (newItemId.getClass().isAssignableFrom(type)) {
            BT pojo = (BT) newItemId;
            list.add(index, pojo);
            BeanItem beanItem = new BeanItem(pojo);
            beanToItem.put(pojo, beanItem);
            fireItemSetChange();
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
        int index = indexOfId(previousItemId) + 1;
        if (index > 0) {
            addItemAt(index, newItemId);
        }
        return null;
    }

    public Object firstItemId() {
        return list.iterator().next();
    }

    public boolean isFirstId(Object itemId) {
        return firstItemId() == itemId;
    }

    public boolean isLastId(Object itemId) {
        return lastItemId() == itemId;
    }

    public Object lastItemId() {
        try {
            return list.get(list.size() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public Object nextItemId(Object itemId) {
        try {
            return list.get(list.indexOf(itemId) + 1);
        } catch (Exception e) {
            // out of bounds
            return null;
        }
    }

    public Object prevItemId(Object itemId) {
        try {
            return list.get(list.indexOf(itemId) - 1);
        } catch (Exception e) {
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
        if (list.isEmpty()) {
            return addItemAt(0, itemId);
        } else {
            return addItemAt(indexOfId(lastItemId()) + 1, itemId);
        }
    }

    public boolean containsId(Object itemId) {
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
        list.clear();
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
        if (list.contains(itemId)) {
            beanToItem.remove(itemId);
            list.remove(itemId);
            fireItemSetChange();
            return true;
        } else {
            return false;
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
            Collections.sort(list, new Comparator<BT>() {
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
            final Object[] l = itemSetChangeListeners.toArray();
            final Container.ItemSetChangeEvent event = new Container.ItemSetChangeEvent() {
                public Container getContainer() {
                    return BeanItemContainer.this;
                }
            };
            for (int i = 0; i < l.length; i++) {
                ((ItemSetChangeListener) l[i]).containerItemSetChange(event);
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
        if (filters == null) {
            allItems = list;
            list = (ArrayList<BT>) list.clone();
            filters = new HashSet<Filter>();
        }
        Filter f = new Filter(propertyId, filterString, ignoreCase,
                onlyMatchPrefix);
        filter(f);
        filters.add(f);
        fireItemSetChange();
    }

    protected void filter(Filter f) {
        for (Iterator<BT> iterator = list.iterator(); iterator.hasNext();) {
            BT bean = iterator.next();
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
        if (filters != null) {
            filters = null;
            list = allItems;
            fireItemSetChange();
        }
    }

    @SuppressWarnings("unchecked")
    public void removeContainerFilters(Object propertyId) {
        if (filters != null) {
            for (Iterator<Filter> iterator = filters.iterator(); iterator
                    .hasNext();) {
                Filter f = iterator.next();
                if (f.propertyId.equals(propertyId)) {
                    iterator.remove();
                }
            }
            list = (ArrayList<BT>) list.clone();
            for (Filter f : filters) {
                filter(f);
            }
            fireItemSetChange();
        }
    }

}
