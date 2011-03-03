package com.vaadin.data.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;

/**
 * Abstract {@link Container} class that handles common functionality for
 * in-memory containers. Concrete in-memory container classes can either inherit
 * this class, inherit {@link AbstractContainer}, or implement the
 * {@link Container} interface directly.
 * 
 * TODO this version does not implement {@link Container.Sortable}
 * 
 * TODO this version does not implement {@link Container.Filterable}
 * 
 * TODO this version does not implement container modification methods
 * 
 * Features:
 * <ul>
 * <li> {@link Container.Ordered}
 * <li> {@link Container.Indexed}
 * </ul>
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
    protected List<ITEMIDTYPE> allItemIds;

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

    // Constructors

    /**
     * Constructor for an abstract in-memory container.
     * 
     * @param allItemIds
     *            the internal {@link List} of item identifiers which must not
     *            be null; used and modified by various operations
     */
    protected AbstractInMemoryContainer(List<ITEMIDTYPE> allItemIds) {
        this.allItemIds = allItemIds;
    }

    // Container interface methods with more specific return class

    public abstract ITEMCLASS getItem(Object itemId);

    // cannot override getContainerPropertyIds() and getItemIds(): if subclass
    // uses Object as ITEMIDCLASS or PROPERTYIDCLASS, Collection<Object> cannot
    // be cast to Collection<MyInterface>

    // public abstract Collection<PROPERTYIDCLASS> getContainerPropertyIds();
    // public abstract Collection<ITEMIDCLASS> getItemIds();

    // Container interface method implementations

    public int size() {
        return getVisibleItemIds().size();
    }

    public boolean containsId(Object itemId) {
        // only look at visible items after filtering
        if (itemId == null) {
            return false;
        } else {
            return getVisibleItemIds().contains(itemId);
        }
    }

    public Collection<?> getItemIds() {
        return Collections.unmodifiableCollection(getVisibleItemIds());
    }

    // Container.Ordered

    public ITEMIDTYPE nextItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index >= 0 && index < size() - 1) {
            return getIdByIndex(index + 1);
        } else {
            // out of bounds
            return null;
        }
    }

    public ITEMIDTYPE prevItemId(Object itemId) {
        int index = indexOfId(itemId);
        if (index > 0) {
            return getIdByIndex(index - 1);
        } else {
            // out of bounds
            return null;
        }
    }

    public ITEMIDTYPE firstItemId() {
        if (size() > 0) {
            return getIdByIndex(0);
        } else {
            return null;
        }
    }

    public ITEMIDTYPE lastItemId() {
        if (size() > 0) {
            return getIdByIndex(size() - 1);
        } else {
            return null;
        }
    }

    public boolean isFirstId(Object itemId) {
        if (itemId == null) {
            return false;
        }
        return itemId.equals(firstItemId());
    }

    public boolean isLastId(Object itemId) {
        if (itemId == null) {
            return false;
        }
        return itemId.equals(lastItemId());
    }

    // Container.Indexed

    public ITEMIDTYPE getIdByIndex(int index) {
        return getVisibleItemIds().get(index);
    }

    public int indexOfId(Object itemId) {
        return getVisibleItemIds().indexOf(itemId);
    }

    // ItemSetChangeNotifier

    @Override
    public void addListener(Container.ItemSetChangeListener listener) {
        super.addListener(listener);
    }

    @Override
    public void removeListener(Container.ItemSetChangeListener listener) {
        super.removeListener(listener);
    }

    // internal methods

    /**
     * Filter the view to recreate the visible item list from the unfiltered
     * items, and send a notification if the set of visible items changed in any
     * way.
     */
    protected abstract void filterAll();

    /**
     * Removes all items from the internal data structures of this class. This
     * can be used to implement {@link #removeAllItems()} in subclasses.
     * 
     * No notification is sent, the caller has to fire a suitable item set
     * change notification.
     */
    protected void internalRemoveAllItems() {
        // Removes all Items
        allItemIds.clear();
        if (getFilteredItemIds() != null) {
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

        boolean result = allItemIds.remove(itemId);
        if (result && getFilteredItemIds() != null) {
            getFilteredItemIds().remove(itemId);
        }

        return result;
    }

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
     * {@link #internalAddItemAt(int, Object, Item)} and
     * {@link #internalAddItemAfter(Object, Object, Item)} instead.
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
        if (position < 0 || position > allItemIds.size() || itemId == null
                || item == null) {
            return null;
        }
        // Make sure that the item has not been added previously
        if (allItemIds.contains(itemId)) {
            return null;
        }

        // "filteredList" will be updated in filterAll() which should be invoked
        // by the caller after calling this method.
        allItemIds.add(position, itemId);
        registerNewItem(position, itemId, item);

        return item;
    }

    /**
     * Add an item at the end of the container, and perform filtering if
     * necessary. An event is fired if the filtered view changes.
     * 
     * The new item is added at the beginning if previousItemId is null.
     * 
     * @param newItemId
     * @param item
     *            new item to add
     * @param filter
     *            true to perform filtering and send event after adding the
     *            item, false to skip them
     * @return item added or null if no item was added
     */
    protected ITEMCLASS internalAddItemAtEnd(ITEMIDTYPE newItemId,
            ITEMCLASS item, boolean filter) {
        ITEMCLASS newItem = internalAddAt(allItemIds.size(), newItemId, item);
        if (newItem != null && filter) {
            filterAll();
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
     * @return item added or null if no item was added
     */
    protected ITEMCLASS internalAddItemAfter(ITEMIDTYPE previousItemId,
            ITEMIDTYPE newItemId, ITEMCLASS item) {
        // only add if the previous item is visible
        ITEMCLASS newItem = null;
        if (previousItemId == null) {
            newItem = internalAddAt(0, newItemId, item);
        } else if (containsId(previousItemId)) {
            newItem = internalAddAt(internalIndexOf(previousItemId) + 1,
                    newItemId, item);
        }
        if (newItem != null) {
            filterAll();
        }
        return newItem;
    }

    /**
     * Add an item at a given (visible) item index, and perform filtering. An
     * event is fired if the filtered view changes.
     * 
     * @param index
     *            position where to att the item (visible/view index)
     * @param newItemId
     * @return item added or null if no item was added
     * @return
     */
    protected ITEMCLASS internalAddItemAt(int index, ITEMIDTYPE newItemId,
            ITEMCLASS item) {
        if (index < 0 || index > size()) {
            return null;
        } else if (index == 0) {
            // add before any item, visible or not
            return internalAddItemAfter(null, newItemId, item);
        } else {
            // if index==size(), adds immediately after last visible item
            return internalAddItemAfter(getIdByIndex(index - 1), newItemId,
                    item);
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

    /**
     * Returns the index of an item within the unfiltered collection of items.
     * 
     * For internal use by subclasses only. This API is experimental and subject
     * to change.
     * 
     * @param itemId
     * @return
     */
    protected int internalIndexOf(ITEMIDTYPE itemId) {
        return allItemIds.indexOf(itemId);
    }

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

    /**
     * Returns the internal list of visible item identifiers after filtering.
     * 
     * For internal use only.
     */
    protected List<ITEMIDTYPE> getVisibleItemIds() {
        if (getFilteredItemIds() != null) {
            return getFilteredItemIds();
        } else {
            return allItemIds;
        }
    }

    /**
     * TODO Temporary internal helper method to set the internal list of
     * filtered item identifiers.
     * 
     * @param filteredItemIds
     */
    protected void setFilteredItemIds(List<ITEMIDTYPE> filteredItemIds) {
        this.filteredItemIds = filteredItemIds;
    }

    /**
     * TODO Temporary internal helper method to get the internal list of
     * filtered item identifiers.
     * 
     * @return List<ITEMIDTYPE>
     */
    protected List<ITEMIDTYPE> getFilteredItemIds() {
        return filteredItemIds;
    }

}
