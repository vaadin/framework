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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;

/**
 * Default attribute handler implementation used when parsing designs to
 * component trees. Handles all the component attributes that do not require
 * custom handling.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignAttributeHandler implements Serializable {

    protected static Logger getLogger() {
        return Logger.getLogger(DesignAttributeHandler.class.getName());
    }

    private static Map<Class, AttributeCacheEntry> cache = Collections
            .synchronizedMap(new HashMap<Class, AttributeCacheEntry>());

    /**
     * Clears the children and attributes of the given element
     * 
     * @param design
     *            the element to be cleared
     */
    public static void clearElement(Element design) {
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
     * @param component
     *            the component to which the attribute should be set
     * @param attribute
     *            the attribute to be set
     * @param attributes
     *            the attribute map. If the attributes does not contain the
     *            requested attribute, the value is retrieved from the
     *            <code> defaultInstance</code>
     * @return true on success
     */
    public static boolean readAttribute(Component component, String attribute,
            Attributes attributes) {
        String value = null;
        if (component == null || attribute == null || attributes == null) {
            throw new IllegalArgumentException(
                    "Parameters with null value not allowed");
        }
        if (attributes.hasKey(attribute)) {
            value = attributes.get(attribute);
        }
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
                getLogger().log(
                        Level.WARNING,
                        "Attribute value for " + attribute
                                + " is null, this should not happen");
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
     * @param clazz
     *            the class scanned for setters
     * @return the list of supported design attributes
     */
    public static Collection<String> getSupportedAttributes(Class<?> clazz) {
        resolveSupportedAttributes(clazz);
        return cache.get(clazz).getAttributes();
    }

    /**
     * Resolves the supported attributes and corresponding getters and setters
     * for the class using introspection. After resolving, the information is
     * cached internally by this class
     * 
     * @param clazz
     *            the class to resolve the supported attributes for
     */
    private static void resolveSupportedAttributes(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("The clazz can not be null");
        }
        if (cache.containsKey(clazz.getCanonicalName())) {
            // NO-OP
            return;
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new RuntimeException(
                    "Could not get supported attributes for class "
                            + clazz.getName());
        }
        AttributeCacheEntry entry = new AttributeCacheEntry();
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Method getter = descriptor.getReadMethod();
            Method setter = descriptor.getWriteMethod();
            if (getter != null && setter != null
                    && isSupported(descriptor.getPropertyType())) {
                String attribute = toAttributeName(descriptor.getName());
                entry.addAttribute(attribute, getter, setter);
            }
        }
        cache.put(clazz, entry);
    }

    /**
     * Writes the specified attribute to the design if it differs from the
     * default value got from the <code> defaultInstance <code>
     * 
     * @param component
     *            the component used to get the attribute value
     * @param attribute
     *            the key for the attribute
     * @param attr
     *            the attribute list where the attribute will be written
     * @param defaultInstance
     *            the default instance for comparing default values
     */
    public static void writeAttribute(Component component, String attribute,
            Attributes attr, Component defaultInstance) {
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
                if (!SharedUtil.equals(value, defaultValue)) {
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
     * Reads the given attribute from attributes.
     * 
     * @param attribute
     *            the attribute key
     * @param attributes
     *            the set of attributes to read from
     * @param outputType
     *            the output type for the attribute
     * @return the attribute value or the default value if the attribute is not
     *         found
     */
    @SuppressWarnings("unchecked")
    public static <T> T readAttribute(String attribute, Attributes attributes,
            Class<T> outputType) {
        if (!isSupported(outputType)) {
            throw new IllegalArgumentException("output type: "
                    + outputType.getName() + " not supported");
        }
        if (!attributes.hasKey(attribute)) {
            return null;
        } else {
            try {
                String value = attributes.get(attribute);
                return (T) fromAttributeValue(outputType, value);
            } catch (Exception e) {
                throw new DesignException("Failed to read attribute "
                        + attribute, e);
            }
        }
    }

    /**
     * Writes the given attribute value to attributes if it differs from the
     * default attribute value.
     * 
     * @param attribute
     *            the attribute key
     * @param attributes
     *            the set of attributes where the new attribute is written
     * @param value
     *            the attribute value
     * @param defaultValue
     *            the default attribute value
     * @param inputType
     *            the type of the input value
     */
    public static <T> void writeAttribute(String attribute,
            Attributes attributes, T value, T defaultValue, Class<T> inputType) {
        if (!isSupported(inputType)) {
            throw new IllegalArgumentException("input type: "
                    + inputType.getName() + " not supported");
        }
        if (!SharedUtil.equals(value, defaultValue)) {
            String attributeValue = toAttributeValue(inputType, value);
            attributes.put(attribute, attributeValue);
        }
    }

    /**
     * Formats the given design attribute value. The method is provided to
     * ensure consistent number formatting for design attribute values
     * 
     * @param number
     *            the number to be formatted
     * @return the formatted number
     */
    public static String formatFloat(float number) {
        return getDecimalFormat().format(number);
    }

    /**
     * Formats the given design attribute value. The method is provided to
     * ensure consistent number formatting for design attribute values
     * 
     * @param number
     *            the number to be formatted
     * @return the formatted number
     */
    public static String formatDouble(double number) {
        return getDecimalFormat().format(number);
    }

    /**
     * Convert ShortcutAction to attribute string presentation
     * 
     * @param shortcut
     *            the shortcut action
     * @return the action as attribute string presentation
     */
    private static String formatShortcutAction(ShortcutAction shortcut) {
        StringBuilder sb = new StringBuilder();
        // handle modifiers
        if (shortcut.getModifiers() != null) {
            for (int modifier : shortcut.getModifiers()) {
                sb.append(ShortcutKeyMapper.getStringForKeycode(modifier))
                        .append("-");
            }
        }
        // handle keycode
        sb.append(ShortcutKeyMapper.getStringForKeycode(shortcut.getKeyCode()));
        return sb.toString();
    }

    /**
     * Reads shortcut action from attribute presentation
     * 
     * @param attributeValue
     *            attribute presentation of shortcut action
     * @return shortcut action with keycode and modifier keys from attribute
     *         value
     */
    private static ShortcutAction readShortcutAction(String attributeValue) {
        if (attributeValue.length() == 0) {
            return null;
        }
        String[] parts = attributeValue.split("-");
        // handle keycode
        String keyCodePart = parts[parts.length - 1];
        int keyCode = ShortcutKeyMapper.getKeycodeForString(keyCodePart);
        if (keyCode < 0) {
            throw new IllegalArgumentException("Invalid shortcut definition "
                    + attributeValue);
        }
        // handle modifiers
        int[] modifiers = null;
        if (parts.length > 1) {
            modifiers = new int[parts.length - 1];
        }
        for (int i = 0; i < parts.length - 1; i++) {
            int modifier = ShortcutKeyMapper.getKeycodeForString(parts[i]);
            if (modifier > 0) {
                modifiers[i] = modifier;
            } else {
                throw new IllegalArgumentException(
                        "Invalid shortcut definition " + attributeValue);
            }
        }
        return new ShortcutAction(null, keyCode, modifiers);
    }

    /**
     * Creates the decimal format used when writing attributes to the design.
     * 
     * @return the decimal format
     */
    private static DecimalFormat getDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale(
                "en_US"));
        DecimalFormat fmt = new DecimalFormat("0.###", symbols);
        fmt.setGroupingUsed(false);
        return fmt;
    }

    /**
     * Returns the design attribute name corresponding the given method name.
     * For example given a method name <code>setPrimaryStyleName</code> the
     * return value would be <code>primary-style-name</code>
     * 
     * @param propertyName
     *            the property name returned by {@link IntroSpector}
     * @return the design attribute name corresponding the given method name
     */
    private static String toAttributeName(String propertyName) {
        String[] words = propertyName.split("(?<!^)(?=[A-Z])");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (builder.length() > 0) {
                builder.append("-");
            }
            builder.append(words[i].toLowerCase());
        }
        return builder.toString();
    }

    /**
     * Parses the given attribute value to specified target type
     * 
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
            return !value.equalsIgnoreCase("false");
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
        if (targetType == Resource.class) {
            return parseResource(value);
        }
        if (Enum.class.isAssignableFrom(targetType)) {
            return Enum.valueOf((Class<? extends Enum>) targetType,
                    value.toUpperCase());
        }
        if (targetType == ShortcutAction.class) {
            return readShortcutAction(value);
        }
        return null;
    }

    /**
     * Serializes the given value to valid design attribute representation
     * 
     * @param sourceType
     *            the type of the value
     * @param value
     *            the value to be serialized
     * @return the given value as design attribute representation
     */
    private static String toAttributeValue(Class<?> sourceType, Object value) {
        if (value == null) {
            // TODO: Handle corner case where sourceType is String and default
            // value is not null. How to represent null value in attributes?
            return "";
        }
        if (sourceType == Resource.class) {
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
                getLogger().warning(
                        "Unknown resource type " + value.getClass().getName());
                return null;
            }
        } else if (sourceType == Float.class || sourceType == Float.TYPE) {
            return formatFloat(((Float) value).floatValue());
        } else if (sourceType == Double.class || sourceType == Double.TYPE) {
            return formatDouble(((Double) value).doubleValue());
        } else if (sourceType == ShortcutAction.class) {
            return formatShortcutAction((ShortcutAction) value);
        } else {
            return value.toString();
        }
    }

    /**
     * Parses the given attribute value as resource
     * 
     * @param value
     *            the attribute value to be parsed
     * @return resource instance based on the attribute value
     */
    private static Resource parseResource(String value) {
        if (value.startsWith("http://")) {
            return new ExternalResource(value);
        } else if (value.startsWith("theme://")) {
            return new ThemeResource(value.substring(8));
        } else if (value.startsWith("font://")) {
            return FontAwesome.valueOf(value.substring(7));
        } else {
            return new FileResource(new File(value));
        }
    }

    /**
     * Returns a setter that can be used for assigning the given design
     * attribute to the class
     * 
     * @param clazz
     *            the class that is scanned for setters
     * @param attribute
     *            the design attribute to find setter for
     * @return the setter method or null if not found
     */
    private static Method findSetterForAttribute(Class<?> clazz,
            String attribute) {
        resolveSupportedAttributes(clazz);
        return cache.get(clazz).getSetter(attribute);
    }

    /**
     * Returns a getter that can be used for reading the given design attribute
     * value from the class
     * 
     * @param clazz
     *            the class that is scanned for getters
     * @param attribute
     *            the design attribute to find getter for
     * @return the getter method or null if not found
     */
    private static Method findGetterForAttribute(Class<?> clazz,
            String attribute) {
        resolveSupportedAttributes(clazz);
        return cache.get(clazz).getGetter(attribute);
    }

    // supported property types
    private static final List<Class<?>> supportedClasses = Arrays
            .asList(new Class<?>[] { String.class, Boolean.class,
                    Integer.class, Byte.class, Short.class, Long.class,
                    Character.class, Float.class, Double.class, Resource.class,
                    ShortcutAction.class });

    /**
     * Returns true if the specified value type is supported by this class.
     * Currently the handler supports primitives, {@link Locale.class} and
     * {@link Resource.class}.
     * 
     * @param valueType
     *            the value type to be tested
     * @return true if the value type is supported, otherwise false
     */
    private static boolean isSupported(Class<?> valueType) {
        return valueType != null
                && (valueType.isPrimitive()
                        || supportedClasses.contains(valueType) || Enum.class
                            .isAssignableFrom(valueType));
    }

    /**
     * Cache object for caching supported attributes and their getters and
     * setters
     * 
     * @author Vaadin Ltd
     */
    private static class AttributeCacheEntry implements Serializable {
        private Map<String, Method[]> accessMethods = Collections
                .synchronizedMap(new HashMap<String, Method[]>());

        private void addAttribute(String attribute, Method getter, Method setter) {
            Method[] methods = new Method[2];
            methods[0] = getter;
            methods[1] = setter;
            accessMethods.put(attribute, methods);
        }

        private Collection<String> getAttributes() {
            ArrayList<String> attributes = new ArrayList<String>();
            attributes.addAll(accessMethods.keySet());
            return attributes;
        }

        private Method getGetter(String attribute) {
            Method[] methods = accessMethods.get(attribute);
            return (methods != null && methods.length > 0) ? methods[0] : null;
        }

        private Method getSetter(String attribute) {
            Method[] methods = accessMethods.get(attribute);
            return (methods != null && methods.length > 1) ? methods[1] : null;
        }
    }

    /**
     * Provides mappings between shortcut keycodes and their representation in
     * design attributes
     * 
     * @author Vaadin Ltd
     */
    private static class ShortcutKeyMapper implements Serializable {

        private static Map<Integer, String> keyCodeMap = Collections
                .synchronizedMap(new HashMap<Integer, String>());
        private static Map<String, Integer> presentationMap = Collections
                .synchronizedMap(new HashMap<String, Integer>());

        static {
            // map modifiers
            mapKey(ModifierKey.ALT, "alt");
            mapKey(ModifierKey.CTRL, "ctrl");
            mapKey(ModifierKey.META, "meta");
            mapKey(ModifierKey.SHIFT, "shift");
            // map keys
            mapKey(KeyCode.ENTER, "enter");
            mapKey(KeyCode.ESCAPE, "escape");
            mapKey(KeyCode.PAGE_UP, "pageup");
            mapKey(KeyCode.PAGE_DOWN, "pagedown");
            mapKey(KeyCode.TAB, "tab");
            mapKey(KeyCode.ARROW_LEFT, "left");
            mapKey(KeyCode.ARROW_UP, "up");
            mapKey(KeyCode.ARROW_RIGHT, "right");
            mapKey(KeyCode.ARROW_DOWN, "down");
            mapKey(KeyCode.BACKSPACE, "backspace");
            mapKey(KeyCode.DELETE, "delete");
            mapKey(KeyCode.INSERT, "insert");
            mapKey(KeyCode.END, "end");
            mapKey(KeyCode.HOME, "home");
            mapKey(KeyCode.F1, "f1");
            mapKey(KeyCode.F2, "f2");
            mapKey(KeyCode.F3, "f3");
            mapKey(KeyCode.F4, "f4");
            mapKey(KeyCode.F5, "f5");
            mapKey(KeyCode.F6, "f6");
            mapKey(KeyCode.F7, "f7");
            mapKey(KeyCode.F8, "f8");
            mapKey(KeyCode.F9, "f9");
            mapKey(KeyCode.F10, "f10");
            mapKey(KeyCode.F11, "f11");
            mapKey(KeyCode.F12, "f12");
            mapKey(KeyCode.NUM0, "0");
            mapKey(KeyCode.NUM1, "1");
            mapKey(KeyCode.NUM2, "2");
            mapKey(KeyCode.NUM3, "3");
            mapKey(KeyCode.NUM4, "4");
            mapKey(KeyCode.NUM5, "5");
            mapKey(KeyCode.NUM6, "6");
            mapKey(KeyCode.NUM7, "7");
            mapKey(KeyCode.NUM8, "8");
            mapKey(KeyCode.NUM9, "9");
            mapKey(KeyCode.SPACEBAR, "spacebar");
            mapKey(KeyCode.A, "a");
            mapKey(KeyCode.B, "b");
            mapKey(KeyCode.C, "c");
            mapKey(KeyCode.D, "d");
            mapKey(KeyCode.E, "e");
            mapKey(KeyCode.F, "f");
            mapKey(KeyCode.G, "g");
            mapKey(KeyCode.H, "h");
            mapKey(KeyCode.I, "i");
            mapKey(KeyCode.J, "j");
            mapKey(KeyCode.K, "k");
            mapKey(KeyCode.L, "l");
            mapKey(KeyCode.M, "m");
            mapKey(KeyCode.N, "n");
            mapKey(KeyCode.O, "o");
            mapKey(KeyCode.P, "p");
            mapKey(KeyCode.Q, "q");
            mapKey(KeyCode.R, "r");
            mapKey(KeyCode.S, "s");
            mapKey(KeyCode.T, "t");
            mapKey(KeyCode.U, "u");
            mapKey(KeyCode.V, "v");
            mapKey(KeyCode.X, "x");
            mapKey(KeyCode.Y, "y");
            mapKey(KeyCode.Z, "z");
        }

        private static void mapKey(int keyCode, String presentation) {
            keyCodeMap.put(keyCode, presentation);
            presentationMap.put(presentation, keyCode);
        }

        private static int getKeycodeForString(String attributePresentation) {
            Integer code = presentationMap.get(attributePresentation);
            return code != null ? code.intValue() : -1;
        }

        private static String getStringForKeycode(int keyCode) {
            return keyCodeMap.get(keyCode);
        }
    }

    /**
     * Converts the given string attribute value to its corresponding boolean.
     * 
     * An empty string and "true" are considered to represent a true value and
     * "false" to represent a false value. All other input will throw an
     * exception
     * 
     * @param booleanValue
     *            the boolean value from an attribute
     * @return the parsed boolean
     */
    public static boolean getBoolean(String booleanValue) {
        return "".equals(booleanValue) ? true : Boolean.valueOf(booleanValue);
    }

}