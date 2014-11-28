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
package com.vaadin.ui.declarative;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Attributes;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.DesignSynchronizable;

/**
 * Default attribute handler implementation used when parsing designs to
 * component trees. Handles all the component attributes that do require custom
 * handling.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignAttributeHandler {

    protected static Logger getLogger() {
        return Logger.getLogger(DesignAttributeHandler.class.getName());
    }

    /**
     * Returns the design attribute name corresponding the given method name.
     * For example given a method name <code>setPrimaryStyleName</code> the
     * return value would be <code>primary-style-name</code>
     * 
     * @since 7.4
     * @param methodName
     *            the method name
     * @return the design attribute name corresponding the given method name
     */
    private static String toAttributeName(String methodName) {
        String[] words = methodName.split("(?<!^)(?=[A-Z])");
        StringBuilder builder = new StringBuilder();
        // ignore first toget ("set")
        for (int i = 1; i < words.length; i++) {
            if (builder.length() > 0) {
                builder.append("-");
            }
            builder.append(words[i].toLowerCase());
        }
        return builder.toString();
    }

    /**
     * Returns the setter method name corresponding the given design attribute
     * name. For example given a attribute name <code>primary-style-name</code>
     * the return value would be <code>setPrimaryStyleName</code>.
     * 
     * @since 7.4
     * @param designAttributeName
     *            the design attribute name
     * @return the setter method name corresponding the given design attribute
     *         name
     */
    private static String toSetterName(String designAttributeName) {
        String[] parts = designAttributeName.split("-");
        StringBuilder builder = new StringBuilder();
        builder.append("set");
        for (String part : parts) {
            builder.append(part.substring(0, 1).toUpperCase());
            builder.append(part.substring(1));
        }
        return builder.toString();
    }

    /**
     * Parses the given attribute value to specified target type
     * 
     * @since 7.4
     * @param targetType
     *            the target type for the value
     * @param value
     *            the parsed value
     * @return the object of specified target type
     */
    private static Object parseAttributeValue(Class<?> targetType, String value) {
        if (targetType == String.class) {
            return value;
        }
        // special handling for boolean type. The attribute evaluates to true if
        // it is present and the value is not "false" or "FALSE". Thus empty
        // value evaluates to true.
        if (targetType == Boolean.TYPE || targetType == Boolean.class) {
            return value == null || !value.equalsIgnoreCase("false");
        }
        if (targetType == Integer.TYPE || targetType == Integer.class) {
            return Integer.valueOf(value);
        }
        if (targetType == Byte.TYPE || targetType == Byte.class) {
            return Byte.valueOf(value);
        }
        if (targetType == Short.TYPE || targetType == Short.class) {
            return Short.valueOf(value);
        }
        if (targetType == Long.TYPE || targetType == Long.class) {
            return Long.valueOf(value);
        }
        if (targetType == Character.TYPE || targetType == Character.class) {
            return value.charAt(0);
        }
        if (targetType == Float.TYPE || targetType == Float.class) {
            return Float.valueOf(value);
        }
        if (targetType == Double.TYPE || targetType == Double.class) {
            return Double.valueOf(value);
        }
        if (targetType == Locale.class) {
            return new Locale(value);
        }
        if (targetType == Resource.class) {
            return parseResource(value);
        }
        return null;
    }

    private static Resource parseResource(String value) {
        if (value.startsWith("http://")) {
            return new ExternalResource("value");
        } else if (value.startsWith("theme://")) {
            return new ThemeResource(value.substring(8));
        } else if (value.startsWith("font://")) {
            return FontAwesome.valueOf(value.substring(7));
        } else {
            return new FileResource(new File(value));
        }
    }

    /**
     * Finds a corresponding getter method for the given setter method
     * 
     * @since 7.4
     * @param clazz
     *            the class to search methods from
     * @param setter
     *            the setter that is used to find the matching getter
     * @return the matching getter or null if not found
     */
    private static Method findGetter(Class<?> clazz, Method setter) {
        String propertyName = setter.getName().substring(3);
        Class<?> returnType = setter.getParameterTypes()[0];
        for (Method method : clazz.getMethods()) {
            if (isGetterForProperty(method, propertyName)
                    && method.getParameterTypes().length == 0
                    && method.getReturnType().equals(returnType)) {
                return method;
            }
        }
        getLogger().warning("Could not find getter for " + setter.getName());
        return null;
    }

    private static boolean isGetterForProperty(Method method, String property) {
        String methodName = method.getName();
        return methodName.equals("get" + property)
                || methodName.equals("is" + property)
                || methodName.equals("has" + property);
    }

    /**
     * Returns a setter that can be used for assigning the given design
     * attribute to the class
     * 
     * @since 7.4
     * @param clazz
     *            the class that is scanned for setters
     * @param attribute
     *            the design attribute to find setter for
     * @return the setter method or null if not found
     */
    private static Method findSetterForAttribute(Class<?> clazz,
            String attribute) {
        String methodName = toSetterName(attribute);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)
                    && method.getParameterTypes().length == 1
                    && isSupported(method.getParameterTypes()[0])) {
                return method;
            }
        }
        getLogger().warning(
                "Could not find setter with supported type for property "
                        + attribute);
        return null;
    }

    private static final List<Class<?>> supportedClasses = Arrays
            .asList(new Class<?>[] { String.class, Boolean.class,
                    Integer.class, Byte.class, Short.class, Long.class,
                    Character.class, Float.class, Double.class, Locale.class,
                    Resource.class });

    /**
     * Returns true if the specified value type is supported by this class.
     * Currently the handler supports primitives, {@link Locale.class} and
     * {@link Resource.class}.
     * 
     * @since 7.4
     * @param valueType
     *            the value type to be tested
     * @return true if the value type is supported, otherwise false
     */
    private static boolean isSupported(Class<?> valueType) {
        return valueType != null
                && (valueType.isPrimitive() || supportedClasses
                        .contains(valueType));
    }

    /**
     * Searches for supported setter types from the specified class and returns
     * the list of corresponding design attributes
     * 
     * @since 7.4
     * @param clazz
     *            the class scanned for setters
     * @return the list of supported design attributes
     */
    public static List<String> findSupportedAttributes(Class<?> clazz) {
        List<String> attributes = new ArrayList<String>();
        // TODO: should we check that we have the corresponding getter too?
        // Otherwise we can not revert to default value. On the other hand, do
        // we want that leaving the getter out will prevent reading the
        // attribute from the design?
        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("set")
                    && method.getParameterTypes().length == 1
                    && isSupported(method.getParameterTypes()[0])) {
                attributes.add(toAttributeName(method.getName()));
            }
        }
        return attributes;
    }

    /**
     * Assigns the specified design attribute to the given component. If the
     * attribute is not present, (value is null) the corresponding property is
     * got from the <code>defaultInstance</code>
     * 
     * @since 7.4
     * @param component
     *            the component to which the attribute should be set
     * @param attribute
     *            the attribute to be set
     * @param value
     *            the value for the attribute. If null, the corresponding
     *            property is got from the <code> defaultInstance</code>
     * @param defaultInstance
     *            the default instance of the class for fetching the default
     *            values
     */
    public static boolean assignAttribute(DesignSynchronizable component,
            String attribute, String value, DesignSynchronizable defaultInstance) {
        getLogger().info("Assigning attribute " + attribute + " -> " + value);
        // find setter for the property
        boolean success = false;
        try {
            Method setter = findSetterForAttribute(component.getClass(),
                    attribute);
            if (setter == null) {
                // if we don't have the setter, there is no point in continuing
                success = false;
            } else if (value != null) {
                // we have a value from design attributes, let's use that
                getLogger().info("Setting the value from attributes");
                Object param = parseAttributeValue(
                        setter.getParameterTypes()[0], value);
                setter.invoke(component, param);
                success = true;
            } else {
                // otherwise find the getter for the property
                Method getter = findGetter(component.getClass(), setter);
                if (getter != null) {
                    // read the default value from defaults
                    getLogger().info("Setting the default value");
                    Object defaultValue = getter.invoke(defaultInstance);
                    setter.invoke(component, defaultValue);
                    success = true;
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING,
                    "Failed to set attribute " + attribute, e);
        }
        if (!success) {
            getLogger().info(
                    "property " + attribute
                            + " ignored by default attribute handler");
        }
        return success;
    }

    /**
     * Assigns the width for the component based on the design attributes
     * 
     * @since 7.4
     * @param component
     *            the component to assign the width
     * @param attributes
     *            the attributes to be used for determining the width
     * @param defaultInstance
     *            the default instance of the class for fetching the default
     *            value
     */
    public static void assignWidth(DesignSynchronizable component,
            Attributes attributes, DesignSynchronizable defaultInstance) {
        if (attributes.hasKey("width-auto") || attributes.hasKey("size-auto")) {
            component.setWidth(null);
        } else if (attributes.hasKey("width-full")
                || attributes.hasKey("size-full")) {
            component.setWidth("100%");
        } else if (attributes.hasKey("width")) {
            component.setWidth(attributes.get("width"));
        } else {
            component.setWidth(defaultInstance.getWidth(),
                    defaultInstance.getWidthUnits());
        }
    }

    /**
     * Assigns the height for the component based on the design attributes
     * 
     * @since 7.4
     * @param component
     *            the component to assign the height
     * @param attributes
     *            the attributes to be used for determining the height
     * @param defaultInstance
     *            the default instance of the class for fetching the default
     *            value
     */
    public static void assignHeight(DesignSynchronizable component,
            Attributes attributes, DesignSynchronizable defaultInstance) {
        if (attributes.hasKey("height-auto") || attributes.hasKey("size-auto")) {
            component.setHeight(null);
        } else if (attributes.hasKey("height-full")
                || attributes.hasKey("size-full")) {
            component.setHeight("100%");
        } else if (attributes.hasKey("height")) {
            component.setHeight(attributes.get("height"));
        } else {
            component.setHeight(defaultInstance.getHeight(),
                    defaultInstance.getHeightUnits());
        }
    }
}
