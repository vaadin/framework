/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */


package com.enably.tk.data.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Hashtable;

import com.enably.tk.data.Container;
import com.enably.tk.data.Item;
import com.enably.tk.data.Property;

/** <p>A wrapper class for adding external ordering to containers not
 * implementing the {@link com.enably.tk.data.Container.Ordered}
 * interface.</p>
 * 
 * <p>If the wrapped container is changed directly (that is, not through
 * the wrapper), the ordering must be updated with the
 * {@link #updateOrderWrapper()} method.</p>
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class ContainerOrderedWrapper
	implements
		Container.Ordered,
		Container.ItemSetChangeNotifier,
		Container.PropertySetChangeNotifier {

	/** The wrapped container */
	private Container container;

	/** Ordering information, ie. the mapping from Item ID to the next
	 * item ID
	 */
	private Hashtable next;

	/** Reverse ordering information for convenience and performance
	 * reasons.
	 */
	private Hashtable prev;

	/** ID of the first Item in the container. */
	private Object first;

	/** ID of the last Item in the container. */
	private Object last;

	/** Is the wrapped container ordered by itself, ie. does it implement
	 * the Container.Ordered interface by itself? If it does, this class
	 * will use the methods of the underlying container directly.
	 */
	private boolean ordered = false;

	/** Constructs a new ordered wrapper for an existing Container. Works
	 * even if the to-be-wrapped container already implements the
	 * Container.Ordered interface.
	 * 
	 * @param toBeWrapped the container whose contents need to be ordered
	 */
	public ContainerOrderedWrapper(Container toBeWrapped) {

		container = toBeWrapped;
		ordered = container instanceof Container.Ordered;

		// Check arguments
		if (container == null)
			throw new NullPointerException("Null can not be wrapped");

		// Create initial order if needed
		updateOrderWrapper();
	}

	/** Removes the specified Item from the wrapper's internal hierarchy
	 * structure. Note that the Item is not removed from the underlying
	 * Container.
	 * 
	 * @param id ID of the Item to be removed from the ordering
	 */
	private void removeFromOrderWrapper(Object id) {
		if (id != null) {
			Object pid = prev.get(id);
			Object nid = next.get(id);
			if (first.equals(id))
				first = nid;
			if (last.equals(id))
				first = pid;
			if (nid != null)
				prev.put(nid, pid);
			if (pid != null)
				next.put(pid, nid);
			next.remove(id);
			prev.remove(id);
		}
	}

	/** Adds the specified Item to the last position in the wrapper's
	 * internal ordering. The underlying container is not modified.
	 * 
	 * @param id ID of the Item to be added to the ordering
	 */
	private void addToOrderWrapper(Object id) {

		// Add the if to tail
		if (last != null) {
			next.put(last, id);
			prev.put(id, last);
			last = id;
		} else {
			first = last = id;
		}
	}

	/** Adds the specified Item after the specified itemId in the wrapper's
	 * internal ordering. The underlying container is not modified.
	 * Given item id must be in the container, or must be null.
	 * 
	 * @param id ID of the Item to be added to the ordering
	 */
	private void addToOrderWrapper(Object id, Object previousItemId) {

		if (last == previousItemId || last == null)
			addToOrderWrapper(id);
		else {
			if (previousItemId == null) {
				next.put(id, first);
				prev.put(first, id);
				first = id;
			} else {
				prev.put(id, previousItemId);
				next.put(id, next.get(previousItemId));
				prev.put(next.get(previousItemId), id);
				next.put(previousItemId, id);
			}
		}
	}

	/** Updates the wrapper's internal ordering information to include all
	 * Items in the underlying container. If the contents of the wrapped
	 * container change without the wrapper's knowledge, this method needs
	 * to be called to update the ordering information of the Items.
	 */
	public void updateOrderWrapper() {

		if (!ordered) {

			Collection ids = container.getItemIds();

			// Recreate ordering if some parts of it are missing
			if (next == null
				|| first == null
				|| last == null
				|| prev != null) {
				first = null;
				last = null;
				next = new Hashtable();
				prev = new Hashtable();
			}

			// Filter out all the missing items
			LinkedList l = new LinkedList(next.keySet());
			for (Iterator i = l.iterator(); i.hasNext();) {
				Object id = i.next();
				if (!container.containsId(id))
					removeFromOrderWrapper(id);
			}

			// Add missing items
			for (Iterator i = ids.iterator(); i.hasNext();) {
				Object id = i.next();
				if (!next.containsKey(id))
					addToOrderWrapper(id);
			}
		}
	}

	/* Gets the first item stored in the ordered container 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Object firstItemId() {
		if (ordered)
			return ((Container.Ordered) container).firstItemId();
		return first;
	}

	/* Test if the given item is the first item in the container
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isFirstId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).isFirstId(itemId);
		return first != null && first.equals(itemId);
	}

	/* Test if the given item is the last item in the container
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public boolean isLastId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).isLastId(itemId);
		return last != null && last.equals(itemId);
	}

	/* Gets the last item stored in the ordered container 
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Object lastItemId() {
		if (ordered)
			return ((Container.Ordered) container).lastItemId();
		return last;
	}

	/* Get the item that is next from the specified item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Object nextItemId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).nextItemId(itemId);
		return next.get(itemId);
	}

	/* Get the item that is previous from the specified item.
	 * Don't add a JavaDoc comment here, we use the default documentation
	 * from implemented interface.
	 */
	public Object prevItemId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).prevItemId(itemId);
		return prev.get(itemId);
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

	/** Creates a new Item into the Container, assigns it an
	 * automatic ID, and adds it to the ordering.
	 * 
	 * @return the autogenerated ID of the new Item or <code>null</code>
	 * if the operation failed
	 */
	public Object addItem() throws UnsupportedOperationException {

		Object id = container.addItem();
		if (id != null)
			addToOrderWrapper(id);
		return id;
	}

	/** Adds a new Item by its ID to the underlying container and to the
	 * ordering.
	 * 
	 * @return the added Item or <code>null</code> if the operation failed
	 */
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		Item item = container.addItem(itemId);
		if (item != null)
			addToOrderWrapper(itemId);
		return item;
	}

	/** Removes all items from the underlying container and from the
	 * ordering.
	 * 
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean removeAllItems() throws UnsupportedOperationException {
		boolean success = container.removeAllItems();
		if (success) {
			first = last = null;
			next.clear();
			prev.clear();
		}
		return success;
	}

	/** Removes an Item specified by <code>itemId</code> from the underlying
	 * container and from the ordering.
	 * 
	 * @return <code>true</code> if the operation succeeded,
	 * <code>false</code> if not
	 */
	public boolean removeItem(Object itemId)
		throws UnsupportedOperationException {

		boolean success = container.removeItem(itemId);
		if (success)
			removeFromOrderWrapper(itemId);
		return success;
	}

	/** Removes the specified Property from the underlying container and
	 * from the ordering. Note that the Property will be removed from all
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
	/**
	 * @see com.enably.tk.data.Container.Ordered#addItemAfter(Object, Object)
	 */
	public Item addItemAfter(Object previousItemId, Object newItemId)
		throws UnsupportedOperationException {

		// If the previous item is not in the container, fail
		if (previousItemId != null && !containsId(previousItemId))
			return null;

		// Add the item to container
		Item item = container.addItem(newItemId);

		// Put the new item to its correct place
		if (item != null)
			addToOrderWrapper(newItemId, previousItemId);

		return item;
	}

	/**
	 * @see com.enably.tk.data.Container.Ordered#addItemAfter(Object)
	 */
	public Object addItemAfter(Object previousItemId)
		throws UnsupportedOperationException {

		// If the previous item is not in the container, fail
		if (previousItemId != null && !containsId(previousItemId))
			return null;

		// Add the item to container
		Object id = container.addItem();

		// Put the new item to its correct place
		if (id != null)
			addToOrderWrapper(id, previousItemId);

		return id;
	}

}
