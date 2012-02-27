/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Collection;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * An interface used by client-side paintables whose widget is a component
 * container (implements {@link HasWidgets}).
 */
public interface ComponentContainerConnector extends ComponentConnector {

    /**
     * Update child components caption, description and error message.
     * 
     * <p>
     * Each component is responsible for maintaining its caption, description
     * and error message. In most cases components doesn't want to do that and
     * those elements reside outside of the component. Because of this layouts
     * must provide service for it's childen to show those elements for them.
     * </p>
     * 
     * @param paintable
     *            Child component for which service is requested.
     * @param uidl
     *            UIDL of the child component.
     */
    void updateCaption(ComponentConnector paintable, UIDL uidl);

    /**
     * Returns the children for this paintable.
     * <p>
     * The children for this paintable are defined as all
     * {@link ComponentConnector}s whose parent is this
     * {@link ComponentContainerConnector}.
     * </p>
     * 
     * @return A collection of children for this paintable. An empty collection
     *         if there are no children.
     */
    public Collection<ComponentConnector> getChildren();

}
