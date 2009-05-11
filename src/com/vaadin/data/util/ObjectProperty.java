/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import com.vaadin.data.Property;

/**
 * A simple data object containing one typed value. This class is a
 * straightforward implementation of the the
 * {@link com.vaadin.data.Property} interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ObjectProperty implements Property, Property.ValueChangeNotifier,
        Property.ReadOnlyStatusChangeNotifier {

    /**
     * A boolean value storing the Property's read-only status information.
     */
    private boolean readOnly = false;

    /**
     * The value contained by the Property.
     */
    private Object value;

    /**
     * Data type of the Property's value.
     */
    private final Class type;

    /**
     * Internal list of registered value change listeners.
     */
    private LinkedList valueChangeListeners = null;

    /**
     * Internal list of registered read-only status change listeners.
     */
    private LinkedList readOnlyStatusChangeListeners = null;

    /**
     * Creates a new instance of ObjectProperty with the given value. The type
     * of the property is automatically initialized to be the type of the given
     * value.
     * 
     * @param value
     *            the Initial value of the Property.
     */
    public ObjectProperty(Object value) {
        this(value, value.getClass());
    }

    /**
     * Creates a new instance of ObjectProperty with the given value and type.
     * 
     * @param value
     *            the Initial value of the Property.
     * @param type
     *            the type of the value. The value must be assignable to given
     *            type.
     */
    public ObjectProperty(Object value, Class type) {

        // Set the values
        this.type = type;
        setValue(value);
    }

    /**
     * Creates a new instance of ObjectProperty with the given value, type and
     * read-only mode status.
     * 
     * @param value
     *            the Initial value of the property.
     * @param type
     *            the type of the value. <code>value</code> must be assignable
     *            to this type.
     * @param readOnly
     *            Sets the read-only mode.
     */
    public ObjectProperty(Object value, Class type, boolean readOnly) {
        this(value, type);
        setReadOnly(readOnly);
    }

    /**
     * Returns the type of the ObjectProperty. The methods <code>getValue</code>
     * and <code>setValue</code> must be compatible with this type: one must be
     * able to safely cast the value returned from <code>getValue</code> to the
     * given type and pass any variable assignable to this type as an argument
     * to <code>setValue</code>.
     * 
     * @return type of the Property
     */
    public final Class getType() {
        return type;
    }

    /**
     * Gets the value stored in the Property.
     * 
     * @return the value stored in the Property
     */
    public Object getValue() {
        return value;
    }

    /**
     * Returns the value of the ObjectProperty in human readable textual format.
     * The return value should be assignable to the <code>setValue</code> method
     * if the Property is not in read-only mode.
     * 
     * @return <code>String</code> representation of the value stored in the
     *         ObjectProperty
     */
    @Override
    public String toString() {
        final Object value = getValue();
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }

    /**
     * Tests if the Property is in read-only mode. In read-only mode calls to
     * the method <code>setValue</code> will throw
     * <code>ReadOnlyException</code>s and will not modify the value of the
     * Property.
     * 
     * @return <code>true</code> if the Property is in read-only mode,
     *         <code>false</code> if it's not
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the Property's read-only mode to the specified status.
     * 
     * @param newStatus
     *            the new read-only status of the Property.
     */
    public void setReadOnly(boolean newStatus) {
        if (newStatus != readOnly) {
            readOnly = newStatus;
            fireReadOnlyStatusChange();
        }
    }

    /**
     * Sets the value of the property. This method supports setting from
     * <code>String</code> if either <code>String</code> is directly assignable
     * to property type, or the type class contains a string constructor.
     * 
     * @param newValue
     *            the New value of the property.
     * @throws <code>Property.ReadOnlyException</code> if the object is in
     *         read-only mode
     * @throws <code>Property.ConversionException</code> if the newValue can't
     *         be converted into the Property's native type directly or through
     *         <code>String</code>
     */
    public void setValue(Object newValue) throws Property.ReadOnlyException,
            Property.ConversionException {

        // Checks the mode
        if (isReadOnly()) {
            throw new Property.ReadOnlyException();
        }

        // Tries to assign the compatible value directly
        if (newValue == null || type.isAssignableFrom(newValue.getClass())) {
            value = newValue;
        } else {
            try {

                // Gets the string constructor
                final Constructor constr = getType().getConstructor(
                        new Class[] { String.class });

                // Creates new object from the string
                value = constr
                        .newInstance(new Object[] { newValue.toString() });

            } catch (final java.lang.Exception e) {
                throw new Property.ConversionException(e);
            }
        }

        fireValueChange();
    }

    /* Events */

    /**
     * An <code>Event</code> object specifying the ObjectProperty whose value
     * has changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    private class ValueChangeEvent extends java.util.EventObject implements
            Property.ValueChangeEvent {

        /**
         * Constructs a new value change event for this object.
         * 
         * @param source
         *            the source object of the event.
         */
        protected ValueChangeEvent(ObjectProperty source) {
            super(source);
        }

        /**
         * Gets the Property whose read-only state has changed.
         * 
         * @return source the Property of the event.
         */
        public Property getProperty() {
            return (Property) getSource();
        }
    }

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has been changed.
     * 
     * @author IT Mill Ltd.
     * @version
     * @VERSION@
     * @since 3.0
     */
    private class ReadOnlyStatusChangeEvent extends java.util.EventObject
            implements Property.ReadOnlyStatusChangeEvent {

        /**
         * Constructs a new read-only status change event for this object.
         * 
         * @param source
         *            source object of the event
         */
        protected ReadOnlyStatusChangeEvent(ObjectProperty source) {
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
     * Removes a previously registered value change listener.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeListener(Property.ValueChangeListener listener) {
        if (valueChangeListeners != null) {
            valueChangeListeners.remove(listener);
        }
    }

    /**
     * Registers a new value change listener for this ObjectProperty.
     * 
     * @param listener
     *            the new Listener to be registered
     */
    public void addListener(Property.ValueChangeListener listener) {
        if (valueChangeListeners == null) {
            valueChangeListeners = new LinkedList();
        }
        valueChangeListeners.add(listener);
    }

    /**
     * Registers a new read-only status change listener for this Property.
     * 
     * @param listener
     *            the new Listener to be registered
     */
    public void addListener(Property.ReadOnlyStatusChangeListener listener) {
        if (readOnlyStatusChangeListeners == null) {
            readOnlyStatusChangeListeners = new LinkedList();
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
     * Sends a value change event to all registered listeners.
     */
    private void fireValueChange() {
        if (valueChangeListeners != null) {
            final Object[] l = valueChangeListeners.toArray();
            final Property.ValueChangeEvent event = new ObjectProperty.ValueChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Property.ValueChangeListener) l[i]).valueChange(event);
            }
        }
    }

    /**
     * Sends a read only status change event to all registered listeners.
     */
    private void fireReadOnlyStatusChange() {
        if (readOnlyStatusChangeListeners != null) {
            final Object[] l = readOnlyStatusChangeListeners.toArray();
            final Property.ReadOnlyStatusChangeEvent event = new ObjectProperty.ReadOnlyStatusChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Property.ReadOnlyStatusChangeListener) l[i])
                        .readOnlyStatusChange(event);
            }
        }
    }
}
