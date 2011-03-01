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
