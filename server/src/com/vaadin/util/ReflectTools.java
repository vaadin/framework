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
package com.vaadin.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An util class with helpers for reflection operations. Used internally by
 * Vaadin and should not be used by application developers. Subject to change at
 * any time.
 * 
 * @since 6.2
 */
public class ReflectTools implements Serializable {
    /**
     * Locates the method in the given class. Returns null if the method is not
     * found. Throws an ExceptionInInitializerError if there is a problem
     * locating the method as this is mainly called from static blocks.
     * 
     * @param cls
     *            Class that contains the method
     * @param methodName
     *            The name of the method
     * @param parameterTypes
     *            The parameter types for the method.
     * @return A reference to the method
     * @throws ExceptionInInitializerError
     *             Wraps any exception in an {@link ExceptionInInitializerError}
     *             so this method can be called from a static initializer.
     */
    public static Method findMethod(Class<?> cls, String methodName,
            Class<?>... parameterTypes) throws ExceptionInInitializerError {
        try {
            return cls.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Returns the value of the java field.
     * <p>
     * Uses getter if present, otherwise tries to access even private fields
     * directly.
     * 
     * @param object
     *            The object containing the field
     * @param field
     *            The field we want to get the value for
     * @return The value of the field in the object
     * @throws InvocationTargetException
     *             If the value could not be retrieved
     * @throws IllegalAccessException
     *             If the value could not be retrieved
     * @throws IllegalArgumentException
     *             If the value could not be retrieved
     */
    public static Object getJavaFieldValue(Object object,
            java.lang.reflect.Field field) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            Method getter = pd.getReadMethod();
            if (getter != null) {
                return getter.invoke(object, (Object[]) null);
            }
        } catch (IntrospectionException e1) {
            // Ignore this and try to get directly using the field
        }

        // Try to get the value or throw an exception
        if (!field.isAccessible()) {
            // Try to gain access even if field is private
            field.setAccessible(true);
        }
        return field.get(object);
    }

    /**
     * Returns the value of the java field that is assignable to the property
     * type.
     * <p>
     * Uses getter if a getter for the correct return type is present, otherwise
     * tries to access even private fields directly. If the java field is not
     * assignable to the property type throws an IllegalArgumentException.
     * 
     * @param object
     *            The object containing the field
     * @param field
     *            The field we want to get the value for
     * @param propertyType
     *            The type the field must be assignable to
     * @return The value of the field in the object
     * @throws InvocationTargetException
     *             If the value could not be retrieved
     * @throws IllegalAccessException
     *             If the value could not be retrieved
     * @throws IllegalArgumentException
     *             If the value could not be retrieved
     */
    public static Object getJavaFieldValue(Object object,
            java.lang.reflect.Field field, Class<?> propertyType)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            if (propertyType.isAssignableFrom(pd.getPropertyType())) {
                Method getter = pd.getReadMethod();
                if (getter != null) {
                    return getter.invoke(object, (Object[]) null);
                }
            }
        } catch (IntrospectionException e1) {
            // Ignore this and try to get directly using the field
        }
        // If the field's type cannot be casted in to the requested type
        if (!propertyType.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException();
        }
        // Try to get the value or throw an exception
        if (!field.isAccessible()) {
            // Try to gain access even if field is private
            field.setAccessible(true);
        }
        return field.get(object);
    }

    /**
     * Sets the value of a java field.
     * <p>
     * Uses setter if present, otherwise tries to access even private fields
     * directly.
     * 
     * @param object
     *            The object containing the field
     * @param field
     *            The field we want to set the value for
     * @param value
     *            The value to set
     * @throws IllegalAccessException
     *             If the value could not be assigned to the field
     * @throws IllegalArgumentException
     *             If the value could not be assigned to the field
     * @throws InvocationTargetException
     *             If the value could not be assigned to the field
     */
    public static void setJavaFieldValue(Object object,
            java.lang.reflect.Field field, Object value)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            Method setter = pd.getWriteMethod();
            if (setter != null) {
                // Exceptions are thrown forward if this fails
                setter.invoke(object, value);
            }
        } catch (IntrospectionException e1) {
            // Ignore this and try to set directly using the field
        }

        // Try to set the value directly to the field or throw an exception
        if (!field.isAccessible()) {
            // Try to gain access even if field is private
            field.setAccessible(true);
        }
        field.set(object, value);
    }
}
