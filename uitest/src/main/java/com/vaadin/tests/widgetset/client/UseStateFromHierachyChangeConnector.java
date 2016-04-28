/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.UseStateFromHierachyComponent;

@Connect(UseStateFromHierachyComponent.class)
public class UseStateFromHierachyChangeConnector extends
        AbstractSingleComponentContainerConnector {

    @Override
    public SimplePanel getWidget() {
        return (SimplePanel) super.getWidget();
    }

    @Override
    public UseStateFromHierachyChangeConnectorState getState() {
        return (UseStateFromHierachyChangeConnectorState) super.getState();
    }

    @Override
    public void updateCaption(ComponentConnector connector) {
        // Caption not supported
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        Connector stateChild = getState().child;
        if (stateChild == null) {
            if (getChildComponents().size() != 0) {
                throw new IllegalStateException(
                        "Hierarchy has child but state has not");
            } else {
                getWidget().setWidget(null);
            }
        } else {
            if (getChildComponents().size() != 1
                    || getChildComponents().get(0) != stateChild) {
                throw new IllegalStateException(
                        "State has child but hierarchy has not");
            } else {
                getWidget().setWidget(getChildComponents().get(0).getWidget());
            }
        }
    }

}
