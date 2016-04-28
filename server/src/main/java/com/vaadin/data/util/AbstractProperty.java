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
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.vaadin.data.Property;

/**
 * Abstract base class for {@link Property} implementations.
 * 
 * Handles listener management for {@link ValueChangeListener}s and
 * {@link ReadOnlyStatusChangeListener}s.
 * 
 * @since 6.6
 */
public abstract class AbstractProperty<T> implements Property<T>,
        Property.ValueChangeNotifier, Property.ReadOnlyStatusChangeNotifier {

    /**
     * List of listeners who are interested in the read-only status changes of
     * the Property
     */
    private LinkedList<ReadOnlyStatusChangeListener> readOnlyStatusChangeListeners = null;

    /**
     * List of listeners who are interested in the value changes of the Property
     */
    private LinkedList<ValueChangeListener> valueChangeListeners = null;

    /**
     * Is the Property read-only?
     */
    private boolean readOnly;

    /**
     * {@inheritDoc}
     * 
     * Override for additional restrictions on what is considered a read-only
     * property.
     */
    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        boolean oldStatus = isReadOnly();
        readOnly = newStatus;
        if (oldStatus != isReadOnly()) {
            fireReadOnlyStatusChange();
        }
    }

    /**
     * Returns a string representation of this object. The returned string
     * representation depends on if the legacy Property toString mode is enabled
     * or disabled.
     * <p>
     * If legacy Property toString mode is enabled, returns the value of the
     * <code>Property</code> converted to a String.
     * </p>
     * <p>
     * If legacy Property toString mode is disabled, the string representation
     * has no special meaning
     * </p>
     * 
     * @see LegacyPropertyHelper#isLegacyToStringEnabled()
     * 
     * @return A string representation of the value value stored in the Property
     *         or a string representation of the Property object.
     * @deprecated As of 7.0. To get the property value, use {@link #getValue()}
     *             instead (and possibly toString on that)
     */
    @Deprecated
    @Override
    public String toString() {
        if (!LegacyPropertyHelper.isLegacyToStringEnabled()) {
            return super.toString();
        } else {
            return LegacyPropertyHelper.legacyPropertyToString(this);
        }
    }

    /* Events */

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has been changed.
     */
    protected static class ReadOnlyStatusChangeEvent extends
            java.util.EventObject implements Property.ReadOnlyStatusChangeEvent {

        /**
         * Constructs a new read-only status change event for this object.
         * 
         * @param source
         *            source object of the event.
         */
        protected ReadOnlyStatusChangeEvent(Property source) {
            super(source);
        }

        /**
         * Gets the Property whose read-only state has changed.
         * 
         * @return source Property of the event.
         */
        @Override
        public Property getProperty() {
            return (Property) getSource();
        }

    }

    /**
     * Registers a new read-only status change listener for this Property.
     * 
     * @param listener
     *            the new Listener to be registered.
     */
    @Override
    public void addReadOnlyStatusChangeListener(
            Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners == null) {
            readOnlyStatusChangeListeners = new LinkedList<ReadOnlyStatusChangeListener>();
        }
        readOnlyStatusChangeListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addReadOnlyStatusChangeListener(com.vaadin.data.Property.ReadOnlyStatusChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        addReadOnlyStatusChangeListener(listener);
    }

    /**
     * Removes a previously registered read-only status change listener.
     * 
     * @param listener
     *            the listener to be removed.
     */
    @Override
    public void removeReadOnlyStatusChangeListener(
            Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners != null) {
            readOnlyStatusChangeListeners.remove(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeReadOnlyStatusChangeListener(com.vaadin.data.Property.ReadOnlyStatusChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
        removeReadOnlyStatusChangeListener(listener);
    }

    /**
     * Sends a read only status change event to all registered listeners.
     */
    protected void fireReadOnlyStatusChange() {
        if (readOnlyStatusChangeListeners != null) {
            final Object[] l = readOnlyStatusChangeListeners.toArray();
            final Property.ReadOnlyStatusChangeEvent event = new ReadOnlyStatusChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Property.ReadOnlyStatusChangeListener) l[i])
                        .readOnlyStatusChange(event);
            }
        }
    }

    /**
     * An <code>Event</code> object specifying the Property whose value has been
     * changed.
     */
    private static class ValueChangeEvent extends java.util.EventObject
            implements Property.ValueChangeEvent {

        /**
         * Constructs a new value change event for this object.
         * 
         * @param source
         *            source object of the event.
         */
        protected ValueChangeEvent(Property source) {
            super(source);
        }

        /**
         * Gets the Property whose value has changed.
         * 
         * @return source Property of the event.
         */
        @Override
        public Property getProperty() {
            return (Property) getSource();
        }

    }

    @Override
    public void addValueChangeListener(ValueChangeListener listener) {
        if (valueChangeListeners == null) {
            valueChangeListeners = new LinkedList<ValueChangeListener>();
        }
        valueChangeListeners.add(listener);

    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addValueChangeListener(com.vaadin.data.Property.ValueChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(ValueChangeListener listener) {
        addValueChangeListener(listener);
    }

    @Override
    public void removeValueChangeListener(ValueChangeListener listener) {
        if (valueChangeListeners != null) {
            valueChangeListeners.remove(listener);
        }

    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeValueChangeListener(com.vaadin.data.Property.ValueChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(ValueChangeListener listener) {
        removeValueChangeListener(listener);
    }

    /**
     * Sends a value change event to all registered listeners.
     */
    protected void fireValueChange() {
        if (valueChangeListeners != null) {
            final Object[] l = valueChangeListeners.toArray();
            final Property.ValueChangeEvent event = new ValueChangeEvent(this);
            for (int i = 0; i < l.length; i++) {
                ((Property.ValueChangeListener) l[i]).valueChange(event);
            }
        }
    }

    public Collection<?> getListeners(Class<?> eventType) {
        if (Property.ValueChangeEvent.class.isAssignableFrom(eventType)) {
            if (valueChangeListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections.unmodifiableCollection(valueChangeListeners);
            }
        } else if (Property.ReadOnlyStatusChangeEvent.class
                .isAssignableFrom(eventType)) {
            if (readOnlyStatusChangeListeners == null) {
                return Collections.EMPTY_LIST;
            } else {
                return Collections
                        .unmodifiableCollection(readOnlyStatusChangeListeners);
            }
        }

        return Collections.EMPTY_LIST;
    }

    private static Logger getLogger() {
        return Logger.getLogger(AbstractProperty.class.getName());
    }
}
