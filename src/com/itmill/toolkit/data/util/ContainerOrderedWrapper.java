/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

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
import java.util.Hashtable;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;

/**
 * <p>
 * A wrapper class for adding external ordering to containers not implementing
 * the {@link com.itmill.toolkit.data.Container.Ordered} interface.
 * </p>
 * 
 * <p>
 * If the wrapped container is changed directly (that is, not through the
 * wrapper), the ordering must be updated with the {@link #updateOrderWrapper()}
 * method.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class ContainerOrderedWrapper implements Container.Ordered,
		Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier {

	/**
	 * The wrapped container
	 */
	private Container container;

	/**
	 * Ordering information, ie. the mapping from Item ID to the next item ID
	 */
	private Hashtable next;

	/**
	 * Reverse ordering information for convenience and performance reasons.
	 */
	private Hashtable prev;

	/**
	 * ID of the first Item in the container.
	 */
	private Object first;

	/**
	 * ID of the last Item in the container.
	 */
	private Object last;

	/**
	 * Is the wrapped container ordered by itself, ie. does it implement the
	 * Container.Ordered interface by itself? If it does, this class will use
	 * the methods of the underlying container directly.
	 */
	private boolean ordered = false;

	/**
	 * Constructs a new ordered wrapper for an existing Container. Works even if
	 * the to-be-wrapped container already implements the Container.Ordered
	 * interface.
	 * 
	 * @param toBeWrapped
	 *            the container whose contents need to be ordered.
	 */
	public ContainerOrderedWrapper(Container toBeWrapped) {

		container = toBeWrapped;
		ordered = container instanceof Container.Ordered;

		// Checks arguments
		if (container == null)
			throw new NullPointerException("Null can not be wrapped");

		// Creates initial order if needed
		updateOrderWrapper();
	}

	/**
	 * Removes the specified Item from the wrapper's internal hierarchy
	 * structure.
	 * <p>
	 * Note : The Item is not removed from the underlying Container.
	 * </p>
	 * 
	 * @param id
	 *            the ID of the Item to be removed from the ordering.
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

	/**
	 * Registers the specified Item to the last position in the wrapper's
	 * internal ordering. The underlying container is not modified.
	 * 
	 * @param id
	 *            the ID of the Item to be added to the ordering.
	 */
	private void addToOrderWrapper(Object id) {

		// Adds the if to tail
		if (last != null) {
			next.put(last, id);
			prev.put(id, last);
			last = id;
		} else {
			first = last = id;
		}
	}

	/**
	 * Registers the specified Item after the specified itemId in the wrapper's
	 * internal ordering. The underlying container is not modified. Given item
	 * id must be in the container, or must be null.
	 * 
	 * @param id
	 *            the ID of the Item to be added to the ordering.
	 * @param previousItemId
	 *            the Id of the previous item.
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

	/**
	 * Updates the wrapper's internal ordering information to include all Items
	 * in the underlying container.
	 * <p>
	 * Note : If the contents of the wrapped container change without the
	 * wrapper's knowledge, this method needs to be called to update the
	 * ordering information of the Items.
	 * </p>
	 */
	public void updateOrderWrapper() {

		if (!ordered) {

			Collection ids = container.getItemIds();

			// Recreates ordering if some parts of it are missing
			if (next == null || first == null || last == null || prev != null) {
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

			// Adds missing items
			for (Iterator i = ids.iterator(); i.hasNext();) {
				Object id = i.next();
				if (!next.containsKey(id))
					addToOrderWrapper(id);
			}
		}
	}

	/*
	 * Gets the first item stored in the ordered container Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public Object firstItemId() {
		if (ordered)
			return ((Container.Ordered) container).firstItemId();
		return first;
	}

	/*
	 * Tests if the given item is the first item in the container Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public boolean isFirstId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).isFirstId(itemId);
		return first != null && first.equals(itemId);
	}

	/*
	 * Tests if the given item is the last item in the container Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public boolean isLastId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).isLastId(itemId);
		return last != null && last.equals(itemId);
	}

	/*
	 * Gets the last item stored in the ordered container Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public Object lastItemId() {
		if (ordered)
			return ((Container.Ordered) container).lastItemId();
		return last;
	}

	/*
	 * Gets the item that is next from the specified item. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public Object nextItemId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).nextItemId(itemId);
		return next.get(itemId);
	}

	/*
	 * Gets the item that is previous from the specified item. Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public Object prevItemId(Object itemId) {
		if (ordered)
			return ((Container.Ordered) container).prevItemId(itemId);
		return prev.get(itemId);
	}

	/**
	 * Registers a new Property to all Items in the Container.
	 * 
	 * @param propertyId
	 *            the ID of the new Property.
	 * @param type
	 *            the Data type of the new Property.
	 * @param defaultValue
	 *            the value all created Properties are initialized to.
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 */
	public boolean addContainerProperty(Object propertyId, Class type,
			Object defaultValue) throws UnsupportedOperationException {

		return container.addContainerProperty(propertyId, type, defaultValue);
	}

	/**
	 * Creates a new Item into the Container, assigns it an automatic ID, and
	 * adds it to the ordering.
	 * 
	 * @return the autogenerated ID of the new Item or <code>null</code> if
	 *         the operation failed
	 * @throws UnsupportedOperationException
	 *             if the addItem is not supported.
	 */
	public Object addItem() throws UnsupportedOperationException {

		Object id = container.addItem();
		if (id != null)
			addToOrderWrapper(id);
		return id;
	}

	/**
	 * Registers a new Item by its ID to the underlying container and to the
	 * ordering.
	 * 
	 * @param itemId
	 *            the ID of the Item to be created.
	 * @return the added Item or <code>null</code> if the operation failed
	 * @throws UnsupportedOperationException
	 *             if the addItem is not supported.
	 */
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		Item item = container.addItem(itemId);
		if (item != null)
			addToOrderWrapper(itemId);
		return item;
	}

	/**
	 * Removes all items from the underlying container and from the ordering.
	 * 
	 * @return <code>true</code> if the operation succeeded, otherwise
	 *         <code>false</code>
	 * @throws UnsupportedOperationException
	 *             if the removeAllItems is not supported.
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

	/**
	 * Removes an Item specified by the itemId from the underlying container and
	 * from the ordering.
	 * 
	 * @param itemId
	 *            the ID of the Item to be removed.
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 * @throws UnsupportedOperationException
	 *             if the removeItem is not supported.
	 */
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {

		boolean success = container.removeItem(itemId);
		if (success)
			removeFromOrderWrapper(itemId);
		return success;
	}

	/**
	 * Removes the specified Property from the underlying container and from the
	 * ordering.
	 * <p>
	 * Note : The Property will be removed from all the Items in the Container.
	 * </p>
	 * 
	 * @param propertyId
	 *            the ID of the Property to remove.
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 * @throws UnsupportedOperationException
	 *             if the removeContainerProperty is not supported.
	 */
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		return container.removeContainerProperty(propertyId);
	}

	/*
	 * Does the container contain the specified Item? Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public boolean containsId(Object itemId) {
		return container.containsId(itemId);
	}

	/*
	 * Gets the specified Item from the container. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public Item getItem(Object itemId) {
		return container.getItem(itemId);
	}

	/*
	 * Gets the ID's of all Items stored in the Container Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public Collection getItemIds() {
		return container.getItemIds();
	}

	/*
	 * Gets the Property identified by the given itemId and propertyId from the
	 * Container Don't add a JavaDoc comment here, we use the default
	 * documentation from implemented interface.
	 */
	public Property getContainerProperty(Object itemId, Object propertyId) {
		return container.getContainerProperty(itemId, propertyId);
	}

	/*
	 * Gets the ID's of all Properties stored in the Container Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public Collection getContainerPropertyIds() {
		return container.getContainerPropertyIds();
	}

	/*
	 * Gets the data type of all Properties identified by the given Property ID.
	 * Don't add a JavaDoc comment here, we use the default documentation from
	 * implemented interface.
	 */
	public Class getType(Object propertyId) {
		return container.getType(propertyId);
	}

	/*
	 * Gets the number of Items in the Container. Don't add a JavaDoc comment
	 * here, we use the default documentation from implemented interface.
	 */
	public int size() {
		return container.size();
	}

	/*
	 * Registers a new Item set change listener for this Container. Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void addListener(Container.ItemSetChangeListener listener) {
		if (container instanceof Container.ItemSetChangeNotifier)
			((Container.ItemSetChangeNotifier) container).addListener(listener);
	}

	/*
	 * Removes a Item set change listener from the object. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void removeListener(Container.ItemSetChangeListener listener) {
		if (container instanceof Container.ItemSetChangeNotifier)
			((Container.ItemSetChangeNotifier) container)
					.removeListener(listener);
	}

	/*
	 * Registers a new Property set change listener for this Container. Don't
	 * add a JavaDoc comment here, we use the default documentation from
	 * implemented interface.
	 */
	public void addListener(Container.PropertySetChangeListener listener) {
		if (container instanceof Container.PropertySetChangeNotifier)
			((Container.PropertySetChangeNotifier) container)
					.addListener(listener);
	}

	/*
	 * Removes a Property set change listener from the object. Don't add a
	 * JavaDoc comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void removeListener(Container.PropertySetChangeListener listener) {
		if (container instanceof Container.PropertySetChangeNotifier)
			((Container.PropertySetChangeNotifier) container)
					.removeListener(listener);
	}

	/**
	 * @see com.itmill.toolkit.data.Container.Ordered#addItemAfter(Object,
	 *      Object)
	 */
	public Item addItemAfter(Object previousItemId, Object newItemId)
			throws UnsupportedOperationException {

		// If the previous item is not in the container, fail
		if (previousItemId != null && !containsId(previousItemId))
			return null;

		// Adds the item to container
		Item item = container.addItem(newItemId);

		// Puts the new item to its correct place
		if (item != null)
			addToOrderWrapper(newItemId, previousItemId);

		return item;
	}

	/**
	 * @see com.itmill.toolkit.data.Container.Ordered#addItemAfter(Object)
	 */
	public Object addItemAfter(Object previousItemId)
			throws UnsupportedOperationException {

		// If the previous item is not in the container, fail
		if (previousItemId != null && !containsId(previousItemId))
			return null;

		// Adds the item to container
		Object id = container.addItem();

		// Puts the new item to its correct place
		if (id != null)
			addToOrderWrapper(id, previousItemId);

		return id;
	}

}
