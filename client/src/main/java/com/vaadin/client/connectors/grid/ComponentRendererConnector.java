/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.client.connectors.grid;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.renderers.WidgetRenderer;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.grid.renderers.ComponentRendererState;
import com.vaadin.ui.renderers.ComponentRenderer;

/**
 * Connector for {@link ComponentRenderer}. The renderer wraps the component
 * into a {@link SimplePanel} to allow handling events correctly.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(ComponentRenderer.class)
public class ComponentRendererConnector
        extends AbstractGridRendererConnector<String> {

    private HashSet<String> knownConnectors = new HashSet<>();
    private HandlerRegistration handlerRegistration;

    @Override
    protected Renderer<String> createRenderer() {
        return new WidgetRenderer<String, SimplePanel>() {

            @Override
            public SimplePanel createWidget() {
                SimplePanel panel = GWT.create(SimplePanel.class);
                panel.setStyleName("component-wrap");
                return panel;
            }

            @Override
            public void render(RendererCellReference cell, String connectorId,
                    SimplePanel widget) {
                createConnectorHierarchyChangeHandler();
                Widget connectorWidget = null;
                if (connectorId != null) {
                    ComponentConnector connector = (ComponentConnector) ConnectorMap
                            .get(getConnection()).getConnector(connectorId);
                    if (connector != null) {
                        connectorWidget = connector.getWidget();
                        knownConnectors.add(connectorId);
                    }
                }
                if (connectorWidget != null) {
                    widget.setWidget(connectorWidget);
                } else if (widget.getWidget() != null) {
                    widget.remove(widget.getWidget());
                    knownConnectors.remove(connectorId);
                }
            }
        };
    }

    @Override
    public ComponentRendererState getState() {
        return (ComponentRendererState) super.getState();
    }

    @Override
    public void onUnregister() {
        unregisterHierarchyHandler();
        super.onUnregister();
    }

    /**
     * Adds a listener for grid hierarchy changes to find detached connectors
     * previously handled by this renderer in order to detach from DOM their
     * widgets before {@link AbstractComponentConnector#onUnregister()} is
     * invoked otherwise an error message is logged.
     */
    private void createConnectorHierarchyChangeHandler() {
        if (handlerRegistration == null) {
            handlerRegistration = getGridConnector()
                    .addConnectorHierarchyChangeHandler(event -> {
                        Iterator<String> iterator = knownConnectors.iterator();
                        while (iterator.hasNext()) {
                            ComponentConnector connector = (ComponentConnector) ConnectorMap
                                    .get(getConnection())
                                    .getConnector(iterator.next());
                            if (connector != null
                                    && connector.getParent() == null) {
                                connector.getWidget().removeFromParent();
                                iterator.remove();
                            }
                        }
                    });
        }
    }

    private void unregisterHierarchyHandler() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
    }

}
