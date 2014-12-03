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

import com.vaadin.ui.Component;
import com.vaadin.ui.DesignSynchronizable;

/**
 * This class contains contextual information that is collected when a component
 * tree is constructed based on HTML design template.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignContext {

    // cache for object instances
    private static Map<Class<?>, Object> instanceCache = Collections
            .synchronizedMap(new HashMap<Class<?>, Object>());

    // The root component of the component hierarchy
    private DesignSynchronizable componentRoot = null;
    // Attribute names for global id and caption and the prefix name for a local
    // id
    public static final String ID_ATTRIBUTE = "id";
    public static final String CAPTION_ATTRIBUTE = "caption";
    public static final String LOCAL_ID_PREFIX = "_";
    // Mappings from IDs to components. Modified when synchronizing from design.
    private Map<String, DesignSynchronizable> globalIdToComponent = new HashMap<String, DesignSynchronizable>();
    private Map<String, DesignSynchronizable> localIdToComponent = new HashMap<String, DesignSynchronizable>();
    private Map<String, DesignSynchronizable> captionToComponent = new HashMap<String, DesignSynchronizable>();
    // Mapping from components to local IDs. Accessed when synchronizing to
    // design. Modified when synchronizing from design.
    private Map<DesignSynchronizable, String> componentToLocalId = new HashMap<DesignSynchronizable, String>();
    private Document doc; // required for calling createElement(String)
    // namespace mappings
    private Map<String, String> packageToPrefix = new HashMap<String, String>();
    private Map<String, String> prefixToPackage = new HashMap<String, String>();
    // prefix names for which no package-mapping element will be created in the
    // html tree (this includes at least "v" which is always taken to refer
    // to "com.vaadin.ui".
    private Map<String, String> defaultPrefixes = new HashMap<String, String>();

    public DesignContext(Document doc) {
        this.doc = doc;
        // Initialize the mapping between prefixes and package names.
        defaultPrefixes.put("v", "com.vaadin.ui");
        for (String prefix : defaultPrefixes.keySet()) {
            String packageName = defaultPrefixes.get(prefix);
            mapPrefixToPackage(prefix, packageName);
        }
    }

    public DesignContext() {
        this(new Document(""));
    }

    /**
     * Creates a mapping between the given global id and the component. Returns
     * true if globalId was already mapped to some component or if component was
     * mapped to some string. Otherwise returns false. Also sets the id of the
     * component to globalId.
     * 
     * If the string was mapped to a component c different from the given
     * component, the mapping from c to the string is removed. Similarly, if
     * component was mapped to some string s different from globalId, the
     * mapping from s to component is removed.
     * 
     * @since
     * @param globalId
     *            The new global id of the component.
     * @param component
     *            The component whose global id is to be set.
     * @return true, if there already was a global id mapping from the string to
     *         some component or from the component to some string. Otherwise
     *         returns false.
     */
    public boolean mapGlobalId(String globalId, DesignSynchronizable component) {
        DesignSynchronizable oldComponent = globalIdToComponent.get(globalId);
        if (oldComponent != null && !oldComponent.equals(component)) {
            oldComponent.setId(null);
        }
        String oldGID = component.getId();
        if (oldGID != null && !oldGID.equals(globalId)) {
            globalIdToComponent.remove(oldGID);
        }
        component.setId(globalId);
        return oldComponent != null || oldGID != null;
    }

    /**
     * Creates a mapping between the given local id and the component. Returns
     * true if localId was already mapped to some component or if component was
     * mapped to some string. Otherwise returns false.
     * 
     * If the string was mapped to a component c different from the given
     * component, the mapping from c to the string is removed. Similarly, if
     * component was mapped to some string s different from localId, the mapping
     * from s to component is removed.
     * 
     * @since
     * @param globalId
     *            The new local id of the component.
     * @param component
     *            The component whose local id is to be set.
     * @return true, if there already was a local id mapping from the string to
     *         some component or from the component to some string. Otherwise
     *         returns false.
     */
    public boolean mapLocalId(String localId, DesignSynchronizable component) {
        return twoWayMap(localId, component, localIdToComponent,
                componentToLocalId);
    }

    /**
     * Creates a mapping between the given caption and the component. Returns
     * true if caption was already mapped to some component.
     * 
     * Note that unlike mapGlobalId, if some component already has the given
     * caption, the caption is not cleared from the component. This allows
     * non-unique captions. However, only one of the components corresponding to
     * a given caption can be found using the map captionToComponent. Hence, any
     * captions that are used to identify an object should be unique.
     * 
     * @since
     * @param caption
     *            The new caption of the component.
     * @param component
     *            The component whose caption is to be set.
     * @return true, if there already was a caption mapping from the string to
     *         some component.
     */
    public boolean mapCaption(String caption, DesignSynchronizable component) {
        return captionToComponent.put(caption, component) != null;
    }

    /**
     * Creates a two-way mapping between key and value, i.e. adds key -> value
     * to keyToValue and value -> key to valueToKey. If key was mapped to a
     * value v different from the given value, the mapping from v to key is
     * removed. Similarly, if value was mapped to some key k different from key,
     * the mapping from k to value is removed.
     * 
     * Returns true if there already was a mapping from key to some value v or
     * if there was a mapping from value to some key k. Otherwise returns false.
     * 
     * @since
     * @param key
     *            The new key in keyToValue.
     * @param value
     *            The new value in keyToValue.
     * @param keyToValue
     *            A map from keys to values.
     * @param valueToKey
     *            A map from values to keys.
     * @return whether there already was some mapping from key to a value or
     *         from value to a key.
     */
    private <S, T> boolean twoWayMap(S key, T value, Map<S, T> keyToValue,
            Map<T, S> valueToKey) {
        T oldValue = keyToValue.put(key, value);
        if (oldValue != null && !oldValue.equals(value)) {
            valueToKey.remove(oldValue);
        }
        S oldKey = valueToKey.put(value, key);
        if (oldKey != null && !oldKey.equals(key)) {
            keyToValue.remove(oldKey);
        }
        return oldValue != null || oldKey != null;
    }

    /**
     * Creates a two-way mapping between a prefix and a package name. Return
     * true if prefix was already mapped to some package name or packageName to
     * some prefix.
     * 
     * @since
     * @param prefix
     *            the prefix name without an ending dash (for instance, "v" is
     *            always used for "com.vaadin.ui")
     * @param packageName
     *            the name of the package corresponding to prefix
     * @return whether there was a mapping from prefix to some package name or
     *         from packageName to some prefix.
     */
    public boolean mapPrefixToPackage(String prefix, String packageName) {
        return twoWayMap(prefix, packageName, prefixToPackage, packageToPrefix);
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

    /**
     * Get and store the mappings from prefixes to package names from meta tags
     * located under <head> in the html document.
     * 
     * @since
     */
    public void getPrefixes(Document doc) {
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
                            throw new LayoutInflaterException("The meta tag '"
                                    + child.toString() + "' cannot be parsed.");
                        }
                        String prefixName = parts[0];
                        String packageName = parts[1];
                        twoWayMap(prefixName, packageName, prefixToPackage,
                                packageToPrefix);
                    }
                }
            }
        }
    }

    /**
     * 
     */
    public void storePrefixes(Document doc) {
        Element head = doc.head();
        for (String prefix : prefixToPackage.keySet()) {
            // Only store the prefix-name mapping if it is not a default mapping
            // (such as "v" -> "com.vaadin.ui")
            if (defaultPrefixes.get(prefix) == null) {
                Node newNode = doc.createElement("meta");
                newNode.attr("name", "package-mapping");
                String prefixToPackageName = prefix + ":"
                        + prefixToPackage.get(prefix);
                newNode.attr("content", prefixToPackageName);
                head.appendChild(newNode);

            }
        }
    }

    /**
     * Creates an html tree node corresponding to the given element. Also
     * initializes its attributes by calling synchronizeToDesign. As a result of
     * the synchronizeToDesign() call, this method creates the entire subtree
     * rooted at the returned Node.
     * 
     * @since
     * @param childComponent
     *            A component implementing the DesignSynchronizable interface.
     * @return An html tree node corresponding to the given component. The tag
     *         name of the created node is derived from the class name of
     *         childComponent.
     */
    public Element createNode(DesignSynchronizable childComponent) {
        Class<?> componentClass = childComponent.getClass();
        String packageName = componentClass.getPackage().getName();
        String prefix = packageToPrefix.get(packageName);
        if (prefix == null) {
            prefix = packageName.replace('.', '_');
            twoWayMap(prefix, packageName, prefixToPackage, packageToPrefix);
        }
        prefix = prefix + "-";
        String className = classNameToElementName(componentClass
                .getSimpleName());
        Element newElement = doc.createElement(prefix + className);
        childComponent.synchronizeToDesign(newElement, this);
        // Handle the local id. Global id and caption should have been taken
        // care of by synchronizeToDesign.
        String localId = componentToLocalId.get(childComponent);
        if (localId != null) {
            localId = LOCAL_ID_PREFIX + localId;
            newElement.attr(localId, "");
        }
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
     * node. Also calls synchronizeFromDesign() for the created node, in effect
     * creating the entire component hierarchy rooted at the returned component.
     * 
     * @since
     * @param componentDesign
     *            The html tree node containing the description of the component
     *            to be created.
     * @return a DesignSynchronizable object corresponding to componentDesign,
     *         with no attributes set.
     */
    public DesignSynchronizable createChild(Element componentDesign) {
        // Create the component.
        DesignSynchronizable component = instantiateComponent(componentDesign);
        component.synchronizeFromDesign(componentDesign, this);
        // Get the IDs and the caption of the component and store them in the
        // maps of this design context.
        org.jsoup.nodes.Attributes attributes = componentDesign.attributes();
        // global id: only update the mapping, the id has already been set for
        // the component
        String id = component.getCaption();
        if (id != null && id.length() > 0) {
            boolean mappingExists = mapGlobalId(id, component);
            if (mappingExists) {
                throw new LayoutInflaterException(
                        "The following global id is not unique: " + id);
            }
        }
        // local id: this is not a property of a component, so need to fetch it
        // from the attributes of componentDesign
        String localId = null;
        for (Attribute attribute : attributes.asList()) {
            if (attribute.getKey().startsWith(LOCAL_ID_PREFIX)) {
                if (localId != null) {
                    throw new LayoutInflaterException(
                            "Duplicate local ids specified: "
                                    + localId
                                    + " and "
                                    + attribute.getKey().substring(
                                            LOCAL_ID_PREFIX.length()));
                }
                localId = attribute.getKey()
                        .substring(LOCAL_ID_PREFIX.length());
                mapLocalId(localId, component); // two-way map
            }
        }
        // caption: a property of a component, possibly not unique
        String caption = component.getCaption();
        if (caption != null) {
            mapCaption(caption, component);
        }
        return component;
    }

    /**
     * Creates a DesignSynchronizable component corresponding to the given node.
     * Does not set the attributes for the created object.
     * 
     * @since
     * @param node
     *            a node of an html tree
     * @return a DesignSynchronizable object corresponding to node, with no
     *         attributes set.
     */
    private DesignSynchronizable instantiateComponent(Node node) {
        // Extract the package and class names.
        String qualifiedClassName = tagNameToClassName(node);
        try {
            Class<? extends DesignSynchronizable> componentClass = resolveComponentClass(qualifiedClassName);
            DesignSynchronizable newComponent = componentClass.newInstance();
            return newComponent;
        } catch (Exception e) {
            throw createException(e, qualifiedClassName);
        }
    }

    /**
     * Returns the qualified class name corresponding to the given html tree
     * node. If the node is not a span or a div, the class name is extracted
     * from the tag name of node.
     * 
     * @since
     * @param node
     *            an html tree node
     * @return The qualified class name corresponding to the given node.
     */
    private String tagNameToClassName(Node node) {
        String tagName = node.nodeName();
        if (tagName.equals("v-addon")) {
            return node.attr("class");
        } else if (tagName.toLowerCase(Locale.ENGLISH).equals("span")
                || tagName.toLowerCase(Locale.ENGLISH).equals("div")) {
            return "com.vaadin.ui.Label";
        }
        // Otherwise, get the full class name using the prefix to package
        // mapping. Example: "v-vertical-layout" ->
        // "com.vaadin.ui.VerticalLayout"
        String[] parts = tagName.split("-");
        if (parts.length < 2) {
            throw new LayoutInflaterException("The tagname '" + tagName
                    + "' is invalid: missing prefix.");
        }
        String prefixName = parts[0];
        String packageName = prefixToPackage.get(prefixName);
        if (packageName == null) {
            throw new LayoutInflaterException("Unknown tag: " + tagName);
        }
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
                System.out.println("A tag name should not end with '-'.");
            }
        }
        return packageName + "." + tagName;
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

    /*
     * Create a new ComponentInstantiationException.
     */
    private ComponentInstantiationException createException(Exception e,
            String qualifiedClassName) {
        String message = String.format(
                "Couldn't instantiate a component for %s.", qualifiedClassName);
        if (e != null) {
            return new ComponentInstantiationException(message, e);
        } else {
            return new ComponentInstantiationException(message);
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
    private static boolean isDesignSynchronizable(Class<?> componentClass) {
        if (componentClass != null) {
            return DesignSynchronizable.class.isAssignableFrom(componentClass);
        } else {
            return false;
        }
    }

    /**
     * Returns the root component of a created component hierarchy.
     * 
     * @since
     * @return
     */
    public DesignSynchronizable getComponentRoot() {
        return componentRoot;
    }

    /**
     * Sets the root component of a created component hierarchy.
     */
    public void setComponentRoot(DesignSynchronizable componentRoot) {
        this.componentRoot = componentRoot;
    }
}