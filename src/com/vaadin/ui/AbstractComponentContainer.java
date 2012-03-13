/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.terminal.gwt.server.ComponentSizeValidator;

/**
 * Extension to {@link AbstractComponent} that defines the default
 * implementation for the methods in {@link ComponentContainer}. Basic UI
 * components that need to contain other components inherit this class to easily
 * qualify as a component container.
 * 
 * @author Vaadin Ltd
 * @version
 * @VERSION@
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

    /**
     * Removes all components from the container. This should probably be
     * re-implemented in extending classes for a more powerful implementation.
     */
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

    /**
     * Notifies all contained components that the container is attached to a
     * window.
     * 
     * @see com.vaadin.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();

        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            (i.next()).attach();
        }
    }

    /**
     * Notifies all contained components that the container is detached from a
     * window.
     * 
     * @see com.vaadin.ui.Component#detach()
     */
    @Override
    public void detach() {
        super.detach();

        for (final Iterator<Component> i = getComponentIterator(); i.hasNext();) {
            (i.next()).detach();
        }
    }

    /* Events */

    private static final Method COMPONENT_ATTACHED_METHOD;

    private static final Method COMPONENT_DETACHED_METHOD;

    static {
        try {
            COMPONENT_ATTACHED_METHOD = ComponentAttachListener.class
                    .getDeclaredMethod("componentAttachedToContainer",
                            new Class[] { ComponentAttachEvent.class });
            COMPONENT_DETACHED_METHOD = ComponentDetachListener.class
                    .getDeclaredMethod("componentDetachedFromContainer",
                            new Class[] { ComponentDetachEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in AbstractComponentContainer");
        }
    }

    /* documented in interface */
    public void addListener(ComponentAttachListener listener) {
        addListener(ComponentContainer.ComponentAttachEvent.class, listener,
                COMPONENT_ATTACHED_METHOD);
    }

    /* documented in interface */
    public void addListener(ComponentDetachListener listener) {
        addListener(ComponentContainer.ComponentDetachEvent.class, listener,
                COMPONENT_DETACHED_METHOD);
    }

    /* documented in interface */
    public void removeListener(ComponentAttachListener listener) {
        removeListener(ComponentContainer.ComponentAttachEvent.class, listener,
                COMPONENT_ATTACHED_METHOD);
    }

    /* documented in interface */
    public void removeListener(ComponentDetachListener listener) {
        removeListener(ComponentContainer.ComponentDetachEvent.class, listener,
                COMPONENT_DETACHED_METHOD);
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
    public void addComponent(Component c) {
        if (c instanceof ComponentContainer) {
            // Make sure we're not adding the component inside it's own content
            for (Component parent = this; parent != null; parent = parent
                    .getParent()) {
                if (parent == c) {
                    throw new IllegalArgumentException(
                            "Component cannot be added inside it's own content");
                }
            }
        }

        if (c.getParent() != null) {
            // If the component already has a parent, try to remove it
            ComponentContainer oldParent = (ComponentContainer) c.getParent();
            oldParent.removeComponent(c);

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
    public void removeComponent(Component c) {
        if (c.getParent() == this) {
            c.setParent(null);
            fireComponentDetachEvent(c);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (getParent() != null && !getParent().isEnabled()) {
            // some ancestor still disabled, don't update children
            return;
        } else {
            requestRepaintAll();
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
        if (this instanceof Panel) {
            Panel p = (Panel) this;
            ComponentContainer content = p.getContent();
            boolean valid = vertical ? ComponentSizeValidator
                    .checkHeights(content) : ComponentSizeValidator
                    .checkWidths(content);

            if (!valid) {
                components = new HashSet<Component>(1);
                components.add(content);
            }
        } else {
            for (Iterator<Component> componentIterator = getComponentIterator(); componentIterator
                    .hasNext();) {
                Component component = componentIterator.next();
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
        }
        return components;
    }

    private void repaintChildTrees(Collection<Component> dirtyChildren) {
        for (Component c : dirtyChildren) {
            if (c instanceof ComponentContainer) {
                ComponentContainer cc = (ComponentContainer) c;
                cc.requestRepaintAll();
            } else {
                c.requestRepaint();
            }
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

    public void requestRepaintAll() {
        requestRepaint();
        for (Iterator<Component> childIterator = getComponentIterator(); childIterator
                .hasNext();) {
            Component c = childIterator.next();
            if (c instanceof Form) {
                // Form has children in layout, but is not ComponentContainer
                c.requestRepaint();
                ((Form) c).getLayout().requestRepaintAll();
            } else if (c instanceof Table) {
                ((Table) c).requestRepaintAll();
            } else if (c instanceof ComponentContainer) {
                ((ComponentContainer) c).requestRepaintAll();
            } else {
                c.requestRepaint();
            }
        }
    }

    /**
     * Returns an iterator for the child components.
     * 
     * @return An iterator for the child components.
     * @see #getComponentIterator()
     * @since 7.0.0
     */
    public Iterator<Component> iterator() {
        return getComponentIterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.HasComponents#isComponentVisible(com.vaadin.ui.Component)
     */
    public boolean isComponentVisible(Component childComponent) {
        return true;
    }
}