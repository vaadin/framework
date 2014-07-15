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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * <p>
 * A wrapper class for adding external ordering to containers not implementing
 * the {@link com.vaadin.data.Container.Ordered} interface.
 * </p>
 * 
 * <p>
 * If the wrapped container is changed directly (that is, not through the
 * wrapper), and does not implement Container.ItemSetChangeNotifier and/or
 * Container.PropertySetChangeNotifier the hierarchy information must be updated
 * with the {@link #updateOrderWrapper()} method.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ContainerOrderedWrapper implements Container.Ordered,
        Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier {

    /**
     * The wrapped container
     */
    private final Container container;

    /**
     * Ordering information, ie. the mapping from Item ID to the next item ID
     */
    private Hashtable<Object, Object> next;

    /**
     * Reverse ordering information for convenience and performance reasons.
     */
    private Hashtable<Object, Object> prev;

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
     * The last known size of the wrapped container. Used to check whether items
     * have been added or removed to the wrapped container, when the wrapped
     * container does not send ItemSetChangeEvents.
     */
    private int lastKnownSize = -1;

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
        if (container == null) {
            throw new NullPointerException("Null can not be wrapped");
        }

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
            final Object pid = prev.get(id);
            final Object nid = next.get(id);
            if (first.equals(id)) {
                first = nid;
            }
            if (last.equals(id)) {
                first = pid;
            }
            if (nid != null) {
                prev.put(nid, pid);
            }
            if (pid != null) {
                next.put(pid, nid);
            }
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

        if (last == previousItemId || last == null) {
            addToOrderWrapper(id);
        } else {
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

            final Collection<?> ids = container.getItemIds();

            // Recreates ordering if some parts of it are missing
            if (next == null || first == null || last == null || prev != null) {
                first = null;
                last = null;
                next = new Hashtable<Object, Object>();
                prev = new Hashtable<Object, Object>();
            }

            // Filter out all the missing items
            final LinkedList<?> l = new LinkedList<Object>(next.keySet());
            for (final Iterator<?> i = l.iterator(); i.hasNext();) {
                final Object id = i.next();
                if (!container.containsId(id)) {
                    removeFromOrderWrapper(id);
                }
            }

            // Adds missing items
            for (final Iterator<?> i = ids.iterator(); i.hasNext();) {
                final Object id = i.next();
                if (!next.containsKey(id)) {
                    addToOrderWrapper(id);
                }
            }
        }
    }

    /*
     * Gets the first item stored in the ordered container Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object firstItemId() {
        if (ordered) {
            return ((Container.Ordered) container).firstItemId();
        }
        return first;
    }

    /*
     * Tests if the given item is the first item in the container Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean isFirstId(Object itemId) {
        if (ordered) {
            return ((Container.Ordered) container).isFirstId(itemId);
        }
        return first != null && first.equals(itemId);
    }

    /*
     * Tests if the given item is the last item in the container Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public boolean isLastId(Object itemId) {
        if (ordered) {
            return ((Container.Ordered) container).isLastId(itemId);
        }
        return last != null && last.equals(itemId);
    }

    /*
     * Gets the last item stored in the ordered container Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object lastItemId() {
        if (ordered) {
            return ((Container.Ordered) container).lastItemId();
        }
        return last;
    }

    /*
     * Gets the item that is next from the specified item. Don't add a JavaDoc
     * comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object nextItemId(Object itemId) {
        if (ordered) {
            return ((Container.Ordered) container).nextItemId(itemId);
        }
        if (itemId == null) {
            return null;
        }
        return next.get(itemId);
    }

    /*
     * Gets the item that is previous from the specified item. Don't add a
     * JavaDoc comment here, we use the default documentation from implemented
     * interface.
     */
    @Override
    public Object prevItemId(Object itemId) {
        if (ordered) {
            return ((Container.Ordered) container).prevItemId(itemId);
        }
        if (itemId == null) {
            return null;
        }
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
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
            Object defaultValue) throws UnsupportedOperationException {

        return container.addContainerProperty(propertyId, type, defaultValue);
    }

    /**
     * Creates a new Item into the Container, assigns it an automatic ID, and
     * adds it to the ordering.
     * 
     * @return the autogenerated ID of the new Item or <code>null</code> if the
     *         operation failed
     * @throws UnsupportedOperationException
     *             if the addItem is not supported.
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {

        final Object id = container.addItem();
        if (!ordered && id != null) {
            addToOrderWrapper(id);
        }
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
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        final Item item = container.addItem(itemId);
        if (!ordered && item != null) {
            addToOrderWrapper(itemId);
        }
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
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        final boolean success = container.removeAllItems();
        if (!ordered && success) {
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
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     * @throws UnsupportedOperationException
     *             if the removeItem is not supported.
     */
    @Override
    public boolean removeItem(Object itemId)
            throws UnsupportedOperationException {

        final boolean success = container.removeItem(itemId);
        if (!ordered && success) {
            removeFromOrderWrapper(itemId);
        }
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
        int newSize = container.size();
        assert newSize >= 0;
        if (lastKnownSize != -1 && newSize != lastKnownSize
                && !(container instanceof Container.ItemSetChangeNotifier)) {
            // Update the internal cache when the size of the container changes
            // and the container is incapable of sending ItemSetChangeEvents
            updateOrderWrapper();
        }
        lastKnownSize = newSize;
        return newSize;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId)
            throws UnsupportedOperationException {

        // If the previous item is not in the container, fail
        if (previousItemId != null && !containsId(previousItemId)) {
            return null;
        }

        // Adds the item to container
        final Item item = container.addItem(newItemId);

        // Puts the new item to its correct place
        if (!ordered && item != null) {
            addToOrderWrapper(newItemId, previousItemId);
        }

        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */
    @Override
    public Object addItemAfter(Object previousItemId)
            throws UnsupportedOperationException {

        // If the previous item is not in the container, fail
        if (previousItemId != null && !containsId(previousItemId)) {
            return null;
        }

        // Adds the item to container
        final Object id = container.addItem();

        // Puts the new item to its correct place
        if (!ordered && id != null) {
            addToOrderWrapper(id, previousItemId);
        }

        return id;
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
            updateOrderWrapper();
            ((Container.ItemSetChangeListener) listener)
                    .containerItemSetChange(event);

        }

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            updateOrderWrapper();
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
