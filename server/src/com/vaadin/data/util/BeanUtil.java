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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
     * 
     * NOTE : This utility method relies on introspection (and returns
     * PropertyDescriptor) which is a part of java.beans package. The latter
     * package could require bigger JDK in the future (with Java 9+). So it may
     * be changed in the future.
     * 
     * For interfaces, the iteration is depth first and the properties of
     * superinterfaces are returned before those of their subinterfaces.
     * 
     * @param beanClass
     * @return
     * @throws IntrospectionException
     */
    public static List<PropertyDescriptor> getBeanPropertyDescriptor(
            final Class<?> beanClass) throws IntrospectionException {
        // Oracle bug 4275879: Introspector does not consider superinterfaces of
        // an interface
        if (beanClass.isInterface()) {
            List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();

            for (Class<?> cls : beanClass.getInterfaces()) {
                propertyDescriptors.addAll(getBeanPropertyDescriptor(cls));
            }

            BeanInfo info = Introspector.getBeanInfo(beanClass);
            propertyDescriptors.addAll(getPropertyDescriptors(info));

            return propertyDescriptors;
        } else {
            BeanInfo info = Introspector.getBeanInfo(beanClass);
            return getPropertyDescriptors(info);
        }
    }

    /**
     * Returns {@code propertyId} class for property declared in {@code clazz}.
     * Property could be of form "property.subProperty[.subProperty2]" i.e.
     * refer to some nested property.
     * 
     * @param clazz
     *            class where property is declared
     * @param propertyId
     *            property of form "property" or
     *            "property.subProperty[.subProperty2]"
     * @return class of the property
     * @throws IntrospectionException
     */
    public static Class<?> getPropertyType(Class<?> clazz, String propertyId)
            throws IntrospectionException {
        if (propertyId.contains(".")) {
            String[] parts = propertyId.split("\\.", 2);
            // Get the type of the field in the "cls" class
            Class<?> propertyBean = getPropertyType(clazz, parts[0]);
            // Find the rest from the sub type
            return getPropertyType(propertyBean, parts[1]);
        } else {
            List<PropertyDescriptor> descriptors = getBeanPropertyDescriptor(clazz);

            for (PropertyDescriptor descriptor : descriptors) {
                final Method getMethod = descriptor.getReadMethod();
                if (descriptor.getName().equals(propertyId)
                        && getMethod != null
                        && getMethod.getDeclaringClass() != Object.class) {
                    return descriptor.getPropertyType();
                }
            }
            return null;
        }
    }

    // Workaround for Java6 bug JDK-6788525. Do nothing for JDK7+.
    private static List<PropertyDescriptor> getPropertyDescriptors(
            BeanInfo beanInfo) {
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        List<PropertyDescriptor> result = new ArrayList<PropertyDescriptor>(
                descriptors.length);
        for (PropertyDescriptor descriptor : descriptors) {
            try {
                Method readMethod = getMethodFromBridge(descriptor
                        .getReadMethod());
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
            return bridgeMethod.getDeclaringClass().getMethod(
                    bridgeMethod.getName(), paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
