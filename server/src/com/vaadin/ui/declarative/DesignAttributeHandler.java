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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.LocaleUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.DesignSynchronizable;

/**
 * Default attribute handler implementation used when parsing designs to
 * component trees. Handles all the component attributes that do not require
 * custom handling.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignAttributeHandler {

    protected static Logger getLogger() {
        return Logger.getLogger(DesignAttributeHandler.class.getName());
    }

    /**
     * Clears the children and attributes of the given node
     * 
     * @since 7.4
     * @param design
     *            the node to be cleared
     */
    public static void clearNode(Node design) {
        Attributes attr = design.attributes();
        for (Attribute a : attr.asList()) {
            attr.remove(a.getKey());
        }
        List<Node> children = new ArrayList<Node>();
        children.addAll(design.childNodes());
        for (Node node : children) {
            node.remove();
        }
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
     * @param attributes
     *            the attribute map. If the attributes does not contain the
     *            requested attribute, the value is retrieved from the
     *            <code> defaultInstance</code>
     * @param defaultInstance
     *            the default instance of the class for fetching the default
     *            values
     */
    public static boolean readAttribute(DesignSynchronizable component,
            String attribute, Attributes attributes,
            DesignSynchronizable defaultInstance) {

        String value = null;
        if (attributes.hasKey(attribute)) {
            value = attributes.get(attribute);
        }

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
                Object param = fromAttributeValue(
                        setter.getParameterTypes()[0], value);
                setter.invoke(component, param);
                success = true;
            } else {
                // otherwise find the getter for the property
                Method getter = findGetterForSetter(component.getClass(),
                        setter);
                if (getter != null) {
                    // read the default value from defaults
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
     * Searches for supported setter and getter types from the specified class
     * and returns the list of corresponding design attributes
     * 
     * @since 7.4
     * @param clazz
     *            the class scanned for setters
     * @return the list of supported design attributes
     */
    public static List<String> findSupportedAttributes(Class<?> clazz) {
        List<String> attributes = new ArrayList<String>();
        for (Method method : clazz.getMethods()) {
            // check that the method is setter, has single argument of supported
            // type and has a corresponding getter
            if (method.getName().startsWith("set")
                    && method.getParameterTypes().length == 1
                    && isSupported(method.getParameterTypes()[0])
                    && findGetterForSetter(clazz, method) != null) {
                attributes.add(toAttributeName(method.getName()));
                // TODO: we might want to cache the getters and setters?
            }
        }
        return attributes;
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
    public static void readWidth(DesignSynchronizable component,
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
    public static void readHeight(DesignSynchronizable component,
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

    /**
     * Writes the specified attribute to the design if it differs from the
     * default value got from the <code> defaultInstance <code>
     * 
     * @since 7.4
     * @param component
     *            the component used to get the attribute value
     * @param attribute
     *            the key for the attribute
     * @param attr
     *            the attribute list where the attribute will be written
     * @param defaultInstance
     *            the default instance for comparing default values
     */
    public static void writeAttribute(DesignSynchronizable component,
            String attribute, Attributes attr,
            DesignSynchronizable defaultInstance) {
        Method getter = findGetterForAttribute(component.getClass(), attribute);
        if (getter == null) {
            getLogger().warning(
                    "Could not find getter for attribute " + attribute);
        } else {
            try {
                // compare the value with default value
                Object value = getter.invoke(component);
                Object defaultValue = getter.invoke(defaultInstance);
                // if the values are not equal, write the data
                if (value == defaultValue
                        || (value != null && value.equals(defaultValue))) {
                } else {
                    String attributeValue = toAttributeValue(
                            getter.getReturnType(), value);
                    attr.put(attribute, attributeValue);
                }
            } catch (Exception e) {
                getLogger()
                        .log(Level.SEVERE,
                                "Failed to invoke getter for attribute "
                                        + attribute, e);
            }
        }
    }

    /**
     * Writes the size related attributes for the component if they differ from
     * the defaults
     * 
     * @since 7.4
     * @param component
     *            the component
     * @param attributes
     *            the attribute map where the attribute are written
     * @param defaultInstance
     *            the default instance of the class for fetching the default
     *            values
     */
    public static void writeSize(DesignSynchronizable component,
            Attributes attributes, DesignSynchronizable defaultInstance) {
        if (areEqualSize(component, defaultInstance)) {
            // we have default values -> ignore
            return;
        }
        boolean widthFull = component.getWidth() == 100f
                && component.getWidthUnits().equals(Sizeable.Unit.PERCENTAGE);
        boolean heightFull = component.getHeight() == 100f
                && component.getHeightUnits().equals(Sizeable.Unit.PERCENTAGE);
        boolean widthAuto = component.getWidth() == -1;
        boolean heightAuto = component.getHeight() == -1;

        // first try the full shorthands
        if (widthFull && heightFull) {
            attributes.put("size-full", "true");
        } else if (widthAuto && heightAuto) {
            attributes.put("size-auto", "true");
        } else {
            // handle width
            if (!areEqualWidth(component, defaultInstance)) {
                if (widthFull) {
                    attributes.put("width-full", "true");
                } else if (widthAuto) {
                    attributes.put("width-auto", "true");
                } else {
                    attributes.put("width",
                            formatDesignAttribute(component.getWidth())
                                    + component.getWidthUnits().getSymbol());
                }
            }
            if (!areEqualHeight(component, defaultInstance)) {
                // handle height
                if (heightFull) {
                    attributes.put("height-full", "true");
                } else if (heightAuto) {
                    attributes.put("height-auto", "true");
                } else {
                    attributes.put("height",
                            formatDesignAttribute(component.getHeight())
                                    + component.getHeightUnits().getSymbol());
                }
            }
        }
    }

    /**
     * Formats the given design attribute value. The method is provided to
     * ensure consistent number formatting for design attribute values
     * 
     * @since 7.4
     * @param number
     *            the number to be formatted
     * @return the formatted number
     */
    public static String formatDesignAttribute(float number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale(
                "en_US"));
        DecimalFormat fmt = new DecimalFormat("0.###", symbols);
        fmt.setGroupingUsed(false);
        return fmt.format(number);
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
        // ignore first token ("set")
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
     * Returns a list of possible getter method names for the corresponding
     * design attribute name.
     * 
     * @since 7.4
     * @param designAttributeName
     *            the design attribute name
     * @return the list of getter method names corresponding the given design
     *         attribute name
     */
    private static List<String> toGetterNames(String designAttributeName) {
        String[] parts = designAttributeName.split("-");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(part.substring(0, 1).toUpperCase());
            builder.append(part.substring(1));
        }
        String propertyName = builder.toString();
        List<String> result = new ArrayList<String>();
        result.add("get" + propertyName);
        result.add("is" + propertyName);
        result.add("has" + propertyName);
        return result;
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
    private static Object fromAttributeValue(Class<?> targetType, String value) {
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
            return LocaleUtils.toLocale(value);
        }
        if (targetType == Resource.class) {
            return parseResource(value);
        }
        return null;
    }

    /**
     * Serializes the given value to valid design attribute representation
     * (string)
     * 
     * @since 7.4
     * @param sourceType
     *            the type of the value
     * @param value
     *            the value to be serialized
     * @return the given value as design attribute representation
     */
    private static String toAttributeValue(Class<?> sourceType, Object value) {
        if (sourceType == Locale.class) {
            return value != null ? ((Locale) value).toString() : null;
        } else if (sourceType == Resource.class) {
            if (value instanceof ExternalResource) {
                return ((ExternalResource) value).getURL();
            } else if (value instanceof ThemeResource) {
                return "theme://" + ((ThemeResource) value).getResourceId();
            } else if (value instanceof FontAwesome) {
                return "font://" + ((FontAwesome) value).name();
            } else if (value instanceof FileResource) {
                String path = ((FileResource) value).getSourceFile().getPath();
                if (File.separatorChar != '/') {
                    // make sure we use '/' as file separator in templates
                    return path.replace(File.separatorChar, '/');
                } else {
                    return path;
                }
            } else {
                return null;
            }
        } else {
            return value.toString();
        }
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
    private static Method findGetterForSetter(Class<?> clazz, Method setter) {
        String propertyName = setter.getName().substring(3);
        Class<?> returnType = setter.getParameterTypes()[0];
        for (Method method : clazz.getMethods()) {
            if (isGetterForProperty(method, propertyName)
                    && method.getParameterTypes().length == 0
                    && method.getReturnType().equals(returnType)) {
                return method;
            }
        }
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

    /**
     * Returns a getter that can be used for reading the value of the given
     * design attribute from the class
     * 
     * @since 7.4
     * @param clazz
     *            the class that is scanned for getters
     * @param attribute
     *            the design attribute to find getter for
     * @return the getter method or null if not found
     */
    private static Method findGetterForAttribute(Class<?> clazz,
            String attribute) {
        List<String> methodNames = toGetterNames(attribute);
        for (Method method : clazz.getMethods()) {
            if (methodNames.contains(method.getName())
                    && method.getParameterTypes().length == 0
                    && isSupported(method.getReturnType())) {
                return method;
            }
        }
        getLogger().warning(
                "Could not find getter with supported return type for attribute "
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
     * Test if the given components have equal width
     * 
     * @since 7.4
     * @param comp1
     * @param comp2
     * @return true if the widths of the components are equal
     */
    private static boolean areEqualWidth(Component comp1, Component comp2) {
        return comp1.getWidth() == comp2.getWidth()
                && comp1.getWidthUnits().equals(comp2.getWidthUnits());
    }

    /**
     * Tests if the given components have equal height
     * 
     * @since 7.4
     * @param comp1
     * @param comp2
     * @return true if the heights of the components are equal
     */
    private static boolean areEqualHeight(Component comp1, Component comp2) {
        return comp1.getHeight() == comp2.getHeight()
                && comp1.getHeightUnits().equals(comp2.getHeightUnits());
    }

    /**
     * Test if the given components have equal size
     * 
     * @since 7.4
     * @param comp1
     * @param comp2
     * @return true if the widht and height of the components are equal
     */
    private static boolean areEqualSize(Component comp1, Component comp2) {
        return areEqualWidth(comp1, comp2) && areEqualHeight(comp1, comp2);
    }

}
