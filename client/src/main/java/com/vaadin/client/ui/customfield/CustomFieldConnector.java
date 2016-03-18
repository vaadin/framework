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
package com.vaadin.client.ui.customfield;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorHierarchyChangeEvent.ConnectorHierarchyChangeHandler;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VCustomComponent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.CustomField;

@Connect(value = CustomField.class)
public class CustomFieldConnector extends AbstractFieldConnector implements
        HasComponentsConnector, ConnectorHierarchyChangeHandler {

    List<ComponentConnector> childComponents;

    /**
     * Default constructor
     */
    public CustomFieldConnector() {
        addConnectorHierarchyChangeHandler(this);
    }

    @Override
    public VCustomComponent getWidget() {
        return (VCustomComponent) super.getWidget();
    }

    @Override
    public void updateCaption(ComponentConnector connector) {
        // NOP, custom field does not render the caption of its content
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        // We always have 1 child, unless the child is hidden
        getWidget().setWidget(getContentWidget());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.HasComponentsConnector#getChildren()
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
     * @see com.vaadin.client.HasComponentsConnector#setChildren
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

    /**
     * Returns the content (only/first child) of the container.
     * 
     * @return child connector or null if none (e.g. invisible or not set on
     *         server)
     */
    protected ComponentConnector getContent() {
        List<ComponentConnector> children = getChildComponents();
        if (children.isEmpty()) {
            return null;
        } else {
            return children.get(0);
        }
    }

    /**
     * Returns the widget (if any) of the content of the container.
     * 
     * @return widget of the only/first connector of the container, null if no
     *         content or if there is no widget for the connector
     */
    protected Widget getContentWidget() {
        ComponentConnector content = getContent();
        if (null != content) {
            return content.getWidget();
        } else {
            return null;
        }
    }

}
