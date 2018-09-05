/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.data.Converter;
import com.vaadin.data.ValueContext;
import com.vaadin.shared.ui.AlignmentInfo;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Alignment;

/**
 * Default attribute handler implementation used when parsing designs to
 * component trees. Handles all the component attributes that do not require
 * custom handling.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignAttributeHandler implements Serializable {

    private static Logger getLogger() {
        return Logger.getLogger(DesignAttributeHandler.class.getName());
    }

    private static final Map<Class<?>, AttributeCacheEntry> CACHE = new ConcurrentHashMap<>();

    // translates string <-> object
    private static final DesignFormatter FORMATTER = new DesignFormatter();

    /**
     * Returns the currently used formatter. All primitive types and all types
     * needed by Vaadin components are handled by that formatter.
     *
     * @return An instance of the formatter.
     */
    public static DesignFormatter getFormatter() {
        return FORMATTER;
    }

    /**
     * Clears the children and attributes of the given element.
     *
     * @param design
     *            the element to be cleared
     */
    public static void clearElement(Element design) {
        Attributes attr = design.attributes();
        for (Attribute a : attr.asList()) {
            attr.remove(a.getKey());
        }
        List<Node> children = new ArrayList<>();
        children.addAll(design.childNodes());
        for (Node node : children) {
            node.remove();
        }
    }

    /**
     * Assigns the specified design attribute to the given component.
     *
     * @param target
     *            the target to which the attribute should be set
     * @param attribute
     *            the name of the attribute to be set
     * @param value
     *            the string value of the attribute
     * @return true on success
     */
    public static boolean assignValue(Object target, String attribute,
            String value) {
        if (target == null || attribute == null || value == null) {
            throw new IllegalArgumentException(
                    "Parameters with null value not allowed");
        }
        boolean success = false;
        try {
            Method setter = findSetterForAttribute(target.getClass(),
                    attribute);
            if (setter == null) {
                // if we don't have the setter, there is no point in continuing
                success = false;
            } else {
                // we have a value from design attributes, let's use that
                Type[] types = GenericTypeReflector
                        .getExactParameterTypes(setter, target.getClass());
                Object param = getFormatter().parse(value, (Class<?>) types[0]);
                setter.invoke(target, param);
                success = true;
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Failed to set value \"" + value
                    + "\" to attribute " + attribute, e);
        }
        if (!success) {
            getLogger().info("property " + attribute
                    + " ignored by default attribute handler");
        }
        return success;
    }

    /**
     * Searches for supported setter and getter types from the specified class
     * and returns the list of corresponding design attributes.
     *
     * @param clazz
     *            the class scanned for setters
     * @return the list of supported design attributes
     */
    public static Collection<String> getSupportedAttributes(Class<?> clazz) {
        resolveSupportedAttributes(clazz);
        return CACHE.get(clazz).getAttributes();
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
        if (CACHE.containsKey(clazz)) {
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
        for (PropertyDescriptor descriptor : beanInfo
                .getPropertyDescriptors()) {
            Method getter = descriptor.getReadMethod();
            Method setter = descriptor.getWriteMethod();
            Class<?> propertyType = descriptor.getPropertyType();
            if (getter != null && setter != null && propertyType != null
                    && getFormatter().canConvert(propertyType)) {
                String attribute = toAttributeName(descriptor.getName());
                entry.addAttribute(attribute, getter, setter);
            }
        }
        CACHE.put(clazz, entry);
    }

    /**
     * Writes the specified attribute to the design if it differs from the
     * default value got from the <code> defaultInstance </code>.
     *
     * @param component
     *            the component used to get the attribute value
     * @param attribute
     *            the key for the attribute
     * @param attr
     *            the attribute list where the attribute will be written
     * @param defaultInstance
     *            the default instance for comparing default values
     * @since 8.0
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void writeAttribute(Object component, String attribute,
            Attributes attr, Object defaultInstance, DesignContext context) {
        Method getter = findGetterForAttribute(component.getClass(), attribute);
        if (getter == null) {
            getLogger().warning(
                    "Could not find getter for attribute " + attribute);
        } else {
            try {
                // compare the value with default value
                Object value = getter.invoke(component);
                Object defaultValue = getter.invoke(defaultInstance);
                writeAttribute(attribute, attr, value, defaultValue,
                        (Class) GenericTypeReflector.getExactReturnType(getter,
                                component.getClass()),
                        context);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE,
                        "Failed to invoke getter for attribute " + attribute,
                        e);
            }
        }
    }

    /**
     * Writes the given attribute value to a set of attributes if it differs
     * from the default attribute value.
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
     * @since 8.0
     */
    public static <T> void writeAttribute(String attribute,
            Attributes attributes, T value, T defaultValue, Class<T> inputType,
            DesignContext context) {
        if (!getFormatter().canConvert(inputType)) {
            throw new IllegalArgumentException(
                    "input type: " + inputType.getName() + " not supported");
        }
        if (context.shouldWriteDefaultValues()
                || !SharedUtil.equals(value, defaultValue)) {
            String attributeValue = toAttributeValue(inputType, value);
            if ("".equals(attributeValue) && (inputType == boolean.class
                    || inputType == Boolean.class)) {
                attributes.put(attribute, true);
            } else {
                attributes.put(attribute, attributeValue);
            }
        }
    }

    /**
     * Reads the given attribute from a set of attributes. If attribute does not
     * exist return a given default value.
     *
     * @param attribute
     *            the attribute key
     * @param attributes
     *            the set of attributes to read from
     * @param defaultValue
     *            the default value to return if attribute does not exist
     * @param outputType
     *            the output type for the attribute
     * @return the attribute value or the default value if the attribute is not
     *         found
     */
    public static <T> T readAttribute(String attribute, Attributes attributes,
            T defaultValue, Class<T> outputType) {
        T value = readAttribute(attribute, attributes, outputType);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    /**
     * Reads the given attribute from a set of attributes.
     *
     * @param attribute
     *            the attribute key
     * @param attributes
     *            the set of attributes to read from
     * @param outputType
     *            the output type for the attribute
     * @return the attribute value or null
     */
    public static <T> T readAttribute(String attribute, Attributes attributes,
            Class<T> outputType) {
        if (!getFormatter().canConvert(outputType)) {
            throw new IllegalArgumentException(
                    "output type: " + outputType.getName() + " not supported");
        }
        if (!attributes.hasKey(attribute)) {
            return null;
        } else {
            try {
                String value = attributes.get(attribute);
                return getFormatter().parse(value, outputType);
            } catch (Exception e) {
                throw new DesignException(
                        "Failed to read attribute " + attribute, e);
            }
        }
    }

    /**
     * Returns the design attribute name corresponding the given method name.
     * For example given a method name <code>setPrimaryStyleName</code> the
     * return value would be <code>primary-style-name</code>
     *
     * @param propertyName
     *            the property name returned by {@link Introspector}
     * @return the design attribute name corresponding the given method name
     */
    private static String toAttributeName(String propertyName) {
        propertyName = removeSubsequentUppercase(propertyName);
        String[] words = propertyName.split("(?<!^)(?=[A-Z])");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (builder.length() != 0) {
                builder.append('-');
            }
            builder.append(word.toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    /**
     * Replaces subsequent UPPERCASE strings of length 2 or more followed either
     * by another uppercase letter or an end of string. This is to generalise
     * handling of method names like <tt>showISOWeekNumbers</tt>.
     *
     * @param param
     *            Input string.
     * @return Input string with sequences of UPPERCASE turned into Normalcase.
     */
    private static String removeSubsequentUppercase(String param) {
        StringBuffer result = new StringBuffer();
        // match all two-or-more caps letters lead by a non-uppercase letter
        // followed by either a capital letter or string end
        Pattern pattern = Pattern.compile("(^|[^A-Z])([A-Z]{2,})([A-Z]|$)");
        Matcher matcher = pattern.matcher(param);
        while (matcher.find()) {
            String matched = matcher.group(2);
            // if this is a beginning of the string, the whole matched group is
            // written in lower case
            if (matcher.group(1).isEmpty()) {
                matcher.appendReplacement(result,
                        matched.toLowerCase(Locale.ROOT) + matcher.group(3));
                // otherwise the first character of the group stays uppercase,
                // while the others are lower case
            } else {
                matcher.appendReplacement(result,
                        matcher.group(1) + matched.substring(0, 1)
                                + matched.substring(1).toLowerCase(Locale.ROOT)
                                + matcher.group(3));
            }
            // in both cases the uppercase letter of the next word (or string's
            // end) is added
            // this implies there is at least one extra lowercase letter after
            // it to be caught by the next call to find()
        }
        matcher.appendTail(result);
        return result.toString();
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
        @SuppressWarnings("unchecked")
        Converter<String, Object> converter = getFormatter()
                .findConverterFor(sourceType);
        if (converter != null) {
            return converter.convertToPresentation(value, new ValueContext());
        } else {
            return value.toString();
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
        return CACHE.get(clazz).getSetter(attribute);
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
        return CACHE.get(clazz).getGetter(attribute);
    }

    /**
     * Cache object for caching supported attributes and their getters and
     * setters
     *
     * @author Vaadin Ltd
     */
    private static class AttributeCacheEntry implements Serializable {
        private final Map<String, Method[]> accessMethods = new ConcurrentHashMap<>();

        private void addAttribute(String attribute, Method getter,
                Method setter) {
            Method[] methods = new Method[2];
            methods[0] = getter;
            methods[1] = setter;
            accessMethods.put(attribute, methods);
        }

        private Collection<String> getAttributes() {
            List<String> attributes = new ArrayList<>();
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
     * Read the alignment from the given child component attributes.
     *
     * @since 7.6.4
     * @param attr
     *            the child component attributes
     * @return the component alignment
     */
    public static Alignment readAlignment(Attributes attr) {
        int bitMask = 0;
        if (attr.hasKey(":middle")) {
            bitMask += AlignmentInfo.Bits.ALIGNMENT_VERTICAL_CENTER;
        } else if (attr.hasKey(":bottom")) {
            bitMask += AlignmentInfo.Bits.ALIGNMENT_BOTTOM;
        } else {
            bitMask += AlignmentInfo.Bits.ALIGNMENT_TOP;
        }
        if (attr.hasKey(":center")) {
            bitMask += AlignmentInfo.Bits.ALIGNMENT_HORIZONTAL_CENTER;
        } else if (attr.hasKey(":right")) {
            bitMask += AlignmentInfo.Bits.ALIGNMENT_RIGHT;
        } else {
            bitMask += AlignmentInfo.Bits.ALIGNMENT_LEFT;
        }

        return new Alignment(bitMask);
    }

    /**
     * Writes the alignment to the given child element attributes.
     *
     * @since 7.6.4
     * @param childElement
     *            the child element
     * @param alignment
     *            the component alignment
     */
    public static void writeAlignment(Element childElement,
            Alignment alignment) {
        if (alignment.isMiddle()) {
            childElement.attr(":middle", true);
        } else if (alignment.isBottom()) {
            childElement.attr(":bottom", true);
        }
        if (alignment.isCenter()) {
            childElement.attr(":center", true);
        } else if (alignment.isRight()) {
            childElement.attr(":right", true);
        }
    }

    private DesignAttributeHandler() {
    }

}
