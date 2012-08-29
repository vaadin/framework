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
package com.vaadin.client.ui.customlayout;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractLayoutConnector;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.customlayout.CustomLayoutState;
import com.vaadin.ui.CustomLayout;

@Connect(CustomLayout.class)
public class CustomLayoutConnector extends AbstractLayoutConnector implements
        SimpleManagedLayout, Paintable {

    @Override
    public CustomLayoutState getState() {
        return (CustomLayoutState) super.getState();
    }

    @Override
    protected void init() {
        super.init();
        getWidget().client = getConnection();
        getWidget().pid = getConnectorId();

    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Evaluate scripts
        VCustomLayout.eval(getWidget().scripts);
        getWidget().scripts = null;

    }

    private void updateHtmlTemplate() {
        if (getWidget().hasTemplate()) {
            // We (currently) only do this once. You can't change the template
            // later on.
            return;
        }
        String templateName = getState().getTemplateName();
        String templateContents = getState().getTemplateContents();

        if (templateName != null) {
            // Get the HTML-template from client. Overrides templateContents
            // (even though both can never be given at the same time)
            templateContents = getConnection().getResource(
                    "layouts/" + templateName + ".html");
            if (templateContents == null) {
                templateContents = "<em>Layout file layouts/"
                        + templateName
                        + ".html is missing. Components will be drawn for debug purposes.</em>";
            }
        }

        getWidget().initializeHTML(templateContents,
                getConnection().getThemeUri());
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        // Must do this once here so the HTML has been set up before we start
        // adding child widgets.

        updateHtmlTemplate();

        // For all contained widgets
        for (ComponentConnector child : getChildComponents()) {
            String location = getState().getChildLocations().get(child);
            try {
                getWidget().setWidget(child.getWidget(), location);
            } catch (final IllegalArgumentException e) {
                // If no location is found, this component is not visible
            }
        }
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() == this) {
                // Connector still a child of this
                continue;
            }
            Widget oldChildWidget = oldChild.getWidget();
            if (oldChildWidget.isAttached()) {
                // slot of this widget is emptied, remove it
                getWidget().remove(oldChildWidget);
            }
        }

    }

    @Override
    public VCustomLayout getWidget() {
        return (VCustomLayout) super.getWidget();
    }

    @Override
    public void updateCaption(ComponentConnector paintable) {
        getWidget().updateCaption(paintable);
    }

    @Override
    public void layout() {
        getWidget().iLayoutJS(DOM.getFirstChild(getWidget().getElement()));
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Not interested in anything from the UIDL - just implementing the
        // interface to avoid some warning (#8688)
    }
}
