/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

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
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.data.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.HashSet;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;

/** <p>A wrapper class for adding external hierarchy to containers not
 * implementing the {@link com.itmill.toolkit.data.Container.Hierarchical}
 * interface.</p>
 * 
 * <p>If the wrapped container is changed directly (that is, not through
 * the wrapper), the hierarchy information must be updated with the
 * {@link #updateHierarchicalWrapper()} method.</p>
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ContainerHierarchicalWrapper
	implements
		Container.Hierarchical,
		Container.ItemSetChangeNotifier,
		Container.PropertySetChangeNotifier {

	/** The wrapped container */
	private Container container;

	/** Set of IDs of those contained Items that can't have children. */
	private HashSet noChildrenAllowed = null;

	/** Mapping from Item ID to parent Item */
	private Hashtable parent = null;

	/** Mapping from Item ID to a list of child IDs */
	private Hashtable children = null;

	/** List that contains all root elements of the container. */
	private LinkedList roots = null;

	/** Is the wrapped container hierarchical by itself ? */
	private boolean hierarchical;

	/** Constructs a new hierarchical wrapper for an existing Container.
	 * Works even if the to-be-wrapped container already implements the
	 * Container.Hierarchical interface.
	 * 
	 * @param toBeWrapped the container that needs to be accessed
	 * hierarchically
	 */
	public ContainerHierarchicalWrapper(Container toBeWrapped) {

		container = toBeWrapped;
		hierarchical = container instanceof Container.Hierarchical;

		// Check arguments
		if (container == null)
			throw new NullPointerException("Null can not be wrapped");

		// Create initial order if needed
		if (!hierarchical) {
			noChildrenAllowed = new HashSet();
			parent = new Hashtable();
			children = new Hashtable();
			roots = new LinkedList(container.getItemIds());
		}

		updateHierarchicalWrapper();
	}

	/** Updates the wrapper's internal hierarchy data to include all Items
	 * in the underlying container. If the contents of the wrapped container
	 * change without the wrapper's knowledge, this method needs to be
	 * called to update the hierarchy information of the Items.
	 */
	public void updateHierarchicalWrapper() {

		if (!hierarchical) {

			// Recreate hierarchy and datasrtuctures if missing
			if (noChildrenAllowed == null
				|| parent == null
				|| children == null
				|| roots == null) {
				noChildrenAllowed = new HashSet();
				parent = new Hashtable();
				children = new Hashtable();
				roots = new LinkedList(container.getItemIds());
			}

			// Check that the hierarchy is up-to-date
			else {

				// Calculate the set of all items in the hierarchy
				HashSet s = new HashSet();
				s.add(parent.keySet());
				s.add(children.keySet());
				s.addAll(roots);

				// Remove unnecessary items
				for (Iterator i = s.iterator(); i.hasNext();) {
					Object id = i.next();
					if (!container.containsId(id))
						removeFromHierarchyWrapper(id);
				}

				// Add all the missing items
				Collection ids = container.getItemIds();
				for (Iterator i = ids.iterator(); i.hasNext();) {
					Object id = i.next();
					if (!s.contains(id)) {
						addToHierarchyWrapper(id);
						s.add(id);
					}
				}
			}
		}
	}

	/** Removes the specified Item from the wrapper's internal hierarchy
	 * structure. Note that the Item is not removed from the underlying
	 * Container.
	 * 
	 * @param itemId ID of the item to remove from the hierarchy
	 */
	private void removeFromHierarchyWrapper(Object itemId) {

		if (isRoot(itemId))
			roots.remove(itemId);
		Object p = parent.get(itemId);
		if (p != null) {
			LinkedList c = (LinkedList) children.get(p);
			if (c != null)
				c.remove(itemId);
		}
		parent.remove(itemId);
		children.remove(itemId);
		noChildrenAllowed.remove(itemId);
	}

	/** Adds the specified Item specified to the internal hierarchy
	 * structure. The new item is added as a root Item. The underlying
	 * container is not modified.
	 * 
	 * @param itemId ID of the item to add to the hierarchy
	 */
	private void addToHierarchyWrapper(Object itemId) {
		roots.add(itemId);
	}

	/* Can the specified Item have any children?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean areChildrenAllowed(Object itemId) {

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).areChildrenAllowed(
				itemId);
		return !noChildrenAllowed.contains(itemId);
	}

	/* Get the IDs of the children of the specified Item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection getChildren(Object itemId) {

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).getChildren(itemId);

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

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).getParent(itemId);

		return parent.get(itemId);
	}

	/* Is the Item corresponding to the given ID a leaf node?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean hasChildren(Object itemId) {

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).hasChildren(itemId);

		return children.get(itemId) != null;
	}

	/* Is the Item corresponding to the given ID a root node?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isRoot(Object itemId) {

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).isRoot(itemId);

		return parent.get(itemId) == null;
	}

	/* Get the IDs of the root elements in the container.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection rootItemIds() {

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).rootItemIds();

		return Collections.unmodifiableCollection(roots);
	}

	/** <p>Sets the given Item's capability to have children. If the Item
	 * identified with <code>itemId</code> already has children and
	 * <code>areChildrenAllowed</code> is false this method fails and
	 * <code>false</code> is returned; the children must be first explicitly
	 * removed with {@link #setParent(Object itemId, Object newParentId)} or
	 * {@link com.itmill.toolkit.data.Container#removeItem(Object itemId)}.</p>
	 * 
	 * @param itemId ID of the Item in the container whose child
	 * capability is to be set
	 * @param childrenAllowed boolean value specifying if the Item
	 * can have children or not
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean setChildrenAllowed(Object itemId, boolean childrenAllowed) {

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).setChildrenAllowed(
				itemId,
				childrenAllowed);

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

		// If the wrapped container implements the method directly, use it
		if (hierarchical)
			return ((Container.Hierarchical) container).setParent(
				itemId,
				newParentId);

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
		while (o != null && !o.equals(itemId))
			o = parent.get(o);
		if (o != null)
			return false;

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

	/** Creates a new Item into the Container, assigns it an
	 * automatic ID, and adds it to the hierarchy. 
	 * 
	 * @return the autogenerated ID of the new Item or <code>null</code>
	 * if the operation failed
	 */
	public Object addItem() throws UnsupportedOperationException {

		Object id = container.addItem();
		if (id != null)
			addToHierarchyWrapper(id);
		return id;
	}

	/** Adds a new Item by its ID to the underlying container and to the
	 * hierarchy.
	 * 
	 * @return the added Item or <code>null</code> if the operation failed
	 */
	public Item addItem(Object itemId) throws UnsupportedOperationException {

		Item item = container.addItem(itemId);
		if (item != null)
			addToHierarchyWrapper(itemId);
		return item;
	}

	/** Removes all items from the underlying container and from the
	 * hierarcy. 
	 * 
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {

		boolean success = container.removeAllItems();

		if (success) {
			roots.clear();
			parent.clear();
			children.clear();
			noChildrenAllowed.clear();
		}
		return success;
	}

	/** Removes an Item specified by <code>itemId</code> from the underlying
	 * container and from the hierarcy.
	 * 
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean removeItem(Object itemId)
		throws UnsupportedOperationException {

		boolean success = container.removeItem(itemId);

		if (success)
			removeFromHierarchyWrapper(itemId);

		return success;
	}

	/** Adds a new Property to all Items in the Container.
	 *
	 * @param propertyId ID of the new Property
	 * @param type Data type of the new Property
	 * @param defaultValue The value all created Properties are
	 * initialized to
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean addContainerProperty(
		Object propertyId,
		Class type,
		Object defaultValue)
		throws UnsupportedOperationException {

		return container.addContainerProperty(propertyId, type, defaultValue);
	}

	/** Removes the specified Property from the underlying container and
	 * from the hierarchy. Note that the Property will be removed from all
	 * Items in the Container.
	 *
	 * @param propertyId ID of the Property to remove
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean removeContainerProperty(Object propertyId)
		throws UnsupportedOperationException {
		return container.removeContainerProperty(propertyId);
	}

	/* Does the container contain the specified Item?
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean containsId(Object itemId) {
		return container.containsId(itemId);
	}

	/* Gets the specified Item from the container.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Item getItem(Object itemId) {
		return container.getItem(itemId);
	}

	/* Gets the ID's of all Items stored in the Container
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection getItemIds() {
		return container.getItemIds();
	}

	/* Gets the Property identified by the given itemId and propertyId from
	 * the Container
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Property getContainerProperty(Object itemId, Object propertyId) {
		return container.getContainerProperty(itemId, propertyId);
	}

	/* Gets the ID's of all Properties stored in the Container
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Collection getContainerPropertyIds() {
		return container.getContainerPropertyIds();
	}

	/* Gets the data type of all Properties identified by the given Property
	 * ID.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Class getType(Object propertyId) {
		return container.getType(propertyId);
	}

	/* Gets the number of Items in the Container.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public int size() {
		return container.size();
	}

	/* Registers a new Item set change listener for this Container. 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void addListener(Container.ItemSetChangeListener listener) {
		if (container instanceof Container.ItemSetChangeNotifier)
			((Container.ItemSetChangeNotifier) container).addListener(listener);
	}

	/* Removes a Item set change listener from the object. 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void removeListener(Container.ItemSetChangeListener listener) {
		if (container instanceof Container.ItemSetChangeNotifier)
			((Container.ItemSetChangeNotifier) container).removeListener(
				listener);
	}

	/* Registers a new Property set change listener for this Container. 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void addListener(Container.PropertySetChangeListener listener) {
		if (container instanceof Container.PropertySetChangeNotifier)
			((Container.PropertySetChangeNotifier) container).addListener(
				listener);
	}

	/* Removes a Property set change listener from the object. 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public void removeListener(Container.PropertySetChangeListener listener) {
		if (container instanceof Container.PropertySetChangeNotifier)
			((Container.PropertySetChangeNotifier) container).removeListener(
				listener);
	}
}
