/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.ui;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ComponentContainerConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;

public abstract class AbstractComponentContainerConnector extends
        AbstractComponentConnector implements ComponentContainerConnector,
        ConnectorHierarchyChangeHandler {

    List<ComponentConnector> childComponents;

    private final boolean debugLogging = false;

    /**
     * Default constructor
     */
    public AbstractComponentContainerConnector() {
        addConnectorHierarchyChangeHandler(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ComponentContainerConnector#getChildren()
     */
    @Override
    public List<ComponentConnector> getChildComponents() {
        if (childComponents == null) {
            return Collections.emptyList();
        }

        return childComponents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ComponentContainerConnector#setChildren
     * (java.util.Collection)
     */
    @Override
    public void setChildComponents(List<ComponentConnector> childComponents) {
        this.childComponents = childComponents;
    }

    @Override
    public HandlerRegistration addConnectorHierarchyChangeHandler(
            ConnectorHierarchyChangeHandler handler) {
        return ensureHandlerManager().addHandler(
                ConnectorHierarchyChangeEvent.TYPE, handler);
    }
}
