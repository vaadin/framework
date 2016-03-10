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
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;

import com.vaadin.data.Container;

/**
 * Abstract container class that manages event listeners and sending events to
 * them ({@link PropertySetChangeNotifier}, {@link ItemSetChangeNotifier}).
 * 
 * Note that this class provides the internal implementations for both types of
 * events and notifiers as protected methods, but does not implement the
 * {@link PropertySetChangeNotifier} and {@link ItemSetChangeNotifier}
 * interfaces directly. This way, subclasses can choose not to implement them.
 * Subclasses implementing those interfaces should also override the
 * corresponding {@link #addListener()} and {@link #removeListener()} methods to
 * make them public.
 * 
 * @since 6.6
 */
public abstract class AbstractContainer implements Container {

    /**
     * List of all Property set change event listeners.
     */
    private Collection<Container.PropertySetChangeListener> propertySetChangeListeners = null;

    /**
     * List of all container Item set change event listeners.
     */
    private Collection<Container.ItemSetChangeListener> itemSetChangeListeners = null;

    /**
     * An <code>event</code> object specifying the container whose Property set
     * has changed.
     * 
     * This class does not provide information about which properties were
     * concerned by the change, but subclasses can provide additional
     * information about the changes.
     */
    protected static class BasePropertySetChangeEvent extends EventObject
            implements Container.PropertySetChangeEvent, Serializable {

        protected BasePropertySetChangeEvent(Container source) {
            super(source);
        }

        @Override
        public Container getContainer() {
            return (Container) getSource();
        }
    }

    /**
     * An <code>event</code> object specifying the container whose Item set has
     * changed.
     * 
     * This class does not provide information about the exact changes
     * performed, but subclasses can add provide additional information about
     * the changes.
     */
    protected static class BaseItemSetChangeEvent extends EventObject implements
            Container.ItemSetChangeEvent, Serializable {

        protected BaseItemSetChangeEvent(Container source) {
            super(source);
        }

        @Override
        public Container getContainer() {
            return (Container) getSource();
        }
    }

    // PropertySetChangeNotifier

    /**
     * Implementation of the corresponding method in
     * {@link PropertySetChangeNotifier}, override with the corresponding public
     * method and implement the interface to use this.
     * 
     * @see PropertySetChangeNotifier#addListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    protected void addPropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        if (getPropertySetChangeListeners() == null) {
            setPropertySetChangeListeners(new LinkedList<Container.PropertySetChangeListener>());
        }
        getPropertySetChangeListeners().add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addPropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Deprecated
    protected void addListener(Container.PropertySetChangeListener listener) {
        addPropertySetChangeListener(listener);
    }

    /**
     * Implementation of the corresponding method in
     * {@link PropertySetChangeNotifier}, override with the corresponding public
     * method and implement the interface to use this.
     * 
     * @see PropertySetChangeNotifier#removeListener(com.vaadin.data.Container.
     *      PropertySetChangeListener)
     */
    protected void removePropertySetChangeListener(
            Container.PropertySetChangeListener listener) {
        if (getPropertySetChangeListeners() != null) {
            getPropertySetChangeListeners().remove(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removePropertySetChangeListener(com.vaadin.data.Container.PropertySetChangeListener)}
     **/
    @Deprecated
    protected void removeListener(Container.PropertySetChangeListener listener) {
        removePropertySetChangeListener(listener);
    }

    // ItemSetChangeNotifier

    /**
     * Implementation of the corresponding method in
     * {@link ItemSetChangeNotifier}, override with the corresponding public
     * method and implement the interface to use this.
     * 
     * @see ItemSetChangeNotifier#addListener(com.vaadin.data.Container.ItemSetChangeListener)
     */
    protected void addItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() == null) {
            setItemSetChangeListeners(new LinkedList<Container.ItemSetChangeListener>());
        }
        getItemSetChangeListeners().add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Deprecated
    protected void addListener(Container.ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
    }

    /**
     * Implementation of the corresponding method in
     * {@link ItemSetChangeNotifier}, override with the corresponding public
     * method and implement the interface to use this.
     * 
     * @see ItemSetChangeNotifier#removeListener(com.vaadin.data.Container.ItemSetChangeListener)
     */
    protected void removeItemSetChangeListener(
            Container.ItemSetChangeListener listener) {
        if (getItemSetChangeListeners() != null) {
            getItemSetChangeListeners().remove(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addItemSetChangeListener(com.vaadin.data.Container.ItemSetChangeListener)}
     **/
    @Deprecated
    protected void removeListener(Container.ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
    }

    /**
     * Sends a simple Property set change event to all interested listeners.
     */
    protected void fireContainerPropertySetChange() {
        fireContainerPropertySetChange(new BasePropertySetChangeEvent(this));
    }

    /**
     * Sends a Property set change event to all interested listeners.
     * 
     * Use {@link #fireContainerPropertySetChange()} instead of this method
     * unless additional information about the exact changes is available and
     * should be included in the event.
     * 
     * @param event
     *            the property change event to send, optionally with additional
     *            information
     */
    protected void fireContainerPropertySetChange(
            Container.PropertySetChangeEvent event) {
        if (getPropertySetChangeListeners() != null) {
            final Object[] l = getPropertySetChangeListeners().toArray();
            for (int i = 0; i < l.length; i++) {
                ((Container.PropertySetChangeListener) l[i])
                        .containerPropertySetChange(event);
            }
        }
    }

    /**
     * Sends a simple Item set change event to all interested listeners,
     * indicating that anything in the contents may have changed (items added,
     * removed etc.).
     */
    protected void fireItemSetChange() {
        fireItemSetChange(new BaseItemSetChangeEvent(this));
    }

    /**
     * Sends an Item set change event to all registered interested listeners.
     * 
     * @param event
     *            the item set change event to send, optionally with additional
     *            information
     */
    protected void fireItemSetChange(ItemSetChangeEvent event) {
        if (getItemSetChangeListeners() != null) {
            final Object[] l = getItemSetChangeListeners().toArray();
            for (int i = 0; i < l.length; i++) {
                ((Container.ItemSetChangeListener) l[i])
                        .containerItemSetChange(event);
            }
        }
    }

    /**
     * Sets the property set change listener collection. For internal use only.
     * 
     * @param propertySetChangeListeners
     */
    protected void setPropertySetChangeListeners(
            Collection<Container.PropertySetChangeListener> propertySetChangeListeners) {
        this.propertySetChangeListeners = propertySetChangeListeners;
    }

    /**
     * Returns the property set change listener collection. For internal use
     * only.
     */
    protected Collection<Container.PropertySetChangeListener> getPropertySetChangeListeners() {
        return propertySetChangeListeners;
    }

    /**
     * Sets the item set change listener collection. For internal use only.
     * 
     * @param itemSetChangeListeners
     */
    protected void setItemSetChangeListeners(
            Collection<Container.ItemSetChangeListener> itemSetChangeListeners) {
        this.itemSetChangeListeners = itemSetChangeListeners;
    }

    /**
     * Returns the item set change listener collection. For internal use only.
     */
    protected Collection<Container.ItemSetChangeListener> getItemSetChangeListeners() {
        return itemSetChangeListeners;
    }

    public Collection<?> getListeners(Class<?> eventType) {
        if (Container.PropertySetChangeEvent.class.isAssignableFrom(eventType)) {
            if (propertySetChangeListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections
                        .unmodifiableCollection(propertySetChangeListeners);
            }
        } else if (Container.ItemSetChangeEvent.class
                .isAssignableFrom(eventType)) {
            if (itemSetChangeListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections
                        .unmodifiableCollection(itemSetChangeListeners);
            }
        }

        return Collections.EMPTY_LIST;
    }
}
