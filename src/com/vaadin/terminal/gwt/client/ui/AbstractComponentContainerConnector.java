/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.LinkedList;
import java.util.List;

import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentContainerConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangedEvent;
import com.vaadin.terminal.gwt.client.Util;

public abstract class AbstractComponentContainerConnector extends
        AbstractComponentConnector implements ComponentContainerConnector {

    List<ComponentConnector> children;

    /**
     * Default constructor
     */
    public AbstractComponentContainerConnector() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ComponentContainerConnector#getChildren()
     */
    public List<ComponentConnector> getChildren() {
        if (children == null) {
            return new LinkedList<ComponentConnector>();
        }

        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ComponentContainerConnector#setChildren
     * (java.util.Collection)
     */
    public void setChildren(List<ComponentConnector> children) {
        this.children = children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ComponentContainerConnector#
     * connectorHierarchyChanged
     * (com.vaadin.terminal.gwt.client.ConnectorHierarchyChangedEvent)
     */
    public void connectorHierarchyChanged(ConnectorHierarchyChangedEvent event) {
        // TODO Remove debug info
        System.out.println("Hierarchy changed for " + Util.getSimpleName(this));
        System.out.println("* Old children: " + event.getOldChildren());
        System.out.println("* New children: " + getChildren());
    }
}
