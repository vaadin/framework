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

package com.vaadin.client;

import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasWidgets;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.ui.HasComponents;

/**
 * An interface used by client-side connectors whose widget is a component
 * container (implements {@link HasWidgets}).
 */
public interface HasComponentsConnector extends ServerConnector {

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
     * @param connector
     *            Child component for which service is requested.
     */
    void updateCaption(ComponentConnector connector);

    /**
     * Returns the children for this connector.
     * <p>
     * The children for this connector are defined as all {@link HasComponents}s
     * whose parent is this {@link HasComponentsConnector}.
     * </p>
     * 
     * @return A collection of children for this connector. An empty collection
     *         if there are no children. Never returns null.
     */
    public List<ComponentConnector> getChildComponents();

    /**
     * Sets the children for this connector. This method should only be called
     * by the framework to ensure that the connector hierarchy on the client
     * side and the server side are in sync.
     * <p>
     * Note that calling this method does not call
     * {@link ConnectorHierarchyChangeHandler#onConnectorHierarchyChange(ConnectorHierarchyChangeEvent)}
     * . The event method is called only when the hierarchy has been updated for
     * all connectors.
     * 
     * @param children
     *            The new child connectors
     */
    public void setChildComponents(List<ComponentConnector> children);

    /**
     * Adds a handler that is called whenever the child hierarchy of this
     * connector has been updated by the server.
     * 
     * @param handler
     *            The handler that should be added.
     * @return A handler registration reference that can be used to unregister
     *         the handler
     */
    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler);

}
