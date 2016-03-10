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
package com.vaadin.data.util.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Simple container filter comparing an item property value against a given
 * constant value. Use the nested classes {@link Equal}, {@link Greater},
 * {@link Less}, {@link GreaterOrEqual} and {@link LessOrEqual} instead of this
 * class directly.
 * 
 * This filter also directly supports in-memory filtering.
 * 
 * The reference and actual values must implement {@link Comparable} and the
 * class of the actual property value must be assignable from the class of the
 * reference value.
 * 
 * @since 6.6
 */
public abstract class Compare implements Filter {

    public enum Operation {
        EQUAL, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL
    }

    private final Object propertyId;
    private final Operation operation;
    private final Object value;

    /**
     * A {@link Compare} filter that accepts items for which the identified
     * property value is equal to <code>value</code>.
     * 
     * For in-memory filters, {@link Comparable#compareTo(Object)} or, if not
     * Comparable, {@link #equals(Object)} is used for the comparison. For other
     * containers, the comparison implementation is container dependent and may
     * use e.g. database comparison operations.
     * 
     * @since 6.6
     */
    public static final class Equal extends Compare {
        /**
         * Construct a filter that accepts items for which the identified
         * property value is equal to <code>value</code>.
         * 
         * For in-memory filters, equals() is used for the comparison. For other
         * containers, the comparison implementation is container dependent and
         * may use e.g. database comparison operations.
         * 
         * @param propertyId
         *            the identifier of the property whose value to compare
         *            against value, not null
         * @param value
         *            the value to compare against - null values may or may not
         *            be supported depending on the container
         */
        public Equal(Object propertyId, Object value) {
            super(propertyId, value, Operation.EQUAL);
        }
    }

    /**
     * A {@link Compare} filter that accepts items for which the identified
     * property value is greater than <code>value</code>.
     * 
     * For in-memory filters, the values must implement {@link Comparable} and
     * {@link Comparable#compareTo(Object)} is used for the comparison. For
     * other containers, the comparison implementation is container dependent
     * and may use e.g. database comparison operations.
     * 
     * @since 6.6
     */
    public static final class Greater extends Compare {
        /**
         * Construct a filter that accepts items for which the identified
         * property value is greater than <code>value</code>.
         * 
         * For in-memory filters, the values must implement {@link Comparable}
         * and {@link Comparable#compareTo(Object)} is used for the comparison.
         * For other containers, the comparison implementation is container
         * dependent and may use e.g. database comparison operations.
         * 
         * @param propertyId
         *            the identifier of the property whose value to compare
         *            against value, not null
         * @param value
         *            the value to compare against - null values may or may not
         *            be supported depending on the container
         */
        public Greater(Object propertyId, Object value) {
            super(propertyId, value, Operation.GREATER);
        }
    }

    /**
     * A {@link Compare} filter that accepts items for which the identified
     * property value is less than <code>value</code>.
     * 
     * For in-memory filters, the values must implement {@link Comparable} and
     * {@link Comparable#compareTo(Object)} is used for the comparison. For
     * other containers, the comparison implementation is container dependent
     * and may use e.g. database comparison operations.
     * 
     * @since 6.6
     */
    public static final class Less extends Compare {
        /**
         * Construct a filter that accepts items for which the identified
         * property value is less than <code>value</code>.
         * 
         * For in-memory filters, the values must implement {@link Comparable}
         * and {@link Comparable#compareTo(Object)} is used for the comparison.
         * For other containers, the comparison implementation is container
         * dependent and may use e.g. database comparison operations.
         * 
         * @param propertyId
         *            the identifier of the property whose value to compare
         *            against value, not null
         * @param value
         *            the value to compare against - null values may or may not
         *            be supported depending on the container
         */
        public Less(Object propertyId, Object value) {
            super(propertyId, value, Operation.LESS);
        }
    }

    /**
     * A {@link Compare} filter that accepts items for which the identified
     * property value is greater than or equal to <code>value</code>.
     * 
     * For in-memory filters, the values must implement {@link Comparable} and
     * {@link Comparable#compareTo(Object)} is used for the comparison. For
     * other containers, the comparison implementation is container dependent
     * and may use e.g. database comparison operations.
     * 
     * @since 6.6
     */
    public static final class GreaterOrEqual extends Compare {
        /**
         * Construct a filter that accepts items for which the identified
         * property value is greater than or equal to <code>value</code>.
         * 
         * For in-memory filters, the values must implement {@link Comparable}
         * and {@link Comparable#compareTo(Object)} is used for the comparison.
         * For other containers, the comparison implementation is container
         * dependent and may use e.g. database comparison operations.
         * 
         * @param propertyId
         *            the identifier of the property whose value to compare
         *            against value, not null
         * @param value
         *            the value to compare against - null values may or may not
         *            be supported depending on the container
         */
        public GreaterOrEqual(Object propertyId, Object value) {
            super(propertyId, value, Operation.GREATER_OR_EQUAL);
        }
    }

    /**
     * A {@link Compare} filter that accepts items for which the identified
     * property value is less than or equal to <code>value</code>.
     * 
     * For in-memory filters, the values must implement {@link Comparable} and
     * {@link Comparable#compareTo(Object)} is used for the comparison. For
     * other containers, the comparison implementation is container dependent
     * and may use e.g. database comparison operations.
     * 
     * @since 6.6
     */
    public static final class LessOrEqual extends Compare {
        /**
         * Construct a filter that accepts items for which the identified
         * property value is less than or equal to <code>value</code>.
         * 
         * For in-memory filters, the values must implement {@link Comparable}
         * and {@link Comparable#compareTo(Object)} is used for the comparison.
         * For other containers, the comparison implementation is container
         * dependent and may use e.g. database comparison operations.
         * 
         * @param propertyId
         *            the identifier of the property whose value to compare
         *            against value, not null
         * @param value
         *            the value to compare against - null values may or may not
         *            be supported depending on the container
         */
        public LessOrEqual(Object propertyId, Object value) {
            super(propertyId, value, Operation.LESS_OR_EQUAL);
        }
    }

    /**
     * Constructor for a {@link Compare} filter that compares the value of an
     * item property with the given constant <code>value</code>.
     * 
     * This constructor is intended to be used by the nested static classes only
     * ({@link Equal}, {@link Greater}, {@link Less}, {@link GreaterOrEqual},
     * {@link LessOrEqual}).
     * 
     * For in-memory filtering, comparisons except EQUAL require that the values
     * implement {@link Comparable} and {@link Comparable#compareTo(Object)} is
     * used for the comparison. The equality comparison is performed using
     * {@link Object#equals(Object)}.
     * 
     * For other containers, the comparison implementation is container
     * dependent and may use e.g. database comparison operations. Therefore, the
     * behavior of comparisons might differ in some cases between in-memory and
     * other containers.
     * 
     * @param propertyId
     *            the identifier of the property whose value to compare against
     *            value, not null
     * @param value
     *            the value to compare against - null values may or may not be
     *            supported depending on the container
     * @param operation
     *            the comparison {@link Operation} to use
     */
    Compare(Object propertyId, Object value, Operation operation) {
        this.propertyId = propertyId;
        this.value = value;
        this.operation = operation;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item) {
        final Property<?> p = item.getItemProperty(getPropertyId());
        if (null == p) {
            return false;
        }
        Object value = p.getValue();
        switch (getOperation()) {
        case EQUAL:
            return compareEquals(value);
        case GREATER:
            return compareValue(value) > 0;
        case LESS:
            return compareValue(value) < 0;
        case GREATER_OR_EQUAL:
            return compareValue(value) >= 0;
        case LESS_OR_EQUAL:
            return compareValue(value) <= 0;
        }
        // all cases should have been processed above
        return false;
    }

    /**
     * Checks if the this value equals the given value. Favors Comparable over
     * equals to better support e.g. BigDecimal where equals is stricter than
     * compareTo.
     * 
     * @param otherValue
     *            The value to compare to
     * @return true if the values are equal, false otherwise
     */
    private boolean compareEquals(Object otherValue) {
        if (value == null || otherValue == null) {
            return (otherValue == value);
        } else if (value == otherValue) {
            return true;
        } else if (value instanceof Comparable
                && otherValue.getClass()
                        .isAssignableFrom(getValue().getClass())) {
            return ((Comparable) value).compareTo(otherValue) == 0;
        } else {
            return value.equals(otherValue);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected int compareValue(Object value1) {
        if (null == value) {
            return null == value1 ? 0 : -1;
        } else if (null == value1) {
            return 1;
        } else if (getValue() instanceof Comparable
                && value1.getClass().isAssignableFrom(getValue().getClass())) {
            return -((Comparable) getValue()).compareTo(value1);
        }
        throw new IllegalArgumentException("Could not compare the arguments: "
                + value1 + ", " + getValue());
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return getPropertyId().equals(propertyId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        // Only objects of the same class can be equal
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        final Compare o = (Compare) obj;

        // Checks the properties one by one
        if (getPropertyId() != o.getPropertyId() && null != o.getPropertyId()
                && !o.getPropertyId().equals(getPropertyId())) {
            return false;
        }
        if (getOperation() != o.getOperation()) {
            return false;
        }
        return (null == getValue()) ? null == o.getValue() : getValue().equals(
                o.getValue());
    }

    @Override
    public int hashCode() {
        return (null != getPropertyId() ? getPropertyId().hashCode() : 0)
                ^ (null != getValue() ? getValue().hashCode() : 0);
    }

    /**
     * Returns the property id of the property to compare against the fixed
     * value.
     * 
     * @return property id (not null)
     */
    public Object getPropertyId() {
        return propertyId;
    }

    /**
     * Returns the comparison operation.
     * 
     * @return {@link Operation}
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Returns the value to compare the property against.
     * 
     * @return comparison reference value
     */
    public Object getValue() {
        return value;
    }
}
