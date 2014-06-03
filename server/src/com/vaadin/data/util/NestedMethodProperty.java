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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty.MethodException;

/**
 * Nested accessor based property for a bean.
 * 
 * The property is specified in the dotted notation, e.g. "address.street", and
 * can contain multiple levels of nesting.
 * 
 * When accessing the property value, all intermediate getters must exist and
 * should return non-null values when the property value is accessed. If an
 * intermediate getter returns null, a null value will be returned.
 * 
 * @see MethodProperty
 * 
 * @since 6.6
 */
public class NestedMethodProperty<T> extends AbstractProperty<T> {

    // needed for de-serialization
    private String propertyName;

    // chain of getter methods
    private transient List<Method> getMethods;
    /**
     * The setter method.
     */
    private transient Method setMethod;

    /**
     * Bean instance used as a starting point for accessing the property value.
     */
    private Object instance;

    private Class<? extends T> type;

    /* Special serialization to handle method references */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // getMethods and setMethod are reconstructed on read based on
        // propertyName
    }

    /* Special serialization to handle method references */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        initialize(instance.getClass(), propertyName);
    }

    /**
     * Constructs a nested method property for a given object instance. The
     * property name is a dot separated string pointing to a nested property,
     * e.g. "manager.address.street".
     * <p>
     * Calling getValue will return null if any intermediate getter returns null
     * 
     * @param instance
     *            top-level bean to which the property applies
     * @param propertyName
     *            dot separated nested property name
     * @throws IllegalArgumentException
     *             if the property name is invalid
     */
    public NestedMethodProperty(Object instance, String propertyName) {
        this.instance = instance;
        initialize(instance.getClass(), propertyName);
    }

    /**
     * For internal use to deduce property type etc. without a bean instance.
     * Calling {@link #setValue(Object)} or {@link #getValue()} on properties
     * constructed this way is not supported.
     * 
     * @param instanceClass
     *            class of the top-level bean
     * @param propertyName
     */
    NestedMethodProperty(Class<?> instanceClass, String propertyName) {
        instance = null;
        initialize(instanceClass, propertyName);
    }

    /**
     * Initializes most of the internal fields based on the top-level bean
     * instance and property name (dot-separated string).
     * 
     * @param beanClass
     *            class of the top-level bean to which the property applies
     * @param propertyName
     *            dot separated nested property name
     * @throws IllegalArgumentException
     *             if the property name is invalid
     */
    private void initialize(Class<?> beanClass, String propertyName)
            throws IllegalArgumentException {

        List<Method> getMethods = new ArrayList<Method>();

        String lastSimplePropertyName = propertyName;
        Class<?> lastClass = beanClass;

        // first top-level property, then go deeper in a loop
        Class<?> propertyClass = beanClass;
        String[] simplePropertyNames = propertyName.split("\\.");
        if (propertyName.endsWith(".") || 0 == simplePropertyNames.length) {
            throw new IllegalArgumentException("Invalid property name '"
                    + propertyName + "'");
        }
        for (int i = 0; i < simplePropertyNames.length; i++) {
            String simplePropertyName = simplePropertyNames[i].trim();
            if (simplePropertyName.length() > 0) {
                lastSimplePropertyName = simplePropertyName;
                lastClass = propertyClass;
                try {
                    Method getter = MethodProperty.initGetterMethod(
                            simplePropertyName, propertyClass);
                    propertyClass = getter.getReturnType();
                    getMethods.add(getter);
                } catch (final java.lang.NoSuchMethodException e) {
                    throw new IllegalArgumentException("Bean property '"
                            + simplePropertyName + "' not found", e);
                }
            } else {
                throw new IllegalArgumentException(
                        "Empty or invalid bean property identifier in '"
                                + propertyName + "'");
            }
        }

        // In case the get method is found, resolve the type
        Method lastGetMethod = getMethods.get(getMethods.size() - 1);
        Class<?> type = lastGetMethod.getReturnType();

        // Finds the set method
        Method setMethod = null;
        try {
            // Assure that the first letter is upper cased (it is a common
            // mistake to write firstName, not FirstName).
            if (Character.isLowerCase(lastSimplePropertyName.charAt(0))) {
                final char[] buf = lastSimplePropertyName.toCharArray();
                buf[0] = Character.toUpperCase(buf[0]);
                lastSimplePropertyName = new String(buf);
            }

            setMethod = lastClass.getMethod("set" + lastSimplePropertyName,
                    new Class[] { type });
        } catch (final NoSuchMethodException skipped) {
        }

        this.type = (Class<? extends T>) MethodProperty
                .convertPrimitiveType(type);
        this.propertyName = propertyName;
        this.getMethods = getMethods;
        this.setMethod = setMethod;
    }

    @Override
    public Class<? extends T> getType() {
        return type;
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly() || (null == setMethod);
    }

    /**
     * Gets the value stored in the Property. The value is resolved by calling
     * the specified getter method with the argument specified at instantiation.
     * 
     * @return the value of the Property
     */
    @Override
    public T getValue() {
        try {
            Object object = instance;
            for (Method m : getMethods) {
                object = m.invoke(object);
                if (object == null) {
                    return null;
                }
            }
            return (T) object;
        } catch (final Throwable e) {
            throw new MethodException(this, e);
        }
    }

    /**
     * Sets the value of the property. The new value must be assignable to the
     * type of this property.
     * 
     * @param newValue
     *            the New value of the property.
     * @throws <code>Property.ReadOnlyException</code> if the object is in
     *         read-only mode.
     * @see #invokeSetMethod(Object)
     */
    @Override
    public void setValue(T newValue) throws ReadOnlyException {
        // Checks the mode
        if (isReadOnly()) {
            throw new Property.ReadOnlyException();
        }

        invokeSetMethod(newValue);
        fireValueChange();
    }

    /**
     * Internal method to actually call the setter method of the wrapped
     * property.
     * 
     * @param value
     */
    protected void invokeSetMethod(T value) {
        try {
            Object object = instance;
            for (int i = 0; i < getMethods.size() - 1; i++) {
                object = getMethods.get(i).invoke(object);
            }
            setMethod.invoke(object, new Object[] { value });
        } catch (final InvocationTargetException e) {
            throw new MethodException(this, e.getTargetException());
        } catch (final Exception e) {
            throw new MethodException(this, e);
        }
    }

    /**
     * Returns an unmodifiable list of getter methods to call in sequence to get
     * the property value.
     * 
     * This API may change in future versions.
     * 
     * @return unmodifiable list of getter methods corresponding to each segment
     *         of the property name
     */
    protected List<Method> getGetMethods() {
        return Collections.unmodifiableList(getMethods);
    }

}
