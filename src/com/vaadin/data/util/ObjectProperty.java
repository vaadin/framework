/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.data.util;

import java.lang.reflect.Constructor;

import com.vaadin.data.Property;

/**
 * A simple data object containing one typed value. This class is a
 * straightforward implementation of the the {@link com.vaadin.data.Property}
 * interface.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ObjectProperty<T> extends AbstractProperty {

    /**
     * The value contained by the Property.
     */
    private T value;

    /**
     * Data type of the Property's value.
     */
    private final Class<T> type;

    /**
     * Creates a new instance of ObjectProperty with the given value. The type
     * of the property is automatically initialized to be the type of the given
     * value.
     * 
     * @param value
     *            the Initial value of the Property.
     */
    @SuppressWarnings("unchecked")
    // the cast is safe, because an object of type T has class Class<T>
    public ObjectProperty(T value) {
        this(value, (Class<T>) value.getClass());
    }

    /**
     * Creates a new instance of ObjectProperty with the given value and type.
     * 
     * Any value of type Object is accepted because, if the type class contains
     * a string constructor, the toString of the value is used to create the new
     * value. See {@link #setValue(Object)}.
     * 
     * @param value
     *            the Initial value of the Property.
     * @param type
     *            the type of the value. The value must be assignable to given
     *            type.
     */
    public ObjectProperty(Object value, Class<T> type) {

        // Set the values
        this.type = type;
        setValue(value);
    }

    /**
     * Creates a new instance of ObjectProperty with the given value, type and
     * read-only mode status.
     * 
     * Any value of type Object is accepted, see
     * {@link #ObjectProperty(Object, Class)}.
     * 
     * @param value
     *            the Initial value of the property.
     * @param type
     *            the type of the value. <code>value</code> must be assignable
     *            to this type.
     * @param readOnly
     *            Sets the read-only mode.
     */
    public ObjectProperty(Object value, Class<T> type, boolean readOnly) {
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
    public final Class<T> getType() {
        return type;
    }

    /**
     * Gets the value stored in the Property.
     * 
     * @return the value stored in the Property
     */
    public T getValue() {
        return value;
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
            @SuppressWarnings("unchecked")
            // the cast is safe after an isAssignableFrom check
            T value = (T) newValue;
            this.value = value;
        } else {
            try {

                // Gets the string constructor
                final Constructor<T> constr = getType().getConstructor(
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

}
