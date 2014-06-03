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
package com.vaadin.data.util.sqlcontainer;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.LegacyPropertyHelper;
import com.vaadin.data.util.converter.Converter.ConversionException;

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
    private boolean primaryKey = false;

    /**
     * Prevent instantiation without required parameters.
     */
    @SuppressWarnings("unused")
    private ColumnProperty() {
    }

    /**
     * Deprecated constructor for ColumnProperty. If this is used the primary
     * keys are not identified correctly in some cases for some databases (i.e.
     * Oracle). See http://dev.vaadin.com/ticket/9145.
     * 
     * @param propertyId
     * @param readOnly
     * @param allowReadOnlyChange
     * @param nullable
     * @param value
     * @param type
     * 
     * @deprecated As of 7.0. Use
     *             {@link #ColumnProperty(String, boolean, boolean, boolean, boolean, Object, Class)
     *             instead
     */
    @Deprecated
    public ColumnProperty(String propertyId, boolean readOnly,
            boolean allowReadOnlyChange, boolean nullable, Object value,
            Class<?> type) {
        this(propertyId, readOnly, allowReadOnlyChange, nullable, false, value,
                type);
    }

    /**
     * Creates a new ColumnProperty instance.
     * 
     * @param propertyId
     *            The ID of this property.
     * @param readOnly
     *            Whether this property is read-only.
     * @param allowReadOnlyChange
     *            Whether the read-only status of this property can be changed.
     * @param nullable
     *            Whether this property accepts null values.
     * @param primaryKey
     *            Whether this property corresponds to a database primary key.
     * @param value
     *            The value of this property.
     * @param type
     *            The type of this property.
     */
    public ColumnProperty(String propertyId, boolean readOnly,
            boolean allowReadOnlyChange, boolean nullable, boolean primaryKey,
            Object value, Class<?> type) {

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
        this.primaryKey = primaryKey;
    }

    /**
     * Returns the current value for this property. To get the previous value
     * (if one exists) for a modified property use {@link #getOldValue()}.
     * 
     * @return
     */
    @Override
    public Object getValue() {
        if (isModified()) {
            return changedValue;
        }
        return value;
    }

    /**
     * Returns the original non-modified value of this property if it has been
     * modified.
     * 
     * @return The original value if <code>isModified()</code> is true,
     *         <code>getValue()</code> otherwise.
     */
    public Object getOldValue() {
        return value;
    }

    @Override
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

            if (!getType().isAssignableFrom(newValue.getClass())) {
                throw new IllegalArgumentException(
                        "Illegal value type for ColumnProperty");
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
        modified = true;
        owner.getContainer().itemChangeNotification(owner);
    }

    private boolean isValueAlreadySet(Object newValue) {
        Object referenceValue = isModified() ? changedValue : value;

        return (isNullable() && newValue == null && referenceValue == null)
                || newValue.equals(referenceValue);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Returns whether the read-only status of this property can be changed
     * using {@link #setReadOnly(boolean)}.
     * <p>
     * Used to prevent setting to read/write mode a property that is not allowed
     * to be written by the underlying database. Also used for values like
     * VERSION and AUTO_INCREMENT fields that might be set to read-only by the
     * container but the database still allows writes.
     * 
     * @return true if the read-only status can be changed, false otherwise.
     */
    public boolean isReadOnlyChangeAllowed() {
        return allowReadOnlyChange;
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        if (allowReadOnlyChange) {
            readOnly = newStatus;
        }
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getPropertyId() {
        return propertyId;
    }

    /**
     * Returns a string representation of this object. The returned string
     * representation depends on if the legacy Property toString mode is enabled
     * or disabled.
     * <p>
     * If legacy Property toString mode is enabled, returns the value of this
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

    private static Logger getLogger() {
        return Logger.getLogger(ColumnProperty.class.getName());
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
     * Return whether the value of this property should be persisted to the
     * database.
     * 
     * @return true if the value should be written to the database, false
     *         otherwise.
     */
    public boolean isPersistent() {
        if (isVersionColumn()) {
            return false;
        } else if (isReadOnlyChangeAllowed() && !isReadOnly()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether or not this property is used as a row identifier.
     * 
     * @return true if the property is a row identifier, false otherwise.
     */
    public boolean isRowIdentifier() {
        return isPrimaryKey() || isVersionColumn();
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
