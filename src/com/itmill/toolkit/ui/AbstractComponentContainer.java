/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

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
     * reimplemented in extending classes for a more powerfull implementation.
     */
    public void removeAllComponents() {
        LinkedList l = new LinkedList();

        // Adds all components
        for (Iterator i = getComponentIterator(); i.hasNext();) {
            l.add(i.next());
        }

        // Removes all component
        for (Iterator i = l.iterator(); i.hasNext();) {
            removeComponent((Component) i.next());
        }
    }

    /*
     * Moves all components from an another container into this container. Don't
     * add a JavaDoc comment here, we use the default documentation from
     * implemented interface.
     */
    public void moveComponentsFrom(ComponentContainer source) {
        LinkedList components = new LinkedList();
        for (Iterator i = source.getComponentIterator(); i.hasNext();) {
            components.add(i.next());
        }

        for (Iterator i = components.iterator(); i.hasNext();) {
            Component c = (Component) i.next();
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
    public void attach() {
        super.attach();

        for (Iterator i = getComponentIterator(); i.hasNext();) {
            ((Component) i.next()).attach();
        }
    }

    /**
     * Notifies all contained components that the container is detached from a
     * window.
     * 
     * @see com.itmill.toolkit.ui.Component#detach()
     */
    public void detach() {
        super.detach();

        for (Iterator i = getComponentIterator(); i.hasNext();) {
            ((Component) i.next()).detach();
        }
    }

    /* Events ************************************************************ */

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
        } catch (java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException();
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
     *                the component that has been added to this container.
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
     *                the component that has been removed from this container.
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

}
