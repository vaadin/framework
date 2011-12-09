/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

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
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean newStatus) {
        boolean oldStatus = isReadOnly();
        readOnly = newStatus;
        if (oldStatus != isReadOnly()) {
            fireReadOnlyStatusChange();
        }
    }

    /**
     * Returns the value of the <code>Property</code> in human readable textual
     * format.
     * 
     * @return String representation of the value stored in the Property
     * @deprecated use the property value directly, or {@link #getStringValue()}
     *             during migration period
     */
    @Deprecated
    @Override
    public String toString() {
        throw new UnsupportedOperationException(
                "Use Property.getValue() instead of " + getClass()
                        + ".toString()");
    }

    /**
     * Returns the value of the <code>Property</code> in human readable textual
     * format.
     * 
     * @return String representation of the value stored in the Property
     * @since 7.0
     */
    public String getStringValue() {
        final Object value = getValue();
        if (value == null) {
            return null;
        }
        return value.toString();
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
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners == null) {
            readOnlyStatusChangeListeners = new LinkedList<ReadOnlyStatusChangeListener>();
        }
        readOnlyStatusChangeListeners.add(listener);
    }

    /**
     * Removes a previously registered read-only status change listener.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeListener(Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners != null) {
            readOnlyStatusChangeListeners.remove(listener);
        }
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
        public Property getProperty() {
            return (Property) getSource();
        }

    }

    public void addListener(ValueChangeListener listener) {
        if (valueChangeListeners == null) {
            valueChangeListeners = new LinkedList<ValueChangeListener>();
        }
        valueChangeListeners.add(listener);

    }

    public void removeListener(ValueChangeListener listener) {
        if (valueChangeListeners != null) {
            valueChangeListeners.remove(listener);
        }

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

}
