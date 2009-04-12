/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.ui;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Extension to {@link AbstractComponent} that defines the default
 * implementation for the methods in {@link ComponentContainer}. Basic UI
 * components that need to contain other components inherit this class to easily
 * qualify as a component container.
 * 
 * @author IT Mill Ltd
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
        final LinkedList l = new LinkedList();

        // Adds all components
        for (final Iterator i = getComponentIterator(); i.hasNext();) {
            l.add(i.next());
        }

        // Removes all component
        for (final Iterator i = l.iterator(); i.hasNext();) {
            removeComponent((Component) i.next());
        }
    }

    /*
     * Moves all components from an another container into this container. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    public void moveComponentsFrom(ComponentContainer source) {
        final LinkedList components = new LinkedList();
        for (final Iterator i = source.getComponentIterator(); i.hasNext();) {
            components.add(i.next());
        }

        for (final Iterator i = components.iterator(); i.hasNext();) {
            final Component c = (Component) i.next();
            source.removeComponent(c);
            addComponent(c);
        }
    }

    /**
     * Notifies all contained components that the container is attached to a
     * window.
     * 
     * @see com.itmill.toolkit.ui.Component#attach()
     */
    @Override
    public void attach() {
        super.attach();

        for (final Iterator i = getComponentIterator(); i.hasNext();) {
            ((Component) i.next()).attach();
        }
    }

    /**
     * Notifies all contained components that the container is detached from a
     * window.
     * 
     * @see com.itmill.toolkit.ui.Component#detach()
     */
    @Override
    public void detach() {
        super.detach();

        for (final Iterator i = getComponentIterator(); i.hasNext();) {
            ((Component) i.next()).detach();
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
     * @see com.itmill.toolkit.ui.ComponentContainer#addComponent(Component)
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
     * @see com.itmill.toolkit.ui.ComponentContainer#removeComponent(Component)
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
    public void setWidth(float width, int unit) {
        if (getWidth() < 0 && width >= 0) {
            // width becoming defined -> relative width children currently
            // painted undefined may become defined
            // TODO could be optimized(subtree of only those components
            // which have undefined height due this component), currently just
            // repaints whole subtree
            requestRepaintAll();
        } else if (getWidth() >= 0 && width < 0) {
            requestRepaintAll();
        }
        super.setWidth(width, unit);
    }

    @Override
    public void setHeight(float height, int unit) {
        float currentHeight = getHeight();
        if (currentHeight < 0.0f && height >= 0.0f) {
            // height becoming defined -> relative height childs currently
            // painted undefined may become defined
            // TODO this could be optimized (subtree of only those components
            // which have undefined width due this component), currently just
            // repaints whole
            // subtree
            requestRepaintAll();
        } else if (currentHeight >= 0 && height < 0) {
            requestRepaintAll();
        }
        super.setHeight(height, unit);
    }

    public void requestRepaintAll() {
        requestRepaint();
        for (Iterator childIterator = getComponentIterator(); childIterator
                .hasNext();) {
            Component c = (Component) childIterator.next();
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

}