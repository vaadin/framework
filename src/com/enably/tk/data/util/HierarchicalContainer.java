/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.data.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.HashSet;

import com.enably.tk.data.Container;
import com.enably.tk.data.Item;

/** A specialized Container whose contents can be accessed like it was a
 * tree-like structure.
 *  
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class HierarchicalContainer
	extends IndexedContainer
	implements Container.Hierarchical {

	/** Set of IDs of those contained Items that can't have children. */
	private HashSet noChildrenAllowed = new HashSet();

	/** Mapping from Item ID to parent Item */
	private Hashtable parent = new Hashtable();

	/** Mapping from Item ID to a list of child IDs */
	private Hashtable children = new Hashtable();

	/** List that contains all root elements of the container. */
	private LinkedList roots = new LinkedList();

	/* Can the specified Item have any children?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean areChildrenAllowed(Object itemId) {
		return !noChildrenAllowed.contains(itemId);
	}

	/* Get the IDs of the children of the specified Item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection getChildren(Object itemId) {
		Collection c = (Collection) children.get(itemId);
		if (c == null)
			return null;
		return Collections.unmodifiableCollection(c);
	}

	/* Get the ID of the parent of the specified Item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Object getParent(Object itemId) {
		return parent.get(itemId);
	}

	/* Is the Item corresponding to the given ID a leaf node?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean hasChildren(Object itemId) {
		return children.get(itemId) != null;
	}

	/* Is the Item corresponding to the given ID a root node?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isRoot(Object itemId) {
		return parent.get(itemId) == null;
	}

	/* Get the IDs of the root elements in the container.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection rootItemIds() {
		return Collections.unmodifiableCollection(roots);
	}

    /** <p>Sets the given Item's capability to have children. If the Item
     * identified with <code>itemId</code> already has children and
     * <code>areChildrenAllowed</code> is false this method fails and
     * <code>false</code> is returned; the children must be first explicitly
     * removed with {@link #setParent(Object itemId, Object newParentId)} or
     * {@link com.enably.tk.data.Container#removeItem(Object itemId)}.</p>
     * 
     * @param itemId ID of the Item in the container whose child
     * capability is to be set
     * @param childrenAllowed boolean value specifying if the Item
     * can have children or not
     * @return <code>true</code> if the operation succeeded,
     * <code>false</code> if not
     */
	public boolean setChildrenAllowed(Object itemId, boolean childrenAllowed) {

		// Check that the item is in the container
		if (!containsId(itemId))
			return false;

		// Update status
		if (childrenAllowed)
			noChildrenAllowed.remove(itemId);
		else
			noChildrenAllowed.add(itemId);

		return true;
	}

	/** <p>Sets the parent of an Item. The new parent item must exist and be
	 * able to have children.
	 * (<code>canHaveChildren(newParentId) == true</code>). It is also 
	 * possible to detach a node from the hierarchy (and thus make it root)
	 * by setting the parent <code>null</code>.</p>
	 * 
	 * @param itemId ID of the item to be set as the child of the Item
	 * identified with <code>newParentId</code>
	 * @param newParentId ID of the Item that's to be the new parent
	 * of the Item identified with <code>itemId</code>
     * @return <code>true</code> if the operation succeeded,
     * <code>false</code> if not
	 */
	public boolean setParent(Object itemId, Object newParentId) {

		// Check that the item is in the container
		if (!containsId(itemId))
			return false;

		// Get the old parent
		Object oldParentId = parent.get(itemId);

		// Check if no change is necessary		
		if ((newParentId == null && oldParentId == null)
			|| newParentId.equals(oldParentId))
			return true;

		// Making root		
		if (newParentId == null) {

			// Remove from old parents children list
			LinkedList l = (LinkedList) children.get(itemId);
			if (l != null) {
				l.remove(itemId);
				if (l.isEmpty())
					children.remove(itemId);
			}

			// Add to be a root
			roots.add(itemId);

			// Update parent
			parent.remove(itemId);

			return true;
		}

		// Check that the new parent exists in container and can have
		// children
		if (!containsId(newParentId)
			|| noChildrenAllowed.contains(newParentId))
			return false;

		// Check that setting parent doesn't result to a loop
		Object o = newParentId;
		while (o != null && !o.equals(itemId)) o = parent.get(o);
		if (o != null) return false;

		// Update parent
		parent.put(itemId, newParentId);
		LinkedList pcl = (LinkedList) children.get(newParentId);
		if (pcl == null) {
			pcl = new LinkedList();
			children.put(newParentId, pcl);
		}
		pcl.add(itemId);

		// Remove from old parent or root
		if (oldParentId == null)
			roots.remove(itemId);
		else {
			LinkedList l = (LinkedList) children.get(oldParentId);
			if (l != null) {
				l.remove(itemId);
				if (l.isEmpty())
					children.remove(oldParentId);
			}
		}

		return true;
	}
	/**
	 * @see com.enably.tk.data.Container#addItem()
	 */
	public Object addItem() {
		Object id = super.addItem();
		if (id != null && !roots.contains(id))
			roots.add(id);
		return id;
		
	}

	/**
	 * @see com.enably.tk.data.Container#addItem(Object)
	 */
	public Item addItem(Object itemId) {
		Item item = super.addItem(itemId);
		if (item != null) 
		roots.add(itemId);
		return item;
	}

	/**
	 * @see com.enably.tk.data.Container#removeAllItems()
	 */
	public boolean removeAllItems() {
		boolean success = super.removeAllItems();

		if (success) {
			roots.clear();
			parent.clear();
			children.clear();
			noChildrenAllowed.clear();
		}
		return success;
	}

	/**
	 * @see com.enably.tk.data.Container#removeItem(Object)
	 */
	public boolean removeItem(Object itemId) {
		boolean success = super.removeItem(itemId);
		
		if (success) {
			if (isRoot(itemId)) roots.remove(itemId);
			children.remove(itemId);
			Object p = parent.get(itemId);
			if (p != null) {
				LinkedList c = (LinkedList) children.get(p);
				if (c != null) c.remove(itemId);
			}
			parent.remove(itemId);
			noChildrenAllowed.remove(itemId);
		}
		
		return success;
	}

}
