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

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.vaadin.ui.Component;
import com.vaadin.ui.DesignSynchronizable;

/**
 * DesignContext can create a component corresponding to a given html tree node
 * or an html tree node corresponding to a given component. DesignContext also
 * keeps track of id values found in the current html tree and can detect
 * non-uniqueness of these values. Non-id attributes are handled by the
 * component classes instead of DesignContext.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class DesignContext {

    // cache for object instances
    private static Map<Class<?>, Object> instanceCache = Collections
            .synchronizedMap(new HashMap<Class<?>, Object>());

    public static final String ID_ATTRIBUTE = "id";
    public static final String CAPTION_ATTRIBUTE = "caption";
    public static final String LOCAL_ID_PREFIX = "_";
    private Map<String, DesignSynchronizable> globalIds = new HashMap<String, DesignSynchronizable>();
    private Map<String, DesignSynchronizable> localIds = new HashMap<String, DesignSynchronizable>();
    private Map<String, DesignSynchronizable> captions = new HashMap<String, DesignSynchronizable>();
    private Document doc; // used for accessing
                          // Document.createElement(String)
    // namespace mappings
    private Map<String, String> packageToPrefix = new HashMap<String, String>();
    private Map<String, String> prefixToPackage = new HashMap<String, String>();
    // prefix names for which no package-mapping element will be created in the
    // html tree
    private Map<String, String> defaultPrefixes = new HashMap<String, String>();

    public DesignContext() {
        doc = new Document("");
        // Initialize the mapping between prefixes and package names. First add
        // any default mappings (v -> com.vaadin.ui). The default mappings are
        // the prefixes for which
        // no meta tags will be created when writing a design to html.
        defaultPrefixes.put("v", "com.vaadin.ui");
        for (String prefix : defaultPrefixes.keySet()) {
            String packageName = defaultPrefixes.get(prefix);
            prefixToPackage.put(prefix, packageName);
            packageToPrefix.put(packageName, prefix);
        }
    }

    /**
     * Get the mappings from prefixes to package names from meta tags located
     * under <head> in the html document.
     * 
     * @since
     */
    public void getPrefixes(Document doc) {
        // TODO this method has not been tested in any way.
        Element head = doc.head();
        if (head == null) {
            return;
        }
        for (Node child : head.childNodes()) {
            if (child instanceof Element) {
                Element childElement = (Element) child;
                if ("meta".equals(childElement.tagName())) {
                    Attributes attributes = childElement.attributes();
                    if (attributes.hasKey("name")
                            && attributes.hasKey("content")
                            && "package-mapping".equals(attributes.get("name"))) {
                        String contentString = attributes.get("content");
                        String[] parts = contentString.split(":");
                        if (parts.length != 2) {
                            throw new RuntimeException("The meta tag '"
                                    + child.toString() + "' cannot be parsed.");
                        }
                        String prefixName = parts[0];
                        String packageName = parts[1];
                        prefixToPackage.put(prefixName, packageName);
                        packageToPrefix.put(packageName, prefixName);
                    }
                }
            }
        }
    }

    /**
     * Creates an html tree node corresponding to the given element. Note that
     * this method does not set the attribute values. That can be done by
     * calling childComponent.synchronizeToDesign(result, designContext), where
     * result is the node returned by this method and designContext is this
     * context.
     * 
     * @since
     * @param childComponent
     *            A component implementing the DesignSynchronizable interface.
     * @return An html tree node corresponding to the given component, with no
     *         attributes set. The tag name of the created node is derived from
     *         the class name of childComponent.
     */
    public Node createNode(DesignSynchronizable childComponent) {
        // TODO handle namespaces and id's.
        Class<?> componentClass = childComponent.getClass();
        String packageName = componentClass.getPackage().getName();
        String prefix = packageToPrefix.get(packageName);
        if (prefix == null) {
            prefix = packageName.replace('.', '_');
            prefixToPackage.put(prefix, packageName);
            packageToPrefix.put(packageName, prefix);
        }
        prefix = prefix + "-";
        String className = classNameToElementName(componentClass
                .getSimpleName());
        Element newElement = doc.createElement(prefix + className);
        return newElement;
    }

    /**
     * Creates the name of the html tag corresponding to the given class name.
     * The name is derived by converting each uppercase letter to lowercase and
     * inserting a dash before the letter. No dash is inserted before the first
     * letter of the class name.
     * 
     * @since
     * @param className
     *            the name of the class without a package name
     * @return the html tag name corresponding to className
     */
    private String classNameToElementName(String className) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < className.length(); i++) {
            Character c = className.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("-");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Creates a DesignSynchronizable object corresponding to the given html
     * node. Note that the attributes of the node are not taken into account by
     * this method, except IDs. To get the attributes, call
     * result.synchronizeFromDesign(componentDesign, designContext), where
     * result is the node returned by this method and designContext is this
     * context.
     * 
     * @since
     * @param componentDesign
     *            The html tree node containing the description of the component
     *            to be created.
     * @return a DesignSynchronizable object corresponding to componentDesign,
     *         with no attributes set.
     */
    public DesignSynchronizable createChild(Node componentDesign) {
        // Create the component.
        DesignSynchronizable component = instantiateComponent(componentDesign);
        // Get the IDs and the caption of the component and store them in the
        // maps of this design context.
        org.jsoup.nodes.Attributes attributes = componentDesign.attributes();

        // Global id
        String id = attributes.get(ID_ATTRIBUTE);
        if (id != null && id.length() > 0) {
            Component oldComponent = globalIds.put(camelCase(id), component);
            if (oldComponent != null) {
                throw new RuntimeException("Duplicate ids: " + id);
            }
        }

        // Local id
        String localId = null;
        for (Attribute attribute : attributes.asList()) {
            if (attribute.getKey().startsWith(LOCAL_ID_PREFIX)) {
                if (localId != null) {
                    throw new RuntimeException(
                            "Duplicate local ids specified: " + localId
                                    + " and " + attribute.getValue());
                }
                localId = attribute.getKey()
                        .substring(LOCAL_ID_PREFIX.length());
                localIds.put(camelCase(localId), component);
            }
        }

        // Caption
        String caption = null;
        if (componentDesign.nodeName().equals("v-button")) {
            String buttonCaption = textContent(componentDesign);
            if (buttonCaption != null && !(buttonCaption.equals(""))) {
                caption = buttonCaption;
            }
        }
        if (caption == null) {
            String componentCaption = attributes.get(CAPTION_ATTRIBUTE);
            if (componentCaption != null && !("".equals(componentCaption))) {
                caption = componentCaption;
            }
        }
        if (caption != null) {
            Component oldComponent = captions
                    .put(camelCase(caption), component);
            if (oldComponent != null) {
                throw new RuntimeException("Duplicate captions: " + caption);
            }
        }
        return component;
    }

    /**
     * Returns the text content of an html tree node. Used for getting the
     * caption of a button.
     * 
     * @since
     * @param node
     *            A node of an html tree
     * @return the text content of node, obtained by concatenating the text
     *         contents of its children
     */
    private String textContent(Node node) {
        String text = "";
        for (Node child : node.childNodes()) {
            if (child instanceof TextNode) {
                text += ((TextNode) child).text();
            }
        }
        return text;
    }

    /**
     * Creates a DesignSynchronizable component corresponding to the given node.
     * 
     * @since
     * @param node
     *            a node of an html tree
     * @return a DesignSynchronizable object corresponding to node
     */
    private DesignSynchronizable instantiateComponent(Node node) {
        // Extract the package and class names.
        String qualifiedClassName = tagNameToClassName(node);
        return createComponent(qualifiedClassName);
    }

    private String tagNameToClassName(Node node) {
        String tagName = node.nodeName();
        if (tagName.equals("v-addon")) {
            return node.attr("class");
        } else if (tagName.toLowerCase(Locale.ENGLISH).equals("span")
                || tagName.toLowerCase(Locale.ENGLISH).equals("div")) {
            return "com.vaadin.ui.Label";
        }
        // Otherwise, get the package name from the prefixToPackage mapping.
        String[] parts = tagName.split("-");
        if (parts.length < 2) {
            throw new RuntimeException("The tagname '" + tagName
                    + "' is invalid: missing prefix.");
        }
        String prefixName = parts[0];
        String packageName = prefixToPackage.get(prefixName);
        if (packageName == null) {
            throw new RuntimeException("Unknown tag: " + tagName);
        }
        // v-vertical-layout -> com.vaadin.ui.VerticalLayout
        int firstCharacterIndex = prefixName.length() + 1; // +1 is for '-'
        tagName = tagName.substring(firstCharacterIndex,
                firstCharacterIndex + 1).toUpperCase(Locale.ENGLISH)
                + tagName.substring(firstCharacterIndex + 1);
        int i;
        while ((i = tagName.indexOf("-")) != -1) {
            int length = tagName.length();
            if (i != length - 1) {
                tagName = tagName.substring(0, i)
                        + tagName.substring(i + 1, i + 2).toUpperCase(
                                Locale.ENGLISH) + tagName.substring(i + 2);

            } else {
                // Ends with "-", WTF?
                System.out.println("ends with '-', really?");
            }
        }
        return packageName + "." + tagName;
    }

    /**
     * Returns a new component instance of given class name. If the component
     * cannot be instantiated a ComponentInstantiationException is thrown.
     * 
     * @param qualifiedClassName
     *            The full class name of the object to be created.
     * @return a new DesignSynchronizable instance.
     * @throws ComponentInstantiationException
     */
    public DesignSynchronizable createComponent(String qualifiedClassName) {
        try {
            Class<? extends DesignSynchronizable> componentClass = resolveComponentClass(qualifiedClassName);
            DesignSynchronizable newComponent = componentClass.newInstance();
            return newComponent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DesignSynchronizable> resolveComponentClass(
            String qualifiedClassName) throws ClassNotFoundException {
        Class<?> componentClass = null;
        componentClass = Class.forName(qualifiedClassName);

        // Check that we're dealing with a DesignSynchronizable component.
        if (isDesignSynchronizable(componentClass)) {
            return (Class<? extends DesignSynchronizable>) componentClass;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Resolved class %s is not a %s.", componentClass.getName(),
                    Component.class.getName()));
        }
    }

    /**
     * Returns {@code true} if the given {@link Class} implements the
     * {@link Component} interface of Vaadin Framework otherwise {@code false}.
     * 
     * @param componentClass
     *            {@link Class} to check against {@link Component} interface.
     * @return {@code true} if the given {@link Class} is a {@link Component},
     *         {@code false} otherwise.
     */
    public static boolean isDesignSynchronizable(Class<?> componentClass) {
        if (componentClass != null) {
            return DesignSynchronizable.class.isAssignableFrom(componentClass);
        } else {
            return false;
        }
    }

    private String camelCase(String localId) {
        // TODO does this method do what it should (it was taken from another
        // project without any modifications)

        // Remove all but a-Z, 0-9 (used for names) and _- and space (used
        // for separators)
        // localId = localId.replaceAll("[^a-zA-Z0-9_- ]", "");
        return localId.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(
                Locale.ENGLISH);
        // String[] parts = localId.split("[ -_]+");
        // String thisPart = parts[0];
        // String camelCase =
        // thisPart.substring(0,1).toLowerCase(Locale.ENGLISH);
        // if (parts[0].length() > 1) {
        // camelCase += thisPart.substring(1);
        // }
        //
        // for (int i=1; i < parts.length; i++) {
        // thisPart = parts[i];
        // camelCase += thisPart.substring(0,1).toUpperCase(Locale.ENGLISH);
        // if (thisPart.length() > 1) {
        // camelCase += thisPart.substring(1);
        // }
        // }
        // return camelCase;
    }

    /**
     * Returns the default instance for the given class. The instance must not
     * be modified by the caller.
     * 
     * @since
     * @param instanceClass
     * @return
     */
    public <T> T getDefaultInstance(Class<T> instanceClass) {
        T instance = (T) instanceCache.get(instanceClass);
        if (instance == null) {
            try {
                instance = instanceClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            instanceCache.put(instanceClass, instance);
        }
        return instance;
    }

}
