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

import java.util.*;
import java.lang.reflect.Constructor;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;

/**
 * Indexed container implementation.
 * <p>
 * A list implementation of the <code>com.itmill.toolkit.data.Container</code>
 * interface. A list is a ordered collection wherein the user has a precise
 * control over where in the list each new Item is inserted. The user may access
 * the Items by their integer index (position in the list) or by their Item ID.
 * </p>
 * 
 * @see com.itmill.toolkit.data.Container
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */

public class IndexedContainer implements Container, Container.Indexed,
		Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier,
		Property.ValueChangeNotifier, Container.Sortable, Comparator, Cloneable {

	/* Internal structure *************************************************** */

	/**
	 * Linked list of ordered Item IDs.
	 */
	private ArrayList itemIds = new ArrayList();

	/**
	 * Linked list of ordered Property IDs.
	 */
	private ArrayList propertyIds = new ArrayList();

	/**
	 * Property ID to type mapping.
	 */
	private Hashtable types = new Hashtable();

	/**
	 * Hash of Items, where each Item is implemented as a mapping from Property
	 * ID to Property value.
	 */
	private Hashtable items = new Hashtable();

	/**
	 * Set of properties that are read-only.
	 */
	private HashSet readOnlyProperties = new HashSet();

	/**
	 * List of all Property value change event listeners listening all the
	 * properties.
	 */
	private LinkedList propertyValueChangeListeners = null;

	/**
	 * Data structure containing all listeners interested in changes to single
	 * Properties. The data structure is a hashtable mapping Property IDs to a
	 * hashtable that maps Item IDs to a linked list of listeners listening
	 * Property identified by given Property ID and Item ID.
	 */
	private Hashtable singlePropertyValueChangeListeners = null;

	/**
	 * List of all Property set change event listeners.
	 */
	private LinkedList propertySetChangeListeners = null;

	/**
	 * List of all container Item set change event listeners.
	 */
	private LinkedList itemSetChangeListeners = null;

	/**
	 * Temporary store for sorting property ids.
	 */
	private Object[] sortPropertyId;

	/**
	 * Temporary store for sorting direction.
	 */
	private boolean[] sortDirection;

	/* Container constructors *********************************************** */

	public IndexedContainer() {
	}

	public IndexedContainer(Collection itemIds) {
		if (items != null) {
			for (Iterator i = itemIds.iterator(); i.hasNext();) {
				this.addItem(i.next());
			}
		}
	}

	/* Container methods **************************************************** */

	/**
	 * Gets the Item with the given Item ID from the list. If the list does not
	 * contain the requested Item, <code>null</code> is returned.
	 * 
	 * @param itemId
	 *            the ID of the Item to retrieve.
	 * @return the Item with the given ID or <code>null</code> if the Item is
	 *         not found in the list
	 */
	public Item getItem(Object itemId) {
		if (items.containsKey(itemId))
			return new IndexedContainerItem(itemId);
		return null;
	}

	/**
	 * Gets the ID's of all Items stored in the list. The ID's are returned as a
	 * unmodifiable collection.
	 * 
	 * @return unmodifiable collection of Item IDs
	 */
	public Collection getItemIds() {
		return Collections.unmodifiableCollection(itemIds);
	}

	/**
	 * Gets the ID's of all Properties stored in the list. The ID's are returned
	 * as a unmodifiable collection.
	 * 
	 * @return unmodifiable collection of Property IDs
	 */
	public Collection getContainerPropertyIds() {
		return Collections.unmodifiableCollection(propertyIds);
	}

	/**
	 * Gets the type of a Property stored in the list.
	 * 
	 * @param id
	 *            the ID of the Property.
	 * @return Type of the requested Property
	 */
	public Class getType(Object propertyId) {
		return (Class) types.get(propertyId);
	}

	/**
	 * Gets the Property identified by the given Item ID and Property ID from
	 * the lsit. If the list does not contain the Property, <code>null</code>
	 * is returned.
	 * 
	 * @param itemId
	 *            the ID of the Item which contains the requested Property.
	 * @param propertyId
	 *            the ID of the Property to retrieve.
	 * @return Property with the given ID or <code>null</code>
	 * 
	 * @see com.itmill.toolkit.data.Container#getContainerProperty(Object,
	 *      Object)
	 */
	public Property getContainerProperty(Object itemId, Object propertyId) {
		if (!items.containsKey(itemId))
			return null;
		return new IndexedContainerProperty(itemId, propertyId);
	}

	/**
	 * Gets the number of Items in the list.
	 * 
	 * @return number of Items in the list
	 */
	public int size() {
		return itemIds.size();
	}

	/**
	 * Tests if the list contains the specified Item
	 * 
	 * @param itemId
	 *            the ID the of Item to be tested for.
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 */
	public boolean containsId(Object itemId) {
		return items.containsKey(itemId);
	}

	/**
	 * Adds a new Property to all Items in the list. The Property ID, data type
	 * and default value of the new Property are given as parameters.
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
			Object defaultValue) {

		// Fails, if nulls are given
		if (propertyId == null || type == null)
			return false;

		// Fails if the Property is already present
		if (propertyIds.contains(propertyId))
			return false;

		// Adds the Property to Property list and types
		propertyIds.add(propertyId);
		types.put(propertyId, type);

		// If default value is given, set it
		if (defaultValue != null)
			for (Iterator i = itemIds.iterator(); i.hasNext();)
				getItem(i.next()).getItemProperty(propertyId).setValue(
						defaultValue);

		// Sends a change event
		fireContainerPropertySetChange();

		return true;
	}

	/**
	 * Removes all Items from the list.
	 * <p>
	 * Note : The Property ID and type information is preserved.
	 * </p>
	 * 
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 */
	public boolean removeAllItems() {

		// Removes all Items
		itemIds.clear();
		items.clear();

		// Sends a change event
		fireContentsChange();

		return true;
	}

	/**
	 * Creates a new Item into the list, and assign it an automatic ID. The new
	 * ID is returned, or <code>null</code> if the operation fails. After a
	 * successful call you can use the
	 * {@link #getItem(Object ItemId) <code>getItem</code>}method to fetch the
	 * Item.
	 * 
	 * @return ID of the newly created Item, or <code>null</code> in case of a
	 *         failure
	 */
	public Object addItem() {

		// Creates a new id
		Object id = new Object();

		// Adds the Item into container
		addItem(id);

		return id;
	}

	/**
	 * Creates a new Item with the given ID into the list. The new Item is
	 * returned, and it is ready to have its Properties modified. Returns
	 * <code>null</code> if the operation fails or the Container already
	 * contains a Item with the given ID.
	 * 
	 * @param itemId
	 *            the ID of the Item to be created.
	 * @return Created new Item, or <code>null</code> in case of a failure
	 */
	public Item addItem(Object itemId) {

		// Makes sure that the Item has not been created yet
		if (items.containsKey(itemId))
			return null;

		// Adds the Item to container
		itemIds.add(itemId);
		items.put(itemId, new Hashtable());

		// Sends the event
		fireContentsChange();

		return getItem(itemId);
	}

	/**
	 * Removes the Item corresponding to the given Item ID from the list.
	 * 
	 * @param itemId
	 *            the ID of the Item to remove.
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 */
	public boolean removeItem(Object itemId) {

		if (items.remove(itemId) == null)
			return false;
		itemIds.remove(itemId);

		fireContentsChange();

		return true;
	}

	/**
	 * Removes a Property specified by the given Property ID from the list. Note
	 * that the Property will be removed from all Items in the list.
	 * 
	 * @param propertyId
	 *            the ID of the Property to remove.
	 * @return <code>true</code> if the operation succeeded,
	 *         <code>false</code> if not
	 */
	public boolean removeContainerProperty(Object propertyId) {

		// Fails if the Property is not present
		if (!propertyIds.contains(propertyId))
			return false;

		// Removes the Property to Property list and types
		propertyIds.remove(propertyId);
		types.remove(propertyId);

		// If remove the Property from all Items
		for (Iterator i = itemIds.iterator(); i.hasNext();)
			((Hashtable) items.get(i.next())).remove(propertyId);

		// Sends a change event
		fireContainerPropertySetChange();

		return true;
	}

	/* Container.Ordered methods ******************************************** */

	/**
	 * Gets the ID of the first Item in the list.
	 * 
	 * @return ID of the first Item in the list
	 */
	public Object firstItemId() {
		try {
			return itemIds.get(0);
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * Gets the ID of the last Item in the list.
	 * 
	 * @return ID of the last Item in the list
	 */
	public Object lastItemId() {
		try {
			return itemIds.get(itemIds.size() - 1);
		} catch (IndexOutOfBoundsException e) {
		}
		return null;
	}

	/**
	 * Gets the ID of the Item following the Item that corresponds to the
	 * itemId. If the given Item is the last or not found in the list,
	 * <code>null</code> is returned.
	 * 
	 * @param itemId
	 *            the ID of an Item in the list.
	 * @return ID of the next Item or <code>null</code>
	 */
	public Object nextItemId(Object itemId) {
		try {
			return itemIds.get(itemIds.indexOf(itemId) + 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets the ID of the Item preceding the Item that corresponds to the
	 * itemId. If the given Item is the first or not found in the list,
	 * <code>null</code> is returned.
	 * 
	 * @param itemId
	 *            the ID of an Item in the list.
	 * @return ID of the previous Item or <code>null</code>
	 */
	public Object prevItemId(Object itemId) {
		try {
			return itemIds.get(itemIds.indexOf(itemId) - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Tests if the Item corresponding to the given Item ID is the first Item in
	 * the list.
	 * 
	 * @param itemId
	 *            the ID of an Item in the list.
	 * @return <code>true</code> if the Item is first in the list,
	 *         <code>false</code> if not
	 */
	public boolean isFirstId(Object itemId) {
		return (size() >= 1 && itemIds.get(0).equals(itemId));
	}

	/**
	 * Tests if the Item corresponding to the given Item ID is the last Item in
	 * the list.
	 * 
	 * @param itemId
	 *            the ID of an Item in the list.
	 * @return <code>true</code> if the Item is last in the list,
	 *         <code>false</code> if not
	 */
	public boolean isLastId(Object itemId) {
		int s = size();
		return (s >= 1 && itemIds.get(s - 1).equals(itemId));
	}

	/**
	 * @see com.itmill.toolkit.data.Container.Ordered#addItemAfter(Object,
	 *      Object)
	 */
	public Item addItemAfter(Object previousItemId, Object newItemId) {

		// Get the index of the addition
		int index = 0;
		if (previousItemId != null) {
			index = 1 + indexOfId(previousItemId);
			if (index <= 0 || index > size())
				return null;
		}

		return addItemAt(index, newItemId);
	}

	/**
	 * @see com.itmill.toolkit.data.Container.Ordered#addItemAfter(Object)
	 */
	public Object addItemAfter(Object previousItemId) {

		// Get the index of the addition
		int index = 0;
		if (previousItemId != null) {
			index = 1 + indexOfId(previousItemId);
			if (index <= 0 || index > size())
				return null;
		}

		return addItemAt(index);
	}

	/**
	 * Gets ID with the index. The following is true for the index: 0 <= index <
	 * size().
	 * 
	 * @return ID in the given index.
	 * @param index
	 *            Index of the requested ID in the container.
	 */
	public Object getIdByIndex(int index) {
		return itemIds.get(index);
	}

	/**
	 * Gets the index of an id. The following is true for the index: 0 <= index <
	 * size().
	 * 
	 * @return Index of the Item or -1 if the Item is not in the container.
	 * @param itemId
	 *            ID of an Item in the collection
	 */
	public int indexOfId(Object itemId) {
		return itemIds.indexOf(itemId);
	}

	/**
	 * @see com.itmill.toolkit.data.Container.Indexed#addItemAt(int, Object)
	 */
	public Item addItemAt(int index, Object newItemId) {

		// Make sure that the Item has not been created yet
		if (items.containsKey(newItemId))
			return null;

		// Adds the Item to container
		itemIds.add(index, newItemId);
		items.put(newItemId, new Hashtable());

		// Sends the event
		fireContentsChange();

		return getItem(newItemId);
	}

	/**
	 * @see com.itmill.toolkit.data.Container.Indexed#addItemAt(int)
	 */
	public Object addItemAt(int index) {

		// Creates a new id
		Object id = new Object();

		// Adds the Item into container
		addItemAt(index, id);

		return id;
	}

	/* Event notifiers ****************************************************** */

	/**
	 * An <code>event</code> object specifying the list whose Property set has
	 * changed.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	private class PropertySetChangeEvent extends EventObject implements
			Container.PropertySetChangeEvent {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3257002172528079926L;

		private PropertySetChangeEvent(IndexedContainer source) {
			super(source);
		}

		/**
		 * Gets the list whose Property set has changed.
		 * 
		 * @return source object of the event as a Container
		 */
		public Container getContainer() {
			return (Container) getSource();
		}
	}

	/**
	 * An <code>event</code> object specifying the list whose Item set has
	 * changed.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	private class ItemSetChangeEvent extends EventObject implements
			Container.ItemSetChangeEvent {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3832616279386372147L;

		private ItemSetChangeEvent(IndexedContainer source) {
			super(source);
		}

		/**
		 * Gets the list whose Item set has changed.
		 * 
		 * @return source object of the event as a Container
		 */
		public Container getContainer() {
			return (Container) getSource();
		}

	}

	/**
	 * An <code>event</code> object specifying the Propery in a list whose
	 * value has changed.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	private class PropertyValueChangeEvent extends EventObject implements
			Property.ValueChangeEvent {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3833749884498359857L;

		private PropertyValueChangeEvent(Property source) {
			super(source);
		}

		/**
		 * Gets the Property whose value has changed.
		 * 
		 * @return source object of the event as a Property
		 */
		public Property getProperty() {
			return (Property) getSource();
		}

	}

	/**
	 * Registers a new Property set change listener for this list.
	 * 
	 * @param listener
	 *            the new Listener to be registered.
	 */
	public void addListener(Container.PropertySetChangeListener listener) {
		if (propertySetChangeListeners == null)
			propertySetChangeListeners = new LinkedList();
		propertySetChangeListeners.add(listener);
	}

	/**
	 * Removes a previously registered Property set change listener.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeListener(Container.PropertySetChangeListener listener) {
		if (propertySetChangeListeners != null)
			propertySetChangeListeners.remove(listener);
	}

	/**
	 * Adds a Item set change listener for the list.
	 * 
	 * @param listener
	 *            the listener to be added.
	 */
	public void addListener(Container.ItemSetChangeListener listener) {
		if (itemSetChangeListeners == null)
			itemSetChangeListeners = new LinkedList();
		itemSetChangeListeners.add(listener);
	}

	/**
	 * Removes a Item set change listener from the object.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeListener(Container.ItemSetChangeListener listener) {
		if (itemSetChangeListeners != null)
			itemSetChangeListeners.remove(listener);
	}

	/**
	 * Registers a new value change listener for this object.
	 * 
	 * @param listener
	 *            the new Listener to be registered
	 */
	public void addListener(Property.ValueChangeListener listener) {
		if (propertyValueChangeListeners == null)
			propertyValueChangeListeners = new LinkedList();
		propertyValueChangeListeners.add(listener);
	}

	/**
	 * Removes a previously registered value change listener.
	 * 
	 * @param listener
	 *            the listener to be removed.
	 */
	public void removeListener(Property.ValueChangeListener listener) {
		if (propertyValueChangeListeners != null)
			propertyValueChangeListeners.remove(listener);
	}

	/**
	 * Sends a Property value change event to all interested listeners.
	 * 
	 * @param source
	 *            the IndexedContainerProperty object.
	 */
	private void firePropertyValueChange(IndexedContainerProperty source) {

		// Sends event to listeners listening all value changes
		if (propertyValueChangeListeners != null) {
			Object[] l = propertyValueChangeListeners.toArray();
			Property.ValueChangeEvent event = new IndexedContainer.PropertyValueChangeEvent(
					source);
			for (int i = 0; i < l.length; i++)
				((Property.ValueChangeListener) l[i]).valueChange(event);
		}

		// Sends event to single property value change listeners
		if (singlePropertyValueChangeListeners != null) {
			Hashtable propertySetToListenerListMap = (Hashtable) singlePropertyValueChangeListeners
					.get(source.propertyId);
			if (propertySetToListenerListMap != null) {
				LinkedList listenerList = (LinkedList) propertySetToListenerListMap
						.get(source.itemId);
				if (listenerList != null) {
					Property.ValueChangeEvent event = new IndexedContainer.PropertyValueChangeEvent(
							source);
					for (Iterator i = listenerList.iterator(); i.hasNext();)
						((Property.ValueChangeListener) i.next())
								.valueChange(event);
				}
			}
		}

	}

	/**
	 * Sends a Property set change event to all interested listeners.
	 */
	private void fireContainerPropertySetChange() {
		if (propertySetChangeListeners != null) {
			Object[] l = propertySetChangeListeners.toArray();
			Container.PropertySetChangeEvent event = new IndexedContainer.PropertySetChangeEvent(
					this);
			for (int i = 0; i < l.length; i++)
				((Container.PropertySetChangeListener) l[i])
						.containerPropertySetChange(event);
		}
	}

	/**
	 * Sends Item set change event to all registered interested listeners.
	 */
	private void fireContentsChange() {
		if (itemSetChangeListeners != null) {
			Object[] l = itemSetChangeListeners.toArray();
			Container.ItemSetChangeEvent event = new IndexedContainer.ItemSetChangeEvent(
					this);
			for (int i = 0; i < l.length; i++)
				((Container.ItemSetChangeListener) l[i])
						.containerItemSetChange(event);
		}
	}

	/**
	 * Adds new single Property change listener.
	 * 
	 * @param propertyId
	 *            the ID of the Property to add.
	 * @param itemId
	 *            the ID of the Item .
	 * @param listener
	 *            the listener to be added.
	 */
	private void addSinglePropertyChangeListener(Object propertyId,
			Object itemId, Property.ValueChangeListener listener) {
		if (listener != null) {
			if (singlePropertyValueChangeListeners == null)
				singlePropertyValueChangeListeners = new Hashtable();
			Hashtable propertySetToListenerListMap = (Hashtable) singlePropertyValueChangeListeners
					.get(propertyId);
			if (propertySetToListenerListMap == null) {
				propertySetToListenerListMap = new Hashtable();
				singlePropertyValueChangeListeners.put(propertyId,
						propertySetToListenerListMap);
			}
			LinkedList listenerList = (LinkedList) propertySetToListenerListMap
					.get(itemId);
			if (listenerList == null) {
				listenerList = new LinkedList();
				propertySetToListenerListMap.put(itemId, listenerList);
			}
			listenerList.addLast(listener);
		}
	}

	/**
	 * Removes a previously registered single Property change listener.
	 * 
	 * @param propertyId
	 *            the ID of the Property to remove.
	 * @param itemId
	 *            the ID of the Item.
	 * @param listener
	 *            the listener to be removed.
	 */
	private void removeSinglePropertyChangeListener(Object propertyId,
			Object itemId, Property.ValueChangeListener listener) {
		if (listener != null && singlePropertyValueChangeListeners != null) {
			Hashtable propertySetToListenerListMap = (Hashtable) singlePropertyValueChangeListeners
					.get(propertyId);
			if (propertySetToListenerListMap != null) {
				LinkedList listenerList = (LinkedList) propertySetToListenerListMap
						.get(itemId);
				if (listenerList != null) {
					listenerList.remove(listener);
					if (listenerList.isEmpty())
						propertySetToListenerListMap.remove(itemId);
				}
				if (propertySetToListenerListMap.isEmpty())
					singlePropertyValueChangeListeners.remove(propertyId);
			}
			if (singlePropertyValueChangeListeners.isEmpty())
				singlePropertyValueChangeListeners = null;
		}
	}

	/* Internal Item and Property implementations *************************** */

	/*
	 * A class implementing the com.itmill.toolkit.data.Item interface to be
	 * contained in the list. @author IT Mill Ltd.
	 * 
	 * @version @VERSION@
	 * @since 3.0
	 */
	class IndexedContainerItem implements Item {

		/**
		 * Item ID in the host container for this Item.
		 */
		private Object itemId;

		/**
		 * Constructs a new ListItem instance and connects it to a host
		 * container.
		 * 
		 * @param itemId
		 *            the Item ID of the new Item.
		 */
		private IndexedContainerItem(Object itemId) {

			// Gets the item contents from the host
			if (itemId == null)
				throw new NullPointerException();
			this.itemId = itemId;
		}

		/**
		 * Gets the Property corresponding to the given Property ID stored in
		 * the Item. If the Item does not contain the Property,
		 * <code>null</code> is returned.
		 * 
		 * @param id
		 *            the identifier of the Property to get.
		 * @return the Property with the given ID or <code>null</code>
		 */
		public Property getItemProperty(Object id) {
			return new IndexedContainerProperty(itemId, id);
		}

		/**
		 * Gets the collection containing the IDs of all Properties stored in
		 * the Item.
		 * 
		 * @return unmodifiable collection contaning IDs of the Properties
		 *         stored the Item
		 */
		public Collection getItemPropertyIds() {
			return Collections.unmodifiableCollection(propertyIds);
		}

		/**
		 * Gets the <code>String</code> representation of the contents of the
		 * Item. The format of the string is a space separated catenation of the
		 * <code>String</code> representations of the Properties contained by
		 * the Item.
		 * 
		 * @return <code>String</code> representation of the Item contents
		 */
		public String toString() {
			String retValue = "";

			for (Iterator i = propertyIds.iterator(); i.hasNext();) {
				Object propertyId = i.next();
				retValue += getItemProperty(propertyId).toString();
				if (i.hasNext())
					retValue += " ";
			}

			return retValue;
		}

		/**
		 * Calculates a integer hash-code for the Item that's unique inside the
		 * list. Two Items inside the same list have always different
		 * hash-codes, though Items in different lists may have identical
		 * hash-codes.
		 * 
		 * @return A locally unique hash-code as integer
		 */
		public int hashCode() {
			return getHost().hashCode() ^ itemId.hashCode();
		}

		/**
		 * Tests if the given object is the same as the this object. Two Items
		 * got from a list container with the same ID are equal.
		 * 
		 * @param obj
		 *            an object to compare with this object
		 * @return <code>true</code> if the given object is the same as this
		 *         object, <code>false</code> if not
		 */
		public boolean equals(Object obj) {
			if (obj == null
					|| !obj.getClass().equals(IndexedContainerItem.class))
				return false;
			IndexedContainerItem li = (IndexedContainerItem) obj;
			return getHost() == li.getHost() && itemId.equals(li.itemId);
		}

		/**
		 * 
		 * @return
		 */
		private IndexedContainer getHost() {
			return IndexedContainer.this;
		}

		/**
		 * Indexed container does not support adding new properties.
		 * 
		 * @see com.itmill.toolkit.data.Item#addProperty(Object, Property)
		 */
		public boolean addItemProperty(Object id, Property property)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Indexed container item "
					+ "does not support adding new properties");
		}

		/**
		 * Indexed container does not support removing properties.
		 * 
		 * @see com.itmill.toolkit.data.Item#removeProperty(Object)
		 */
		public boolean removeItemProperty(Object id)
				throws UnsupportedOperationException {
			throw new UnsupportedOperationException(
					"Indexed container item does not support property removal");
		}

	}

	/**
	 * A class implementing the com.itmill.toolkit.data.Property interface to be
	 * contained in the Items contained in the list.
	 * 
	 * @author IT Mill Ltd.
	 * 
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	private class IndexedContainerProperty implements Property,
			Property.ValueChangeNotifier {

		/**
		 * ID of the Item, where the Property resides.
		 */
		private Object itemId;

		/**
		 * Id of the Property.
		 */
		private Object propertyId;

		/**
		 * Constructs a new ListProperty object and connect it to a ListItem and
		 * a ListContainer.
		 * 
		 * @param itemId
		 *            the ID of the Item to connect the new Property to.
		 * @param propertyId
		 *            the Property ID of the new Property.
		 * @param host
		 *            the list that contains the Item to contain the new
		 *            Property.
		 */
		private IndexedContainerProperty(Object itemId, Object propertyId) {
			if (itemId == null || propertyId == null)
				throw new NullPointerException();
			this.propertyId = propertyId;
			this.itemId = itemId;
		}

		/**
		 * Returns the type of the Property. The methods <code>getValue</code>
		 * and <code>setValue</code> must be compatible with this type: one
		 * must be able to safely cast the value returned from
		 * <code>getValue</code> to the given type and pass any variable
		 * assignable to this type as a parameter to <code>setValue</code.
		 * 
		 * @return the type of the Property.
		 */
		public Class getType() {
			return (Class) types.get(propertyId);
		}

		/**
		 * Gets the value stored in the Property.
		 * 
		 * @return the value stored in the Property.
		 */
		public Object getValue() {
			return ((Hashtable) items.get(itemId)).get(propertyId);
		}

		/**
		 * <p>
		 * Tests if the Property is in read-only mode. In read-only mode calls
		 * to the method <code>setValue</code> will throw
		 * <code>ReadOnlyException</code> s and will not modify the value of
		 * the Property.
		 * </p>
		 * 
		 * @return <code>true</code> if the Property is in read-only mode,
		 *         <code>false</code> if it's not.
		 */
		public boolean isReadOnly() {
			return readOnlyProperties.contains(this);
		}

		/**
		 * Sets the Property's read-only mode to the specified status.
		 * 
		 * @param newStatus
		 *            the new read-only status of the Property.
		 */
		public void setReadOnly(boolean newStatus) {
			if (newStatus)
				readOnlyProperties.add(this);
			else
				readOnlyProperties.remove(this);
		}

		/**
		 * Sets the value of the Property. By default this method will try to
		 * assign the value directly, but if it is unassignable, it will try to
		 * use the <code>String</code> constructor of the internal data type
		 * to assign the value,
		 * 
		 * @param newValue
		 *            the New value of the Property. This should be assignable
		 *            to the Property's internal type or support
		 *            <code>toString</code>.
		 * 
		 * @throws Property.ReadOnlyException
		 *             if the object is in read-only mode
		 * @throws Property.ConversionException
		 *             if <code>newValue</code> can't be converted into the
		 *             Property's native type directly or through
		 *             <code>String</code>
		 */
		public void setValue(Object newValue)
				throws Property.ReadOnlyException, Property.ConversionException {

			// Gets the Property set
			Hashtable propertySet = (Hashtable) items.get(itemId);

			// Support null values on all types
			if (newValue == null)
				propertySet.remove(propertyId);

			// Try to assign the compatible value directly
			else if (getType().isAssignableFrom(newValue.getClass()))
				propertySet.put(propertyId, newValue);

			// Otherwise try to convert the value trough string constructor
			else
				try {

					// Gets the string constructor
					Constructor constr = getType().getConstructor(
							new Class[] { String.class });

					// Creates new object from the string
					propertySet.put(propertyId, constr
							.newInstance(new Object[] { newValue.toString() }));

				} catch (java.lang.Exception e) {
					throw new Property.ConversionException(
							"Conversion for value '" + newValue + "' of class "
									+ newValue.getClass().getName() + " to "
									+ getType().getName() + " failed");
				}

			firePropertyValueChange(this);
		}

		/**
		 * Returns the value of the Property in human readable textual format.
		 * The return value should be assignable to the <code>setValue</code>
		 * method if the Property is not in read-only mode.
		 * 
		 * @return <code>String</code> representation of the value stored in
		 *         the Property
		 */
		public String toString() {
			Object value = getValue();
			if (value == null)
				return null;
			return value.toString();
		}

		/**
		 * Calculates a integer hash-code for the Property that's unique inside
		 * the Item containing the Property. Two different Properties inside the
		 * same Item contained in the same list always have different
		 * hash-codes, though Properties in different Items may have identical
		 * hash-codes.
		 * 
		 * @return A locally unique hash-code as integer
		 */
		public int hashCode() {
			return itemId.hashCode() ^ propertyId.hashCode()
					^ IndexedContainer.this.hashCode();
		}

		/**
		 * Tests if the given object is the same as the this object. Two
		 * Properties got from an Item with the same ID are equal.
		 * 
		 * @param obj
		 *            an object to compare with this object
		 * @return <code>true</code> if the given object is the same as this
		 *         object, <code>false</code> if not
		 */
		public boolean equals(Object obj) {
			if (obj == null
					|| !obj.getClass().equals(IndexedContainerProperty.class))
				return false;
			IndexedContainerProperty lp = (IndexedContainerProperty) obj;
			return lp.getHost() == getHost()
					&& lp.propertyId.equals(propertyId)
					&& lp.itemId.equals(itemId);
		}

		/**
		 * Registers a new value change listener for this Property.
		 * 
		 * @param listener
		 *            the new Listener to be registered.
		 * @see com.itmill.toolkit.data.Property.ValueChangeNotifier#addListener(ValueChangeListener)
		 */
		public void addListener(Property.ValueChangeListener listener) {
			addSinglePropertyChangeListener(propertyId, itemId, listener);
		}

		/**
		 * Removes a previously registered value change listener.
		 * 
		 * @param listener
		 *            listener to be removed
		 * @see com.itmill.toolkit.data.Property.ValueChangeNotifier#removeListener(ValueChangeListener)
		 */
		public void removeListener(Property.ValueChangeListener listener) {
			removeSinglePropertyChangeListener(propertyId, itemId, listener);
		}

		private IndexedContainer getHost() {
			return IndexedContainer.this;
		}

	}

	/**
	 * @see com.itmill.toolkit.data.Container.Sortable#sort(java.lang.Object[],
	 *      boolean[])
	 */
	public synchronized void sort(Object[] propertyId, boolean[] ascending) {

		// Removes any non-sortable property ids
		ArrayList ids = new ArrayList();
		ArrayList orders = new ArrayList();
		Collection sortable = getSortableContainerPropertyIds();
		for (int i = 0; i < propertyId.length; i++)
			if (sortable.contains(propertyId[i])) {
				ids.add(propertyId[i]);
				orders.add(new Boolean(i < ascending.length ? ascending[i]
						: true));
			}

		if (ids.size() == 0)
			return;
		sortPropertyId = ids.toArray();
		sortDirection = new boolean[orders.size()];
		for (int i = 0; i < sortDirection.length; i++)
			sortDirection[i] = ((Boolean) orders.get(i)).booleanValue();

		// Sort
		Collections.sort(this.itemIds, this);
		fireContentsChange();

		// Remove temporary references
		sortPropertyId = null;
		sortDirection = null;

	}

	/**
	 * @see com.itmill.toolkit.data.Container.Sortable#getSortableContainerPropertyIds()
	 */
	public Collection getSortableContainerPropertyIds() {

		LinkedList list = new LinkedList();
		for (Iterator i = this.propertyIds.iterator(); i.hasNext();) {
			Object id = i.next();
			Class type = this.getType(id);
			if (type != null && Comparable.class.isAssignableFrom(type)) {
				list.add(id);
			}
		}

		return list;
	}

	/**
	 * Compares two items for sorting.
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * @see #sort((java.lang.Object[], boolean[])
	 */
	public int compare(Object o1, Object o2) {

		for (int i = 0; i < sortPropertyId.length; i++) {

			// Get the compared properties
			Property pp1 = getContainerProperty(o1, this.sortPropertyId[i]);
			Property pp2 = getContainerProperty(o2, this.sortPropertyId[i]);

			// Get the compared values
			Object p1 = pp1 == null ? null : pp1.getValue();
			Object p2 = pp2 == null ? null : pp2.getValue();

			// Result of the comparison
			int r = 0;

			// Normal non-null comparison
			if (p1 != null && p2 != null) {
				if ((p1 instanceof Boolean) && (p2 instanceof Boolean))
					r = p1.equals(p2) ? 0
							: ((this.sortDirection[i] ? 1 : -1) * (((Boolean) p1)
									.booleanValue() ? 1 : -1));
				else
					r = this.sortDirection[i] ? ((Comparable) p1).compareTo(p2)
							: -((Comparable) p1).compareTo(p2);
			}

			// If both are nulls
			else if (p1 == p2)
				r = 0;

			// If one of the properties are null
			else
				r = (this.sortDirection[i] ? 1 : -1) * (p1 == null ? -1 : 1);

			// If order can be decided
			if (r != 0)
				return r;
		}

		return 0;
	}

	/**
	 * Supports cloning of the IndexedContainer cleanly.
	 * 
	 * @throws CloneNotSupportedException
	 *             if an object cannot be cloned. .
	 */
	public Object clone() throws CloneNotSupportedException {

		// Creates the clone
		IndexedContainer nc = new IndexedContainer();

		// Clone the shallow properties
		nc.itemIds = this.itemIds != null ? (ArrayList) this.itemIds.clone()
				: null;
		nc.itemSetChangeListeners = this.itemSetChangeListeners != null ? (LinkedList) this.itemSetChangeListeners
				.clone()
				: null;
		nc.propertyIds = this.propertyIds != null ? (ArrayList) this.propertyIds
				.clone()
				: null;
		nc.propertySetChangeListeners = this.propertySetChangeListeners != null ? (LinkedList) this.propertySetChangeListeners
				.clone()
				: null;
		nc.propertyValueChangeListeners = this.propertyValueChangeListeners != null ? (LinkedList) this.propertyValueChangeListeners
				.clone()
				: null;
		nc.readOnlyProperties = this.readOnlyProperties != null ? (HashSet) this.readOnlyProperties
				.clone()
				: null;
		nc.singlePropertyValueChangeListeners = this.singlePropertyValueChangeListeners != null ? (Hashtable) this.singlePropertyValueChangeListeners
				.clone()
				: null;
		nc.sortDirection = this.sortDirection != null ? (boolean[]) this.sortDirection
				.clone()
				: null;
		nc.sortPropertyId = this.sortPropertyId != null ? (Object[]) this.sortPropertyId
				.clone()
				: null;
		nc.types = this.types != null ? (Hashtable) this.types.clone() : null;

		// Clone property-values
		if (this.items == null)
			nc.items = null;
		else {
			nc.items = new Hashtable();
			for (Iterator i = this.items.keySet().iterator(); i.hasNext();) {
				Object id = i.next();
				Hashtable it = (Hashtable) this.items.get(id);
				nc.items.put(id, it.clone());
			}
		}

		return nc;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {

		// Only ones of the objects of the same class can be equal
		if (!(obj instanceof IndexedContainer))
			return false;
		IndexedContainer o = (IndexedContainer) obj;

		// Checks the properties one by one
		if (itemIds != o.itemIds && o.itemIds != null
				&& !o.itemIds.equals(this.itemIds))
			return false;
		if (items != o.items && o.items != null && !o.items.equals(this.items))
			return false;
		if (itemSetChangeListeners != o.itemSetChangeListeners
				&& o.itemSetChangeListeners != null
				&& !o.itemSetChangeListeners
						.equals(this.itemSetChangeListeners))
			return false;
		if (propertyIds != o.propertyIds && o.propertyIds != null
				&& !o.propertyIds.equals(this.propertyIds))
			return false;
		if (propertySetChangeListeners != o.propertySetChangeListeners
				&& o.propertySetChangeListeners != null
				&& !o.propertySetChangeListeners
						.equals(this.propertySetChangeListeners))
			return false;
		if (propertyValueChangeListeners != o.propertyValueChangeListeners
				&& o.propertyValueChangeListeners != null
				&& !o.propertyValueChangeListeners
						.equals(this.propertyValueChangeListeners))
			return false;
		if (readOnlyProperties != o.readOnlyProperties
				&& o.readOnlyProperties != null
				&& !o.readOnlyProperties.equals(this.readOnlyProperties))
			return false;
		if (singlePropertyValueChangeListeners != o.singlePropertyValueChangeListeners
				&& o.singlePropertyValueChangeListeners != null
				&& !o.singlePropertyValueChangeListeners
						.equals(this.singlePropertyValueChangeListeners))
			return false;
		if (sortDirection != o.sortDirection && o.sortDirection != null
				&& !o.sortDirection.equals(this.sortDirection))
			return false;
		if (sortPropertyId != o.sortPropertyId && o.sortPropertyId != null
				&& !o.sortPropertyId.equals(this.sortPropertyId))
			return false;
		if (types != o.types && o.types != null && !o.types.equals(this.types))
			return false;

		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {

		// The hash-code is calculated as combination hash of the members
		return (this.itemIds != null ? this.itemIds.hashCode() : 0)
				^ (this.items != null ? this.items.hashCode() : 0)
				^ (this.itemSetChangeListeners != null ? this.itemSetChangeListeners
						.hashCode()
						: 0)
				^ (this.propertyIds != null ? this.propertyIds.hashCode() : 0)
				^ (this.propertySetChangeListeners != null ? this.propertySetChangeListeners
						.hashCode()
						: 0)
				^ (this.propertyValueChangeListeners != null ? this.propertyValueChangeListeners
						.hashCode()
						: 0)
				^ (this.readOnlyProperties != null ? this.readOnlyProperties
						.hashCode() : 0)
				^ (this.singlePropertyValueChangeListeners != null ? this.singlePropertyValueChangeListeners
						.hashCode()
						: 0)
				^ (this.sortPropertyId != null ? this.sortPropertyId.hashCode()
						: 0)
				^ (this.types != null ? this.types.hashCode() : 0)
				^ (this.sortDirection != null ? this.sortDirection.hashCode()
						: 0);
	}

}