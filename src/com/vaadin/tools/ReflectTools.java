/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tools;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.vaadin.data.fieldbinder.FormBuilder.FormBuilderException;

/**
 * An util class with helpers for reflection operations. Used internally by
 * Vaadin and should not be used by application developers. Subject to change at
 * any time.
 * 
 * @since 6.2
 */
public class ReflectTools {
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
     * @throws FormBuilderException
     *             If the field value cannot be determined
     */
    public static Object getJavaFieldValue(Object object,
            java.lang.reflect.Field field) throws FormBuilderException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            Method getter = pd.getReadMethod();
            if (getter != null) {
                return getter.invoke(object, (Object[]) null);
            }
        } catch (Exception e) {
            // Ignore all problems with getter and try to get the value directly
            // from the field
        }

        try {
            if (!field.isAccessible()) {
                // Try to gain access even if field is private
                field.setAccessible(true);
            }
            return field.get(object);
        } catch (IllegalArgumentException e) {
            throw new FormBuilderException("Could not get value for field '"
                    + field.getName() + "'", e.getCause());
        } catch (IllegalAccessException e) {
            throw new FormBuilderException(
                    "Access denied while assigning built component to field '"
                            + field.getName() + "' in "
                            + object.getClass().getName(), e);
        }
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
     * @throws FormBuilderException
     *             If the value could not be assigned to the field
     */
    public static void setJavaFieldValue(Object object,
            java.lang.reflect.Field field, Object value)
            throws FormBuilderException {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(field.getName(), object.getClass());
            Method setter = pd.getWriteMethod();
            if (setter != null) {
                try {
                    setter.invoke(object, value);
                } catch (IllegalArgumentException e) {
                    throw new FormBuilderException(
                            "Could not assign value to field '"
                                    + field.getName() + "'", e);
                } catch (IllegalAccessException e) {
                    throw new FormBuilderException(
                            "Access denied while assigning value to field using "
                                    + setter.getName() + " in "
                                    + object.getClass().getName(), e);
                } catch (InvocationTargetException e) {
                    throw new FormBuilderException(
                            "Could not assign value to field '"
                                    + field.getName() + "'", e.getCause());
                }
            }
        } catch (IntrospectionException e1) {
            // Ignore this and try to set directly using the field
        }

        try {
            if (!field.isAccessible()) {
                // Try to gain access even if field is private
                field.setAccessible(true);
            }
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new FormBuilderException("Could not assign value to field '"
                    + field.getName() + "'", e.getCause());
        } catch (IllegalAccessException e) {
            throw new FormBuilderException(
                    "Access denied while assigning value to field '"
                            + field.getName() + "' in "
                            + object.getClass().getName(), e);
        }
    }
}
