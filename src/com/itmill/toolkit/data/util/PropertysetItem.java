/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.data.util;

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;

/**
 * Class for handling a set of identified Properties. The elements contained in
 * a </code>MapItem</code> can be referenced using locally unique identifiers.
 * The class supports listeners who are interested in changes to the Property
 * set managed by the class.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class PropertysetItem implements Item, Item.PropertySetChangeNotifier,
        Cloneable {

    /* Private representation of the item */

    /**
     * Mapping from property id to property.
     */
    private HashMap map = new HashMap();

    /**
     * List of all property ids to maintain the order.
     */
    private LinkedList list = new LinkedList();

    /**
     * List of property set modification listeners.
     */
    private LinkedList propertySetChangeListeners = null;

    /* Item methods */

    /**
     * Gets the Property corresponding to the given Property ID stored in the
     * Item. If the Item does not contain the Property, <code>null</code> is
     * returned.
     * 
     * @param id
     *            the identifier of the Property to get.
     * @return the Property with the given ID or <code>null</code>
     */
    public Property getItemProperty(Object id) {
        return (Property) map.get(id);
    }

    /**
     * Gets the collection of IDs of all Properties stored in the Item.
     * 
     * @return unmodifiable collection containing IDs of the Properties stored
     *         the Item
     */
    public Collection getItemPropertyIds() {
        return Collections.unmodifiableCollection(list);
    }

    /* Item.Managed methods */

    /**
     * Removes the Property identified by ID from the Item. This functionality
     * is optional. If the method is not implemented, the method always returns
     * <code>false</code>.
     * 
     * @param id
     *            the ID of the Property to be removed.
     * @return <code>true</code> if the operation succeeded <code>false</code>
     *         if not
     */
    public boolean removeItemProperty(Object id) {

        // Cant remove missing properties
        if (map.remove(id) == null) {
            return false;
        }
        list.remove(id);

        // Send change events
        fireItemPropertySetChange();

        return true;
    }

    /**
     * Tries to add a new Property into the Item.
     * 
     * @param id
     *            the ID of the new Property.
     * @param property
     *            the Property to be added and associated with the id.
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    public boolean addItemProperty(Object id, Property property) {

        // Null ids are not accepted
        if (id == null) {
            throw new NullPointerException("Item property id can not be null");
        }

        // Cant add a property twice
        if (map.containsKey(id)) {
            return false;
        }

        // Put the property to map
        map.put(id, property);
        list.add(id);

        // Send event
        fireItemPropertySetChange();

        return true;
    }

    /**
     * Gets the <code>String</code> representation of the contents of the Item.
     * The format of the string is a space separated catenation of the
     * <code>String</code> representations of the Properties contained by the
     * Item.
     * 
     * @return <code>String</code> representation of the Item contents
     */
    @Override
    public String toString() {
        String retValue = "";

        for (final Iterator i = getItemPropertyIds().iterator(); i.hasNext();) {
            final Object propertyId = i.next();
            retValue += getItemProperty(propertyId).toString();
            if (i.hasNext()) {
                retValue += " ";
            }
        }

        return retValue;
    }

    /* Notifiers */

    /**
     * An <code>event</code> object specifying an Item whose Property set has
     * changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    private class PropertySetChangeEvent extends EventObject implements
            Item.PropertySetChangeEvent {

        private PropertySetChangeEvent(Item source) {
            super(source);
        }

        /**
         * Gets the Item whose Property set has changed.
         * 
         * @return source object of the event as an <code>Item</code>
         */
        public Item getItem() {
            return (Item) getSource();
        }
    }

    /**
     * Registers a new property set change listener for this Item.
     * 
     * @param listener
     *            the new Listener to be registered.
     */
    public void addListener(Item.PropertySetChangeListener listener) {
        if (propertySetChangeListeners == null) {
            propertySetChangeListeners = new LinkedList();
        }
        propertySetChangeListeners.add(listener);
    }

    /**
     * Removes a previously registered property set change listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(Item.PropertySetChangeListener listener) {
        if (propertySetChangeListeners != null) {
            propertySetChangeListeners.remove(listener);
        }
    }

    /**
     * Sends a Property set change event to all interested listeners.
     */
    private void fireItemPropertySetChange() {
        if (propertySetChangeListeners != null) {
            final Object[] l = propertySetChangeListeners.toArray();
            final Item.PropertySetChangeEvent event = new PropertysetItem.PropertySetChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Item.PropertySetChangeListener) l[i])
                        .itemPropertySetChange(event);
            }
        }
    }

    /**
     * Creates and returns a copy of this object.
     * <p>
     * The method <code>clone</code> performs a shallow copy of the
     * <code>PropertysetItem</code>.
     * </p>
     * <p>
     * Note : All arrays are considered to implement the interface Cloneable.
     * Otherwise, this method creates a new instance of the class of this object
     * and initializes all its fields with exactly the contents of the
     * corresponding fields of this object, as if by assignment, the contents of
     * the fields are not themselves cloned. Thus, this method performs a
     * "shallow copy" of this object, not a "deep copy" operation.
     * </p>
     * 
     * @throws CloneNotSupportedException
     *             if the object's class does not support the Cloneable
     *             interface.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {

        final PropertysetItem npsi = new PropertysetItem();

        npsi.list = list != null ? (LinkedList) list.clone() : null;
        npsi.propertySetChangeListeners = propertySetChangeListeners != null ? (LinkedList) propertySetChangeListeners
                .clone()
                : null;
        npsi.map = (HashMap) map.clone();

        return npsi;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof PropertysetItem)) {
            return false;
        }

        final PropertysetItem other = (PropertysetItem) obj;

        if (other.list != list) {
            if (other.list == null) {
                return false;
            }
            if (!other.list.equals(list)) {
                return false;
            }
        }
        if (other.map != map) {
            if (other.map == null) {
                return false;
            }
            if (!other.map.equals(map)) {
                return false;
            }
        }
        if (other.propertySetChangeListeners != propertySetChangeListeners) {
            if (other.propertySetChangeListeners == null) {
                return false;
            }
            if (!other.propertySetChangeListeners
                    .equals(propertySetChangeListeners)) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return (list == null ? 0 : list.hashCode())
                ^ (map == null ? 0 : map.hashCode())
                ^ (propertySetChangeListeners == null ? 0
                        : propertySetChangeListeners.hashCode());
    }
}
