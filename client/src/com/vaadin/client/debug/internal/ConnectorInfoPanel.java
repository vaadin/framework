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
package com.vaadin.client.debug.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.communication.SharedState;

/**
 * Connector information view panel of the debug window.
 * 
 * @since 7.1.4
 */
public class ConnectorInfoPanel extends FlowPanel {

    /**
     * Update the panel to show information about a connector.
     * 
     * @param connector
     */
    public void update(ServerConnector connector) {
        SharedState state = connector.getState();

        Set<String> ignoreProperties = new HashSet<String>();
        ignoreProperties.add("id");

        String html = getRowHTML("Id", connector.getConnectorId());
        html += getRowHTML("Connector", Util.getSimpleName(connector));

        if (connector instanceof ComponentConnector) {
            ComponentConnector component = (ComponentConnector) connector;

            ignoreProperties.addAll(Arrays.asList("caption", "description",
                    "width", "height"));

            AbstractComponentState componentState = component.getState();

            html += getRowHTML("Widget",
                    Util.getSimpleName(component.getWidget()));
            html += getRowHTML("Caption", componentState.caption);
            html += getRowHTML("Description", componentState.description);
            html += getRowHTML("Width", componentState.width + " (actual: "
                    + component.getWidget().getOffsetWidth() + "px)");
            html += getRowHTML("Height", componentState.height + " (actual: "
                    + component.getWidget().getOffsetHeight() + "px)");
        }

        try {
            JsArrayObject<Property> properties = AbstractConnector
                    .getStateType(connector).getPropertiesAsArray();
            for (int i = 0; i < properties.size(); i++) {
                Property property = properties.get(i);
                String name = property.getName();
                if (!ignoreProperties.contains(name)) {
                    html += getRowHTML(property.getDisplayName(),
                            property.getValue(state));
                }
            }
        } catch (NoDataException e) {
            html += "<div>Could not read state, error has been logged to the console</div>";
            VConsole.error(e);
        }

        clear();
        add(new HTML(html));
    }

    private String getRowHTML(String caption, Object value) {
        return "<div class=\"" + VDebugWindow.STYLENAME
                + "-row\"><span class=\"caption\">" + caption
                + "</span><span class=\"value\">"
                + Util.escapeHTML(String.valueOf(value)) + "</span></div>";
    }

    /**
     * Clear the contents of the panel.
     */
    public void clearContents() {
        clear();
    }
}
