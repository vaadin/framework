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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.declarative.Design.ComponentFactory;
import com.vaadin.ui.declarative.Design.ComponentMapper;

/**
 * This class contains contextual information that is collected when a component
 * tree is constructed based on HTML design template. This information includes
 * mappings from local ids, global ids and captions to components , as well as a
 * mapping between prefixes and package names (such as "v" -> "com.vaadin.ui").
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DesignContext implements Serializable {

    // cache for object instances
    private static Map<Class<?>, Component> instanceCache = new ConcurrentHashMap<Class<?>, Component>();

    // The root component of the component hierarchy
    private Component rootComponent = null;
    // Attribute names for global id and caption and the prefix name for a local
    // id
    public static final String ID_ATTRIBUTE = "id";
    public static final String CAPTION_ATTRIBUTE = "caption";
    public static final String LOCAL_ID_ATTRIBUTE = "_id";
    // Mappings from ids to components. Modified when reading from design.
    private Map<String, Component> idToComponent = new HashMap<String, Component>();
    private Map<String, Component> localIdToComponent = new HashMap<String, Component>();
    private Map<String, Component> captionToComponent = new HashMap<String, Component>();
    // Mapping from components to local ids. Accessed when writing to
    // design. Modified when reading from design.
    private Map<Component, String> componentToLocalId = new HashMap<Component, String>();
    private Document doc; // required for calling createElement(String)
    // namespace mappings
    private Map<String, String> packageToPrefix = new HashMap<String, String>();
    private Map<String, String> prefixToPackage = new HashMap<String, String>();
    // prefix names for which no package-mapping element will be created in the
    // html tree (this includes at least "v" which is always taken to refer
    // to "com.vaadin.ui".
    private Map<String, String> defaultPrefixes = new HashMap<String, String>();

    // component creation listeners
    private List<ComponentCreationListener> listeners = new ArrayList<ComponentCreationListener>();

    private ShouldWriteDataDelegate shouldWriteDataDelegate = ShouldWriteDataDelegate.DEFAULT;

    public DesignContext(Document doc) {
        this.doc = doc;
        // Initialize the mapping between prefixes and package names.
        defaultPrefixes.put("v", "com.vaadin.ui");
        for (String prefix : defaultPrefixes.keySet()) {
            String packageName = defaultPrefixes.get(prefix);
            addPackagePrefix(prefix, packageName);
        }
    }

    public DesignContext() {
        this(new Document(""));
    }

    /**
     * Returns a component having the specified local id. If no component is
     * found, returns null.
     * 
     * @param localId
     *            The local id of the component
     * @return a component whose local id equals localId
     */
    public Component getComponentByLocalId(String localId) {
        return localIdToComponent.get(localId);
    }

    /**
     * Returns a component having the specified global id. If no component is
     * found, returns null.
     * 
     * @param globalId
     *            The global id of the component
     * @return a component whose global id equals globalId
     */
    public Component getComponentById(String globalId) {
        return idToComponent.get(globalId);
    }

    /**
     * Returns a component having the specified caption. If no component is
     * found, returns null.
     * 
     * @param caption
     *            The caption of the component
     * @return a component whose caption equals the caption given as a parameter
     */
    public Component getComponentByCaption(String caption) {
        return captionToComponent.get(caption);
    }

    /**
     * Creates a mapping between the given global id and the component. Returns
     * true if globalId was already mapped to some component. Otherwise returns
     * false. Also sets the id of the component to globalId.
     * 
     * If there is a mapping from the component to a global id (gid) different
     * from globalId, the mapping from gid to component is removed.
     * 
     * If the string was mapped to a component c different from the given
     * component, the mapping from c to the string is removed. Similarly, if
     * component was mapped to some string s different from globalId, the
     * mapping from s to component is removed.
     * 
     * @param globalId
     *            The new global id of the component.
     * @param component
     *            The component whose global id is to be set.
     * @return true, if there already was a global id mapping from the string to
     *         some component.
     */
    private boolean mapId(String globalId, Component component) {
        Component oldComponent = idToComponent.get(globalId);
        if (oldComponent != null && !oldComponent.equals(component)) {
            oldComponent.setId(null);
        }
        String oldGID = component.getId();
        if (oldGID != null && !oldGID.equals(globalId)) {
            idToComponent.remove(oldGID);
        }
        component.setId(globalId);
        idToComponent.put(globalId, component);
        return oldComponent != null && !oldComponent.equals(component);
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
     * @since 7.5.0
     * 
     * @param component
     *            The component whose local id is to be set.
     * @param localId
     *            The new local id of the component.
     * 
     * @return true, if there already was a local id mapping from the string to
     *         some component or from the component to some string. Otherwise
     *         returns false.
     */
    public boolean setComponentLocalId(Component component, String localId) {
        return twoWayMap(localId, component, localIdToComponent,
                componentToLocalId);
    }

    /**
     * Returns the local id for a component.
     * 
     * @since 7.5.0
     * 
     * @param component
     *            The component whose local id to get.
     * @return the local id of the component, or null if the component has no
     *         local id assigned
     */
    public String getComponentLocalId(Component component) {
        return componentToLocalId.get(component);
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
     * @param caption
     *            The new caption of the component.
     * @param component
     *            The component whose caption is to be set.
     * @return true, if there already was a caption mapping from the string to
     *         some component.
     */
    private boolean mapCaption(String caption, Component component) {
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
     * Creates a two-way mapping between a prefix and a package name.
     * 
     * @param prefix
     *            the prefix name without an ending dash (for instance, "v" is
     *            by default used for "com.vaadin.ui")
     * @param packageName
     *            the name of the package corresponding to prefix
     * 
     * @see #getPackagePrefixes()
     * @see #getPackagePrefix(String)
     * @see #getPackage(String)
     * @since 7.5.0
     */
    public void addPackagePrefix(String prefix, String packageName) {
        twoWayMap(prefix, packageName, prefixToPackage, packageToPrefix);
    }

    /**
     * Gets the prefix mapping for a given package, or <code>null</code> if
     * there is no mapping for the package.
     * 
     * @see #addPackagePrefix(String, String)
     * @see #getPackagePrefixes()
     * 
     * @since 7.5.0
     * @param packageName
     *            the package name to get a prefix for
     * @return the prefix for the package, or <code>null</code> if no prefix is
     *         registered
     */
    public String getPackagePrefix(String packageName) {
        return packageToPrefix.get(packageName);
    }

    /**
     * Gets all registered package prefixes.
     * 
     * 
     * @since 7.5.0
     * @see #getPackage(String)
     * @return a collection of package prefixes
     */
    public Collection<String> getPackagePrefixes() {
        return Collections.unmodifiableCollection(prefixToPackage.keySet());
    }

    /**
     * Gets the package corresponding to the give prefix, or <code>null</code>
     * no package has been registered for the prefix
     * 
     * @since 7.5.0
     * @see #addPackagePrefix(String, String)
     * @param prefix
     *            the prefix to find a package for
     * @return the package prefix, or <code>null</code> if no package is
     *         registered for the provided prefix
     */
    public String getPackage(String prefix) {
        return prefixToPackage.get(prefix);
    }

    /**
     * Returns the default instance for the given class. The instance must not
     * be modified by the caller.
     * 
     * @param abstractComponent
     * @return the default instance for the given class. The return value must
     *         not be modified by the caller
     */
    public <T> T getDefaultInstance(Component component) {
        // If the root is a @DesignRoot component, it can't use itself as a
        // reference or the written design will be empty

        // If the root component in some other way initializes itself in the
        // constructor
        if (getRootComponent() == component
                && component.getClass().isAnnotationPresent(DesignRoot.class)) {
            return (T) getDefaultInstance((Class<? extends Component>) component
                    .getClass().getSuperclass());
        }
        return (T) getDefaultInstance(component.getClass());
    }

    private Component getDefaultInstance(
            Class<? extends Component> componentClass) {
        Component instance = instanceCache.get(componentClass);
        if (instance == null) {
            instance = instantiateClass(componentClass.getName());
            instanceCache.put(componentClass, instance);
        }
        return instance;
    }

    /**
     * Reads and stores the mappings from prefixes to package names from meta
     * tags located under <head> in the html document.
     */
    protected void readPackageMappings(Document doc) {
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
                            throw new DesignException("The meta tag '"
                                    + child.toString() + "' cannot be parsed.");
                        }
                        String prefixName = parts[0];
                        String packageName = parts[1];
                        addPackagePrefix(prefixName, packageName);
                    }
                }
            }
        }
    }

    /**
     * Writes the package mappings (prefix -> package name) of this object to
     * the specified document.
     * <p>
     * The prefixes are stored as <meta> tags under <head> in the document.
     * 
     * @param doc
     *            the Jsoup document tree where the package mappings are written
     */
    public void writePackageMappings(Document doc) {
        Element head = doc.head();
        for (String prefix : getPackagePrefixes()) {
            // Only store the prefix-name mapping if it is not a default mapping
            // (such as "v" -> "com.vaadin.ui")
            if (defaultPrefixes.get(prefix) == null) {
                Node newNode = doc.createElement("meta");
                newNode.attr("name", "package-mapping");
                String prefixToPackageName = prefix + ":" + getPackage(prefix);
                newNode.attr("content", prefixToPackageName);
                head.appendChild(newNode);
            }
        }
    }

    /**
     * Creates an html tree node corresponding to the given element. Also
     * initializes its attributes by calling writeDesign. As a result of the
     * writeDesign() call, this method creates the entire subtree rooted at the
     * returned Node.
     * 
     * @param childComponent
     *            The component with state that is written in to the node
     * @return An html tree node corresponding to the given component. The tag
     *         name of the created node is derived from the class name of
     *         childComponent.
     */
    public Element createElement(Component childComponent) {
        ComponentMapper componentMapper = Design.getComponentMapper();

        String tagName = componentMapper.componentToTag(childComponent, this);

        Element newElement = doc.createElement(tagName);
        childComponent.writeDesign(newElement, this);
        // Handle the local id. Global id and caption should have been taken
        // care of by writeDesign.
        String localId = componentToLocalId.get(childComponent);
        if (localId != null) {
            newElement.attr(LOCAL_ID_ATTRIBUTE, localId);
        }
        return newElement;
    }

    /**
     * Reads the given design node and creates the corresponding component tree
     * 
     * @param componentDesign
     *            The design element containing the description of the component
     *            to be created.
     * @return the root component of component tree
     */
    public Component readDesign(Element componentDesign) {
        // Create the component.
        Component component = instantiateComponent(componentDesign);
        readDesign(componentDesign, component);
        fireComponentCreatedEvent(componentToLocalId.get(component), component);
        return component;
    }

    /**
     * 
     * Reads the given design node and populates the given component with the
     * corresponding component tree
     * <p>
     * Additionally registers the component id, local id and caption of the
     * given component and all its children in the context
     * 
     * @param componentDesign
     *            The design element containing the description of the component
     *            to be created
     * @param component
     *            The component which corresponds to the design element
     */
    public void readDesign(Element componentDesign, Component component) {
        component.readDesign(componentDesign, this);
        // Get the ids and the caption of the component and store them in the
        // maps of this design context.
        org.jsoup.nodes.Attributes attributes = componentDesign.attributes();
        // global id: only update the mapping, the id has already been set for
        // the component
        String id = component.getId();
        if (id != null && id.length() > 0) {
            boolean mappingExists = mapId(id, component);
            if (mappingExists) {
                throw new DesignException(
                        "The following global id is not unique: " + id);
            }
        }
        // local id: this is not a property of a component, so need to fetch it
        // from the attributes of componentDesign
        if (attributes.hasKey(LOCAL_ID_ATTRIBUTE)) {
            String localId = attributes.get(LOCAL_ID_ATTRIBUTE);
            boolean mappingExists = setComponentLocalId(component, localId);
            if (mappingExists) {
                throw new DesignException(
                        "the following local id is not unique: " + localId);
            }
        }
        // caption: a property of a component, possibly not unique
        String caption = component.getCaption();
        if (caption != null) {
            mapCaption(caption, component);
        }
    }

    /**
     * Creates a Component corresponding to the given node. Does not set the
     * attributes for the created object.
     * 
     * @param node
     *            a node of an html tree
     * @return a Component corresponding to node, with no attributes set.
     */
    private Component instantiateComponent(Node node) {
        String tag = node.nodeName();

        ComponentMapper componentMapper = Design.getComponentMapper();
        Component component = componentMapper.tagToComponent(tag,
                Design.getComponentFactory(), this);

        assert tag.equals(componentMapper.componentToTag(component, this));

        return component;
    }

    /**
     * Instantiates given class via ComponentFactory.
     * 
     * @param qualifiedClassName
     *            class name to instantiate
     * @return instance of a given class
     */
    private Component instantiateClass(String qualifiedClassName) {
        ComponentFactory factory = Design.getComponentFactory();
        Component component = factory.createComponent(qualifiedClassName, this);

        if (component == null) {
            throw new DesignException("Got unexpected null component from "
                    + factory.getClass().getName() + " for class "
                    + qualifiedClassName);
        }

        return component;
    }

    /**
     * Returns the root component of a created component hierarchy.
     * 
     * @return the root component of the hierarchy
     */
    public Component getRootComponent() {
        return rootComponent;
    }

    /**
     * Sets the root component of a created component hierarchy.
     * 
     * @param rootComponent
     *            the root component of the hierarchy
     */
    public void setRootComponent(Component rootComponent) {
        this.rootComponent = rootComponent;
    }

    /**
     * Adds a component creation listener. The listener will be notified when
     * components are created while parsing a design template
     * 
     * @param listener
     *            the component creation listener to be added
     */
    public void addComponentCreationListener(ComponentCreationListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a component creation listener.
     * 
     * @param listener
     *            the component creation listener to be removed
     */
    public void removeComponentCreationListener(
            ComponentCreationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires component creation event
     * 
     * @param localId
     *            localId of the component
     * @param component
     *            the component that was created
     */
    private void fireComponentCreatedEvent(String localId, Component component) {
        ComponentCreatedEvent event = new ComponentCreatedEvent(localId,
                component);
        for (ComponentCreationListener listener : listeners) {
            listener.componentCreated(event);
        }
    }

    /**
     * Interface to be implemented by component creation listeners
     * 
     * @author Vaadin Ltd
     */
    public interface ComponentCreationListener extends Serializable {

        /**
         * Called when component has been created in the design context
         * 
         * @param event
         *            the component creation event containing information on the
         *            created component
         */
        public void componentCreated(ComponentCreatedEvent event);
    }

    /**
     * Component creation event that is fired when a component is created in the
     * context
     * 
     * @author Vaadin Ltd
     */
    public class ComponentCreatedEvent implements Serializable {
        private String localId;
        private Component component;
        private DesignContext context;

        /**
         * Creates a new instance of ComponentCreatedEvent
         * 
         * @param localId
         *            the local id of the created component
         * @param component
         *            the created component
         */
        private ComponentCreatedEvent(String localId, Component component) {
            this.localId = localId;
            this.component = component;
            context = DesignContext.this;
        }

        /**
         * Returns the local id of the created component or null if not exist
         * 
         * @return the localId
         */
        public String getLocalId() {
            return localId;
        }

        /**
         * Returns the created component
         * 
         * @return the component
         */
        public Component getComponent() {
            return component;
        }
    }

    /**
     * Helper method for component write implementors to determine whether their
     * children should be written out or not
     * 
     * @param c
     *            The component being written
     * @param defaultC
     *            The default instance for the component
     * @return whether the children of c should be written
     */
    public boolean shouldWriteChildren(Component c, Component defaultC) {
        if (c == getRootComponent()) {
            // The root component should always write its children - otherwise
            // the result is empty
            return true;
        }

        if (defaultC instanceof HasComponents
                && ((HasComponents) defaultC).iterator().hasNext()) {
            // Easy version which assumes that this is a custom component if the
            // constructor adds children
            return false;
        }

        return true;
    }

    /**
     * Determines whether the container data of a component should be written
     * out by delegating to a {@link ShouldWriteDataDelegate}. The default
     * delegate assumes that all component data is provided by a data source
     * connected to a back end system and that the data should thus not be
     * written.
     * 
     * @since 7.5.0
     * @see #setShouldWriteDataDelegate(ShouldWriteDataDelegate)
     * @param component
     *            the component to check
     * @return <code>true</code> if container data should be written out for the
     *         provided component; otherwise <code>false</code>.
     */
    public boolean shouldWriteData(Component component) {
        return getShouldWriteDataDelegate().shouldWriteData(component);
    }

    /**
     * Sets the delegate that determines whether the container data of a
     * component should be written out.
     * 
     * @since 7.5.0
     * @see #shouldWriteChildren(Component, Component)
     * @see #getShouldWriteDataDelegate()
     * @param shouldWriteDataDelegate
     *            the delegate to set, not <code>null</code>
     * @throws IllegalArgumentException
     *             if the provided delegate is <code>null</code>
     */
    public void setShouldWriteDataDelegate(
            ShouldWriteDataDelegate shouldWriteDataDelegate) {
        if (shouldWriteDataDelegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null");
        }
        this.shouldWriteDataDelegate = shouldWriteDataDelegate;
    }

    /**
     * Gets the delegate that determines whether the container data of a
     * component should be written out.
     * 
     * @since 7.5.0
     * @see #setShouldWriteDataDelegate(ShouldWriteDataDelegate)
     * @see #shouldWriteChildren(Component, Component)
     * @return the shouldWriteDataDelegate the currently use delegate
     */
    public ShouldWriteDataDelegate getShouldWriteDataDelegate() {
        return shouldWriteDataDelegate;
    }

}
