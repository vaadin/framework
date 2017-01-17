/*
 * Copyright 2000-2016 Vaadin Ltd.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.vaadin.data.validator.BeanValidator;

/**
 * Utility class for Java Beans information access.
 *
 * @since 7.4
 *
 * @author Vaadin Ltd
 */
public final class BeanUtil implements Serializable {
    // Prevent instantiation of util class
    private BeanUtil() {
    }

    /**
     * Returns the property descriptors of a class or an interface.
     *
     * For an interface, superinterfaces are also iterated as Introspector does
     * not take them into account (Oracle Java bug 4275879), but in that case,
     * both the setter and the getter for a property must be in the same
     * interface and should not be overridden in subinterfaces for the discovery
     * to work correctly.
     * <p>
     * NOTE : This utility method relies on introspection (and returns
     * PropertyDescriptor) which is a part of java.beans package. The latter
     * package could require bigger JDK in the future (with Java 9+). So it may
     * be changed in the future.
     * <p>
     * For interfaces, the iteration is depth first and the properties of
     * superinterfaces are returned before those of their subinterfaces.
     *
     * @param beanType
     *            the type whose properties to query
     * @return a list of property descriptors of the given type
     * @throws IntrospectionException
     *             if the introspection fails
     */
    public static List<PropertyDescriptor> getBeanPropertyDescriptors(
            final Class<?> beanType) throws IntrospectionException {
        // Oracle bug 4275879: Introspector does not consider superinterfaces of
        // an interface
        if (beanType.isInterface()) {
            List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();

            for (Class<?> cls : beanType.getInterfaces()) {
                propertyDescriptors.addAll(getBeanPropertyDescriptors(cls));
            }

            BeanInfo info = Introspector.getBeanInfo(beanType);
            propertyDescriptors.addAll(getPropertyDescriptors(info));

            return propertyDescriptors;
        } else {
            BeanInfo info = Introspector.getBeanInfo(beanType);
            return getPropertyDescriptors(info);
        }
    }

    /**
     * Returns the type of the property with the given name and declaring class.
     * The property name may refer to a nested property, eg.
     * "property.subProperty" or "property.subProperty1.subProperty2". The
     * property must have a public read method (or a chain of read methods in
     * case of a nested property).
     *
     * @param beanType
     *            the type declaring the property
     * @param propertyName
     *            the name of the property
     * @return the property type
     * @throws IntrospectionException
     *             if the introspection fails
     */
    public static Class<?> getPropertyType(Class<?> beanType,
            String propertyName) throws IntrospectionException {
        PropertyDescriptor descriptor = getPropertyDescriptor(beanType,
                propertyName);
        if (descriptor != null) {
            return descriptor.getPropertyType();
        } else {
            return null;
        }
    }

    /**
     * Returns the property descriptor for the property of the given name and
     * declaring class. The property name may refer to a nested property, eg.
     * "property.subProperty" or "property.subProperty1.subProperty2". The
     * property must have a public read method (or a chain of read methods in
     * case of a nested property).
     *
     * @param beanType
     *            the type declaring the property
     * @param propertyName
     *            the name of the property
     * @return the corresponding descriptor
     * @throws IntrospectionException
     *             if the introspection fails
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanType,
            String propertyName) throws IntrospectionException {
        if (propertyName.contains(".")) {
            String[] parts = propertyName.split("\\.", 2);
            // Get the type of the field in the bean class
            Class<?> propertyBean = getPropertyType(beanType, parts[0]);
            // Find the rest from the sub type
            return getPropertyDescriptor(propertyBean, parts[1]);
        } else {
            List<PropertyDescriptor> descriptors = getBeanPropertyDescriptors(
                    beanType);

            for (PropertyDescriptor descriptor : descriptors) {
                final Method getMethod = descriptor.getReadMethod();
                if (descriptor.getName().equals(propertyName)
                        && getMethod != null
                        && getMethod.getDeclaringClass() != Object.class) {
                    return descriptor;
                }
            }
            return null;
        }
    }

    /**
     * Returns whether an implementation of JSR-303 version 1.0 or 1.1 is
     * present on the classpath. If this method returns false, trying to create
     * a {@code BeanValidator} instance will throw an
     * {@code IllegalStateException}. If an implementation is not found, logs a
     * level {@code FINE} message the first time it is run.
     *
     * @return {@code true} if bean validation is available, {@code false}
     *         otherwise.
     */
    public static boolean checkBeanValidationAvailable() {
        return LazyValidationAvailability.BEAN_VALIDATION_AVAILABLE;
    }

    // Workaround for Java6 bug JDK-6788525. Do nothing for JDK7+.
    private static List<PropertyDescriptor> getPropertyDescriptors(
            BeanInfo beanInfo) {
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        List<PropertyDescriptor> result = new ArrayList<>(descriptors.length);
        for (PropertyDescriptor descriptor : descriptors) {
            try {
                Method readMethod = getMethodFromBridge(
                        descriptor.getReadMethod());
                if (readMethod != null) {
                    Method writeMethod = getMethodFromBridge(
                            descriptor.getWriteMethod(),
                            readMethod.getReturnType());
                    if (writeMethod == null) {
                        writeMethod = descriptor.getWriteMethod();
                    }
                    PropertyDescriptor descr = new PropertyDescriptor(
                            descriptor.getName(), readMethod, writeMethod);
                    result.add(descr);
                } else {
                    result.add(descriptor);
                }
            } catch (SecurityException ignore) {
                // handle next descriptor
            } catch (IntrospectionException e) {
                result.add(descriptor);
            }
        }
        return result;
    }

    /**
     * Return declared method for which {@code bridgeMethod} is generated. If
     * {@code bridgeMethod} is not a bridge method then return null.
     */
    private static Method getMethodFromBridge(Method bridgeMethod)
            throws SecurityException {
        if (bridgeMethod == null) {
            return null;
        }
        return getMethodFromBridge(bridgeMethod,
                bridgeMethod.getParameterTypes());
    }

    /**
     * Return declared method for which {@code bridgeMethod} is generated using
     * its {@code paramTypes}. If {@code bridgeMethod} is not a bridge method
     * then return null.
     */
    private static Method getMethodFromBridge(Method bridgeMethod,
            Class<?>... paramTypes) throws SecurityException {
        if (bridgeMethod == null || !bridgeMethod.isBridge()) {
            return null;
        }
        try {
            return bridgeMethod.getDeclaringClass()
                    .getMethod(bridgeMethod.getName(), paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static class LazyValidationAvailability implements Serializable {
        private static final boolean BEAN_VALIDATION_AVAILABLE = isAvailable();

        private static boolean isAvailable() {
            try {
                Class<?> clazz = Class.forName("javax.validation.Validation");
                Method method = clazz.getMethod("buildDefaultValidatorFactory");
                method.invoke(null);
                return true;
            } catch (ClassNotFoundException | NoSuchMethodException
                    | InvocationTargetException e) {
                Logger.getLogger(BeanValidator.class.getName())
                        .fine("A JSR-303 bean validation implementation not found on the classpath. "
                                + BeanValidator.class.getSimpleName()
                                + " cannot be used.");
                return false;
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new RuntimeException(
                        "Unable to invoke javax.validation.Validation.buildDefaultValidatorFactory()",
                        e);
            }
        }

        private LazyValidationAvailability() {
        }
    }
}
