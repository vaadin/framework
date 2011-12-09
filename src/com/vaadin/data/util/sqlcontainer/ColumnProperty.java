/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer;

import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import com.vaadin.data.Property;

/**
 * ColumnProperty represents the value of one column in a RowItem. In addition
 * to the value, ColumnProperty also contains some basic column attributes such
 * as nullability status, read-only status and data type.
 * 
 * Note that depending on the QueryDelegate in use this does not necessarily map
 * into an actual column in a database table.
 */
final public class ColumnProperty implements Property {
    private static final long serialVersionUID = -3694463129581802457L;

    private RowItem owner;

    private String propertyId;

    private boolean readOnly;
    private boolean allowReadOnlyChange = true;
    private boolean nullable = true;

    private Object value;
    private Object changedValue;
    private Class<?> type;

    private boolean modified;

    private boolean versionColumn;

    /**
     * Prevent instantiation without required parameters.
     */
    @SuppressWarnings("unused")
    private ColumnProperty() {
    }

    public ColumnProperty(String propertyId, boolean readOnly,
            boolean allowReadOnlyChange, boolean nullable, Object value,
            Class<?> type) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Properties must be named.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Property type must be set.");
        }
        this.propertyId = propertyId;
        this.type = type;
        this.value = value;

        this.allowReadOnlyChange = allowReadOnlyChange;
        this.nullable = nullable;
        this.readOnly = readOnly;
    }

    public Object getValue() {
        if (isModified()) {
            return changedValue;
        }
        return value;
    }

    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        if (newValue == null && !nullable) {
            throw new NotNullableException(
                    "Null values are not allowed for this property.");
        }
        if (readOnly) {
            throw new ReadOnlyException(
                    "Cannot set value for read-only property.");
        }

        /* Check if this property is a date property. */
        boolean isDateProperty = Time.class.equals(getType())
                || Date.class.equals(getType())
                || Timestamp.class.equals(getType());

        if (newValue != null) {
            /* Handle SQL dates, times and Timestamps given as java.util.Date */
            if (isDateProperty) {
                /*
                 * Try to get the millisecond value from the new value of this
                 * property. Possible type to convert from is java.util.Date.
                 */
                long millis = 0;
                if (newValue instanceof java.util.Date) {
                    millis = ((java.util.Date) newValue).getTime();
                    /*
                     * Create the new object based on the millisecond value,
                     * according to the type of this property.
                     */
                    if (Time.class.equals(getType())) {
                        newValue = new Time(millis);
                    } else if (Date.class.equals(getType())) {
                        newValue = new Date(millis);
                    } else if (Timestamp.class.equals(getType())) {
                        newValue = new Timestamp(millis);
                    }
                }
            }

            /*
             * If the type is not correct, try to generate it through a possibly
             * existing String constructor.
             */
            if (!getType().isAssignableFrom(newValue.getClass())) {
                try {
                    final Constructor<?> constr = getType().getConstructor(
                            new Class[] { String.class });
                    newValue = constr.newInstance(new Object[] { newValue
                            .toString() });
                } catch (Exception e) {
                    throw new ConversionException(e);
                }
            }

            /*
             * If the value to be set is the same that has already been set, do
             * not set it again.
             */
            if (isValueAlreadySet(newValue)) {
                return;
            }
        }

        /* Set the new value and notify container of the change. */
        changedValue = newValue;
        owner.getContainer().itemChangeNotification(owner);
        modified = true;
    }

    private boolean isValueAlreadySet(Object newValue) {
        Object referenceValue = isModified() ? changedValue : value;

        return (isNullable() && newValue == null && referenceValue == null)
                || newValue.equals(referenceValue);
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isReadOnlyChangeAllowed() {
        return allowReadOnlyChange;
    }

    public void setReadOnly(boolean newStatus) {
        if (allowReadOnlyChange) {
            readOnly = newStatus;
        }
    }

    public String getPropertyId() {
        return propertyId;
    }

    /**
     * Returns the value of the Property in human readable textual format.
     * 
     * @see java.lang.Object#toString()
     * @deprecated get the string representation from the value, or use
     *             getStringValue() during migration
     */
    @Deprecated
    @Override
    public String toString() {
        throw new UnsupportedOperationException(
                "Use ColumnProperty.getValue() instead of ColumnProperty.toString()");
    }

    /**
     * Returns the (UI type) value of the field converted to a String using
     * toString().
     * 
     * This method exists to help migration from the use of Property.toString()
     * to get the field value - for new applications, access getValue()
     * directly. This method may disappear in future Vaadin versions.
     * 
     * @return string representation of the field value or null if the value is
     *         null
     * @since 7.0
     */
    public String getStringValue() {
        final Object value = getValue();
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public void setOwner(RowItem owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner can not be set to null.");
        }
        if (this.owner != null) {
            throw new IllegalStateException(
                    "ColumnProperties can only be bound once.");
        }
        this.owner = owner;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(boolean versionColumn) {
        this.versionColumn = versionColumn;
    }

    public boolean isNullable() {
        return nullable;
    }

    /**
     * An exception that signals that a <code>null</code> value was passed to
     * the <code>setValue</code> method, but the value of this property can not
     * be set to <code>null</code>.
     */
    @SuppressWarnings("serial")
    public class NotNullableException extends RuntimeException {

        /**
         * Constructs a new <code>NotNullableException</code> without a detail
         * message.
         */
        public NotNullableException() {
        }

        /**
         * Constructs a new <code>NotNullableException</code> with the specified
         * detail message.
         * 
         * @param msg
         *            the detail message
         */
        public NotNullableException(String msg) {
            super(msg);
        }

        /**
         * Constructs a new <code>NotNullableException</code> from another
         * exception.
         * 
         * @param cause
         *            The cause of the failure
         */
        public NotNullableException(Throwable cause) {
            super(cause);
        }
    }

    public void commit() {
        if (isModified()) {
            modified = false;
            value = changedValue;
        }
    }
}
