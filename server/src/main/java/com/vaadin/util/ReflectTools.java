/*
 * Copyright 2000-2021 Vaadin Ltd.
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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * An util class with helpers for reflection operations. Used internally by
 * Vaadin and should not be used by application developers. Subject to change at
 * any time.
 *
 * @since 6.2
 */
public class ReflectTools implements Serializable {

    static final String CREATE_INSTANCE_FAILED = "Unable to create an instance of '%s'. Make sure it has a no-arg constructor";
    static final String CREATE_INSTANCE_FAILED_FOR_NON_STATIC_MEMBER_CLASS = "Unable to create an instance of '%s'. Make sure the class is static if it is a nested class.";
    static final String CREATE_INSTANCE_FAILED_ACCESS_EXCEPTION = "Unable to create an instance of '%s'. Make sure the class is public and that is has a public no-arg constructor.";
    static final String CREATE_INSTANCE_FAILED_NO_PUBLIC_NOARG_CONSTRUCTOR = "Unable to create an instance of '%s'. Make sure the class has a public no-arg constructor.";
    static final String CREATE_INSTANCE_FAILED_LOCAL_CLASS = "Cannot instantiate local class '%s'. Move class declaration outside the method.";
    static final String CREATE_INSTANCE_FAILED_CONSTRUCTOR_THREW_EXCEPTION = "Unable to create an instance of '%s'. The constructor threw an exception.";

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
    public static Object getJavaFieldValue(Object object, Field field)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
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
    public static Object getJavaFieldValue(Object object, Field field,
            Class<?> propertyType) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
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
    public static void setJavaFieldValue(Object object, Field field,
            Object value) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
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

    /**
     * @since 7.4
     */
    public static Class<?> convertPrimitiveType(Class<?> type) {
        // Gets the return type from get method
        if (type.isPrimitive()) {
            if (type.equals(Boolean.TYPE)) {
                type = Boolean.class;
            } else if (type.equals(Integer.TYPE)) {
                type = Integer.class;
            } else if (type.equals(Float.TYPE)) {
                type = Float.class;
            } else if (type.equals(Double.TYPE)) {
                type = Double.class;
            } else if (type.equals(Byte.TYPE)) {
                type = Byte.class;
            } else if (type.equals(Character.TYPE)) {
                type = Character.class;
            } else if (type.equals(Short.TYPE)) {
                type = Short.class;
            } else if (type.equals(Long.TYPE)) {
                type = Long.class;
            }
        }
        return type;
    }

    private ReflectTools() {
    }

    /**
     * Finds the most specific class that both provided classes extend from.
     *
     * @param a
     *            one class to get the base type for, not <code>null</code>
     * @param b
     *            another class to get the base type for, not <code>null</code>
     * @return the most specific base class, not <code>null</code>
     *
     * @since 8.0
     */
    public static Class<?> findCommonBaseType(Class<?> a, Class<?> b) {
        if (a.isInterface()) {
            throw new IllegalArgumentException("a cannot be an interface");
        }
        if (b.isInterface()) {
            throw new IllegalArgumentException("b cannot be an interface");
        }

        if (a.isAssignableFrom(b)) {
            return a;
        } else if (b.isAssignableFrom(a)) {
            return b;
        }

        Class<?> currentClass = a;
        while (!currentClass.isAssignableFrom(b)) {
            currentClass = currentClass.getSuperclass();
        }

        return currentClass;
    }

    /**
     * Creates a instance of the given class with a no-arg constructor.
     * <p>
     * Catches all exceptions which might occur and wraps them in a
     * {@link IllegalArgumentException} with a descriptive error message hinting
     * of what might be wrong with the class that could not be instantiated.
     *
     * @param cls
     *            the class to instantiate
     * @return an instance of the class
     * @since 8.1.1
     */
    public static <T> T createInstance(Class<T> cls) {
        checkClassAccessibility(cls);
        try {
            return cls.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format(
                    CREATE_INSTANCE_FAILED_NO_PUBLIC_NOARG_CONSTRUCTOR,
                    cls.getName()), e);
        } catch (InstantiationException e) {
            if (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers())) {
                throw new IllegalArgumentException(String.format(
                        CREATE_INSTANCE_FAILED_FOR_NON_STATIC_MEMBER_CLASS,
                        cls.getName()), e);
            } else {
                throw new IllegalArgumentException(
                        String.format(CREATE_INSTANCE_FAILED, cls.getName()),
                        e);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format(
                    CREATE_INSTANCE_FAILED_ACCESS_EXCEPTION, cls.getName()), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format(CREATE_INSTANCE_FAILED, cls.getName()), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(String.format(
                    CREATE_INSTANCE_FAILED_CONSTRUCTOR_THREW_EXCEPTION,
                    cls.getName()), e);
        }
    }

    /**
     * Makes a check whether the provided class is externally accessible for
     * instantiation (e.g. it's not inner class (nested and not static) and is
     * not a local class).
     *
     * @param cls
     *            type to check
     */
    private static void checkClassAccessibility(Class<?> cls) {
        if (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers())) {
            throw new IllegalArgumentException(String.format(
                    CREATE_INSTANCE_FAILED_FOR_NON_STATIC_MEMBER_CLASS,
                    cls.getName()));
        } else if (cls.isLocalClass()) {
            throw new IllegalArgumentException(String
                    .format(CREATE_INSTANCE_FAILED_LOCAL_CLASS, cls.getName()));
        }
    }

    /**
     * Returns the first non-synthetic method of the specified
     * {@code listenerClass}, which must have single method in the source-code.
     *
     * This is needed, to remove the synthetic methods added if the class is
     * instrumented.
     *
     * @param listenerClass
     *            The {@link Class} of the listener, which has a single method
     *            in the source code
     * @return the first non-synthetic method
     * @throws IllegalStateException
     *             if the specified class does not have found method
     * @since 8.2
     */
    public static Method getMethod(Class<?> listenerClass) {
        for (Method m : listenerClass.getDeclaredMethods()) {
            if (!m.isSynthetic()) {
                return m;
            }
        }
        throw new IllegalStateException("Class " + listenerClass.getName()
                + " does not have a method.");
    }
}
