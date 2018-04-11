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

package com.vaadin.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickNotifier;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutServerRpc;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutState;
import com.vaadin.shared.ui.orderedlayout.AbstractOrderedLayoutState.ChildComponentData;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

@SuppressWarnings("serial")
public abstract class AbstractOrderedLayout extends AbstractLayout
        implements Layout.AlignmentHandler, Layout.SpacingHandler,
        LayoutClickNotifier, Layout.MarginHandler {

    private final AbstractOrderedLayoutServerRpc rpc = (
            MouseEventDetails mouseDetails,
            Connector clickedConnector) -> fireEvent(
                    LayoutClickEvent.createEvent(AbstractOrderedLayout.this,
                            mouseDetails, clickedConnector));

    public static final Alignment ALIGNMENT_DEFAULT = Alignment.TOP_LEFT;

    /**
     * Custom layout slots containing the components.
     */
    protected LinkedList<Component> components = new LinkedList<>();

    private Alignment defaultComponentAlignment = Alignment.TOP_LEFT;

    /* Child component alignments */

    /**
     * Constructs an empty AbstractOrderedLayout.
     */
    public AbstractOrderedLayout() {
        registerRpc(rpc);
    }

    @Override
    protected AbstractOrderedLayoutState getState() {
        return (AbstractOrderedLayoutState) super.getState();
    }

    @Override
    protected AbstractOrderedLayoutState getState(boolean markAsDirty) {
        return (AbstractOrderedLayoutState) super.getState(markAsDirty);
    }

    /**
     * Add a component into this container. The component is added to the right
     * or under the previous component.
     *
     * @param c
     *            the component to be added.
     */
    @Override
    public void addComponent(Component c) {
        // Add to components before calling super.addComponent
        // so that it is available to AttachListeners
        components.add(c);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }
        componentAdded(c);
    }

    /**
     * Adds a component into this container. The component is added to the left
     * or on top of the other components.
     *
     * @param c
     *            the component to be added.
     */
    public void addComponentAsFirst(Component c) {
        // If c is already in this, we must remove it before proceeding
        // see ticket #7668
        if (equals(c.getParent())) {
            removeComponent(c);
        }
        components.addFirst(c);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }
        componentAdded(c);

    }

    /**
     * Adds a component into indexed position in this container.
     *
     * @param c
     *            the component to be added.
     * @param index
     *            the index of the component position. The components currently
     *            in and after the position are shifted forwards.
     */
    public void addComponent(Component c, int index) {
        // If c is already in this, we must remove it before proceeding
        // see ticket #7668
        if (equals(c.getParent())) {
            // When c is removed, all components after it are shifted down
            if (index > getComponentIndex(c)) {
                index--;
            }
            removeComponent(c);
        }
        components.add(index, c);
        try {
            super.addComponent(c);
        } catch (IllegalArgumentException e) {
            components.remove(c);
            throw e;
        }

        componentAdded(c);
    }

    private void componentRemoved(Component c) {
        getState().childData.remove(c);
    }

    private void componentAdded(Component c) {
        ChildComponentData ccd = new ChildComponentData();
        ccd.alignmentBitmask = getDefaultComponentAlignment().getBitMask();
        getState().childData.put(c, ccd);
    }

    /**
     * Removes the component from this container.
     *
     * @param c
     *            the component to be removed.
     */
    @Override
    public void removeComponent(Component c) {
        components.remove(c);
        super.removeComponent(c);
        componentRemoved(c);
    }

    /**
     * Gets the component container iterator for going trough all the components
     * in the container.
     *
     * @return the Iterator of the components inside the container.
     */
    @Override
    public Iterator<Component> iterator() {
        return Collections.unmodifiableCollection(components).iterator();
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     *
     * @return the number of contained components
     */
    @Override
    public int getComponentCount() {
        return components.size();
    }

    /* Documented in superclass */
    @Override
    public void replaceComponent(Component oldComponent,
            Component newComponent) {

        // Gets the locations
        int oldLocation = -1;
        int newLocation = -1;
        int location = 0;
        for (final Component component : components) {
            if (component == oldComponent) {
                oldLocation = location;
            }
            if (component == newComponent) {
                newLocation = location;
            }

            location++;
        }

        if (oldLocation == -1) {
            addComponent(newComponent);
        } else if (newLocation == -1) {
            Alignment alignment = getComponentAlignment(oldComponent);
            float expandRatio = getExpandRatio(oldComponent);

            removeComponent(oldComponent);
            addComponent(newComponent, oldLocation);
            applyLayoutSettings(newComponent, alignment, expandRatio);
        } else {
            // Both old and new are in the layout
            if (oldLocation > newLocation) {
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
            } else {
                components.remove(newComponent);
                components.add(oldLocation, newComponent);
                components.remove(oldComponent);
                components.add(newLocation, oldComponent);
            }

            markAsDirty();
        }
    }

    @Override
    public void setComponentAlignment(Component childComponent,
            Alignment alignment) {
        ChildComponentData childData = getState().childData.get(childComponent);
        if (childData != null) {
            // Alignments are bit masks
            childData.alignmentBitmask = alignment.getBitMask();
        } else {
            throw new IllegalArgumentException(
                    "Component must be added to layout before using setComponentAlignment()");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Layout.AlignmentHandler#getComponentAlignment(com
     * .vaadin.ui.Component)
     */
    @Override
    public Alignment getComponentAlignment(Component childComponent) {
        ChildComponentData childData = getState().childData.get(childComponent);
        if (childData == null) {
            throw new IllegalArgumentException(
                    "The given component is not a child of this layout");
        }

        return new Alignment(childData.alignmentBitmask);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Layout.SpacingHandler#setSpacing(boolean)
     */
    @Override
    public void setSpacing(boolean spacing) {
        getState().spacing = spacing;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Layout.SpacingHandler#isSpacing()
     */
    @Override
    public boolean isSpacing() {
        return getState(false).spacing;
    }

    /**
     * <p>
     * This method is used to control how excess space in layout is distributed
     * among components. Excess space may exist if layout is sized and contained
     * non relatively sized components don't consume all available space.
     *
     * <p>
     * Example how to distribute 1:3 (33%) for component1 and 2:3 (67%) for
     * component2 :
     *
     * <code>
     * layout.setExpandRatio(component1, 1);<br>
     * layout.setExpandRatio(component2, 2);
     * </code>
     *
     * <p>
     * If no ratios have been set, the excess space is distributed evenly among
     * all components.
     *
     * <p>
     * Note, that width or height (depending on orientation) needs to be defined
     * for this method to have any effect.
     *
     * @see Sizeable
     *
     * @param component
     *            the component in this layout which expand ratio is to be set
     * @param ratio
     *            new expand ratio (greater or equal to 0)
     * @throws IllegalArgumentException
     *             if the expand ratio is negative or the component is not a
     *             direct child of the layout
     */
    public void setExpandRatio(Component component, float ratio) {
        ChildComponentData childData = getState().childData.get(component);
        if (childData == null) {
            throw new IllegalArgumentException(
                    "The given component is not a child of this layout");
        }
        if (ratio < 0.0f) {
            throw new IllegalArgumentException(
                    "Expand ratio can't be less than 0.0");
        }

        childData.expandRatio = ratio;
    }

    /**
     * Returns the expand ratio of given component.
     *
     * @param component
     *            which expand ratios is requested
     * @return expand ratio of given component, 0.0f by default.
     */
    public float getExpandRatio(Component component) {
        ChildComponentData childData = getState(false).childData.get(component);
        if (childData == null) {
            throw new IllegalArgumentException(
                    "The given component is not a child of this layout");
        }

        return childData.expandRatio;
    }

    @Override
    public Registration addLayoutClickListener(LayoutClickListener listener) {
        return addListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutClickEvent.class, listener,
                LayoutClickListener.clickMethod);
    }

    @Override
    @Deprecated
    public void removeLayoutClickListener(LayoutClickListener listener) {
        removeListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
                LayoutClickEvent.class, listener);
    }

    /**
     * Returns the index of the given component.
     *
     * @param component
     *            The component to look up.
     * @return The index of the component or -1 if the component is not a child.
     */
    public int getComponentIndex(Component component) {
        return components.indexOf(component);
    }

    /**
     * Returns the component at the given position.
     *
     * @param index
     *            The position of the component.
     * @return The component at the given index.
     * @throws IndexOutOfBoundsException
     *             If the index is out of range.
     */
    public Component getComponent(int index) throws IndexOutOfBoundsException {
        return components.get(index);
    }

    @Override
    public void setMargin(boolean enabled) {
        setMargin(new MarginInfo(enabled));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Layout.MarginHandler#getMargin()
     */
    @Override
    public MarginInfo getMargin() {
        return new MarginInfo(getState(false).marginsBitmask);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Layout.MarginHandler#setMargin(MarginInfo)
     */
    @Override
    public void setMargin(MarginInfo marginInfo) {
        getState().marginsBitmask = marginInfo.getBitMask();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.Layout.AlignmentHandler#getDefaultComponentAlignment()
     */
    @Override
    public Alignment getDefaultComponentAlignment() {
        return defaultComponentAlignment;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.ui.Layout.AlignmentHandler#setDefaultComponentAlignment(com
     * .vaadin.ui.Alignment)
     */
    @Override
    public void setDefaultComponentAlignment(Alignment defaultAlignment) {
        defaultComponentAlignment = defaultAlignment;
    }

    private void applyLayoutSettings(Component target, Alignment alignment,
            float expandRatio) {
        setComponentAlignment(target, alignment);
        setExpandRatio(target, expandRatio);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#readDesign(org.jsoup.nodes .Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        // process default attributes
        super.readDesign(design, designContext);

        setMargin(readMargin(design, getMargin(), designContext));

        // handle children
        for (Element childComponent : design.children()) {
            Attributes attr = childComponent.attributes();
            Component newChild = designContext.readDesign(childComponent);
            addComponent(newChild);
            // handle alignment
            setComponentAlignment(newChild,
                    DesignAttributeHandler.readAlignment(attr));
            // handle expand ratio
            if (attr.hasKey(":expand")) {
                String value = attr.get(":expand");
                if (!value.isEmpty()) {
                    try {
                        float ratio = Float.valueOf(value);
                        setExpandRatio(newChild, ratio);
                    } catch (NumberFormatException nfe) {
                        getLogger()
                                .info("Failed to parse expand ratio " + value);
                    }
                } else {
                    setExpandRatio(newChild, 1.0f);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#writeDesign(org.jsoup.nodes.Element
     * , com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        // write default attributes
        super.writeDesign(design, designContext);

        AbstractOrderedLayout def = designContext.getDefaultInstance(this);

        writeMargin(design, getMargin(), def.getMargin(), designContext);

        // handle children
        if (!designContext.shouldWriteChildren(this, def)) {
            return;
        }

        for (Component child : this) {
            Element childElement = designContext.createElement(child);
            design.appendChild(childElement);
            // handle alignment
            Alignment alignment = getComponentAlignment(child);
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
            // handle expand ratio
            float expandRatio = getExpandRatio(child);
            if (expandRatio == 1.0f) {
                childElement.attr(":expand", true);
            } else if (expandRatio > 0) {
                childElement.attr(":expand", DesignAttributeHandler
                        .getFormatter().format(expandRatio));
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractComponent#getCustomAttributes()
     */
    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> customAttributes = super.getCustomAttributes();
        customAttributes.add("margin");
        customAttributes.add("margin-left");
        customAttributes.add("margin-right");
        customAttributes.add("margin-top");
        customAttributes.add("margin-bottom");
        return customAttributes;
    }

    private static Logger getLogger() {
        return Logger.getLogger(AbstractOrderedLayout.class.getName());
    }
}
