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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;

/**
 * Abstract {@link Container} class that handles common functionality for
 * in-memory containers. Concrete in-memory container classes can either inherit
 * this class, inherit {@link AbstractContainer}, or implement the
 * {@link Container} interface directly.
 * 
 * Adding and removing items (if desired) must be implemented in subclasses by
 * overriding the appropriate add*Item() and remove*Item() and removeAllItems()
 * methods, calling the corresponding
 * {@link #internalAddItemAfter(Object, Object, Item)},
 * {@link #internalAddItemAt(int, Object, Item)},
 * {@link #internalAddItemAtEnd(Object, Item, boolean)},
 * {@link #internalRemoveItem(Object)} and {@link #internalRemoveAllItems()}
 * methods.
 * 
 * By default, adding and removing container properties is not supported, and
 * subclasses need to implement {@link #getContainerPropertyIds()}. Optionally,
 * subclasses can override {@link #addContainerProperty(Object, Class, Object)}
 * and {@link #removeContainerProperty(Object)} to implement them.
 * 
 * Features:
 * <ul>
 * <li> {@link Container.Ordered}
 * <li> {@link Container.Indexed}
 * <li> {@link Filterable} and {@link SimpleFilterable} (internal implementation,
 * does not implement the interface directly)
 * <li> {@link Sortable} (internal implementation, does not implement the
 * interface directly)
 * </ul>
 * 
 * To implement {@link Sortable}, subclasses need to implement
 * {@link #getSortablePropertyIds()} and call the superclass method
 * {@link #sortContainer(Object[], boolean[])} in the method
 * <code>sort(Object[], boolean[])</code>.
 * 
 * To implement {@link Filterable}, subclasses need to implement the methods
 * {@link Filterable#addContainerFilter(com.vaadin.data.Container.Filter)}
 * (calling {@link #addFilter(Filter)}),
 * {@link Filterable#removeAllContainerFilters()} (calling
 * {@link #removeAllFilters()}) and
 * {@link Filterable#removeContainerFilter(com.vaadin.data.Container.Filter)}
 * (calling {@link #removeFilter(com.vaadin.data.Container.Filter)}).
 * 
 * To implement {@link SimpleFilterable}, subclasses also need to implement the
 * methods
 * {@link SimpleFilterable#addContainerFilter(Object, String, boolean, boolean)}
 * and {@link SimpleFilterable#removeContainerFilters(Object)} calling
 * {@link #addFilter(com.vaadin.data.Container.Filter)} and
 * {@link #removeFilters(Object)} respectively.
 * 
 * @param <ITEMIDTYPE>
 *            the class of item identifiers in the container, use Object if can
 *            be any class
 * @param <PROPERTYIDCLASS>
 *            the class of property identifiers for the items in the container,
 *            use Object if can be any class
 * @param <ITEMCLASS>
 *            the (base) class of the Item instances in the container, use
 *            {@link Item} if unknown
 * 
 * @since 6.6
 */
public abstract class AbstractInMemoryContainer<ITEMIDTYPE, PROPERTYIDCLASS, ITEMCLASS extends Item>
        extends AbstractContainer implements ItemSetChangeNotifier,
        Container.Indexed {

    /**
     * An ordered {@link List} of all item identifiers in the container,
     * including those that have been filtered out.
     * 
     * Must not be null.
     */
    private List<ITEMIDTYPE> allItemIds;

    /**
     * An ordered {@link List} of item identifiers in the container after
     * filtering, excluding those that have been filtered out.
     * 
     * This is what the external API of the {@link Container} interface and its
     * subinterfaces shows (e.g. {@link #size()}, {@link #nextItemId(Object)}).
     * 
     * If null, the full item id list is used instead.
     */
    private List<ITEMIDTYPE> filteredItemIds;

    /**
     * Filters that are applied to the container to limit the items visible in
     * it
     */
    private Set<Filter> filters = new HashSet<Filter>();

    /**
     * The item sorter which is used for sorting the container.
     */
    private ItemSorter itemSorter = new DefaultItemSorter();

    // Constructors

    /**
     * Constructor for an abstract in-memory container.
     */
    protected AbstractInMemoryContainer() {
        setAllItemIds(new ListSet<ITEMIDTYPE>());
    }

    // Container interface methods with more specific return class

    // default implementation, can be overridden
    @Override
    public ITEMCLASS getItem(Object itemId) {
        if (containsId(itemId)) {
            return getUnfilteredItem(itemId);
        } else {
            return null;
        }
    }

    /**
     * Get an item even if filtered out.
     * 
     * For internal use only.
     * 
     * @param itemId
     * @return
     */
    protected abstract ITEMCLASS getUnfilteredItem(Object itemId);

    // cannot override getContainerPropertyIds() and getItemIds(): if subclass
    // uses Object as ITEMIDCLASS or PROPERTYIDCLASS, Collection<Object> cannot
    // be cast to Collection<MyInterface>

    // public abstract Collection<PROPERTYIDCLASS> getContainerPropertyIds();
    // public abstract Collection<ITEMIDCLASS> getItemIds();

    // Container interface method implementations

    @Override
    public int size() {
        return getVisibleItemIds().size();
    }

    @Override
    public boolean containsId(Object itemId) {
        // only look at visible items after filtering
        if (itemId == null) {
            return false;
        } else {
            return getVisibleItemIds().contains(itemId);
        }
    }

    @Override
    public List<?> getItemIds() {
        return Collections.unmodifiableList(getVisibleItemIds());
    }

    // Container.Ordered

    @Override
    public ITEMIDTYPE nextItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index >= 0 && index < size() - 1) {
            return getIdByIndex(index + 1);
        } else {
            // out of bounds
            return null;
        }
    }

    @Override
    public ITEMIDTYPE prevItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index > 0) {
            return getIdByIndex(index - 1);
        } else {
            // out of bounds
            return null;
        }
    }

    @Override
    public ITEMIDTYPE firstItemId() {
        if (size() > 0) {
            return getIdByIndex(0);
        } else {
            return null;
        }
    }

    @Override
    public ITEMIDTYPE lastItemId() {
        if (size() > 0) {
            return getIdByIndex(size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public boolean isFirstId(Object itemId) {
        if (itemId == null) {
            return false;
        }
        return itemId.equals(firstItemId());
    }

    @Override
    public boolean isLastId(Object itemId) {
        if (itemId == null) {
            return false;
        }
        return itemId.equals(lastItemId());
    }

    // Container.Indexed

    @Override
    public ITEMIDTYPE getIdByIndex(int index) {
        return getVisibleItemIds().get(index);
    }

    @Override
    public List<ITEMIDTYPE> getItemIds(int startIndex, int numberOfIds) {
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException(
                    "Start index cannot be negative! startIndex=" + startIndex);
        }

        if (startIndex > getVisibleItemIds().size()) {
            throw new IndexOutOfBoundsException(
                    "Start index exceeds container size! startIndex="
                            + startIndex + " containerLastItemIndex="
                            + (getVisibleItemIds().size() - 1));
        }

        if (numberOfIds < 1) {
            if (numberOfIds == 0) {
                return Collections.emptyList();
            }

            throw new IllegalArgumentException(
                    "Cannot get negative amount of items! numberOfItems="
                            + numberOfIds);
        }

        int endIndex = startIndex + numberOfIds;

        if (endIndex > getVisibleItemIds().size()) {
            endIndex = getVisibleItemIds().size();
        }

        return Collections.unmodifiableList(getVisibleItemIds().subList(
                startIndex, endIndex));

    }

    @Override
    public int indexOfId(Object itemId) {
        return getVisibleItemIds().indexOf(itemId);
    }

    // methods that are unsupported by default, override to support

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding items not supported. Override the relevant addItem*() methods if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public Item addItemAt(int index, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding items not supported. Override the relevant addItem*() methods if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding items not supported. Override the relevant addItem*() methods if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding items not supported. Override the relevant addItem*() methods if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding items not supported. Override the relevant addItem*() methods if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding items not supported. Override the relevant addItem*() methods if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Removing items not supported. Override the removeItem() method if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Removing items not supported. Override the removeAllItems() method if required as specified in AbstractInMemoryContainer javadoc.");
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Adding container properties not supported. Override the addContainerProperty() method if required.");
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "Removing container properties not supported. Override the addContainerProperty() method if required.");
    }

    // ItemSetChangeNotifier
    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Deprecated
    @Override
    public void addListener(Container.ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
    }

    @Override
    public void addItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        super.addItemSetChangeListener(listener);
    }

    @Override
    public void removeItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        super.removeItemSetChangeListener(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Deprecated
    @Override
    public void removeListener(Container.ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
    }

    // internal methods

    // Filtering support

    /**
     * Filter the view to recreate the visible item list from the unfiltered
     * items, and send a notification if the set of visible items changed in any
     * way.
     */
    protected void filterAll() {
        if (doFilterContainer(!getFilters().isEmpty())) {
            fireItemSetChange();
        }
    }

    /**
     * Filters the data in the container and updates internal data structures.
     * This method should reset any internal data structures and then repopulate
     * them so {@link #getItemIds()} and other methods only return the filtered
     * items.
     * 
     * @param hasFilters
     *            true if filters has been set for the container, false
     *            otherwise
     * @return true if the item set has changed as a result of the filtering
     */
    protected boolean doFilterContainer(boolean hasFilters) {
        if (!hasFilters) {
            boolean changed = getAllItemIds().size() != getVisibleItemIds()
                    .size();
            setFilteredItemIds(null);
            return changed;
        }

        // Reset filtered list
        List<ITEMIDTYPE> originalFilteredItemIds = getFilteredItemIds();
        boolean wasUnfiltered = false;
        if (originalFilteredItemIds == null) {
            originalFilteredItemIds = Collections.emptyList();
            wasUnfiltered = true;
        }
        setFilteredItemIds(new ListSet<ITEMIDTYPE>());

        // Filter
        boolean equal = true;
        Iterator<ITEMIDTYPE> origIt = originalFilteredItemIds.iterator();
        for (final Iterator<ITEMIDTYPE> i = getAllItemIds().iterator(); i
                .hasNext();) {
            final ITEMIDTYPE id = i.next();
            if (passesFilters(id)) {
                // filtered list comes from the full list, can use ==
                equal = equal && origIt.hasNext() && origIt.next() == id;
                getFilteredItemIds().add(id);
            }
        }

        return (wasUnfiltered && !getAllItemIds().isEmpty()) || !equal
                || origIt.hasNext();
    }

    /**
     * Checks if the given itemId passes the filters set for the container. The
     * caller should make sure the itemId exists in the container. For
     * non-existing itemIds the behavior is undefined.
     * 
     * @param itemId
     *            An itemId that exists in the container.
     * @return true if the itemId passes all filters or no filters are set,
     *         false otherwise.
     */
    protected boolean passesFilters(Object itemId) {
        ITEMCLASS item = getUnfilteredItem(itemId);
        if (getFilters().isEmpty()) {
            return true;
        }
        final Iterator<Filter> i = getFilters().iterator();
        while (i.hasNext()) {
            final Filter f = i.next();
            if (!f.passesFilter(itemId, item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a container filter and re-filter the view.
     * 
     * The filter must implement Filter and its sub-filters (if any) must also
     * be in-memory filterable.
     * 
     * This can be used to implement
     * {@link Filterable#addContainerFilter(com.vaadin.data.Container.Filter)}
     * and optionally also
     * {@link SimpleFilterable#addContainerFilter(Object, String, boolean, boolean)}
     * (with {@link SimpleStringFilter}).
     * 
     * Note that in some cases, incompatible filters cannot be detected when
     * added and an {@link UnsupportedFilterException} may occur when performing
     * filtering.
     * 
     * @throws UnsupportedFilterException
     *             if the filter is detected as not supported by the container
     */
    protected void addFilter(Filter filter) throws UnsupportedFilterException {
        getFilters().add(filter);
        filterAll();
    }

    /**
     * Returns true if any filters have been applied to the container.
     * 
     * @return true if the container has filters applied, false otherwise
     * @since 7.1
     */
    protected boolean hasContainerFilters() {
        return !getContainerFilters().isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Filterable#getContainerFilters()
     */
    protected Collection<Filter> getContainerFilters() {
        return Collections.unmodifiableCollection(filters);
    }

    /**
     * Remove a specific container filter and re-filter the view (if necessary).
     * 
     * This can be used to implement
     * {@link Filterable#removeContainerFilter(com.vaadin.data.Container.Filter)}
     * .
     */
    protected void removeFilter(Filter filter) {
        for (Iterator<Filter> iterator = getFilters().iterator(); iterator
                .hasNext();) {
            Filter f = iterator.next();
            if (f.equals(filter)) {
                iterator.remove();
                filterAll();
                return;
            }
        }
    }

    /**
     * Remove all container filters for all properties and re-filter the view.
     * 
     * This can be used to implement
     * {@link Filterable#removeAllContainerFilters()}.
     */
    protected void removeAllFilters() {
        if (getFilters().isEmpty()) {
            return;
        }
        getFilters().clear();
        filterAll();
    }

    /**
     * Checks if there is a filter that applies to a given property.
     * 
     * @param propertyId
     * @return true if there is an active filter for the property
     */
    protected boolean isPropertyFiltered(Object propertyId) {
        if (getFilters().isEmpty() || propertyId == null) {
            return false;
        }
        final Iterator<Filter> i = getFilters().iterator();
        while (i.hasNext()) {
            final Filter f = i.next();
            if (f.appliesToProperty(propertyId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove all container filters for a given property identifier and
     * re-filter the view. This also removes filters applying to multiple
     * properties including the one identified by propertyId.
     * 
     * This can be used to implement
     * {@link Filterable#removeContainerFilters(Object)}.
     * 
     * @param propertyId
     * @return Collection<Filter> removed filters
     */
    protected Collection<Filter> removeFilters(Object propertyId) {
        if (getFilters().isEmpty() || propertyId == null) {
            return Collections.emptyList();
        }
        List<Filter> removedFilters = new LinkedList<Filter>();
        for (Iterator<Filter> iterator = getFilters().iterator(); iterator
                .hasNext();) {
            Filter f = iterator.next();
            if (f.appliesToProperty(propertyId)) {
                removedFilters.add(f);
                iterator.remove();
            }
        }
        if (!removedFilters.isEmpty()) {
            filterAll();
            return removedFilters;
        }
        return Collections.emptyList();
    }

    // sorting

    /**
     * Returns the ItemSorter used for comparing items in a sort. See
     * {@link #setItemSorter(ItemSorter)} for more information.
     * 
     * @return The ItemSorter used for comparing two items in a sort.
     */
    protected ItemSorter getItemSorter() {
        return itemSorter;
    }

    /**
     * Sets the ItemSorter used for comparing items in a sort. The
     * {@link ItemSorter#compare(Object, Object)} method is called with item ids
     * to perform the sorting. A default ItemSorter is used if this is not
     * explicitly set.
     * 
     * @param itemSorter
     *            The ItemSorter used for comparing two items in a sort (not
     *            null).
     */
    protected void setItemSorter(ItemSorter itemSorter) {
        this.itemSorter = itemSorter;
    }

    /**
     * Sort base implementation to be used to implement {@link Sortable}.
     * 
     * Subclasses should call this from a public
     * {@link #sort(Object[], boolean[])} method when implementing Sortable.
     * 
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     *      boolean[])
     */
    protected void sortContainer(Object[] propertyId, boolean[] ascending) {
        if (!(this instanceof Sortable)) {
            throw new UnsupportedOperationException(
                    "Cannot sort a Container that does not implement Sortable");
        }

        // Set up the item sorter for the sort operation
        getItemSorter().setSortProperties((Sortable) this, propertyId,
                ascending);

        // Perform the actual sort
        doSort();

        // Post sort updates
        if (isFiltered()) {
            filterAll();
        } else {
            fireItemSetChange();
        }

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
        Collections.sort(getAllItemIds(), getItemSorter());
    }

    /**
     * Returns the sortable property identifiers for the container. Can be used
     * to implement {@link Sortable#getSortableContainerPropertyIds()}.
     */
    protected Collection<?> getSortablePropertyIds() {
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

    // removing items

    /**
     * Removes all items from the internal data structures of this class. This
     * can be used to implement {@link #removeAllItems()} in subclasses.
     * 
     * No notification is sent, the caller has to fire a suitable item set
     * change notification.
     */
    protected void internalRemoveAllItems() {
        // Removes all Items
        getAllItemIds().clear();
        if (isFiltered()) {
            getFilteredItemIds().clear();
        }
    }

    /**
     * Removes a single item from the internal data structures of this class.
     * This can be used to implement {@link #removeItem(Object)} in subclasses.
     * 
     * No notification is sent, the caller has to fire a suitable item set
     * change notification.
     * 
     * @param itemId
     *            the identifier of the item to remove
     * @return true if an item was successfully removed, false if failed to
     *         remove or no such item
     */
    protected boolean internalRemoveItem(Object itemId) {
        if (itemId == null) {
            return false;
        }

        boolean result = getAllItemIds().remove(itemId);
        if (result && isFiltered()) {
            getFilteredItemIds().remove(itemId);
        }

        return result;
    }

    // adding items

    /**
     * Adds the bean to all internal data structures at the given position.
     * Fails if an item with itemId is already in the container. Returns a the
     * item if it was added successfully, null otherwise.
     * 
     * <p>
     * Caller should initiate filtering after calling this method.
     * </p>
     * 
     * For internal use only - subclasses should use
     * {@link #internalAddItemAtEnd(Object, Item, boolean)},
     * {@link #internalAddItemAt(int, Object, Item, boolean)} and
     * {@link #internalAddItemAfter(Object, Object, Item, boolean)} instead.
     * 
     * @param position
     *            The position at which the item should be inserted in the
     *            unfiltered collection of items
     * @param itemId
     *            The item identifier for the item to insert
     * @param item
     *            The item to insert
     * 
     * @return ITEMCLASS if the item was added successfully, null otherwise
     */
    private ITEMCLASS internalAddAt(int position, ITEMIDTYPE itemId,
            ITEMCLASS item) {
        if (position < 0 || position > getAllItemIds().size() || itemId == null
                || item == null) {
            return null;
        }
        // Make sure that the item has not been added previously
        if (getAllItemIds().contains(itemId)) {
            return null;
        }

        // "filteredList" will be updated in filterAll() which should be invoked
        // by the caller after calling this method.
        getAllItemIds().add(position, itemId);
        registerNewItem(position, itemId, item);

        return item;
    }

    /**
     * Add an item at the end of the container, and perform filtering if
     * necessary. An event is fired if the filtered view changes.
     * 
     * @param newItemId
     * @param item
     *            new item to add
     * @param filter
     *            true to perform filtering and send event after adding the
     *            item, false to skip these operations for batch inserts - if
     *            false, caller needs to make sure these operations are
     *            performed at the end of the batch
     * @return item added or null if no item was added
     */
    protected ITEMCLASS internalAddItemAtEnd(ITEMIDTYPE newItemId,
            ITEMCLASS item, boolean filter) {
        ITEMCLASS newItem = internalAddAt(getAllItemIds().size(), newItemId,
                item);
        if (newItem != null && filter) {
            // TODO filter only this item, use fireItemAdded()
            filterAll();
            if (!isFiltered()) {
                // TODO hack: does not detect change in filterAll() in this case
                fireItemAdded(indexOfId(newItemId), newItemId, item);
            }
        }
        return newItem;
    }

    /**
     * Add an item after a given (visible) item, and perform filtering. An event
     * is fired if the filtered view changes.
     * 
     * The new item is added at the beginning if previousItemId is null.
     * 
     * @param previousItemId
     *            item id of a visible item after which to add the new item, or
     *            null to add at the beginning
     * @param newItemId
     * @param item
     *            new item to add
     * @param filter
     *            true to perform filtering and send event after adding the
     *            item, false to skip these operations for batch inserts - if
     *            false, caller needs to make sure these operations are
     *            performed at the end of the batch
     * @return item added or null if no item was added
     */
    protected ITEMCLASS internalAddItemAfter(ITEMIDTYPE previousItemId,
            ITEMIDTYPE newItemId, ITEMCLASS item, boolean filter) {
        // only add if the previous item is visible
        ITEMCLASS newItem = null;
        if (previousItemId == null) {
            newItem = internalAddAt(0, newItemId, item);
        } else if (containsId(previousItemId)) {
            newItem = internalAddAt(
                    getAllItemIds().indexOf(previousItemId) + 1, newItemId,
                    item);
        }
        if (newItem != null && filter) {
            // TODO filter only this item, use fireItemAdded()
            filterAll();
            if (!isFiltered()) {
                // TODO hack: does not detect change in filterAll() in this case
                fireItemAdded(indexOfId(newItemId), newItemId, item);
            }
        }
        return newItem;
    }

    /**
     * Add an item at a given (visible after filtering) item index, and perform
     * filtering. An event is fired if the filtered view changes.
     * 
     * @param index
     *            position where to add the item (visible/view index)
     * @param newItemId
     * @param item
     *            new item to add
     * @param filter
     *            true to perform filtering and send event after adding the
     *            item, false to skip these operations for batch inserts - if
     *            false, caller needs to make sure these operations are
     *            performed at the end of the batch
     * @return item added or null if no item was added
     */
    protected ITEMCLASS internalAddItemAt(int index, ITEMIDTYPE newItemId,
            ITEMCLASS item, boolean filter) {
        if (index < 0 || index > size()) {
            return null;
        } else if (index == 0) {
            // add before any item, visible or not
            return internalAddItemAfter(null, newItemId, item, filter);
        } else {
            // if index==size(), adds immediately after last visible item
            return internalAddItemAfter(getIdByIndex(index - 1), newItemId,
                    item, filter);
        }
    }

    /**
     * Registers a new item as having been added to the container. This can
     * involve storing the item or any relevant information about it in internal
     * container-specific collections if necessary, as well as registering
     * listeners etc.
     * 
     * The full identifier list in {@link AbstractInMemoryContainer} has already
     * been updated to reflect the new item when this method is called.
     * 
     * @param position
     * @param itemId
     * @param item
     */
    protected void registerNewItem(int position, ITEMIDTYPE itemId,
            ITEMCLASS item) {
    }

    // item set change notifications

    /**
     * Notify item set change listeners that an item has been added to the
     * container.
     * 
     * Unless subclasses specify otherwise, the default notification indicates a
     * full refresh.
     * 
     * @param postion
     *            position of the added item in the view (if visible)
     * @param itemId
     *            id of the added item
     * @param item
     *            the added item
     */
    protected void fireItemAdded(int position, ITEMIDTYPE itemId, ITEMCLASS item) {
        fireItemSetChange();
    }

    /**
     * Notify item set change listeners that an item has been removed from the
     * container.
     * 
     * Unless subclasses specify otherwise, the default notification indicates a
     * full refresh.
     * 
     * @param postion
     *            position of the removed item in the view prior to removal (if
     *            was visible)
     * @param itemId
     *            id of the removed item, of type {@link Object} to satisfy
     *            {@link Container#removeItem(Object)} API
     */
    protected void fireItemRemoved(int position, Object itemId) {
        fireItemSetChange();
    }

    // visible and filtered item identifier lists

    /**
     * Returns the internal list of visible item identifiers after filtering.
     * 
     * For internal use only.
     */
    protected List<ITEMIDTYPE> getVisibleItemIds() {
        if (isFiltered()) {
            return getFilteredItemIds();
        } else {
            return getAllItemIds();
        }
    }

    /**
     * Returns true is the container has active filters.
     * 
     * @return true if the container is currently filtered
     */
    protected boolean isFiltered() {
        return filteredItemIds != null;
    }

    /**
     * Internal helper method to set the internal list of filtered item
     * identifiers. Should not be used outside this class except for
     * implementing clone(), may disappear from future versions.
     * 
     * @param filteredItemIds
     */
    @Deprecated
    protected void setFilteredItemIds(List<ITEMIDTYPE> filteredItemIds) {
        this.filteredItemIds = filteredItemIds;
    }

    /**
     * Internal helper method to get the internal list of filtered item
     * identifiers. Should not be used outside this class except for
     * implementing clone(), may disappear from future versions - use
     * {@link #getVisibleItemIds()} in other contexts.
     * 
     * @return List<ITEMIDTYPE>
     */
    protected List<ITEMIDTYPE> getFilteredItemIds() {
        return filteredItemIds;
    }

    /**
     * Internal helper method to set the internal list of all item identifiers.
     * Should not be used outside this class except for implementing clone(),
     * may disappear from future versions.
     * 
     * @param allItemIds
     */
    @Deprecated
    protected void setAllItemIds(List<ITEMIDTYPE> allItemIds) {
        this.allItemIds = allItemIds;
    }

    /**
     * Internal helper method to get the internal list of all item identifiers.
     * Avoid using this method outside this class, may disappear in future
     * versions.
     * 
     * @return List<ITEMIDTYPE>
     */
    protected List<ITEMIDTYPE> getAllItemIds() {
        return allItemIds;
    }

    /**
     * Set the internal collection of filters without performing filtering.
     * 
     * This method is mostly for internal use, use
     * {@link #addFilter(com.vaadin.data.Container.Filter)} and
     * <code>remove*Filter*</code> (which also re-filter the container) instead
     * when possible.
     * 
     * @param filters
     */
    protected void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }

    /**
     * Returns the internal collection of filters. The returned collection
     * should not be modified by callers outside this class.
     * 
     * @return Set<Filter>
     */
    protected Set<Filter> getFilters() {
        return filters;
    }

}
