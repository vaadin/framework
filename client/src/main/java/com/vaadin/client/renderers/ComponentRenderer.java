/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.renderers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * A Renderer that displays custom components. The renderer wraps the component
 * into a {@link SimplePanel} to allow handling events correctly.
 * Click handlers can be added to the renderer, invoked when the rendered component is clicked.
 *
 * @since 8.2
 */
public abstract class ComponentRenderer extends ClickableRenderer<String, SimplePanel> {

    @Override
    public SimplePanel createWidget() {
        SimplePanel panel = GWT.create(SimplePanel.class);
        panel.setStyleName("component-wrap");
        addClickHandler(panel);
        return panel;
    }

    @Override
    public void render(RendererCellReference cell, String connectorId,
                       SimplePanel widget) {
        if (connectorId != null) {
            ComponentConnector connector = (ComponentConnector) ConnectorMap
                    .get(this.getConnectorConnection()).getConnector(connectorId);
            widget.setWidget(connector.getWidget());
        } else if (widget.getWidget() != null) {
            widget.remove(widget.getWidget());
        }
        addClickHandler(widget);
    }

    public abstract ApplicationConnection getConnectorConnection();

    private void addClickHandler(SimplePanel panel) {
        panel.iterator().forEachRemaining(widget -> widget.addDomHandler(this, ClickEvent.getType()));
    }

}
