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

package com.vaadin.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.server.ComponentSizeValidator;

/**
 * Extension to {@link AbstractComponent} that defines the default
 * implementation for the methods in {@link ComponentContainer}. Basic UI
 * components that need to contain other components inherit this class to easily
 * qualify as a component container.
 * 
 * @author Vaadin Ltd
 * @since 3.0
 */
@SuppressWarnings("serial")
public abstract class AbstractComponentContainer extends AbstractComponent
        implements ComponentContainer {

    /**
     * Constructs a new component container.
     */
    public AbstractComponentContainer() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.ComponentContainer#addComponents(com.vaadin.ui.Component[])
     */
    @Override
    public void addComponents(Component... components) {
        for (Component c : components) {
            addComponent(c);
        }
    }

    /**
     * Removes all components from the container. This should probably be
     * re-implemented in extending classes for a more powerful implementation.
     */
    @Override
    public void removeAllComponents() {
        final LinkedList<Component> l = new LinkedList<Component>();

        // Adds all components
        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            l.add(i.next());
        }

        // Removes all component
        for (final Iterator<Component> i = l.iterator(); i.hasNext();) {
            removeComponent(i.next());
        }
    }

    /*
     * Moves all components from an another container into this container. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    @Override
    public void moveComponentsFrom(ComponentContainer source) {
        final LinkedList<Component> components = new LinkedList<Component>();
        for (final Iterator<Component> i = source.getComponentIterator(); i
                .hasNext();) {
            components.add(i.next());
        }

        for (final Iterator<Component> i = components.iterator(); i.hasNext();) {
            final Component c = i.next();
            source.removeComponent(c);
            addComponent(c);
        }
    }

    /* documented in interface */
    @Override
    public void addComponentAttachListener(ComponentAttachListener listener) {
        addListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addComponentAttachListener(com.vaadin.ui.ComponentContainer.ComponentAttachListener)}
     **/
    @Override
    @Deprecated
    public void addListener(ComponentAttachListener listener) {
        addComponentAttachListener(listener);
    }

    /* documented in interface */
    @Override
    public void removeComponentAttachListener(ComponentAttachListener listener) {
        removeListener(ComponentAttachEvent.class, listener,
                ComponentAttachListener.attachMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addComponentDetachListener(com.vaadin.ui.ComponentContainer.ComponentDetachListener)}
     **/
    @Override
    @Deprecated
    public void addListener(ComponentDetachListener listener) {
        addComponentDetachListener(listener);
    }

    /* documented in interface */
    @Override
    public void addComponentDetachListener(ComponentDetachListener listener) {
        addListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeComponentAttachListener(com.vaadin.ui.ComponentContainer.ComponentAttachListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(ComponentAttachListener listener) {
        removeComponentAttachListener(listener);
    }

    /* documented in interface */
    @Override
    public void removeComponentDetachListener(ComponentDetachListener listener) {
        removeListener(ComponentDetachEvent.class, listener,
                ComponentDetachListener.detachMethod);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeComponentDetachListener(com.vaadin.ui.ComponentContainer.ComponentDetachListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(ComponentDetachListener listener) {
        removeComponentDetachListener(listener);
    }

    /**
     * Fires the component attached event. This should be called by the
     * addComponent methods after the component have been added to this
     * container.
     * 
     * @param component
     *            the component that has been added to this container.
     */
    protected void fireComponentAttachEvent(Component component) {
        fireEvent(new ComponentAttachEvent(this, component));
    }

    /**
     * Fires the component detached event. This should be called by the
     * removeComponent methods after the component have been removed from this
     * container.
     * 
     * @param component
     *            the component that has been removed from this container.
     */
    protected void fireComponentDetachEvent(Component component) {
        fireEvent(new ComponentDetachEvent(this, component));
    }

    /**
     * This only implements the events and component parent calls. The extending
     * classes must implement component list maintenance and call this method
     * after component list maintenance.
     * 
     * @see com.vaadin.ui.ComponentContainer#addComponent(Component)
     */
    @Override
    public void addComponent(Component c) {
        // Make sure we're not adding the component inside it's own content
        if (isOrHasAncestor(c)) {
            throw new IllegalArgumentException(
                    "Component cannot be added inside it's own content");
        }

        if (c.getParent() != null) {
            // If the component already has a parent, try to remove it
            AbstractSingleComponentContainer.removeFromParent(c);
        }

        c.setParent(this);
        fireComponentAttachEvent(c);
    }

    /**
     * This only implements the events and component parent calls. The extending
     * classes must implement component list maintenance and call this method
     * before component list maintenance.
     * 
     * @see com.vaadin.ui.ComponentContainer#removeComponent(Component)
     */
    @Override
    public void removeComponent(Component c) {
        if (equals(c.getParent())) {
            c.setParent(null);
            fireComponentDetachEvent(c);
        }
    }

    @Override
    public void setWidth(float width, Unit unit) {
        /*
         * child tree repaints may be needed, due to our fall back support for
         * invalid relative sizes
         */
        Collection<Component> dirtyChildren = null;
        boolean childrenMayBecomeUndefined = false;
        if (getWidth() == SIZE_UNDEFINED && width != SIZE_UNDEFINED) {
            // children currently in invalid state may need repaint
            dirtyChildren = getInvalidSizedChildren(false);
        } else if ((width == SIZE_UNDEFINED && getWidth() != SIZE_UNDEFINED)
                || (unit == Unit.PERCENTAGE
                        && getWidthUnits() != Unit.PERCENTAGE && !ComponentSizeValidator
                            .parentCanDefineWidth(this))) {
            /*
             * relative width children may get to invalid state if width becomes
             * invalid. Width may also become invalid if units become percentage
             * due to the fallback support
             */
            childrenMayBecomeUndefined = true;
            dirtyChildren = getInvalidSizedChildren(false);
        }
        super.setWidth(width, unit);
        repaintChangedChildTrees(dirtyChildren, childrenMayBecomeUndefined,
                false);
    }

    private void repaintChangedChildTrees(
            Collection<Component> invalidChildren,
            boolean childrenMayBecomeUndefined, boolean vertical) {
        if (childrenMayBecomeUndefined) {
            Collection<Component> previouslyInvalidComponents = invalidChildren;
            invalidChildren = getInvalidSizedChildren(vertical);
            if (previouslyInvalidComponents != null && invalidChildren != null) {
                for (Iterator<Component> iterator = invalidChildren.iterator(); iterator
                        .hasNext();) {
                    Component component = iterator.next();
                    if (previouslyInvalidComponents.contains(component)) {
                        // still invalid don't repaint
                        iterator.remove();
                    }
                }
            }
        } else if (invalidChildren != null) {
            Collection<Component> stillInvalidChildren = getInvalidSizedChildren(vertical);
            if (stillInvalidChildren != null) {
                for (Component component : stillInvalidChildren) {
                    // didn't become valid
                    invalidChildren.remove(component);
                }
            }
        }
        if (invalidChildren != null) {
            repaintChildTrees(invalidChildren);
        }
    }

    private Collection<Component> getInvalidSizedChildren(final boolean vertical) {
        HashSet<Component> components = null;
        for (Component component : this) {
            boolean valid = vertical ? ComponentSizeValidator
                    .checkHeights(component) : ComponentSizeValidator
                    .checkWidths(component);
            if (!valid) {
                if (components == null) {
                    components = new HashSet<Component>();
                }
                components.add(component);
            }
        }
        return components;
    }

    private void repaintChildTrees(Collection<Component> dirtyChildren) {
        for (Component c : dirtyChildren) {
            c.markAsDirtyRecursive();
        }
    }

    @Override
    public void setHeight(float height, Unit unit) {
        /*
         * child tree repaints may be needed, due to our fall back support for
         * invalid relative sizes
         */
        Collection<Component> dirtyChildren = null;
        boolean childrenMayBecomeUndefined = false;
        if (getHeight() == SIZE_UNDEFINED && height != SIZE_UNDEFINED) {
            // children currently in invalid state may need repaint
            dirtyChildren = getInvalidSizedChildren(true);
        } else if ((height == SIZE_UNDEFINED && getHeight() != SIZE_UNDEFINED)
                || (unit == Unit.PERCENTAGE
                        && getHeightUnits() != Unit.PERCENTAGE && !ComponentSizeValidator
                            .parentCanDefineHeight(this))) {
            /*
             * relative height children may get to invalid state if height
             * becomes invalid. Height may also become invalid if units become
             * percentage due to the fallback support.
             */
            childrenMayBecomeUndefined = true;
            dirtyChildren = getInvalidSizedChildren(true);
        }
        super.setHeight(height, unit);
        repaintChangedChildTrees(dirtyChildren, childrenMayBecomeUndefined,
                true);
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated As of 7.0, use {@link #iterator()} instead.
     */
    @Deprecated
    @Override
    public Iterator<Component> getComponentIterator() {
        return iterator();
    }
}
