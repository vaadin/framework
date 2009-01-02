package com.itmill.toolkit.data.util;

import java.util.LinkedList;

import com.itmill.toolkit.data.Property;

/**
 * Formatting proxy for a property.
 * 
 * <p>
 * This class can be used to implement formatting for any type of Property
 * datasources. The idea is to connect this as proxy between UI component and
 * the original datasource.
 * </p>
 * 
 * <p>
 * For example <code>
 * textfield.setPropertyDataSource(new PropertyFormatter(property) {
            public String format(Object value) {
                return ((Double) value).toString() + "000000000";
            }

            public Object parse(String formattedValue) throws Exception {
                return Double.parseDouble(formattedValue);
            }

        });</code> adds formatter for Double-typed property that extends standard
 * "1.0" notation with more zeroes.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @since 5.3.0
 */
public abstract class PropertyFormatter implements Property,
        Property.ValueChangeNotifier, Property.ValueChangeListener,
        Property.ReadOnlyStatusChangeListener,
        Property.ReadOnlyStatusChangeNotifier {

    /**
     * Internal list of registered value change listeners.
     */
    private LinkedList valueChangeListeners = null;

    /**
     * Internal list of registered read-only status change listeners.
     */
    private LinkedList readOnlyStatusChangeListeners = null;

    /** Datasource that stores the actual value. */
    Property dataSource;

    /**
     * Construct a new formatter that is connected to given datasource.
     * 
     * @param propertyDataSource
     *            to connect this property to.
     */
    public PropertyFormatter(Property propertyDataSource) {

        setPropertyDataSource(propertyDataSource);
    }

    /**
     * Gets the current data source of the formatter, if any.
     * 
     * @return the current data source as a Property, or <code>null</code> if
     *         none defined.
     */
    public Property getPropertyDataSource() {
        return dataSource;
    }

    /**
     * Sets the specified Property as the data source for the formatter.
     * 
     * 
     * <p>
     * Remember that new data sources getValue() must return objects that are
     * compatible with parse() and format() methods.
     * </p>
     * 
     * @param newDataSource
     *            the new data source Property.
     */
    public void setPropertyDataSource(Property newDataSource) {

        boolean readOnly = false;
        String prevValue = null;

        if (dataSource != null) {
            if (dataSource instanceof Property.ValueChangeNotifier) {
                ((Property.ValueChangeNotifier) dataSource)
                        .removeListener(this);
            }
            if (dataSource instanceof Property.ReadOnlyStatusChangeListener) {
                ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                        .removeListener(this);
            }
            readOnly = isReadOnly();
            prevValue = toString();
        }

        dataSource = newDataSource;

        if (dataSource != null) {
            if (dataSource instanceof Property.ValueChangeNotifier) {
                ((Property.ValueChangeNotifier) dataSource).addListener(this);
            }
            if (dataSource instanceof Property.ReadOnlyStatusChangeListener) {
                ((Property.ReadOnlyStatusChangeNotifier) dataSource)
                        .addListener(this);
            }
        }

        if (isReadOnly() != readOnly) {
            fireReadOnlyStatusChange();
        }
        String newVal = toString();
        if ((prevValue == null && newVal != null)
                || !prevValue.equals(prevValue)) {
            fireValueChange();
        }
    }

    /* Documented in the interface */
    public Class getType() {
        return String.class;
    }

    /**
     * Get the formatted value.
     * 
     * @return If the datasource returns null, this is null. Otherwise this is
     *         String given by format().
     */
    public Object getValue() {
        return toString();
    }

    /**
     * Get the formatted value.
     * 
     * @return If the datasource returns null, this is null. Otherwise this is
     *         String given by format().
     */
    @Override
    public String toString() {
        Object value = dataSource == null ? false : dataSource.getValue();
        if (value == null) {
            return null;
        }
        return format(value);
    }

    /** Reflects the read-only status of the datasource. */
    public boolean isReadOnly() {
        return dataSource == null ? false : dataSource.isReadOnly();
    }

    /**
     * This method must be implemented to format the values received from
     * DataSource.
     * 
     * @param value
     *            Value object got from the datasource. This is guaranteed to be
     *            non-null and of the type compatible with getType() of the
     *            datasource.
     * @return
     */
    abstract public String format(Object value);

    /**
     * Parse string and convert it to format compatible with datasource.
     * 
     * The method is required to assure that parse(format(x)) equals x.
     * 
     * @param formattedValue
     *            This is guaranteed to be non-null string.
     * @return Non-null value compatible with datasource.
     * @throws Exception
     *             Any type of exception can be thrown to indicate that the
     *             conversion was not succesful.
     */
    abstract public Object parse(String formattedValue) throws Exception;

    /**
     * Sets the Property's read-only mode to the specified status.
     * 
     * @param newStatus
     *            the new read-only status of the Property.
     */
    public void setReadOnly(boolean newStatus) {
        if (dataSource != null) {
            dataSource.setReadOnly(newStatus);
        }
    }

    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        if (dataSource == null) {
            return;
        }
        if (newValue == null) {
            dataSource.setValue(null);
        }
        try {
            dataSource.setValue(parse((String) newValue));
            if (!newValue.equals(toString())) {
                fireValueChange();
            }
        } catch (Exception e) {
            if (e instanceof ConversionException) {
                throw (ConversionException) e;
            } else {
                throw new ConversionException(e);
            }
        }

    }

    /**
     * An <code>Event</code> object specifying the ObjectProperty whose value
     * has changed.
     * 
     * @author IT Mill Ltd.
     * @since 5.3.0
     */
    private class ValueChangeEvent extends java.util.EventObject implements
            Property.ValueChangeEvent {

        /**
 * 
 */
        private static final long serialVersionUID = -489631310964258710L;

        /**
         * Constructs a new value change event for this object.
         * 
         * @param source
         *            the source object of the event.
         */
        protected ValueChangeEvent(PropertyFormatter source) {
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
     * @since 5.3.0
     */
    private class ReadOnlyStatusChangeEvent extends java.util.EventObject
            implements Property.ReadOnlyStatusChangeEvent {

        /**
 * 
 */
        private static final long serialVersionUID = 8329395774911454548L;

        /**
         * Constructs a new read-only status change event for this object.
         * 
         * @param source
         *            source object of the event
         */
        protected ReadOnlyStatusChangeEvent(PropertyFormatter source) {
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
            final Property.ValueChangeEvent event = new ValueChangeEvent(this);
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
            final Property.ReadOnlyStatusChangeEvent event = new ReadOnlyStatusChangeEvent(
                    this);
            for (int i = 0; i < l.length; i++) {
                ((Property.ReadOnlyStatusChangeListener) l[i])
                        .readOnlyStatusChange(event);
            }
        }
    }

    /**
     * Listens for changes in the datasource.
     * 
     * This should not be called directly.
     */
    public void valueChange(
            com.itmill.toolkit.data.Property.ValueChangeEvent event) {
        fireValueChange();
    }

    /**
     * Listens for changes in the datasource.
     * 
     * This should not be called directly.
     */
    public void readOnlyStatusChange(
            com.itmill.toolkit.data.Property.ReadOnlyStatusChangeEvent event) {
        fireReadOnlyStatusChange();
    }

}
