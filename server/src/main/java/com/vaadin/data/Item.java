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

package com.vaadin.data;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * Provides a mechanism for handling a set of Properties, each associated to a
 * locally unique non-null identifier. The interface is split into subinterfaces
 * to enable a class to implement only the functionalities it needs.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 3.0
 */
public interface Item extends Serializable {

    /**
     * Gets the Property corresponding to the given Property ID stored in the
     * Item. If the Item does not contain the Property, <code>null</code> is
     * returned.
     * 
     * @param id
     *            identifier of the Property to get
     * @return the Property with the given ID or <code>null</code>
     */
    public Property getItemProperty(Object id);

    /**
     * Gets the collection of IDs of all Properties stored in the Item.
     * 
     * @return unmodifiable collection containing IDs of the Properties stored
     *         the Item
     */
    public Collection<?> getItemPropertyIds();

    /**
     * Tries to add a new Property into the Item.
     * 
     * <p>
     * This functionality is optional.
     * </p>
     * 
     * @param id
     *            ID of the new Property
     * @param property
     *            the Property to be added and associated with the id
     * @return <code>true</code> if the operation succeeded, <code>false</code>
     *         if not
     * @throws UnsupportedOperationException
     *             if the operation is not supported.
     */
    public boolean addItemProperty(Object id, Property property)
            throws UnsupportedOperationException;

    /**
     * Removes the Property identified by ID from the Item.
     * 
     * <p>
     * This functionality is optional.
     * </p>
     * 
     * @param id
     *            ID of the Property to be removed
     * @return <code>true</code> if the operation succeeded
     * @throws UnsupportedOperationException
     *             if the operation is not supported. <code>false</code> if not
     */
    public boolean removeItemProperty(Object id)
            throws UnsupportedOperationException;

    /**
     * Interface implemented by viewer classes capable of using an Item as a
     * data source.
     */
    public interface Viewer extends Serializable {

        /**
         * Sets the Item that serves as the data source of the viewer.
         * 
         * @param newDataSource
         *            The new data source Item
         */
        public void setItemDataSource(Item newDataSource);

        /**
         * Gets the Item serving as the data source of the viewer.
         * 
         * @return data source Item
         */
        public Item getItemDataSource();
    }

    /**
     * Interface implemented by the <code>Editor</code> classes capable of
     * editing the Item. Implementing this interface means that the Item serving
     * as the data source of the editor can be modified through it.
     * <p>
     * Note : Not implementing the <code>Item.Editor</code> interface does not
     * restrict the class from editing the contents of an internally.
     * </p>
     */
    public interface Editor extends Item.Viewer, Serializable {

    }

    /* Property set change event */

    /**
     * An <code>Event</code> object specifying the Item whose contents has been
     * changed through the <code>Property</code> interface.
     * <p>
     * Note: The values stored in the Properties may change without triggering
     * this event.
     * </p>
     */
    public interface PropertySetChangeEvent extends Serializable {

        /**
         * Retrieves the Item whose contents has been modified.
         * 
         * @return source Item of the event
         */
        public Item getItem();
    }

    /**
     * The listener interface for receiving <code>PropertySetChangeEvent</code>
     * objects.
     */
    public interface PropertySetChangeListener extends Serializable {

        /**
         * Notifies this listener that the Item's property set has changed.
         * 
         * @param event
         *            Property set change event object
         */
        public void itemPropertySetChange(Item.PropertySetChangeEvent event);
    }

    /**
     * The interface for adding and removing <code>PropertySetChangeEvent</code>
     * listeners. By implementing this interface a class explicitly announces
     * that it will generate a <code>PropertySetChangeEvent</code> when its
     * Property set is modified.
     * <p>
     * Note : The general Java convention is not to explicitly declare that a
     * class generates events, but to directly define the
     * <code>addListener</code> and <code>removeListener</code> methods. That
     * way the caller of these methods has no real way of finding out if the
     * class really will send the events, or if it just defines the methods to
     * be able to implement an interface.
     * </p>
     */
    public interface PropertySetChangeNotifier extends Serializable {

        /**
         * Registers a new property set change listener for this Item.
         * 
         * @param listener
         *            The new Listener to be registered.
         */
        public void addPropertySetChangeListener(
                Item.PropertySetChangeListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #addPropertySetChangeListener(PropertySetChangeListener)}
         **/
        @Deprecated
        public void addListener(Item.PropertySetChangeListener listener);

        /**
         * Removes a previously registered property set change listener.
         * 
         * @param listener
         *            Listener to be removed.
         */
        public void removePropertySetChangeListener(
                Item.PropertySetChangeListener listener);

        /**
         * @deprecated As of 7.0, replaced by
         *             {@link #removePropertySetChangeListener(PropertySetChangeListener)}
         **/
        @Deprecated
        public void removeListener(Item.PropertySetChangeListener listener);
    }
}
