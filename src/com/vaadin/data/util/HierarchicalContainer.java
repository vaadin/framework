/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * A specialized Container whose contents can be accessed like it was a
 * tree-like structure.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class HierarchicalContainer extends IndexedContainer implements
        Container.Hierarchical {

    /**
     * Set of IDs of those contained Items that can't have children.
     */
    private final HashSet noChildrenAllowed = new HashSet();

    /**
     * Mapping from Item ID to parent Item.
     */
    private final Hashtable parent = new Hashtable();

    /**
     * Mapping from Item ID to a list of child IDs.
     */
    private final Hashtable children = new Hashtable();

    /**
     * List that contains all root elements of the container.
     */
    private final LinkedList roots = new LinkedList();

    /*
     * Can the specified Item have any children? Don't add a JavaDoc comment
     * here, we use the default documentation from implemented interface.
     */
    public boolean areChildrenAllowed(Object itemId) {
        return !noChildrenAllowed.contains(itemId);
    }

    /*
     * Gets the IDs of the children of the specified Item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    public Collection getChildren(Object itemId) {
        final Collection c = (Collection) children.get(itemId);
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
    public Object getParent(Object itemId) {
        return parent.get(itemId);
    }

    /*
     * Is the Item corresponding to the given ID a leaf node? Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    public boolean hasChildren(Object itemId) {
        return children.get(itemId) != null;
    }

    /*
     * Is the Item corresponding to the given ID a root node? Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    public boolean isRoot(Object itemId) {
        return parent.get(itemId) == null;
    }

    /*
     * Gets the IDs of the root elements in the container. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    public Collection rootItemIds() {
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
    public boolean setChildrenAllowed(Object itemId, boolean childrenAllowed) {

        // Checks that the item is in the container
        if (!containsId(itemId)) {
            return false;
        }

        // Updates status
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
    public boolean setParent(Object itemId, Object newParentId) {

        // Checks that the item is in the container
        if (!containsId(itemId)) {
            return false;
        }

        // Gets the old parent
        final Object oldParentId = parent.get(itemId);

        // Checks if no change is necessary
        if ((newParentId == null && oldParentId == null)
                || ((newParentId != null) && newParentId.equals(oldParentId))) {
            return true;
        }

        // Making root
        if (newParentId == null) {

            // Removes from old parents children list
            final LinkedList l = (LinkedList) children.get(itemId);
            if (l != null) {
                l.remove(itemId);
                if (l.isEmpty()) {
                    children.remove(itemId);
                }
            }

            // Add to be a root
            roots.add(itemId);

            // Updates parent
            parent.remove(itemId);

            return true;
        }

        // Checks that the new parent exists in container and can have
        // children
        if (!containsId(newParentId) || noChildrenAllowed.contains(newParentId)) {
            return false;
        }

        // Checks that setting parent doesn't result to a loop
        Object o = newParentId;
        while (o != null && !o.equals(itemId)) {
            o = parent.get(o);
        }
        if (o != null) {
            return false;
        }

        // Updates parent
        parent.put(itemId, newParentId);
        LinkedList pcl = (LinkedList) children.get(newParentId);
        if (pcl == null) {
            pcl = new LinkedList();
            children.put(newParentId, pcl);
        }
        pcl.add(itemId);

        // Removes from old parent or root
        if (oldParentId == null) {
            roots.remove(itemId);
        } else {
            final LinkedList l = (LinkedList) children.get(oldParentId);
            if (l != null) {
                l.remove(itemId);
                if (l.isEmpty()) {
                    children.remove(oldParentId);
                }
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#addItem()
     */
    @Override
    public Object addItem() {
        final Object id = super.addItem();
        if (id != null && !roots.contains(id)) {
            roots.add(id);
        }
        return id;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.IndexedContainer#addItem(java.lang.Object)
     */
    @Override
    public Item addItem(Object itemId) {
        final Item item = super.addItem(itemId);
        if (item != null) {
            roots.add(itemId);
        }
        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.IndexedContainer#removeAllItems()
     */
    @Override
    public boolean removeAllItems() {
        final boolean success = super.removeAllItems();

        if (success) {
            roots.clear();
            parent.clear();
            children.clear();
            noChildrenAllowed.clear();
        }
        return success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.IndexedContainer#removeItem(java.lang.Object
     * )
     */
    @Override
    public boolean removeItem(Object itemId) {
        final boolean success = super.removeItem(itemId);

        if (success) {
            if (isRoot(itemId)) {
                roots.remove(itemId);
            }
            children.remove(itemId);
            final Object p = parent.get(itemId);
            if (p != null) {
                final LinkedList c = (LinkedList) children.get(p);
                if (c != null) {
                    c.remove(itemId);
                }
            }
            parent.remove(itemId);
            noChildrenAllowed.remove(itemId);
        }

        return success;
    }

}
