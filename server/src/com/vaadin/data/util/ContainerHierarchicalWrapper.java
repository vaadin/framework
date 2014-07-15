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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * <p>
 * A wrapper class for adding external hierarchy to containers not implementing
 * the {@link com.vaadin.data.Container.Hierarchical} interface.
 * </p>
 * 
 * <p>
 * If the wrapped container is changed directly (that is, not through the
 * wrapper), and does not implement Container.ItemSetChangeNotifier and/or
 * Container.PropertySetChangeNotifier the hierarchy information must be updated
 * with the {@link #updateHierarchicalWrapper()} method.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ContainerHierarchicalWrapper implements Container.Hierarchical,
        Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier {

    /** The wrapped container */
    private final Container container;

    /** Set of IDs of those contained Items that can't have children. */
    private HashSet<Object> noChildrenAllowed = null;

    /** Mapping from Item ID to parent Item ID */
    private Hashtable<Object, Object> parent = null;

    /** Mapping from Item ID to a list of child IDs */
    private Hashtable<Object, LinkedList<Object>> children = null;

    /** List that contains all root elements of the container. */
    private LinkedHashSet<Object> roots = null;

    /** Is the wrapped container hierarchical by itself ? */
    private boolean hierarchical;

    /**
     * A comparator that sorts the listed items before other items. Otherwise,
     * the order is undefined.
     */
    private static class ListedItemsFirstComparator implements
            Comparator<Object>, Serializable {
        private final Collection<?> itemIds;

        private ListedItemsFirstComparator(Collection<?> itemIds) {
            this.itemIds = itemIds;
        }

        @Override
        public int compare(Object o1, Object o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            for (Object id : itemIds) {
                if (id == o1) {
                    return -1;
                } else if (id == o2) {
                    return 1;
                }
            }
            return 0;
        }
    }

    /**
     * Constructs a new hierarchical wrapper for an existing Container. Works
     * even if the to-be-wrapped container already implements the
     * <code>Container.Hierarchical</code> interface.
     * 
     * @param toBeWrapped
     *            the container that needs to be accessed hierarchically
     * @see #updateHierarchicalWrapper()
     */
    public ContainerHierarchicalWrapper(Container toBeWrapped) {

        container = toBeWrapped;
        hierarchical = container instanceof Container.Hierarchical;

        // Check arguments
        if (container == null) {
            throw new NullPointerException("Null can not be wrapped");
        }

        // Create initial order if needed
        if (!hierarchical) {
            noChildrenAllowed = new HashSet<Object>();
            parent = new Hashtable<Object, Object>();
            children = new Hashtable<Object, LinkedList<Object>>();
            roots = new LinkedHashSet<Object>(container.getItemIds());
        }

        updateHierarchicalWrapper();

    }

    /**
     * Updates the wrapper's internal hierarchy data to include all Items in the
     * underlying container. If the contents of the wrapped container change
     * without the wrapper's knowledge, this method needs to be called to update
     * the hierarchy information of the Items.
     */
    public void updateHierarchicalWrapper() {

        if (!hierarchical) {

            // Recreate hierarchy and data structures if missing
            if (noChildrenAllowed == null || parent == null || children == null
                    || roots == null) {
                noChildrenAllowed = new HashSet<Object>();
                parent = new Hashtable<Object, Object>();
                children = new Hashtable<Object, LinkedList<Object>>();
                roots = new LinkedHashSet<Object>(container.getItemIds());
            }

            // Check that the hierarchy is up-to-date
            else {

                // ensure order of root and child lists is same as in wrapped
                // container
                Collection<?> itemIds = container.getItemIds();
                Comparator<Object> basedOnOrderFromWrappedContainer = new ListedItemsFirstComparator(
                        itemIds);

                // Calculate the set of all items in the hierarchy
                final HashSet<Object> s = new HashSet<Object>();
                s.addAll(parent.keySet());
                s.addAll(children.keySet());
                s.addAll(roots);

                // Remove unnecessary items
                for (final Iterator<Object> i = s.iterator(); i.hasNext();) {
                    final Object id = i.next();
                    if (!container.containsId(id)) {
                        removeFromHierarchyWrapper(id);
                    }
                }

                // Add all the missing items
                final Collection<?> ids = container.getItemIds();
                for (final Iterator<?> i = ids.iterator(); i.hasNext();) {
                    final Object id = i.next();
                    if (!s.contains(id)) {
                        addToHierarchyWrapper(id);
                        s.add(id);
                    }
                }

                Object[] array = roots.toArray();
                Arrays.sort(array, basedOnOrderFromWrappedContainer);
                roots = new LinkedHashSet<Object>();
                for (int i = 0; i < array.length; i++) {
                    roots.add(array[i]);
                }
                for (Object object : children.keySet()) {
                    LinkedList<Object> object2 = children.get(object);
                    Collections.sort(object2, basedOnOrderFromWrappedContainer);
                }

            }
        }
    }

    /**
     * Removes the specified Item from the wrapper's internal hierarchy
     * structure.
     * <p>
     * Note : The Item is not removed from the underlying Container.
     * </p>
     * 
     * @param itemId
     *            the ID of the item to remove from the hierarchy.
     */
    private void removeFromHierarchyWrapper(Object itemId) {

        LinkedList<Object> oprhanedChildren = children.remove(itemId);
        if (oprhanedChildren != null) {
            for (Object object : oprhanedChildren) {
                // make orphaned children root nodes
                setParent(object, null);
            }
        }

        roots.remove(itemId);
        final Object p = parent.get(itemId);
        if (p != null) {
            final LinkedList<Object> c = children.get(p);
            if (c != null) {
                c.remove(itemId);
            }
        }
        parent.remove(itemId);
        noChildrenAllowed.remove(itemId);
    }

    /**
     * Adds the specified Item specified to the internal hierarchy structure.
     * The new item is added as a root Item. The underlying container is not
     * modified.
     * 
     * @param itemId
     *            the ID of the item to add to the hierarchy.
     */
    private void addToHierarchyWrapper(Object itemId) {
        roots.add(itemId);

    }

    /*
     * Can the specified Item have any children? Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public boolean areChildrenAllowed(Object itemId) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container)
                    .areChildrenAllowed(itemId);
        }

        if (noChildrenAllowed.contains(itemId)) {
            return false;
        }

        return containsId(itemId);
    }

    /*
     * Gets the IDs of the children of the specified Item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<?> getChildren(Object itemId) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).getChildren(itemId);
        }

        final Collection<?> c = children.get(itemId);
        if (c == null) {
            return null;
        }
        return Collections.unmodifiableCollection(c);
    }

    /*
     * Gets the ID of the parent of the specified Item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object getParent(Object itemId) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).getParent(itemId);
        }

        return parent.get(itemId);
    }

    /*
     * Is the Item corresponding to the given ID a leaf node? Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean hasChildren(Object itemId) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).hasChildren(itemId);
        }

        LinkedList<Object> list = children.get(itemId);
        return (list != null && !list.isEmpty());
    }

    /*
     * Is the Item corresponding to the given ID a root node? Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean isRoot(Object itemId) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).isRoot(itemId);
        }

        if (parent.containsKey(itemId)) {
            return false;
        }

        return containsId(itemId);
    }

    /*
     * Gets the IDs of the root elements in the container. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<?> rootItemIds() {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).rootItemIds();
        }

        return Collections.unmodifiableCollection(roots);
    }

    /**
     * <p>
     * Sets the given Item's capability to have children. If the Item identified
     * with the itemId already has children and the areChildrenAllowed is false
     * this method fails and <code>false</code> is returned; the children must
     * be first explicitly removed with
     * {@link #setParent(Object itemId, Object newParentId)} or
     * {@link com.vaadin.data.Container#removeItem(Object itemId)}.
     * </p>
     * 
     * @param itemId
     *            the ID of the Item in the container whose child capability is
     *            to be set.
     * @param childrenAllowed
     *            the boolean value specifying if the Item can have children or
     *            not.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    @Override
    public boolean setChildrenAllowed(Object itemId, boolean childrenAllowed) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).setChildrenAllowed(
                    itemId, childrenAllowed);
        }

        // Check that the item is in the container
        if (!containsId(itemId)) {
            return false;
        }

        // Update status
        if (childrenAllowed) {
            noChildrenAllowed.remove(itemId);
        } else {
            noChildrenAllowed.add(itemId);
        }

        return true;
    }

    /**
     * <p>
     * Sets the parent of an Item. The new parent item must exist and be able to
     * have children. (<code>canHaveChildren(newParentId) == true</code>). It is
     * also possible to detach a node from the hierarchy (and thus make it root)
     * by setting the parent <code>null</code>.
     * </p>
     * 
     * @param itemId
     *            the ID of the item to be set as the child of the Item
     *            identified with newParentId.
     * @param newParentId
     *            the ID of the Item that's to be the new parent of the Item
     *            identified with itemId.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    @Override
    public boolean setParent(Object itemId, Object newParentId) {

        // If the wrapped container implements the method directly, use it
        if (hierarchical) {
            return ((Container.Hierarchical) container).setParent(itemId,
                    newParentId);
        }

        // Check that the item is in the container
        if (!containsId(itemId)) {
            return false;
        }

        // Get the old parent
        final Object oldParentId = parent.get(itemId);

        // Check if no change is necessary
        if ((newParentId == null && oldParentId == null)
                || (newParentId != null && newParentId.equals(oldParentId))) {
            return true;
        }

        // Making root
        if (newParentId == null) {

            // Remove from old parents children list
            final LinkedList<Object> l = children.get(oldParentId);
            if (l != null) {
                l.remove(itemId);
                if (l.isEmpty()) {
                    children.remove(itemId);
                }
            }

            // Add to be a root
            roots.add(itemId);

            // Update parent
            parent.remove(itemId);

            return true;
        }

        // Check that the new parent exists in container and can have
        // children
        if (!containsId(newParentId) || noChildrenAllowed.contains(newParentId)) {
            return false;
        }

        // Check that setting parent doesn't result to a loop
        Object o = newParentId;
        while (o != null && !o.equals(itemId)) {
            o = parent.get(o);
        }
        if (o != null) {
            return false;
        }

        // Update parent
        parent.put(itemId, newParentId);
        LinkedList<Object> pcl = children.get(newParentId);
        if (pcl == null) {
            pcl = new LinkedList<Object>();
            children.put(newParentId, pcl);
        }
        pcl.add(itemId);

        // Remove from old parent or root
        if (oldParentId == null) {
            roots.remove(itemId);
        } else {
            final LinkedList<Object> l = children.get(oldParentId);
            if (l != null) {
                l.remove(itemId);
                if (l.isEmpty()) {
                    children.remove(oldParentId);
                }
            }
        }

        return true;
    }

    /**
     * Creates a new Item into the Container, assigns it an automatic ID, and
     * adds it to the hierarchy.
     * 
     * @return the autogenerated ID of the new Item or <code>null</code> if the
     *         operation failed
     * @throws UnsupportedOperationException
     *             if the addItem is not supported.
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {

        final Object id = container.addItem();
        if (!hierarchical && id != null) {
            addToHierarchyWrapper(id);
        }
        return id;
    }

    /**
     * Adds a new Item by its ID to the underlying container and to the
     * hierarchy.
     * 
     * @param itemId
     *            the ID of the Item to be created.
     * @return the added Item or <code>null</code> if the operation failed.
     * @throws UnsupportedOperationException
     *             if the addItem is not supported.
     */
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {

        // Null ids are not accepted
        if (itemId == null) {
            throw new NullPointerException("Container item id can not be null");
        }

        final Item item = container.addItem(itemId);
        if (!hierarchical && item != null) {
            addToHierarchyWrapper(itemId);
        }
        return item;
    }

    /**
     * Removes all items from the underlying container and from the hierarcy.
     * 
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     * @throws UnsupportedOperationException
     *             if the removeAllItems is not supported.
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {

        final boolean success = container.removeAllItems();

        if (!hierarchical && success) {
            roots.clear();
            parent.clear();
            children.clear();
            noChildrenAllowed.clear();
        }
        return success;
    }

    /**
     * Removes an Item specified by the itemId from the underlying container and
     * from the hierarchy.
     * 
     * @param itemId
     *            the ID of the Item to be removed.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     * @throws UnsupportedOperationException
     *             if the removeItem is not supported.
     */
    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {

        final boolean success = container.removeItem(itemId);

        if (!hierarchical && success) {
            removeFromHierarchyWrapper(itemId);
        }

        return success;
    }

    /**
     * Removes the Item identified by given itemId and all its children.
     * 
     * @see #removeItem(Object)
     * @param itemId
     *            the identifier of the Item to be removed
     * @return true if the operation succeeded
     */
    public boolean removeItemRecursively(Object itemId) {
        return HierarchicalContainer.removeItemRecursively(this, itemId);
    }

    /**
     * Adds a new Property to all Items in the Container.
     * 
     * @param propertyId
     *            the ID of the new Property.
     * @param type
     *            the Data type of the new Property.
     * @param defaultValue
     *            the value all created Properties are initialized to.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     * @throws UnsupportedOperationException
     *             if the addContainerProperty is not supported.
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {

        return container.addContainerProperty(propertyId, type, defaultValue);
    }

    /**
     * Removes the specified Property from the underlying container and from the
     * hierarchy.
     * <p>
     * Note : The Property will be removed from all Items in the Container.
     * </p>
     * 
     * @param propertyId
     *            the ID of the Property to remove.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     * @throws UnsupportedOperationException
     *             if the removeContainerProperty is not supported.
     */
    @Override
    public boolean removeContainerProperty(Object propertyId)
            throws UnsupportedOperationException {
        return container.removeContainerProperty(propertyId);
    }

    /*
     * Does the container contain the specified Item? Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean containsId(Object itemId) {
        return container.containsId(itemId);
    }

    /*
     * Gets the specified Item from the container. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public Item getItem(Object itemId) {
        return container.getItem(itemId);
    }

    /*
     * Gets the ID's of all Items stored in the Container Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<?> getItemIds() {
        return container.getItemIds();
    }

    /*
     * Gets the Property identified by the given itemId and propertyId from the
     * Container Don't add a JavaDoc comment here, we use the default
     * documentation from implemented interface.
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        return container.getContainerProperty(itemId, propertyId);
    }

    /*
     * Gets the ID's of all Properties stored in the Container Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Collection<?> getContainerPropertyIds() {
        return container.getContainerPropertyIds();
    }

    /*
     * Gets the data type of all Properties identified by the given Property ID.
     * Don't add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public Class<?> getType(Object propertyId) {
        return container.getType(propertyId);
    }

    /*
     * Gets the number of Items in the Container. Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    @Override
    public int size() {
        int size = container.size();
        assert size >= 0;
        return size;
    }

    /*
     * Registers a new Item set change listener for this Container. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void addItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (container instanceof Container.ItemSetChangeNotifier) {
            ((Container.ItemSetChangeNotifier) container)
                    .addItemSetChangeListener(new PiggybackListener(listener));
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Container.ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
    }

    /*
     * Removes a Item set change listener from the object. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void removeItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (container instanceof Container.ItemSetChangeNotifier) {
            ((Container.ItemSetChangeNotifier) container)
                    .removeItemSetChangeListener(new PiggybackListener(listener));
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Container.ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
    }

    /*
     * Registers a new Property set change listener for this Container. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public void addPropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        if (container instanceof Container.PropertySetChangeNotifier) {
            ((Container.PropertySetChangeNotifier) container)
                    .addPropertySetChangeListener(new PiggybackListener(
                            listener));
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addPropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Container.PropertySetChangeListener listener) {
        addPropertySetChangeListener(listener);
    }

    /*
     * Removes a Property set change listener from the object. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public void removePropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        if (container instanceof Container.PropertySetChangeNotifier) {
            ((Container.PropertySetChangeNotifier) container)
                    .removePropertySetChangeListener(new PiggybackListener(
                            listener));
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removePropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Container.PropertySetChangeListener listener) {
        removePropertySetChangeListener(listener);
    }

    /**
     * This listener 'piggybacks' on the real listener in order to update the
     * wrapper when needed. It proxies equals() and hashCode() to the real
     * listener so that the correct listener gets removed.
     * 
     */
    private class PiggybackListener implements
            Container.PropertySetChangeListener,
            Container.ItemSetChangeListener {

        Object listener;

        public PiggybackListener(Object realListener) {
            listener = realListener;
        }

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
            updateHierarchicalWrapper();
            ((Container.ItemSetChangeListener) listener)
                    .containerItemSetChange(event);

        }

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            updateHierarchicalWrapper();
            ((Container.PropertySetChangeListener) listener)
                    .containerPropertySetChange(event);

        }

        @Override
        public boolean equals(Object obj) {
            return obj == listener || (obj != null && obj.equals(listener));
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }

    }
}
